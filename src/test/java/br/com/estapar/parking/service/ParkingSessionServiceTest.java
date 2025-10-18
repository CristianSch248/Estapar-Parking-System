package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.model.ParkingSession;
import br.com.estapar.parking.repository.GarageSectorRepository;
import br.com.estapar.parking.repository.GarageSpotRepository;
import br.com.estapar.parking.repository.ParkingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith( MockitoExtension.class )
class ParkingSessionServiceTest
{

    @Mock
    private ParkingSessionRepository parkingSessionRepository;
    @Mock
    private GarageSectorRepository   garageSectorRepository;
    @Mock
    private GarageSpotRepository     garageSpotRepository;
    @Mock
    private GarageSpotService        garageSpotService;

    @InjectMocks
    private ParkingSessionService service;

    // Helpers comuns
    private Instant entryInstant;
    private Instant exitInstant;

    @BeforeEach
    void setUp()
    {
        // 2025-01-01 10:00:00-03 (exemplo)
        entryInstant = ZonedDateTime.of( LocalDate.of( 2025, 1, 1 ),
                LocalTime.of( 10, 0 ), ZoneId.systemDefault() ).toInstant();

        exitInstant = entryInstant.plus( Duration.ofHours( 2 ).plusMinutes( 10 ) ); // 2h10 depois
    }

    // ---------- createEntrySession ----------

    @Test
    void createEntrySession_shouldReturnWhenOpenSessionAlreadyExists()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );
        when( dto.event_type() ).thenReturn( "ENTRY" );
        when( dto.entry_time() ).thenReturn( entryInstant );

        when( parkingSessionRepository.existsByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( true );

        service.createEntrySession( dto );

        verify( parkingSessionRepository, never() ).save( any() );
        verify( garageSectorRepository, never() ).findAll();
        verifyNoInteractions( garageSpotService, garageSpotRepository );
    }

    @Test
    void createEntrySession_shouldCreateWhenFindsOpenSectorAndReservesSpot()
    {
        // DTO de entrada
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ABC1234" );
        when( dto.event_type() ).thenReturn( "ENTRY" );
        when( dto.entry_time() ).thenReturn( entryInstant );

        when( parkingSessionRepository.existsByLicensePlateAndStatusIn( eq( "ABC1234" ), anyList() ) )
                .thenReturn( false );

        // Setores: primeiro fechado, segundo aberto
        GarageSector closed = new GarageSector();
        closed.setId( 1 );
        closed.setSector( "A" );
        closed.setOpenHour( LocalTime.of( 12, 0 ) );
        closed.setCloseHour( LocalTime.of( 22, 0 ) );
        closed.setMaxCapacity( 10 );
        closed.setBasePrice( 10.0 );

        GarageSector open = new GarageSector();
        open.setId( 2 );
        open.setSector( "B" );
        open.setOpenHour( LocalTime.of( 8, 0 ) );
        open.setCloseHour( LocalTime.of( 23, 0 ) );
        open.setMaxCapacity( 10 );
        open.setBasePrice( 10.0 );

        when( garageSectorRepository.findAll() ).thenReturn( List.of( closed, open ) );

        // Reserva de uma vaga no setor aberto
        GarageSpot spot = new GarageSpot();
        spot.setId( 99 );
        spot.setSector( open );
        spot.setOccupied( true ); // após “reserva”, ocupado

        when( garageSpotService.reserveOneSpot( 2 ) ).thenReturn( spot );

        // Para calcular o preço por hora, a service chama findBySectorAndOccupiedFalse
        // Simular ocupação: 7 ocupadas, 3 livres → 70% → fator 1.10 → 10 * 1.10 = 11.00
        GarageSpot free1 = new GarageSpot();
        free1.setSector( open );
        free1.setOccupied( false );
        GarageSpot free2 = new GarageSpot();
        free2.setSector( open );
        free2.setOccupied( false );
        GarageSpot free3 = new GarageSpot();
        free3.setSector( open );
        free3.setOccupied( false );
        when( garageSpotRepository.findBySectorAndOccupiedFalse( open ) ).thenReturn( List.of( free1, free2, free3 ) );

        // Execução
        service.createEntrySession( dto );

        // Verificações
        ArgumentCaptor< ParkingSession > captor = ArgumentCaptor.forClass( ParkingSession.class );
        verify( parkingSessionRepository ).save( captor.capture() );

        ParkingSession saved = captor.getValue();
        assertThat( saved.getLicensePlate() ).isEqualTo( "ABC1234" );
        assertThat( saved.getSpot() ).isEqualTo( spot );
        assertThat( saved.getSector() ).isEqualTo( "B" );
        assertThat( saved.getPricePerHour() ).isEqualByComparingTo( "11.00" ); // 10 * 1.10
        assertThat( saved.getStatus() ).isEqualTo( "ENTRY" );
    }

    @Test
    void createEntrySession_shouldReturnWhenNoOpenSectors()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "AAA0000" );
        when( dto.event_type() ).thenReturn( "ENTRY" );
        when( dto.entry_time() ).thenReturn( entryInstant );

        when( parkingSessionRepository.existsByLicensePlateAndStatusIn( anyString(), anyList() ) ).thenReturn( false );

        GarageSector s = new GarageSector();
        s.setId( 1 );
        s.setSector( "A" );
        s.setOpenHour( LocalTime.of( 22, 0 ) );
        s.setCloseHour( LocalTime.of( 23, 0 ) );
        when( garageSectorRepository.findAll() ).thenReturn( List.of( s ) );

        service.createEntrySession( dto );

        verify( parkingSessionRepository, never() ).save( any() );
    }

    @Test
    void createEntrySession_shouldReturnWhenAllSectorsFull()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "AAA0001" );
        when( dto.event_type() ).thenReturn( "ENTRY" );
        when( dto.entry_time() ).thenReturn( entryInstant );

        when( parkingSessionRepository.existsByLicensePlateAndStatusIn( anyString(), anyList() ) ).thenReturn( false );

        GarageSector open = new GarageSector();
        open.setId( 2 );
        open.setSector( "B" );
        open.setOpenHour( LocalTime.of( 0, 0 ) );
        open.setCloseHour( LocalTime.of( 23, 59 ) );
        when( garageSectorRepository.findAll() ).thenReturn( List.of( open ) );

        when( garageSpotService.reserveOneSpot( 2 ) ).thenReturn( null ); // não conseguiu reservar

        service.createEntrySession( dto );

        verify( parkingSessionRepository, never() ).save( any() );
    }

    // ---------- updateSessionWithParkingSpot ----------

    @Test
    void updateSessionWithParkingSpot_shouldReturnWhenSessionNotFound()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( null );

        service.updateSessionWithParkingSpot( dto );

        verify( parkingSessionRepository, never() ).save( any() );
    }

    @Test
    void updateSessionWithParkingSpot_shouldReturnWhenNewSpotNotAvailable()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );
        when( dto.lat() ).thenReturn( new BigDecimal( "10.000000" ) );
        when( dto.lng() ).thenReturn( new BigDecimal( "20.000000" ) );

        ParkingSession existing = new ParkingSession();
        GarageSpot     old      = new GarageSpot();
        old.setId( 1 );
        old.setOccupied( true );
        existing.setSpot( old );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( existing );

        when( garageSpotRepository.findGarageSpotById( 1 ) ).thenReturn( old );
        when( garageSpotRepository.findByLatAndLngAndOccupiedFalse( any(), any() ) ).thenReturn( Optional.empty() );

        service.updateSessionWithParkingSpot( dto );

        verify( parkingSessionRepository, never() ).save( any() );
        verify( garageSpotService, never() ).reserveSpot( any() );
        verify( garageSpotService, never() ).removeReserveSpot( any() );
    }

    @Test
    void updateSessionWithParkingSpot_shouldUpdateWhenNewSpotAvailable()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );
        when( dto.lat() ).thenReturn( new BigDecimal( "10.000000" ) );
        when( dto.lng() ).thenReturn( new BigDecimal( "20.000000" ) );

        // sessão atual com oldSpot
        ParkingSession existing = new ParkingSession();
        GarageSector   sector   = new GarageSector();
        sector.setSector( "B" );
        GarageSpot oldSpot = new GarageSpot();
        oldSpot.setId( 1 );
        oldSpot.setSector( sector );
        oldSpot.setOccupied( true );
        existing.setSpot( oldSpot );
        existing.setSector( sector.getSector() );
        existing.setStatus( "ENTRY" );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( existing );
        when( garageSpotRepository.findGarageSpotById( 1 ) ).thenReturn( oldSpot );

        // newSpot livre
        GarageSpot newSpot = new GarageSpot();
        newSpot.setId( 2 );
        newSpot.setSector( sector );
        newSpot.setOccupied( false );
        when( garageSpotRepository.findByLatAndLngAndOccupiedFalse( any(), any() ) )
                .thenReturn( Optional.of( newSpot ) );

        // ao reservar, volta ocupado
        GarageSpot reserved = new GarageSpot();
        reserved.setId( 2 );
        reserved.setSector( sector );
        reserved.setOccupied( true );
        when( garageSpotService.reserveSpot( newSpot ) ).thenReturn( reserved );

        service.updateSessionWithParkingSpot( dto );

        ArgumentCaptor< ParkingSession > captor = ArgumentCaptor.forClass( ParkingSession.class );
        verify( parkingSessionRepository ).save( captor.capture() );

        ParkingSession saved = captor.getValue();
        assertThat( saved.getSpot().getId() ).isEqualTo( 2 );
        assertThat( saved.getSector() ).isEqualTo( "B" );
        assertThat( saved.getStatus() ).isEqualTo( "PARKED" );

        verify( garageSpotService ).removeReserveSpot( oldSpot );
    }

    // ---------- closeParkingSession ----------

    @Test
    void closeParkingSession_shouldReturnWhenSessionNotFound()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( null );

        service.closeParkingSession( dto );

        verify( parkingSessionRepository, never() ).save( any() );
    }

    @Test
    void closeParkingSession_shouldReturnWhenExitBeforeEntry()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );
        when( dto.exit_time() ).thenReturn( entryInstant.minusSeconds( 1 ) );

        ParkingSession session = new ParkingSession();
        session.setEntryTime( entryInstant );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( session );

        service.closeParkingSession( dto );

        verify( parkingSessionRepository, never() ).save( any() );
    }

    @Test
    void closeParkingSession_shouldChargeZeroWhenWithinFirst30Minutes()
    {
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );
        when( dto.exit_time() ).thenReturn( entryInstant.plus( Duration.ofMinutes( 30 ) ) );

        ParkingSession session = new ParkingSession();
        session.setEntryTime( entryInstant );
        session.setPricePerHour( new BigDecimal( "11.00" ) );
        GarageSpot spot = new GarageSpot();
        spot.setId( 5 );
        spot.setOccupied( true );
        session.setSpot( spot );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( session );

        service.closeParkingSession( dto );

        ArgumentCaptor< ParkingSession > captor = ArgumentCaptor.forClass( ParkingSession.class );
        verify( parkingSessionRepository ).save( captor.capture() );

        ParkingSession saved = captor.getValue();
        assertThat( saved.getTotalAmount() ).isEqualByComparingTo( "0.00" );
        assertThat( saved.getStatus() ).isEqualTo( "EXIT" );
        verify( garageSpotRepository ).save( argThat( s -> s.getId() == 5 && !Boolean.TRUE.equals( s.getOccupied() ) ) );
    }

    @Test
    void closeParkingSession_shouldBillRoundedUpHoursAfterFirst30Min()
    {
        // 2h10 após entrada -> 130 min -> além de 30 = 100 min -> (100+59)/60 = 2h cobradas
        ParkingSessionDTO dto = mock( ParkingSessionDTO.class );
        when( dto.license_plate() ).thenReturn( "ZUL0001" );
        when( dto.exit_time() ).thenReturn( exitInstant );

        ParkingSession session = new ParkingSession();
        session.setEntryTime( entryInstant );
        session.setPricePerHour( new BigDecimal( "15.50" ) ); // 2h cobradas => 31.00
        GarageSpot spot = new GarageSpot();
        spot.setId( 6 );
        spot.setOccupied( true );
        session.setSpot( spot );

        when( parkingSessionRepository.getByLicensePlateAndStatusIn( eq( "ZUL0001" ), anyList() ) )
                .thenReturn( session );

        service.closeParkingSession( dto );

        ArgumentCaptor< ParkingSession > captor = ArgumentCaptor.forClass( ParkingSession.class );
        verify( parkingSessionRepository ).save( captor.capture() );

        ParkingSession saved = captor.getValue();
        assertThat( saved.getTotalAmount() ).isEqualByComparingTo( "31.00" );
        assertThat( saved.getStatus() ).isEqualTo( "EXIT" );
        verify( garageSpotRepository ).save( argThat( s -> s.getId() == 6 && !Boolean.TRUE.equals( s.getOccupied() ) ) );
    }
}