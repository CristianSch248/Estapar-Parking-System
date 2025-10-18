package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.logging.SimpleConsoleLogger;
import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.model.ParkingSession;
import br.com.estapar.parking.repository.GarageSectorRepository;
import br.com.estapar.parking.repository.GarageSpotRepository;
import br.com.estapar.parking.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingSessionService
{
    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private GarageSectorRepository garageSectorRepository;

    @Autowired
    private GarageSpotRepository garageSpotRepository;

    @Autowired
    private GarageSpotService garageSpotService;

    @Transactional
    public void createEntrySession( ParkingSessionDTO parkingSessionDTO )
    {
        if ( parkingSessionRepository.existsByLicensePlateAndStatusIn( parkingSessionDTO.license_plate(), List.of( "ENTRY", "PARKED" ) ) )
        {
            SimpleConsoleLogger.info( "Já existe sessão ABERTA para esta placa."  );
            return;
        }

        Instant   entryTime = parkingSessionDTO.entry_time();
        LocalTime localTime = entryTime.atZone( ZoneId.systemDefault() ).toLocalTime();

        GarageSector garageSectorSelected = null;
        GarageSpot   reservedSpot         = null;

        List< GarageSector > sectors = garageSectorRepository.findAll();

        for ( GarageSector sector : sectors )
        {
            if ( localTime.isBefore( sector.getOpenHour() ) || localTime.isAfter( sector.getCloseHour() ) )
            {
                SimpleConsoleLogger.warn( "Setor [ " +  sector.getSector() + " ] NÃO está aberto nesse horário." );
                continue;
            }

            reservedSpot = garageSpotService.reserveOneSpot( sector.getId() );

            if ( reservedSpot != null )
            {
                garageSectorSelected = sector;
                break;
            }
        }

        if ( garageSectorSelected == null )
        {
            SimpleConsoleLogger.warn( "Nenhum setor aberto nesse horário."  );
            return;
        }

        if ( reservedSpot == null )
        {
            SimpleConsoleLogger.warn( "As garagens estão lotadas" );
            return;
        }

        ParkingSession session = new ParkingSession();
        session.setLicensePlate( parkingSessionDTO.license_plate() );
        session.setEntryTime( entryTime );
        session.setExitTime( null );
        session.setSector( garageSectorSelected.getSector() );
        session.setSpot( reservedSpot );
        session.setPricePerHour( obtainPricePerHour( garageSectorSelected ) );
        session.setTotalAmount( null );
        session.setStatus( parkingSessionDTO.event_type() );

        parkingSessionRepository.save( session );

        SimpleConsoleLogger.info( "Sessão CRIADA com sucesso!"  );
    }

    @Transactional
    public void updateSessionWithParkingSpot( ParkingSessionDTO parkingSessionDTO )
    {
        ParkingSession newParkingSession = parkingSessionRepository.getByLicensePlateAndStatusIn( parkingSessionDTO.license_plate(), List.of( "ENTRY", "PARKED" ) );

        if ( newParkingSession == null )
        {
            SimpleConsoleLogger.warn( "Sessão não encontrada."  );
            return;
        }

        GarageSpot oldSpot = garageSpotRepository.findGarageSpotById( newParkingSession.getSpot().getId() );
        Optional <GarageSpot> newSpot = garageSpotRepository.findByLatAndLngAndOccupiedFalse( parkingSessionDTO.lat(), parkingSessionDTO.lng() );

        if ( ! newSpot.isPresent() )
        {
            SimpleConsoleLogger.warn( "vaga ocupada ou não encontrada."  );
            return;
        }

        GarageSpot garageSpot = newSpot.get();

        garageSpot = garageSpotService.reserveSpot( garageSpot );

        garageSpotService.removeReserveSpot( oldSpot );

        newParkingSession.setSpot( garageSpot );
        newParkingSession.setSector( garageSpot.getSector().getSector() );
        newParkingSession.setStatus( "PARKED" );

        parkingSessionRepository.save( newParkingSession );

        SimpleConsoleLogger.info( "Sessão ALTERADA com sucesso!"  );
    }

    @Transactional
    public void closeParkingSession( ParkingSessionDTO dto )
    {
        ParkingSession session = parkingSessionRepository.getByLicensePlateAndStatusIn( dto.license_plate(), List.of( "ENTRY", "PARKED" ) );

        if ( session == null )
        {
            SimpleConsoleLogger.warn( "Sessão não encontrada."  );
            return;
        }

        Instant exitTime = dto.exit_time();

        if ( exitTime.isBefore( session.getEntryTime() ) )
        {
           SimpleConsoleLogger.warn( "MOMENTO de SAÍDA não pode ser antes do MOMENTO de ENTRADA" );
            return;
        }

        long minutes = ChronoUnit.MINUTES.between( session.getEntryTime(), exitTime );

        BigDecimal totalAmount;
        if ( minutes <= 30 )
        {
            totalAmount = BigDecimal.ZERO.setScale( 2, RoundingMode.HALF_UP );
        }

        else
        {
            long minutesBeyond = minutes - 30;
            long billableHours = (minutesBeyond + 59) / 60; // arredonda pra cima

            if ( billableHours < 1 )
            {
                billableHours = 1;
            }

            BigDecimal hoursBD = BigDecimal.valueOf( billableHours );

            totalAmount = session.getPricePerHour()
                                 .multiply( hoursBD )
                                 .setScale( 2, RoundingMode.HALF_UP );
        }

        session.setExitTime( exitTime );
        session.setTotalAmount( totalAmount );
        session.setStatus( "EXIT" );

        GarageSpot spot = session.getSpot();
        if ( spot != null && Boolean.TRUE.equals( spot.getOccupied() ) )
        {
            spot.setOccupied( false );
            garageSpotRepository.save( spot );
        }

        parkingSessionRepository.save( session );

        SimpleConsoleLogger.info( "Sessão FINALIZADA com sucesso!" );
    }

    private BigDecimal obtainPricePerHour( GarageSector garageSectorSelected )
    {
        List< GarageSpot > availableSpots = garageSpotRepository.findBySectorAndOccupiedFalse( garageSectorSelected );

        int totalSpots    = garageSectorSelected.getMaxCapacity();
        int occupiedSpots = totalSpots - availableSpots.size();

        BigDecimal occupancyPercentage =
                BigDecimal.valueOf( occupiedSpots )
                          .multiply( BigDecimal.valueOf( 100 ) )
                          .divide( BigDecimal.valueOf( totalSpots ), 2, RoundingMode.HALF_UP );

        BigDecimal basePrice = BigDecimal.valueOf( garageSectorSelected.getBasePrice() );
        BigDecimal fator     = dynamicFactor( occupancyPercentage  );

        return basePrice.multiply( fator ).setScale( 2, RoundingMode.HALF_UP );
    }

    private BigDecimal dynamicFactor( BigDecimal percentageOccupied )
    {
        int compareTo25 = percentageOccupied.compareTo( BigDecimal.valueOf( 25 ) );
        int compareTo50 = percentageOccupied.compareTo( BigDecimal.valueOf( 50 ) );
        int compareTo75 = percentageOccupied.compareTo( BigDecimal.valueOf( 75 ) );

        if ( compareTo25 < 0 )
        {
            return BigDecimal.valueOf( 0.90 ); // < 25%
        }

        else if ( compareTo50 <= 0 )
        {
            return BigDecimal.valueOf( 1.00 ); // 25%..50%
        }

        else if ( compareTo75 <= 0 )
        {
            return BigDecimal.valueOf( 1.10 ); // 50%..75%
        }

        else
        {
            return BigDecimal.valueOf( 1.25 ); // > 75%
        }
    }
}