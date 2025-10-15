package br.com.estapar.parking.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table( name = "incoming_events" )
public class IncomingEvent
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column( name = "event_type", nullable = false )
    private String eventType;

    @Column( name = "license_plate", nullable = false )
    private String licensePlate;

    @Column( name = "event_time", nullable = false )
    private LocalDateTime eventTime;

    @Column( name = "raw_payload", columnDefinition = "JSON", nullable = false )
    private String rawPayload;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType( String eventType )
    {
        this.eventType = eventType;
    }

    public String getLicensePlate()
    {
        return licensePlate;
    }

    public void setLicensePlate( String licensePlate )
    {
        this.licensePlate = licensePlate;
    }

    public LocalDateTime getEventTime()
    {
        return eventTime;
    }

    public void setEventTime( LocalDateTime eventTime )
    {
        this.eventTime = eventTime;
    }

    public String getRawPayload()
    {
        return rawPayload;
    }

    public void setRawPayload( String rawPayload )
    {
        this.rawPayload = rawPayload;
    }
}
