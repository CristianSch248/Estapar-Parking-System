package br.com.estapar.parking;

import br.com.estapar.parking.model.Person;
import br.com.estapar.parking.service.IPersonService;
import br.com.estapar.parking.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonServiceTest
{
    IPersonService service;
    Person person;

    @BeforeEach
    void setup()
    {
        service = new PersonService();
        person = new Person( "Cristian", "Schmitzhaus", "cris@email", "Brasil", 25 );
    }

    // test[System Under Test]_[Condition or State Change]_[Expected Result]
    @DisplayName( "When Create a Person with Success Should Returns a person Object" )
    @Test
    void testCreatePerson_WhenSuccess_ShouldReturnPersonObject()
    {
        // Given / Arrange
        // When / Act
        Person actual = service.createPerson( person );

        // Then / Assert
        assertNotNull( actual, () -> "Create Person should not have a null value" );
    }

    // test[System Under Test]_[Condition or State Change]_[Expected Result]
    @DisplayName( "When Create a Person with Success Should Contains Valid Fields in Returned a Person Object" )
    @Test
    void testCreatePerson_WhenSuccess_ShouldContainsValidFieldsInReturnedPersonObject()
    {
        // Given / Arrange
        // When / Act
        Person actual = service.createPerson( person );

        // Then / Assert
        assertNotNull( actual, () -> "Create Person should not have a null value" );

        assertNotNull( person.getId(), () -> "Person ID is Missing" );

        assertEquals( person.getFirstName(), actual.getFirstName(), () -> "Create Person should contains first name in returned person object" );
        assertEquals( person.getLastName(),  actual.getLastName(),  () -> "Create Person should contains last name in returned person object" );
        assertEquals( person.getEmail(),     actual.getEmail(),     () -> "Create Person should contains email in returned person object" );
        assertEquals( person.getCountry(),   actual.getCountry(),   () -> "Create Person should contains country in returned person object" );
        assertEquals( person.getYearOfBirth(), actual.getYearOfBirth(), () -> "Create Person should contains years of birth in returned person object" );
    }

    // test[System Under Test]_[Condition or State Change]_[Expected Result]
    @DisplayName( "Create Person with Null Email Should Throw Exception" )
    @Test
    void testCreatePerson_withNullEmail_ShouldThrowIllegalArgumentException()
    {
        // Given / Arrange
        person.setEmail( null );

        String expectedMessage = "Email cannot be null or empty";

        // When / Act
        // Then / Assert
        IllegalArgumentException actual = assertThrows( IllegalArgumentException.class,
                () -> service.createPerson( person ),
                () -> "Empty email should have cause an IllegalArgumentException" );

        assertEquals( expectedMessage,
                actual.getMessage(),
                () -> "Unexpected exception message" );
    }
}