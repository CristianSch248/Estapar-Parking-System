package br.com.estapar.parking;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ArraysCompareTest
{

    // test[System Under Test]_[Condition or State Change]_[Expected Result]
    @DisplayName( "Test Compare arrays" )
    @Test
    void testCompareArrays()
    {
        int[] numbers         = { 25, 8, 21, 32, 3 };
        int[] expectedNumbers = { 3, 8, 21, 25, 32 };

        Arrays.sort( numbers );
        assertEquals( numbers, expectedNumbers );
    }

    @DisplayName( "Test Time out" )
    @Test
    //@Timeout( 1 )
    @Timeout( value = 15, unit = TimeUnit.MILLISECONDS)
    void testSortPerformance()
    {
        int[] numbers = { 25, 8, 21, 32, 3 };

        for ( int i =0 ; i < 1_000_000_000; i++)
        {
            numbers[0] = i;
            Arrays.sort( numbers );
        }
    }


}