package br.com.estapar.parking.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table( name = "garage_spots" )
public class GarageSpot
{
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sector_id", nullable = false)
    private GarageSector sector;

    @Column( precision = 10, scale = 6 )
    private BigDecimal lat;

    @Column( precision = 10, scale = 6 )
    private BigDecimal lng;

    @Column( nullable = false )
    private boolean occupied = false;

    public Integer getId()
    {
        return id;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public GarageSector getSector()
    {
        return sector;
    }

    public void setSector( GarageSector sector )
    {
        this.sector = sector;
    }

    public BigDecimal getLat()
    {
        return lat;
    }

    public void setLat( BigDecimal lat )
    {
        this.lat = lat;
    }

    public BigDecimal getLng()
    {
        return lng;
    }

    public void setLng( BigDecimal lng )
    {
        this.lng = lng;
    }

    public boolean getOccupied()
    {
        return occupied;
    }

    public void setOccupied( boolean occupied )
    {
        this.occupied = occupied;
    }
}