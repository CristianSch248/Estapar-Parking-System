package br.com.estapar.parking.util;

import br.com.estapar.parking.DTO.ParkingSessionDTO;

import java.math.BigDecimal;
import java.time.Instant;

public class ParkingSessionDTOFactory
{
    public static ParkingSessionDTO buildEntryEvent()
    {
        return new ParkingSessionDTO(
                "ZUL0001",
                "ENTRY",
                Instant.parse("2025-01-01T10:00:00.000Z"),
                null,
                null,
                null
        );
    }

    public static ParkingSessionDTO buildParkedEvent()
    {
        return new ParkingSessionDTO(
                "ZUL0001",
                "PARKED",
                null,
                null,
                new BigDecimal("-23.561684"),
                new BigDecimal("-46.655981")
        );
    }

    public static ParkingSessionDTO buildExitEvent()
    {
        return new ParkingSessionDTO(
                "ZUL0001",
                "EXIT",
                null,
                Instant.parse("2025-01-01T14:00:00.000Z"),
                null,
                null
        );
    }
}
