package br.com.estapar.parking;

import br.com.estapar.parking.pacoteParaTestes.SimpleMath;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName( "Test math Operations in SimpleMath Class" )
public class PrimeiroTeste
{
    SimpleMath simpleMath;

    @BeforeAll
    static void setup()
    {
        System.out.println( "Rodando método @BeforeAll!" );
    }

    @AfterAll
    static void cleanup()
    {
        System.out.println( "Rodando método @AfterAll!" );
    }

    @BeforeEach
    void beforeEachMethod()
    {
        System.out.println( "Rodando método @BeforeEach!" );
        simpleMath = new SimpleMath();
    }

    @AfterEach
    void afterEachMethod()
    {
        System.out.println( "Rodando método @afterEachMethod!" );
        simpleMath = new SimpleMath();
    }

    @Test
    @DisplayName( "Test 6.2 + 2 = 8.2" )
    void testSum()
    {
        System.out.println( "Rodando método testSum!" );

        // AAA -> Arrange, Act Assert
        // Given -> Arrange
        SimpleMath simpleMath = new SimpleMath();

        double firstNumber = 6.2D;
        double secondNumber = 2D;

        // When -> Act
        Double result = simpleMath.sum( firstNumber, secondNumber );

        // then -> Assert
        // Verifica se o valor calculado é exatamente igual ao valor esperado.
        assertEquals( 8.2D, result,
                /* () -> só é executada se o teste falhar. */
                () -> firstNumber + " + " + secondNumber + " Em TestSum() não produziu o resultado esperado" + 8.2 );

        // Garante que o valor não seja um determinado número.
        assertNotEquals( 10D, result );

        // Verifica que o resultado não é nulo.
        assertNotNull( result );
    }

    @Test
    @DisplayName( "Test 6.2 - 2 = 4.2" )
    void testSubtraction()
    {
        System.out.println( "Rodando método testSubtraction!" );

        double firstNumber = 6.2D;
        double secondNumber = 2D;

        Double result = simpleMath.subtraction( firstNumber, secondNumber );

        assertEquals( 4.2D,
                result,
                () -> firstNumber + " - " + secondNumber + " Em testSubtraction() não produziu o resultado esperado" + 8.2 );

        assertNotEquals( 10D, result );
        assertNotNull( result );
    }

    @Test
    @DisplayName( "Test 6.2 * 2 = 12.4" )
    void testMultiplication()
    {
        System.out.println( "Rodando método testMultiplication!" );

        double firstNumber = 6.2D;
        double secondNumber = 2D;

        Double result = simpleMath.multiplication( firstNumber, secondNumber );

        assertEquals( 12.4D,
                result,
                () -> firstNumber + " * " + secondNumber + " Em testMultiplication() não produziu o resultado esperado" + 12.4D );

        assertNotEquals( 10D, result );
        assertNotNull( result );
    }

    @Test
    @DisplayName( "Test 6.2 / 2 = 3.1" )
    void testDivision()
    {
        System.out.println( "Rodando método testDivision!" );


        double firstNumber = 6.2D;
        double secondNumber = 2D;

        Double result = simpleMath.division( firstNumber, secondNumber );

        assertEquals( 3.1D,
                result,
                () -> firstNumber + " / " + secondNumber + " Em testDivision não produziu o resultado esperado" + 3.1D );

        assertNotEquals( 10D, result );
        assertNotNull( result );
    }

    @Test
    @DisplayName( "Test ( 6.2 + 2 ) / 2 = 4.1" )
    void testMean()
    {
        System.out.println( "Rodando método testMean!" );

        double firstNumber = 6.2D;
        double secondNumber = 2D;

        Double result = simpleMath.mean( firstNumber, secondNumber );

        assertEquals( 4.1D,
                result,
                () -> "(" + firstNumber + " + " + secondNumber + ")" + "/ 2 Em testMean não produziu o resultado esperado" + 4.1D );

        assertNotEquals( 10D, result );
        assertNotNull( result );
    }

    @Test
    @DisplayName("Square root of 9 should return 3")
    void testSquareRoot()
    {
        System.out.println( "Rodando método testSquareRoot!" );

        double firstNumber = 9D;

        Double result = simpleMath.squareRoot( firstNumber );

        assertEquals( 3,
                result,
                () -> firstNumber + " Em testMean não produziu o resultado esperado" + 4.1D );

        assertNotEquals( 10D, result );
        assertNotNull( result );
    }

    // test[System Under Test]_[Condition or State Change]_[Expected Result]
    @Test
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
