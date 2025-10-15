package br.com.estapar.parking.DTO;

import java.time.LocalTime;

public record GarageSectorDTO(
        String sector,
        Double base_price,
        Integer max_capacity,
        LocalTime open_hour,
        LocalTime close_hour,
        Integer duration_limit_minutes )
{
}
