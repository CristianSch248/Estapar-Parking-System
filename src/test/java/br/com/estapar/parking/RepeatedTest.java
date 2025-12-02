package br.com.estapar.parking;

import br.com.estapar.parking.pacoteParaTestes.SimpleMath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RepeatedTest
{
    SimpleMath simpleMath;

    @BeforeEach
    void beforeEachMethod()
    {
        System.out.println( "Rodando método @BeforeEach!" );
        simpleMath = new SimpleMath();
    }

    @org.junit.jupiter.api.RepeatedTest( 3 )
    @DisplayName( "Division by zero" )
    void testDivisionByZero()
    {
        // given
        double firstNumber = 6.2D;
        double secondNumber = 0D;

        String expectedMessage = "Impossible Division by zero";

        // when && then
        ArithmeticException actual = assertThrows( ArithmeticException.class,
                () -> { simpleMath.division( firstNumber, secondNumber ); },
                () ->  "Division by zero should throw an ArithmeticException" );

        assertEquals( expectedMessage,
                actual.getMessage(),
                () -> "Unexpected exception message" );
    }
}
