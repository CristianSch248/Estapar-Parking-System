package br.com.estapar.parking.service;

import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.model.ParkingSession;
import br.com.estapar.parking.repository.GarageSectorRepository;
import br.com.estapar.parking.repository.GarageSpotRepository;
import br.com.estapar.parking.repository.ParkingSessionRepository;
import br.com.estapar.parking.util.ParkingSessionDTOFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParkingSessionServiceTest
{
    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private GarageSectorRepository garageSectorRepository;

    @Mock
    private GarageSpotRepository garageSpotRepository;

    @Mock
    private GarageSpotService garageSpotService;

    @InjectMocks
    private ParkingSessionService parkingSessionService;

    private GarageSector sector;
    private GarageSpot spot;
    private ParkingSession parkingSession;

    @BeforeEach
    public void setup()
    {
        sector = new GarageSector();
        sector.setId( 1 );
        sector.setSector( "A" );
        sector.setBasePrice( 40.50 );
        sector.setMaxCapacity( 20 );
        sector.setOpenHour( LocalTime.of(0, 0) );
        sector.setCloseHour( LocalTime.of(23, 59) );
        sector.setDurationLimitMinutes( 1440 );

        spot = new GarageSpot();
        spot.setOccupied( false );
        spot.setSector( sector );
        spot.setId(1);
        spot.setLat( BigDecimal.valueOf( -23.561684 ) );
        spot.setLng( BigDecimal.valueOf( -46.655981 ) );

        parkingSession = new ParkingSession();
        parkingSession.setLicensePlate( "ZUL0001" );
        parkingSession.setStatus( "EXIT" );
        parkingSession.setSpot( spot );
        parkingSession.setEntryTime( Instant.parse( "2025-01-01T10:00:00.000Z" ) );
        parkingSession.setExitTime( Instant.parse("2025-01-01T12:00:00.000Z") );
        parkingSession.setPricePerHour( new BigDecimal( "40.50" ) );
    }

    @Test
    void testDynamicFactor_WhenOccupancyBelow25Percent_ShouldReturnPricingFactor()
    {
        ParkingSessionService parkingSessionService = new ParkingSessionService();
        BigDecimal percentageOccupied = new BigDecimal( "15.50" );
        BigDecimal bigDecimalResult = parkingSessionService.dynamicFactor( percentageOccupied );

        Assertions.assertEquals(
                0,// valor esperado (igualdade no compareTo)
                          // ( -1 ) o valor da esquerda é menor
                          // ( 0 )  os valores são iguais
                          // ( 1 )  o valor da esquerda é maior
                BigDecimal.valueOf( 0.90 ).compareTo( bigDecimalResult )  // valor real retornado
        );
    }

    @Test
    void testCreateEntrySession_WhenVehicleHasNoActiveSessionAndSpotIsAvailable_ShouldCreateSession()
    {
        // configurando os mocks
        Mockito.when( parkingSessionRepository.existsByLicensePlateAndStatusIn( Mockito.anyString(), Mockito.anyList() ) )
               .thenReturn( false );

        Mockito.when( garageSpotRepository.findFirstAvailableSpotInOpenSector( Mockito.any( LocalTime.class ) ) )
               .thenReturn( Optional.of( spot ) );

        Mockito.when( garageSpotRepository.save( Mockito.any( GarageSpot.class ) ) )
               .thenReturn( spot );

        // executa a service
        parkingSessionService.createEntrySession( ParkingSessionDTOFactory.buildEntryEvent() );

        // verifica que a sessão foi salva, como? verificando se o metodo save do parkingSessionRepository foi chamado aumenos uma vez
        Mockito.verify( parkingSessionRepository, Mockito.times( 1 ) )
               .save( Mockito.any( ParkingSession.class ) );
    }

    @Test
    void testUpdateSessionWithParkingSpot_WhenSessionExistsAndSpotIsAvailable_ShouldUpdateSession()
    {
        // configurando os mocks
        Mockito.when( parkingSessionRepository.getByLicensePlateAndStatusIn( Mockito.anyString(), Mockito.anyList() ) )
                .thenReturn( parkingSession );

        Mockito.when( garageSpotRepository.findGarageSpotById( Mockito.anyInt() ) )
                .thenReturn( spot );

        Mockito.when( garageSpotRepository.findByLatAndLngAndOccupiedFalse( Mockito.any(), Mockito.any() ) )
                .thenReturn( Optional.of( spot ) );

        Mockito.when( garageSpotService.reserveSpot( Mockito.any( GarageSpot.class ) ) )
               .thenReturn( spot );

        parkingSessionService.updateSessionWithParkingSpot( ParkingSessionDTOFactory.buildParkedEvent() );

        Mockito.verify( parkingSessionRepository, Mockito.times( 1 ) )
               .save( Mockito.any( ParkingSession.class ) );
    }

    @Test
    void testCloseParkingSession_WhenSessionExists_ShouldCloseSession()
    {
        Mockito.when( parkingSessionRepository.getByLicensePlateAndStatusIn( Mockito.anyString(), Mockito.anyList() ) )
               .thenReturn( parkingSession );

        parkingSessionService.closeParkingSession( ParkingSessionDTOFactory.buildExitEvent() );

        Mockito.verify( parkingSessionRepository, Mockito.times( 1 ) )
               .save( Mockito.any( ParkingSession.class ) );
    }
}
