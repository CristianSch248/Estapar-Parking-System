package br.com.estapar.parking;

import org.junit.jupiter.api.*;

// @Order( 1 ) se adicionar essa anotação no início da classe o junit usa o valor do indice
// para identificar a ordem de execução das classes de teste

// @TestMethodOrder( MethodOrderer.Random.class )// executa de forma randomica
//@TestMethodOrder( MethodOrderer.MethodName.class )// executa de forma ordenada por nome

@TestMethodOrder( MethodOrderer.OrderAnnotation.class )// executa de forma ordenada por indice
public class MethodOrderRandonlyTest
{
    @Order( 1 )
    @Test
    void testA() {
        System.out.println( "Rodando teste A" );
    }

    @Order( 3 )
    @Test
    void testB() {
        System.out.println( "Rodando teste B" );
    }

    @Order( 4 )
    @Test
    void testC() {
        System.out.println( "Rodando teste C" );
    }

    @Order( 2 )
    @Test
    void testD() {
        System.out.println( "Rodando teste D" );
    }
}
