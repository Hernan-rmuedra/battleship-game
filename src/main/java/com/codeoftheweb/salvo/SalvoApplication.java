package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


@SpringBootApplication
public class SalvoApplication  {


    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        CommandLineRunner commandLineRunner = (args) -> {
            // save a couple of customers


            Player player1 = new Player("Jack", "Bauer", "j.bauer@ctu.gov", passwordEncoder.encode("24") );
            playerRepository.save(player1);

            Player player2 = new Player("Chloe", "O'Brian", "c.obrian@ctu.gov", passwordEncoder.encode("34"));
            playerRepository.save(player2);

            Player player3 = new Player("Kim", "Bauer", "kim_bouer@gmail.com", passwordEncoder.encode("44"));
            playerRepository.save(player3);

            Player player4 = new Player("David", "Palmer", "david@gmail.com", passwordEncoder.encode("54"));
            playerRepository.save(player4);

            Player player5 = new Player("Michelle", "Dessler", "michaelllll@gmail.com", passwordEncoder.encode("64"));
            playerRepository.save(player5);


            Game game1 = new Game(LocalDateTime.now());
            gameRepository.save(game1);
            Game game2 = new Game(LocalDateTime.now());
            gameRepository.save(game2);
            Game game3 = new Game(LocalDateTime.now().plusHours(1));
            gameRepository.save(game3);
            Game game4 = new Game(LocalDateTime.now().plusHours(2));
            gameRepository.save(game4);
            Game game5 = new Game(LocalDateTime.now().plusHours(3));
            gameRepository.save(game5);


            GamePlayer gamePlayer1 = new GamePlayer(LocalDateTime.now(), player1, game1);
            GamePlayer gamePlayer2 = new GamePlayer(LocalDateTime.now(), player2, game1);
            GamePlayer gamePlayer3 = new GamePlayer(LocalDateTime.now(), player3, game2);
            GamePlayer gamePlayer4 = new GamePlayer(LocalDateTime.now(), player4, game2);
            GamePlayer gamePlayer5 = new GamePlayer(LocalDateTime.now(), player1, game3);
            GamePlayer gamePlayer6 = new GamePlayer(LocalDateTime.now(), player3, game3);


            Ship ship1 = new Ship("Submarine", Arrays.asList("A1", "A2", "A3", "A4"));

            Ship ship2 = new Ship("Boat", Arrays.asList("B6", "B7", "B8"));

            Ship ship3 = new Ship("Fishing", Arrays.asList("C3", "D3"));

            Ship ship4 = new Ship("Cruise", Arrays.asList("D4", "E4"));

            Ship ship5 = new Ship("Patrol", Arrays.asList("J1", "J2", "J3", "J4" , "J5"));


            gamePlayer1.addShip(ship1);
            gamePlayer1.addShip(ship2);
            gamePlayer1.addShip(ship5);


            gamePlayer2.addShip(ship3);
            gamePlayer2.addShip(ship4);


            Salvo salvo1 = new Salvo(1, new ArrayList<>(Arrays.asList("B5", "C5", "D8")));
            gamePlayer1.addSalvo(salvo1);
            Salvo salvo2 = new Salvo(2, new ArrayList<>(Arrays.asList("F5", "G5", "H8")));
            gamePlayer1.addSalvo(salvo2);
            Salvo salvo6 = new Salvo(3, new ArrayList<>(Arrays.asList("C3", "D3", "H8")));
            gamePlayer1.addSalvo(salvo6);

            Salvo salvo3 = new Salvo(1, new ArrayList<>(Arrays.asList("A3", "B2", "C4", "J5")));
            gamePlayer2.addSalvo(salvo3);
            Salvo salvo4 = new Salvo(2, new ArrayList<>(Arrays.asList("F1", "C5", "H3" ,"J3")));
            gamePlayer2.addSalvo(salvo4);
            Salvo salvo5 = new Salvo(3, new ArrayList<>(Arrays.asList("J1", "J2", "J4" ,"H1")));
            gamePlayer2.addSalvo(salvo5);


            LocalDateTime localDateTime = LocalDateTime.now();

            Score score1 = new Score(3, localDateTime, game1, player1);
            Score score2 = new Score(0, localDateTime, game1, player2);
            Score score3 = new Score(3, localDateTime, game2, player3);
            Score score4 = new Score(0, localDateTime, game2, player4);
            Score score5 = new Score(3, localDateTime, game3, player1);
            Score score6 = new Score(0, localDateTime, game3, player3);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);

            gamePlayerRepository.save(gamePlayer1);
            gamePlayerRepository.save(gamePlayer2);
            gamePlayerRepository.save(gamePlayer3);
            gamePlayerRepository.save(gamePlayer4);
            gamePlayerRepository.save(gamePlayer5);
            gamePlayerRepository.save(gamePlayer6);
        };
        return commandLineRunner;
    }

}
