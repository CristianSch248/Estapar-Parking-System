package br.com.estapar.parking.controller;

import br.com.estapar.parking.DTO.RevenueQuery;
import br.com.estapar.parking.DTO.RevenueRequestDTO;
import br.com.estapar.parking.DTO.RevenueResponseDTO;
import br.com.estapar.parking.service.GarageSectorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@Validated
@RestController
@RequestMapping( "/revenue" )
public class GarageSectorController
{
    @Autowired
    private GarageSectorService garageSectorService;

    /**
     * O enunciado não especificava como date e sector chegariam (query, path, body…),
     * então decidi fazer um endpoint que aceita os três.
     *
     * A prioridade na hora de resolver os valores é: body → query param → path param.
     * Quem consome a API escolhe como enviar — os três exemplos abaixo funcionam:
     *
     *   GET /revenue?date=2025-01-01&sector=A        <- recomendado
     *   GET /revenue/A?date=2025-01-01               <- path + query
     *   GET /revenue  { "date": "2025-01-01", "sector": "A" }  <- body JSON
     */
    @Operation( summary = "Consulta faturamento por setor e data" )
    @GetMapping( value = { "", "/{sectorPath}" }, produces = MediaType.APPLICATION_JSON_VALUE )
    public ResponseEntity< RevenueResponseDTO > getRevenue(
            // Query params
            @Parameter(description = "Data (yyyy-MM-dd)")
            @RequestParam( required = false )
            @DateTimeFormat( iso = DateTimeFormat.ISO.DATE )
            LocalDate date,

            @Parameter(description = "Setor (ex.: A, B)")
            @RequestParam( required = false )
            String sector,

            // Path param opcional: /revenue/{sectorPath}
            @PathVariable( required = false )
            String sectorPath,

            // Body JSON opcional (GET com body é raro; required=false)
            @RequestBody( required = false )
            RevenueRequestDTO body
    )
    {
        // Prioridade: body -> query -> path
        LocalDate resolvedDate = Optional.ofNullable( body != null ? body.date() : null )
                .or( () -> Optional.ofNullable( date ) )
                .orElse( null );

        String resolvedSector = Optional.ofNullable( body != null ? body.sector() : null )
                .or( () -> Optional.ofNullable( sector ) )
                .or( () -> Optional.ofNullable( sectorPath ) )
                .orElse( null );

        RevenueQuery query = RevenueQuery.of( resolvedDate, resolvedSector );

        return ResponseEntity.ok( garageSectorService.getRevenue( query ) );
    }
}