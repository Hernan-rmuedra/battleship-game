package com.codeoftheweb.salvo.models;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;


@Entity
public class Player {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;

    private String firstName;
    private String lastName;
    private String userName;
    private String password;

    @OneToMany(mappedBy = "player" , fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player" , fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    private Set<Score> scores = new HashSet<>();

    public Player() { }

    public Player (String first, String last , String user , String password)  {

        this.firstName = first;
        this.lastName = last;
        this.userName = user;
        this.password = password;
    }

    public Player(String email, String encode) {
        this.userName = email;
        this.password = encode;

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public String toString() {
        return firstName + " " + lastName + " " + userName;
    }


    @JsonIgnore
    public List<Game> getGame(){
        return this.getGamePlayers().stream().map(gamePlayer -> gamePlayer.getGame()).collect(Collectors.toList());
    }
    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public Score getScore(Game game){
        return scores.stream().filter(p -> p.getGame().equals(game)).findFirst().orElse(null);
    }
}
