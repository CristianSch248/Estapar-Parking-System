package br.com.estapar.parking.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

public record RevenueRequestDTO(
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        String sector
) {}
