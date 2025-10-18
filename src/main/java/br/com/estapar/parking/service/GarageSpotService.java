package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.GarageSpotDTO;
import br.com.estapar.parking.logging.SimpleConsoleLogger;
import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.model.GarageSpot;
import br.com.estapar.parking.repository.GarageSectorRepository;
import br.com.estapar.parking.repository.GarageSpotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GarageSpotService
{
    @Autowired
    private GarageSpotRepository garageSpotRepository;

    @Autowired
    private GarageSectorRepository garageSectorRepository;

    public void createGarageSpot( GarageSpotDTO garageSpotDTO )
    {
        if ( garageSpotRepository.findGarageSpotById( garageSpotDTO.id() ) != null )
        {
            SimpleConsoleLogger.info(
                    "SPOT já existe para a garagem [ '" +
                    garageSpotDTO.sector() +
                    "' ]. Ignorando inserção duplicada."
            );
            return;
        }

        GarageSector sector = garageSectorRepository.findBySector( garageSpotDTO.sector() )
                .orElseThrow( () -> new RuntimeException( "Setor não encontrado: " + garageSpotDTO.sector() ) );

        GarageSpot newGarageSpot = new GarageSpot();

        newGarageSpot.setId( garageSpotDTO.id() );
        newGarageSpot.setSector( sector );
        newGarageSpot.setLat( garageSpotDTO.lat() );
        newGarageSpot.setLng( garageSpotDTO.lng() );
        newGarageSpot.setOccupied( garageSpotDTO.occupied() );

        garageSpotRepository.save( newGarageSpot );

        SimpleConsoleLogger.info( "Novo setor '" + newGarageSpot.getSector().getSector() + "' salvo com sucesso!"  );
    }

    public GarageSpot reserveOneSpot( Integer sectorId )
    {
        GarageSpot GarageSpot = garageSpotRepository.findFirstBySector_IdAndOccupiedFalseOrderByIdAsc( sectorId );

        if ( GarageSpot == null )
        {
            SimpleConsoleLogger.info( "Nenhuma VAGA Livre disponível"  );
            return null;
        }

        GarageSpot.setOccupied( true );

        return garageSpotRepository.save( GarageSpot );
    }

    public GarageSpot reserveSpot( GarageSpot garageSpot )
    {
        GarageSpot newGarageSpot = garageSpotRepository.findGarageSpotById( garageSpot.getId() );

        if ( newGarageSpot == null )
        {
            SimpleConsoleLogger.info( "VAGA Não encontrada"  );
            return null;
        }

        newGarageSpot.setOccupied( true );

        return garageSpotRepository.save( newGarageSpot );
    }

    public void removeReserveSpot( GarageSpot garageSpot )
    {
        GarageSpot oldGarageSpot = garageSpotRepository.findGarageSpotById( garageSpot.getId() );

        if ( oldGarageSpot == null )
        {
             SimpleConsoleLogger.info( "VAGA não encontrada para remover a reserva"  );
             return;
        }

        oldGarageSpot.setOccupied( false );
        garageSpotRepository.save( oldGarageSpot );
    }
}