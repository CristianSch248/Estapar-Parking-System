package br.com.estapar.parking.model;

import java.io.Serializable;
import java.util.Objects;

public class Person implements Serializable
{
    private Long   id;
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private int    yearOfBirth;

    public Person()
    {

    }

    public Person( Long id, String firstName, String lastName, String email, String country, int yearOfBirth )
    {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.yearOfBirth = yearOfBirth;
    }

    public Person( String firstName, String lastName, String email, String country, int yearOfBirth )
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.country = country;
        this.yearOfBirth = yearOfBirth;
    }

    public Long getId()
    {
        return id;
    }

    public void setId( Long id )
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName( String lastName )
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public String getCountry()
    {
        return country;
    }

    public void setCountry( String country )
    {
        this.country = country;
    }

    public int getYearOfBirth()
    {
        return yearOfBirth;
    }

    public void setYearOfBirth( int yearOfBirth )
    {
        this.yearOfBirth = yearOfBirth;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        Person person = ( Person ) o;

        return yearOfBirth == person.yearOfBirth
                && Objects.equals( id, person.id )
                && Objects.equals( firstName, person.firstName )
                && Objects.equals( lastName, person.lastName )
                && Objects.equals( email, person.email )
                && Objects.equals( country, person.country );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, firstName, lastName, email, country, yearOfBirth );
    }
}
