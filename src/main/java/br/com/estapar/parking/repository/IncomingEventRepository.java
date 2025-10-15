package br.com.estapar.parking.repository;

import br.com.estapar.parking.model.IncomingEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncomingEventRepository extends JpaRepository< IncomingEvent, Integer >
{
}
