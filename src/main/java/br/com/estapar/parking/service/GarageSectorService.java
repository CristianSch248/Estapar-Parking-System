package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.GarageSectorDTO;
import br.com.estapar.parking.DTO.RevenueQuery;
import br.com.estapar.parking.DTO.RevenueResponseDTO;
import br.com.estapar.parking.logging.SimpleConsoleLogger;
import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.model.ParkingSession;
import br.com.estapar.parking.repository.GarageSectorRepository;
import br.com.estapar.parking.repository.GarageSpotRepository;
import br.com.estapar.parking.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GarageSectorService
{
    @Autowired
    private GarageSectorRepository garageSectorRepository;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    public void createGarageSector( GarageSectorDTO garageSectorDTO )
    {
        if ( garageSectorRepository.findBySector( garageSectorDTO.sector() ).isPresent() )
        {
            SimpleConsoleLogger.info(
                    "SETOR [ '" +
                    garageSectorDTO.sector() +
                    "' ]. Já existe ignorando inserção duplicada."
            );
            return;
        }

        GarageSector newGarageSector = new GarageSector();

        newGarageSector.setSector( garageSectorDTO.sector() );
        newGarageSector.setBasePrice( garageSectorDTO.base_price() );
        newGarageSector.setMaxCapacity( garageSectorDTO.max_capacity() );
        newGarageSector.setOpenHour( garageSectorDTO.open_hour() );
        newGarageSector.setCloseHour( garageSectorDTO.close_hour() );
        newGarageSector.setDurationLimitMinutes( garageSectorDTO.duration_limit_minutes() );

        garageSectorRepository.save( newGarageSector );

        SimpleConsoleLogger.info( " Novo setor '" + newGarageSector.getSector() + "' salvo com sucesso!" );
    }

    public RevenueResponseDTO getRevenue( RevenueQuery query )
    {
        ZoneId  zone = ZoneId.of( "America/Sao_Paulo" );
        Instant from = query.date().atStartOfDay( zone ).toInstant();

        List< ParkingSession > parkingSessionList = parkingSessionRepository.findAllBySpot_Sector_SectorAndEntryTimeGreaterThanEqual( query.sector(), from );

        BigDecimal amount = BigDecimal.ZERO;

        for ( ParkingSession parkingSession : parkingSessionList )
        {
            BigDecimal value = Objects.requireNonNullElse( parkingSession.getTotalAmount(), BigDecimal.ZERO );
            amount = amount.add( value );
        }

        return new RevenueResponseDTO( amount, "BRL", from );
    }
}