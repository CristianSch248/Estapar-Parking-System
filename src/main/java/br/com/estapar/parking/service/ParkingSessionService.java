package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.model.ParkingSession;
import br.com.estapar.parking.repository.GarageSectorRepository;
import br.com.estapar.parking.repository.GarageSpotRepository;
import br.com.estapar.parking.repository.ParkingSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ParkingSessionService
{

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private GarageSectorRepository garageSectorRepository;

    @Autowired
    private GarageSpotRepository garageSpotRepository;

    @Transactional
    public ParkingSession createEntrySession( ParkingSessionDTO dto )
    {
        if ( parkingSessionRepository.existsByLicensePlateAndStatusIn( dto.license_plate(), List.of( "ENTRY", "PARKED" ) ) )
        {
            throw new IllegalStateException( "Já existe sessão OPEN para esta placa." );
        }

        Instant   entryTime = dto.entry_time();
        LocalTime localTime = entryTime.atZone( ZoneId.systemDefault() ).toLocalTime();

        GarageSector garageSectorSelected = null;

        List< GarageSector > sectors = garageSectorRepository.findAll();

        for ( GarageSector sector : sectors )
        {

            // se a hora local for antes da hora de abertura ou se a hora local for depois da hora de fechar
            if ( localTime.isBefore( sector.getOpenHour() ) || !localTime.isAfter( sector.getCloseHour() ) )
            {
                continue;
            }

            garageSectorSelected = sector;
            break;
        }

        if ( garageSectorSelected == null )
        {
            throw new IllegalStateException( "Nenhum setor aberto nesse horário." );
        }

        GarageSpot reservedSpot = reserveOneSpot( garageSectorSelected.getId() );

        if ( reservedSpot == null )
        {
            throw new IllegalStateException( "garagem está lotada" );
        }

        List< GarageSpot > livres = garageSpotRepository.findBySectorAndOccupiedFalse( garageSectorSelected );

        int totalVagas = garageSectorSelected.getMaxCapacity();
        if ( totalVagas <= 0 )
        {
            throw new IllegalStateException( "Setor com capacidade inválida." );
        }

        int totalVagasLivres = livres.size();
        int vagasOcupadas    = totalVagas - totalVagasLivres;

        BigDecimal cem = BigDecimal.valueOf( 100 );
        BigDecimal percOcupadas = BigDecimal.valueOf( vagasOcupadas )
                .multiply( cem )
                .divide( BigDecimal.valueOf( totalVagas ), 2, RoundingMode.HALF_UP );

        BigDecimal base         = BigDecimal.valueOf( garageSectorSelected.getBasePrice() ); // BigDecimal
        BigDecimal fator        = dynamicFactor( percOcupadas );        // BigDecimal
        BigDecimal pricePerHour = base.multiply( fator ).setScale( 2, RoundingMode.HALF_UP );

        ParkingSession session = new ParkingSession();
        session.setLicensePlate( dto.license_plate() );
        session.setEntryTime( entryTime );
        session.setExitTime( null );
        session.setSector( garageSectorSelected.getSector() );
        session.setSpot( reservedSpot );
        session.setPricePerHour( pricePerHour );
        session.setTotalAmount( null );
        session.setStatus( "ENTRY" );

        return parkingSessionRepository.save( session );
    }

    /**
     * TROCAR DE VAGA O VEICULO
     * <p>
     * busca uma sessão aberta pela placa e com os status "ENTRY" ou "PARKED"
     * <p>
     * pegar a vaga antiga
     * <p>
     * pegar a nova vaga pelas coordenadas vindas na requisição
     * se a vaga estiver livre
     * faz uma reserva nessa nova vaga e libera a vaga antiga
     * <p>
     * atualiza a sessão com o id na nova vaga e o id do setor e seta o status como PARKED
     */
    @Transactional
    public ParkingSession updateSessionWithParkingSpot( ParkingSessionDTO parkingSessionDTO )
    {
        ParkingSession newParkingSession = parkingSessionRepository.getByLicensePlateAndStatusIn( parkingSessionDTO.license_plate(), List.of( "ENTRY", "PARKED" ) );

        if ( newParkingSession == null )
        {
            throw new IllegalStateException( "sessão não encontrada." );
        }

        GarageSpot oldSpot = garageSpotRepository.findGarageSpotById( newParkingSession.getSpot().getId() );

        GarageSpot newSpot = garageSpotRepository.getGarageSpotByLatAndLngOccupiedFalse( parkingSessionDTO.lat(), parkingSessionDTO.lng() );
        if ( newSpot == null )
        {
            throw new IllegalStateException( "vaga ocupada ou não encontrada." );
        }

        newSpot = reserveSpot( newSpot );

        removeReserveSpot( oldSpot );

        newParkingSession.setSpot( newSpot );
        newParkingSession.setSector( newSpot.getSector().getSector() );
        newParkingSession.setStatus( "PARKED" );

        return parkingSessionRepository.save( newParkingSession );
    }

    /**
     * pega a sessão pela placa
     * <p>
     * faz a contagem do tempo que a sessão ficou aberta
     * <p>
     * - Primeiros 30 minutos são grátis.
     * - Após 30 minutos, cobre uma tarifa fixa por hora, inclusive a primeira hora (use `basePrice` da garagem, arredonde para cima).
     * <p>
     * e faz vezes o valor de pricePerHour
     */
    @Transactional
    public ParkingSession closeParkingSession( ParkingSessionDTO dto )
    {
        ParkingSession session = parkingSessionRepository.getByLicensePlateAndStatusIn( dto.license_plate(), List.of( "ENTRY", "PARKED" ) );

        if ( session == null )
        {
            throw new IllegalStateException( "sessão não encontrada." );
        }

        Instant exitTime = dto.exit_time();

        if ( exitTime.isBefore( session.getEntryTime() ) )
        {
            throw new IllegalArgumentException( "exit_time não pode ser antes do entry_time." );
        }

        // Calcula minutos totais
        long minutes = ChronoUnit.MINUTES.between( session.getEntryTime(), exitTime );

        // Regra de cobrança
        BigDecimal totalAmount;
        if ( minutes <= 30 )
        {
            totalAmount = BigDecimal.ZERO.setScale( 2, RoundingMode.HALF_UP );
        }

        else
        {
            long minutesBeyond = minutes - 30;
            long billableHours = (minutesBeyond + 59) / 60; // arredonda pra cima

            // garante pelo menos 1 hora cobrada
            if ( billableHours < 1 )
            {
                billableHours = 1;
            }

            BigDecimal hoursBD = BigDecimal.valueOf( billableHours );

            // pricePerHour já está na entidade. Multiplica e padroniza 2 casas.
            totalAmount = session.getPricePerHour()
                                 .multiply( hoursBD )
                                 .setScale( 2, RoundingMode.HALF_UP );
        }

        // Atualiza sessão
        session.setExitTime( exitTime );
        session.setTotalAmount( totalAmount );
        session.setStatus( "EXIT" );

        // Libera a vaga
        GarageSpot spot = session.getSpot();
        if ( spot != null && Boolean.TRUE.equals( spot.getOccupied() ) )
        {
            spot.setOccupied( false );
            garageSpotRepository.save( spot );
        }

        // Persiste e retorna
        return parkingSessionRepository.save( session );
    }

    private GarageSpot reserveOneSpot( Integer sectorId )
    {
        PageRequest        pageRequest    = PageRequest.of( 0, 1 );
        List< GarageSpot > listGarageSpot = garageSpotRepository.findFreeSpotsForUpdate( sectorId, pageRequest );

        if ( listGarageSpot.isEmpty() )
        {
            return null;
        }

        GarageSpot spot = listGarageSpot.getFirst();
        spot.setOccupied( true );

        return garageSpotRepository.save( spot );
    }

    private GarageSpot reserveSpot( GarageSpot garageSpot )
    {
        GarageSpot newGarageSpot = garageSpotRepository.findGarageSpotById( garageSpot.getId() );

        if ( newGarageSpot == null )
        {
            return null;
        }

        newGarageSpot.setOccupied( true );

        return garageSpotRepository.save( newGarageSpot );
    }

    private void removeReserveSpot( GarageSpot garageSpot )
    {
        GarageSpot oldGarageSpot = garageSpotRepository.findGarageSpotById( garageSpot.getId() );

        if ( oldGarageSpot == null )
        {
            throw new IllegalStateException( "garagem não encontrada para remover a reserva" );
        }

        oldGarageSpot.setOccupied( false );
        garageSpotRepository.save( oldGarageSpot );
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

//        // 1) Converter hora local só para checar janela do setor
//
//        LocalTime entryTime    = entryInstant.atZone( ZoneId.systemDefault() ).toLocalTime();
//
//        List< GarageSector > sectors = garageSectorRepository.findAll();
//
//        GarageSector selected       = null;
//        double       selectedOccPct = 101.0;    // algo maior que 100 para iniciar
//        int          selectedFree   = -1;
//
//        // 2) Varre setores válidos e escolhe o de menor ocupação
//        for ( GarageSector sector : sectors )
//        {
//            // janela de funcionamento
//            if ( entryTime.isBefore( sector.getOpenHour() ) || entryTime.isAfter( sector.getCloseHour() ) )
//            {
//                continue;
//            }
//
//            int total = sector.getMaxCapacity();
//            if ( total <= 0 )
//            {
//                continue;
//            }
//
//            int free = garageSpotRepository.findBySectorAndOccupiedFalse( sector ).size();
//            if ( free <= 0 )
//            {
//                // cheio => fechado por lotação
//                continue;
//            }
//
//            int    occupied = total - free;
//            double occPct   = (occupied * 100.0) / ( double ) total;
//
//            // critério: menor ocupação; se empatar, mais vagas livres; se empatar, menor id
//            boolean choose = false;
//            if ( occPct < selectedOccPct )
//            {
//                choose = true;
//            }
//            else if ( occPct == selectedOccPct )
//            {
//                if ( free > selectedFree )
//                {
//                    choose = true;
//                }
//
//                else if ( free == selectedFree && selected != null && sector.getId() < selected.getId() )
//                {
//                    choose = true;
//                }
//            }
//
//            if ( choose )
//            {
//                selected = sector;
//                selectedOccPct = occPct;
//                selectedFree = free;
//            }
//        }
//
//        if ( selected == null )
//        {
//            // nenhum setor aberto com vaga
//            throw new IllegalStateException( "Estacionamento indisponível (fechado ou lotado)." );
//        }
//
//        // 3) Travar o preço dinâmico na ENTRADA
//        double factor = dynamicFactor( selectedOccPct );
//
//        BigDecimal base = BigDecimal.valueOf( selected.getBasePrice() );       // ex.: 12.34
//        long baseCents = base.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
//        long resultCents = Math.round(baseCents * factor);
//        BigDecimal pricePerHour = BigDecimal.valueOf(resultCents, 2)  // volta para reais
//                .setScale(2, RoundingMode.HALF_UP);
//
//        // 4) Reservar e marcar vaga como ocupada (pessimistic lock)
//        GarageSpot reservedSpot = reserveOneSpot( selected.getId() );
//        if ( reservedSpot == null )
//        {
//            // condição de corrida: outro processo pegou a última vaga
//            throw new IllegalStateException( "Setor ficou lotado durante a operação." );
//        }
