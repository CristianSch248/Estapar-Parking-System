package br.com.estapar.parking.controller;

import br.com.estapar.parking.DTO.ParkingSessionDTO;
import br.com.estapar.parking.service.GarageSectorService;
import br.com.estapar.parking.service.GarageSpotService;
import br.com.estapar.parking.service.IncomingEventService;
import br.com.estapar.parking.service.ParkingSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static br.com.estapar.parking.util.ParkingSessionDTOFactory.*;

@ExtendWith(MockitoExtension.class)
public class ParkingSessionControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private ParkingSessionService parkingSessionService;

    @Mock
    private IncomingEventService incomingEventService;

    @Mock
    private GarageSectorService garageSectorService;

    @Mock
    private GarageSpotService garageSpotService;

    @InjectMocks
    private ParkingSessionController parkingSessionController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup()
    {
        mockMvc = MockMvcBuilders.standaloneSetup(parkingSessionController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testParkingSessionEvent_WhenEventTypeIsEntry_ShouldReturn200() throws Exception
    {
        mockMvc.perform( MockMvcRequestBuilders.post( "/webhook" )
                                               .contentType( MediaType.APPLICATION_JSON )
                                               .content( objectMapper.writeValueAsString( buildEntryEvent() ) ) )
               .andExpect( MockMvcResultMatchers.status().isOk() );

        Mockito.verify (parkingSessionService, Mockito.times( 1 ) )
               .createEntrySession( Mockito.any( ParkingSessionDTO.class ) );
    }

    @Test
    void testParkingSessionEvent_WhenEventTypeIsParked_ShouldReturn200() throws Exception
    {
        mockMvc.perform( MockMvcRequestBuilders.post( "/webhook" )
                                               .contentType( MediaType.APPLICATION_JSON )
                                               .content( objectMapper.writeValueAsString( buildParkedEvent() ) ) )
               .andExpect( MockMvcResultMatchers.status().isOk() );

        Mockito.verify (parkingSessionService, Mockito.times( 1 ) )
               .updateSessionWithParkingSpot( Mockito.any( ParkingSessionDTO.class ) );
    }

    @Test
    void testParkingSessionEvent_WhenEventTypeIsClosed_ShouldReturn200() throws Exception
    {
        mockMvc.perform( MockMvcRequestBuilders.post( "/webhook" )
                                               .contentType( MediaType.APPLICATION_JSON )
                                               .content( objectMapper.writeValueAsString( buildExitEvent() ) ) )
               .andExpect( MockMvcResultMatchers.status().isOk() );

        Mockito.verify (parkingSessionService, Mockito.times( 1 ) )
               .closeParkingSession( Mockito.any( ParkingSessionDTO.class ) );
    }
}