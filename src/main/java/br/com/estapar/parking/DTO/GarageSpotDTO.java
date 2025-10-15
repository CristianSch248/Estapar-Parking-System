package br.com.estapar.parking.DTO;

import br.com.estapar.parking.model.GarageSector;

import java.math.BigDecimal;

public record GarageSpotDTO(
        Integer id,
        String sector,
        BigDecimal lat,
        BigDecimal lng,
        boolean occupied )
{
}
