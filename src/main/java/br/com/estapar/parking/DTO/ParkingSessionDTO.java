package br.com.estapar.parking.DTO;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record ParkingSessionDTO(
        String license_plate,
        String event_type,
        Instant entry_time,
        Instant  exit_time,
        BigDecimal lat,
        BigDecimal lng
)
{
}
