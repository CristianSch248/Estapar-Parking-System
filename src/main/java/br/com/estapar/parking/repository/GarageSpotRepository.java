package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface GarageSpotRepository extends JpaRepository< GarageSpot, Integer >
{
    List<GarageSpot> findBySectorAndOccupiedFalse(GarageSector sector);

    List<GarageSpot> findBySector_IdAndOccupiedFalse(Integer sectorId, Pageable pageable);

    GarageSpot findGarageSpotById( Integer id );

    Optional<GarageSpot> findByLatAndLngAndOccupiedFalse( BigDecimal lat, BigDecimal lng);
}
