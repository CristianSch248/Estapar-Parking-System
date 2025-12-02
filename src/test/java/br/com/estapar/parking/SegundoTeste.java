package br.com.estapar.parking;

import br.com.estapar.parking.pacoteParaTestes.SimpleMath;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName( "Test math Operations in SimpleMath Class" )
public class SegundoTeste
{
    SimpleMath simpleMath;

    @BeforeEach
    void beforeEachMethod()
    {
        System.out.println( "Rodando método @BeforeEach!" );
        simpleMath = new SimpleMath();
    }

    @DisplayName( "testDivision [firstNumber, secondNumber, expectedResult]" )
    @ParameterizedTest
//    @MethodSource( "testDivisionInputParameters" )
//    @CsvSource( {
//            "Pelé, football",
//            "Senna, F1",
//            "keith moon,''"
//    } )
//    @CsvFileSource( resources = "/testDivision.csv" )
//    public static Stream< Arguments > testDivisionInputParameters()
//    {
//        return Stream.of(
//                Arguments.of( 6.2D, 2D, 3.1D ),
//                Arguments.of( 71D, 14D, 5.07D ),
//                Arguments.of( 18.3, 3.1D, 5.90D )
//        );
//    }
    @CsvSource( {
            "6.2, 2, 3.1",
            "71, 14, 5.07",
            "18.3, 3.1, 5.90"
    } )
    void testDivision( double firstNumber, double secondNumber, double expectedResult )
    {
        System.out.println( "Rodando método testDivision!" );

        Double result = simpleMath.division( firstNumber, secondNumber );

        assertEquals( expectedResult,
                result, 2D,
                () -> firstNumber + " / " + secondNumber + " Em testDivision não produziu o resultado esperado" + expectedResult );

        assertNotEquals( 10D, result );
        assertNotNull( result );
    }

    @ParameterizedTest
    @ValueSource( strings = { "Cristian", "Pele", "Senna" } )
    void testValueSource( String firstname )
    {
        System.out.println( firstname );

        assertNotNull( firstname );
    }

}

