package br.com.estapar.parking.service;

import br.com.estapar.parking.model.Person;

import java.util.concurrent.atomic.AtomicLong;

public class PersonService implements IPersonService
{
    @Override
    public Person createPerson( Person person )
    {
        if ( person.getEmail() == null || person.getEmail().isBlank() )
        {
            throw new IllegalArgumentException( "Email cannot be null or empty" );
        }

        var id = new AtomicLong().incrementAndGet();

        person.setId( id );

        return person;
    }


}
