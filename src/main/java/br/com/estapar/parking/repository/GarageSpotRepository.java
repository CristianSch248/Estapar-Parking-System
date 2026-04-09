package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface GarageSpotRepository extends JpaRepository< GarageSpot, Integer >
{
    List<GarageSpot> findBySectorAndOccupiedFalse(GarageSector sector);

    GarageSpot findFirstBySector_IdAndOccupiedFalseOrderByIdAsc( Integer sectorId );

    GarageSpot findGarageSpotById( Integer id );

    Optional<GarageSpot> findByLatAndLngAndOccupiedFalse( BigDecimal lat, BigDecimal lng);

    @Query( "SELECT gs FROM GarageSpot gs " +
            "JOIN gs.sector s " +
            "WHERE gs.occupied = false " +
            "AND s.openHour <= :time " +
            "ORDER BY gs.id ASC " +
            "LIMIT 1" )
    Optional<GarageSpot> findFirstAvailableSpotInOpenSector( @Param("time") LocalTime time );
}
