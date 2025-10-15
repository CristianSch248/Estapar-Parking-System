package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ParkingSessionRepository extends JpaRepository< ParkingSession, Integer >
{
    boolean existsByLicensePlateAndStatus( String licensePlate, String open );

    boolean existsByLicensePlateAndStatusIn( String licensePlate, List < String > status );

    ParkingSession getByLicensePlateAndStatusIn( String licensePlate, Collection< String> statuses );
}
