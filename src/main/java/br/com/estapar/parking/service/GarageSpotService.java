package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.GarageSpotDTO;
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
        GarageSector sector = garageSectorRepository.findBySector( garageSpotDTO.sector() )
                .orElseThrow( () -> new RuntimeException( "Setor não encontrado: " + garageSpotDTO.sector() ) );

        GarageSpot newGarageSpot = new GarageSpot();

        newGarageSpot.setId( garageSpotDTO.id() );
        newGarageSpot.setSector( sector );
        newGarageSpot.setLat( garageSpotDTO.lat() );
        newGarageSpot.setLng( garageSpotDTO.lng() );
        newGarageSpot.setOccupied( garageSpotDTO.occupied() );

        garageSpotRepository.save( newGarageSpot );

        System.out.println(" Novo setor '" + newGarageSpot.getSector().getSector() + "' salvo com sucesso!");
    }
}
