package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface GarageSpotRepository extends JpaRepository< GarageSpot, Integer >
{


    List<GarageSpot> findBySectorAndOccupiedFalse(GarageSector sector);

    List< GarageSpot> findFreeSpotsForUpdate( Integer sectorId, PageRequest pr );

    GarageSpot findGarageSpotById( Integer id );

    GarageSpot getGarageSpotByLatAndLngOccupiedFalse( BigDecimal lat, BigDecimal lng );
}
