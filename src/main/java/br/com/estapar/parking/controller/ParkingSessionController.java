package br.com.estapar.parking.controller;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.logging.SimpleConsoleLogger;
import br.com.estapar.parking.service.IncomingEventService;
import br.com.estapar.parking.service.ParkingSessionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping( "/webhook" )
public class ParkingSessionController
{
    private ParkingSessionService parkingSessionService;
    private IncomingEventService incomingEventService;

    public ParkingSessionController( ParkingSessionService parkingSessionService, IncomingEventService incomingEventService )
    {
        this.parkingSessionService = parkingSessionService;
        this.incomingEventService = incomingEventService;
    }

    @Operation(
            summary = "Recebe eventos do simulador (ENTRY, PARKED, EXIT)",
            description = "ENTRY exige license_plate e entry_time; PARKED exige license_plate, lat e lng; EXIT exige license_plate e exit_time. Sempre responde 200 OK."
    )
    @PostMapping( consumes = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< Void > parkingSessionEvent( @RequestBody ParkingSessionDTO parkingSessionDTO )
    {
        try
        {
            if ( parkingSessionDTO.event_type() == null )
            {
                SimpleConsoleLogger.info( "Evento sem tipo recebido: " + parkingSessionDTO  );
                return ResponseEntity.ok().build();
            }

            incomingEventService.createLogIncomingEvent( parkingSessionDTO );

            switch ( parkingSessionDTO.event_type().toUpperCase() )
            {
                case "ENTRY" -> parkingSessionService.createEntrySession( parkingSessionDTO );
                case "PARKED" -> parkingSessionService.updateSessionWithParkingSpot( parkingSessionDTO );
                case "EXIT" -> parkingSessionService.closeParkingSession( parkingSessionDTO );
                default -> throw new IllegalStateException( "Evento Invalido!" );
            }
        }

        catch ( RuntimeException e )
        {
            SimpleConsoleLogger.warn( "Erro ao processar evento: " + e.getMessage()  );
        }

        return ResponseEntity.ok().build();
    }
}