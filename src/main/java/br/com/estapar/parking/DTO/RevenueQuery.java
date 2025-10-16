package br.com.estapar.parking.DTO;

import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDate;

public record RevenueQuery(
        @NotNull LocalDate date,
        String sector
)
{
    public static RevenueQuery of( LocalDate date, String sector )
    {
        if ( date == null || sector == null || sector.isBlank() )
        {
            throw new IllegalArgumentException( "Campo data e setor não encontrados" );
        }

        return new RevenueQuery( date, sector.trim().toUpperCase() );
    }
}
