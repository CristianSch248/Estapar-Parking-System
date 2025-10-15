package br.com.estapar.parking.model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;

@Entity
@Table( name = "garage_sectors" )
public class GarageSector
{
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @Column( nullable = false, unique = true )
    private String sector;

    @Column( name = "base_price", nullable = false )
    private Double basePrice;

    @Column( name = "max_capacity", nullable = false )
    private Integer maxCapacity;

    @Column( name = "open_hour", nullable = false )
    private LocalTime openHour;

    @Column( name = "close_hour", nullable = false )
    private LocalTime closeHour;

    @Column( name = "duration_limit_minutes", nullable = false )
    private Integer durationLimitMinutes;

    @OneToMany( mappedBy = "sector", cascade = CascadeType.ALL )
    private List< GarageSpot > spots;

    // Getters e setters
    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getSector()
    {
        return sector;
    }

    public void setSector( String sector )
    {
        this.sector = sector;
    }

    public Double getBasePrice()
    {
        return basePrice;
    }

    public void setBasePrice( Double basePrice )
    {
        this.basePrice = basePrice;
    }

    public Integer getMaxCapacity()
    {
        return maxCapacity;
    }

    public void setMaxCapacity( Integer maxCapacity )
    {
        this.maxCapacity = maxCapacity;
    }

    public LocalTime getOpenHour()
    {
        return openHour;
    }

    public void setOpenHour( LocalTime openHour )
    {
        this.openHour = openHour;
    }

    public LocalTime getCloseHour()
    {
        return closeHour;
    }

    public void setCloseHour( LocalTime closeHour )
    {
        this.closeHour = closeHour;
    }

    public Integer getDurationLimitMinutes()
    {
        return durationLimitMinutes;
    }

    public void setDurationLimitMinutes( Integer durationLimitMinutes )
    {
        this.durationLimitMinutes = durationLimitMinutes;
    }

    public List< GarageSpot > getSpots()
    {
        return spots;
    }

    public void setSpots( List< GarageSpot > spots )
    {
        this.spots = spots;
    }
}