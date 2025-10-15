package br.com.estapar.parking.DTO;

import java.util.List;

public record WrapperGarageDTO(
        List<GarageSectorDTO> garage,
        List<GarageSpotDTO> spots
) {}
