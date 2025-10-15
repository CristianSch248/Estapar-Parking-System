package br.com.estapar.parking.service;

import br.com.estapar.parking.DTO.GarageSectorDTO;
import br.com.estapar.parking.model.GarageSector;
import br.com.estapar.parking.repository.GarageSectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GarageSectorService
{
    @Autowired
    private GarageSectorRepository garageSectorRepository;

    public void createGarageSector( GarageSectorDTO garageSectorDTO )
    {
        // Verifica se já existe um setor com o mesmo nome
        if ( garageSectorRepository.findBySector( garageSectorDTO.sector() ).isPresent() )
        {
            System.out.println( "⚠️  Setor '" + garageSectorDTO.sector() + "' já existe. Ignorando inserção duplicada." );
            return;
        }

        GarageSector newGarageSector = new GarageSector();

        newGarageSector.setSector( garageSectorDTO.sector() );
        newGarageSector.setBasePrice( garageSectorDTO.base_price() );
        newGarageSector.setMaxCapacity( garageSectorDTO.max_capacity() );
        newGarageSector.setOpenHour( garageSectorDTO.open_hour() );
        newGarageSector.setCloseHour( garageSectorDTO.close_hour() );
        newGarageSector.setDurationLimitMinutes( garageSectorDTO.duration_limit_minutes() );

        garageSectorRepository.save( newGarageSector );

        System.out.println(" Novo setor '" + newGarageSector.getSector() + "' salvo com sucesso!");
    }
}
