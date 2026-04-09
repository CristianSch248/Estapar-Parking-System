package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.service.GarageSectorService;
import br.com.estapar.parking.service.GarageSpotService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalTime;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GarageSpotRepositoryTest
{
    @MockitoBean
    private GarageSectorService garageSectorService;

    @MockitoBean
    private GarageSpotService garageSpotService;

    @Autowired
    private GarageSpotRepository garageSpotRepository;

    @Test
    void testFindFirstAvailableSpotInOpenSector_WhenSectorIsOpenAndSpotIsAvailable_ShouldReturnIsPresentSpot()
    {
        LocalTime localTime = LocalTime.of(13, 10);

        Optional<GarageSpot> result = garageSpotRepository.findFirstAvailableSpotInOpenSector( localTime );

        Assertions.assertTrue( result.isPresent() );
    }

    @Test
    void testFindFirstAvailableSpotInOpenSector_WhenSectorIsOpenAndSpotIsAvailable_ShouldReturnIsEmptySpot()
    {
        LocalTime localTime = LocalTime.of(13, 10);

        Optional<GarageSpot> result = garageSpotRepository.findFirstAvailableSpotInOpenSector( localTime );

        Assertions.assertFalse( result.isEmpty() );
    }
}