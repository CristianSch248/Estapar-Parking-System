package br.com.estapar.parking.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SimpleConsoleLogger
{
    private static final Logger log = LoggerFactory.getLogger( SimpleConsoleLogger.class );

    private SimpleConsoleLogger()
    {
    }

    public static void error( String msg )
    {
        log.error( msg );
    }

    public static void warn( String msg )
    {
        log.warn( msg );
    }

    public static void info( String msg )
    {
        log.info( msg );
    }

    public static void debug( String msg )
    {
        log.debug( msg );
    }
}
