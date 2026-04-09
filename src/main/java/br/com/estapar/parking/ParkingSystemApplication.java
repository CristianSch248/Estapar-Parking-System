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
        String SimulatorUrl = "http://localhost:3000/garage";

        RestTemplate restTemplate = new RestTemplate();

        try
        {
            ResponseEntity< WrapperGarageDTO > response = restTemplate.getForEntity( SimulatorUrl, WrapperGarageDTO.class );

            WrapperGarageDTO wrapperGarageDTO = response.getBody();

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

            else
            {
                System.out.println( "⚠️ Resposta veio vazia." );
            }
        }

        catch (org.springframework.web.client.ResourceAccessException e)
        {
            System.out.println( "❌ Não foi possível conectar ao simulador: " + e.getMessage() );
        }

        catch (org.springframework.web.client.HttpClientErrorException |
               org.springframework.web.client.HttpServerErrorException e)
        {
            System.out.println( "❌ Erro HTTP ao chamar simulador: " + e.getStatusCode() );
        }

        catch (Exception e)
        {
            System.out.println( "❌ Erro inesperado: " + e.getMessage() );
        }

        System.out.println( "🚗 Simulador iniciado com sucesso!" );
    }
}