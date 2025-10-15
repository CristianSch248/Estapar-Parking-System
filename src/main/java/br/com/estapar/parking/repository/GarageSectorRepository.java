package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.GarageSector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GarageSectorRepository extends JpaRepository< GarageSector, Integer >
{
    Optional< GarageSector > findBySector( String sector );
}
