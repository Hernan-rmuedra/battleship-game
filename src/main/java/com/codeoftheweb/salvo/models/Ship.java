package com.codeoftheweb.salvo.models;

import org.hibernate.annotations.GenericGenerator;


import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    @ManyToOne
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private String shipType;


    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();


    public Ship() {
    }

    public Ship(String shipType, List<String> shipLocation) {
        this.locations = shipLocation;
        this.shipType  = shipType;
    }
    public String getShipType() {
        return shipType;
    }
    public void setShipType(String shipType) {
        this.shipType = shipType;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }
    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }


    public void setLocations(List<String> locations) {
        this.locations = locations;
    }
    public List<String> getLocations() {
        return locations;
    }




}
