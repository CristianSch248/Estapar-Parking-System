package br.com.estapar.parking.DTO;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record RevenueResponseDTO(
        @NotNull
        BigDecimal amount,

        @NotNull
        String currency,

        @NotNull
        Instant timestamp
) {}
