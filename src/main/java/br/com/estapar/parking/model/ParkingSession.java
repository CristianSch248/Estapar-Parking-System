package br.com.estapar.parking.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table( name = "parking_sessions" )
public class ParkingSession
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column( name = "license_plate", nullable = false )
    private String licensePlate;

    @Column( name = "entry_time", nullable = false )
    private Instant  entryTime;

    @Column( name = "exit_time" )
    private Instant exitTime;

    @Column( nullable = false )
    private String sector;

    @ManyToOne
    @JoinColumn( name = "spot_id", nullable = false )
    private GarageSpot spot;

    @Column( name = "price_per_hour", nullable = false )
    private BigDecimal pricePerHour;

    @Column( name = "total_amount" )
    private BigDecimal totalAmount;

    @Column( nullable = false )
    private String status;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getLicensePlate()
    {
        return licensePlate;
    }

    public void setLicensePlate( String licensePlate )
    {
        this.licensePlate = licensePlate;
    }

    public Instant  getEntryTime()
    {
        return entryTime;
    }

    public void setEntryTime( Instant  entryTime )
    {
        this.entryTime = entryTime;
    }

    public Instant  getExitTime()
    {
        return exitTime;
    }

    public void setExitTime( Instant  exitTime )
    {
        this.exitTime = exitTime;
    }

    public String getSector()
    {
        return sector;
    }

    public void setSector( String sector )
    {
        this.sector = sector;
    }

    public GarageSpot getSpot()
    {
        return spot;
    }

    public void setSpot( GarageSpot spot )
    {
        this.spot = spot;
    }

    public BigDecimal getPricePerHour()
    {
        return pricePerHour;
    }

    public void setPricePerHour( BigDecimal pricePerHour )
    {
        this.pricePerHour = pricePerHour;
    }

    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    public void setTotalAmount( BigDecimal totalAmount )
    {
        this.totalAmount = totalAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }
}
