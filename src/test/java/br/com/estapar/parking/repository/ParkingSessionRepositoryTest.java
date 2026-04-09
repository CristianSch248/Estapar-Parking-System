package br.com.estapar.parking.repository;

import br.com.estapar.parking.service.GarageSectorService;
import br.com.estapar.parking.service.GarageSpotService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ParkingSessionRepositoryTest
{
    @MockitoBean
    private GarageSectorService garageSectorService;

    @MockitoBean
    private GarageSpotService garageSpotService;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Test
    void testExistsByLicensePlateAndStatusIn_WhenStatusIsActiveOrParked_ShouldReturnTrue()
    {
        boolean result = parkingSessionRepository.existsByLicensePlateAndStatusIn( "FULL0002", List.of( "ENTRY", "PARKED" ) );

        Assertions.assertTrue( result );
    }

}
