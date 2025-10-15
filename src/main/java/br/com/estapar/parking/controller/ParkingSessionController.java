package br.com.estapar.parking.controller;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.service.ParkingSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/webhookcris" )
public class ParkingSessionController
{
    @Autowired
    private ParkingSessionService parkingSessionService;

    @PostMapping
    public ResponseEntity< Void > parkingSessionEvent( @RequestBody ParkingSessionDTO parkingSessionDTO )
    {
        try
        {
            if ( parkingSessionDTO.event_type() == null )
            {
                System.out.println( "Evento sem tipo recebido: " + parkingSessionDTO );
                return ResponseEntity.ok().build();
            }

            switch ( parkingSessionDTO.event_type().toUpperCase() )
            {
                case "ENTRY"  -> parkingSessionService.createEntrySession( parkingSessionDTO );
                case "PARKED" -> parkingSessionService.updateSessionWithParkingSpot( parkingSessionDTO );
                case "EXIT"   -> parkingSessionService.closeParkingSession( parkingSessionDTO );
            }
        }

        catch ( RuntimeException e )
        {
            System.err.println( "Erro ao processar evento: " + e.getMessage() );
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
    }
}
