package com.codeoftheweb.salvo.models;


import org.hibernate.annotations.GenericGenerator;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToMany(mappedBy = "gamePlayer" , fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer" , fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Salvo> Salvoes = new HashSet<>();

    public GamePlayer(){
    }

    public GamePlayer(LocalDateTime joinDate, Player player, Game game) {
        this.joinDate = joinDate;
        this.player = player;
        this.game = game;
    }

    public Set<Salvo> getSalvoes() {
        return Salvoes;
    }

    public void setSalvoes(Set<Salvo> salvoes) {
        Salvoes = salvoes;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<Ship> getShips() {
        return ships;
    }


    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public long getId() {
        return id;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void addShip(Ship ship) {
        ship.setGamePlayer(this);
        this.ships.add(ship);
    }

    public void addSalvo(Salvo salvo) {
        salvo.setGamePlayer(this);
        this.Salvoes.add(salvo);

    }
    public Score getScore() {
       return player.getScore(game);
    }

    public GamePlayer getOpponent() {
        return getGame().getGamePlayers().stream().filter(gp -> gp.getId() != this.getId()).findFirst().orElse(null);
    }

    public List<Ship> getSunkenShips(){
        if(this.getOpponent()==null) {
            return null;
        }
        int lastTurn = this.getSalvoes().size();
        List<String> salvoLocations = this.getOpponent().getSalvoes().stream().filter(salvo->salvo.getTurnNumber()<=lastTurn).flatMap(salvo->salvo.getLocations().stream().map(location->location)).collect(Collectors.toList());
        return this.getShips().stream().filter(ship->salvoLocations.containsAll(ship.getLocations())).collect(Collectors.toList());
    }


    public GameStatus getGameStatus(){
        if(this.getShips().size()== 0){
            return GameStatus.PLACE_SHIPS;
        }
        if(this.getOpponent()==null){
            if(this.getSalvoes().size()==0){
                return GameStatus.SHOOT;
            }else {
                return GameStatus.WAIT;
            }
        }
        if(this.getSalvoes().size()>this.getOpponent().getSalvoes().size()){
            return GameStatus.WAIT;
        }
        if(this.getSalvoes().size()==this.getOpponent().getSalvoes().size()&&this.getSalvoes().size()>0){
            if(this.getOpponent().getSunkenShips().size()==this.getOpponent().getShips().size()&&this.getSunkenShips().size()==this.getShips().size()) {
                return GameStatus.TIED;
            }
            if(this.getSunkenShips().size()==this.getShips().size()){
                return GameStatus.LOST;
            }
            if(this.getOpponent().getSunkenShips().size()==this.getOpponent().getShips().size()) {
                return GameStatus.WON;
            }
        }
        return GameStatus.SHOOT;
    }


}
