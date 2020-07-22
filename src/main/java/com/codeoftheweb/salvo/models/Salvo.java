package com.codeoftheweb.salvo.models;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    private int turnNumber;

    @ElementCollection
    @Column(name = "location")
    private List<String> locations = new ArrayList<>();



    public Salvo(int turnNumber , List<String> salvoLocations) {
        this.turnNumber = turnNumber;
        this.locations = salvoLocations;

    }
    public Salvo() {
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

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public Map<String, Object> salvoDTO(Salvo salvo) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.getTurnNumber());
        dto.put("location", this.getLocations());
        dto.put("email" , this.getGamePlayer().getPlayer().getUserName());
        dto.put("players" , this.getGamePlayer().getPlayer().getId());

        return dto;
    }


    public Map<String, Object> hitDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turnNumber);
        GamePlayer opponent = this.getGamePlayer().getOpponent();
        if(opponent != null){
            dto.put("hits", this.getHits());
        } else {
            dto.put("hits", new ArrayList<>());
        }

        return dto;
    }


    public Map<String, Object> sunkenDTO(){
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("turn", this.turnNumber);

        GamePlayer opponent = this.getGamePlayer().getOpponent();

        if(opponent != null){
            dto.put("sunken", this.getSunkenShips().stream().map(this::shipDTO));
        } else {
            dto.put("sunken", new ArrayList<>());
        }
        return dto;
    }


    public Map<String, Object> shipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("locations", ship.getLocations());
        dto.put("type", ship.getShipType());
        return dto;
    }


    public List<String> getHits() {
        List<String> allEnemyLocs = new ArrayList<>();
        List<String> myShoots = this.getLocations();
        Set<Ship>opponentShips = this.getGamePlayer().getOpponent().getShips();
        opponentShips.forEach(ship -> allEnemyLocs.addAll(ship.getLocations()));

        return myShoots.stream().filter(shot -> allEnemyLocs.
                stream().anyMatch(loc -> loc.equals(shot))).
                collect(Collectors.toList());
    }
    public List<Ship> getSunkenShips( ) {
        List<String> allShots = new ArrayList<>();
        Set<Ship>opponentShips = this.getGamePlayer().getOpponent().getShips();
        Set<Salvo> mySalvoes = this.getGamePlayer()
                .getSalvoes()
                .stream()
                .filter(salvo -> salvo.getTurnNumber() <= this.getTurnNumber())
                .collect(Collectors.toSet());


        mySalvoes.forEach(salvo -> allShots.addAll(salvo.getLocations()));

        return opponentShips
                .stream()
                .filter(ship -> allShots.containsAll(ship.getLocations())).collect(Collectors.toList());

    }

}

