package br.com.estapar.parking.DTO;

import java.math.BigDecimal;

public record GarageSpotDTO(
        Integer id,
        String sector,
        BigDecimal lat,
        BigDecimal lng,
        boolean occupied )
{
}
