package br.com.estapar.parking;

import br.com.estapar.parking.DTO.GarageSectorDTO;
import br.com.estapar.parking.DTO.GarageSpotDTO;
import br.com.estapar.parking.DTO.WrapperGarageDTO;
import br.com.estapar.parking.service.GarageSectorService;
import br.com.estapar.parking.service.GarageSpotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ParkingSystemApplication implements CommandLineRunner
{

    @Autowired
    private GarageSectorService garageSectorService;

    @Autowired
    private GarageSpotService garageSpotService;

    public static void main( String[] args )
    {
        SpringApplication.run( ParkingSystemApplication.class, args );
    }


    @Override
    public void run( String... args )
    {
        String SimulatorUrl = "http://localhost:8080/garage";

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity< WrapperGarageDTO > response = restTemplate.getForEntity( SimulatorUrl, WrapperGarageDTO.class );

        WrapperGarageDTO wrapperGarageDTO = response.getBody();

        System.out.println( "🚗 Simulador iniciado com sucesso!" );

        if ( wrapperGarageDTO != null )
        {
            System.out.println( "📦 Setores recebidos: " + wrapperGarageDTO.garage().size() );
            System.out.println( "📦 Vagas recebidas: " + wrapperGarageDTO.spots().size() );

            for ( GarageSectorDTO garageSectorDTO : wrapperGarageDTO.garage() )
            {
                System.out.println( garageSectorDTO.toString() );

                garageSectorService.createGarageSector( garageSectorDTO );
            }

            for ( GarageSpotDTO garageSpotDTO : wrapperGarageDTO.spots() )
            {
                System.out.println( garageSpotDTO.toString() );

                garageSpotService.createGarageSpot( garageSpotDTO );
            }
        }
    }
}