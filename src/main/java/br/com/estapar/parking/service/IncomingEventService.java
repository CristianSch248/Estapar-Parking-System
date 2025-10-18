package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.model.IncomingEvent;
import br.com.estapar.parking.repository.IncomingEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class IncomingEventService
{

    @Autowired
    private IncomingEventRepository incomingEventRepository;

    public void createLogIncomingEvent( ParkingSessionDTO parkingSessionDTO )
    {
        IncomingEvent incomingEvent = new IncomingEvent();

        ZoneId        sp      = ZoneId.of("America/Sao_Paulo");
        LocalDateTime timeNow = LocalDateTime.now( sp );

        incomingEvent.setEventType( parkingSessionDTO.event_type() );
        incomingEvent.setEventTime( timeNow );
        incomingEvent.setLicensePlate( parkingSessionDTO.license_plate() );
        incomingEvent.setRawPayload( parkingSessionDTO.toString() );

        incomingEventRepository.save( incomingEvent );
    }
}
