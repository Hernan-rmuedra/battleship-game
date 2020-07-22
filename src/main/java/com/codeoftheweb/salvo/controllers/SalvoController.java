package com.codeoftheweb.salvo.controllers;

import com.codeoftheweb.salvo.models.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import com.codeoftheweb.salvo.repositories.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
    public class SalvoController<map> {

    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private ScoreRepository scoreRepository ;


    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity <Map<String , Object>> createUser(@RequestParam String email, @RequestParam String password) {
        ResponseEntity <Map<String , Object>> response;
        Player player = playerRepository.findPlayerByUserName(email);

        if (email.isEmpty() || password.isEmpty()) {
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR , Message.MSG_MISSING_DATA), HttpStatus.FORBIDDEN);
        }

        else if (player != null) {
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR , Message.MSG_NAME_IN_USE), HttpStatus.FORBIDDEN);
        }

        else {
            Player newPlayer = playerRepository.save(new Player(email, passwordEncoder.encode(password)));
            response = new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping("/games")
    public Map< String, Object> getGames(Authentication authentication ) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        if (!this.isGuest(authentication)) {
            dto.put("player", this.playerDTO(playerRepository.findPlayerByUserName(authentication.getName())));
            dto.put("game", gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(Collectors.toList()));

        }else {
            dto.put("player", "Guest");
            dto.put("game", gameRepository.findAll().stream().map(game -> makeGameDTO(game)).collect(Collectors.toList()));
        }
        return dto;
    }



    @RequestMapping(path = "/games"  , method = RequestMethod.POST)
    public  ResponseEntity <Map< String, Object>> createGames(Authentication authentication ) {
        ResponseEntity <Map<String , Object>> response;


        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR , Message.MSG_MUST_BE_LOGGED), HttpStatus.UNAUTHORIZED);
        }else {
            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer( newGame.getCreationDate(),player,newGame ));

            response = new ResponseEntity<>(makeMap(Message.MSG_GP_ID, newGamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }




    @RequestMapping(path = "/games/{gameId}/players"  , method = RequestMethod.POST)
    public  ResponseEntity <Map< String, Object>> joinGame(@PathVariable long gameId,  Authentication authentication  ) {
        ResponseEntity <Map<String , Object>> response;


        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR, Message.MSG_MUST_BE_LOGGED), HttpStatus.UNAUTHORIZED);
        }else {
            Game game = gameRepository.findById(gameId).orElse(null);
            if ( game == null){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_NO_SUCH_GAME), HttpStatus.NOT_FOUND);
            }else if (game.getGamePlayers().size() > 1){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_GAME_IS_FUL), HttpStatus.FORBIDDEN);
            }else {
                Player player = playerRepository.findPlayerByUserName(authentication.getName());
                if (game.getGamePlayers().stream().anyMatch(gp -> gp.getPlayer().getId() == player.getId())) {
                    response = new ResponseEntity<>(makeMap(Message.KEY_ERROR, Message.MSG_CANT_PLAY_ALONE), HttpStatus.FORBIDDEN);
                } else {
                    GamePlayer newGamePlayer = gamePlayerRepository.save(new GamePlayer(LocalDateTime.now() , player,game ));
                    response = new ResponseEntity<>(makeMap(Message.MSG_GP_ID, newGamePlayer.getId()), HttpStatus.CREATED);
                }
            }

        }
        return response;
    }





    @RequestMapping(path = "/games/players/{gamePlayerId}/ships"  , method = RequestMethod.POST)
    public  ResponseEntity <Map< String, Object>> addShips(@PathVariable long gamePlayerId, @RequestBody List<Ship>ships ,Authentication authentication  ) {
        ResponseEntity <Map<String , Object>> response;
        if (isGuest(authentication)){
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR, Message.MSG_MUST_BE_LOGGED), HttpStatus.UNAUTHORIZED);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            if (gamePlayer == null){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_NO_SUCH_GAME), HttpStatus.NOT_FOUND);
            }else if (gamePlayer.getPlayer().getId() != player.getId()){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_NOT_YOUR_GAME), HttpStatus.UNAUTHORIZED);
            }else if (gamePlayer.getShips().size() > 0){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_ALREADY_HAVE_SHIPS), HttpStatus.FORBIDDEN);
            }else if (ships == null || ships.size() != 5){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_MUST_ADD_5), HttpStatus.FORBIDDEN);
            }else{

                    ships.forEach(ship -> gamePlayer.addShip(ship));
                    gamePlayerRepository.save(gamePlayer);
                    response = new ResponseEntity<>(makeMap( Message.KEY_OK , Message.MSG_SHIPS_SAVE), HttpStatus.OK);
                }
            }
        return response;
    }




    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes"  , method = RequestMethod.POST)
    public  ResponseEntity <Map< String, Object>> addSalvoes(@PathVariable long gamePlayerId, @RequestBody List<String>shots ,Authentication authentication  ) {
        ResponseEntity <Map<String , Object>> response;
        if (isGuest(authentication)){
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR, Message.MSG_MUST_BE_LOGGED), HttpStatus.UNAUTHORIZED);
        }else{
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findPlayerByUserName(authentication.getName());
            if (gamePlayer == null){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_NO_SUCH_GAME), HttpStatus.NOT_FOUND);
            }else if (gamePlayer.getPlayer().getId() != player.getId()){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_NOT_YOUR_GAME), HttpStatus.UNAUTHORIZED);
            }else if (shots.size() != 5){
                response = new ResponseEntity<>(makeMap( Message.KEY_ERROR , Message.MSG_WRONG_NUMBER), HttpStatus.FORBIDDEN);
            }else{

                int turn = gamePlayer.getSalvoes().size() + 1;

                Salvo salvo = new Salvo(turn,shots);
                gamePlayer.addSalvo(salvo);
                gamePlayerRepository.save(gamePlayer);

                if(gamePlayer.getGameStatus() == GameStatus.WON){

                    Score score = new Score(3,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getPlayer());
                    Score score1 = new Score(0,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getOpponent().getPlayer());
                    scoreRepository.save(score);
                    scoreRepository.save(score1);
                } else if (gamePlayer.getGameStatus() == GameStatus.LOST){
                    Score score = new Score(0,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getPlayer());
                    Score score1 = new Score(3,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getOpponent().getPlayer());
                    scoreRepository.save(score);
                    scoreRepository.save(score1);
                } else if (gamePlayer.getGameStatus() == GameStatus.TIED){
                    Score score = new Score(1.5,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getPlayer());
                    Score score1 = new Score(1.5,LocalDateTime.now(),gamePlayer.getGame(),gamePlayer.getOpponent().getPlayer());
                    scoreRepository.save(score);
                    scoreRepository.save(score1);
                }

                response = new ResponseEntity<>(makeMap( Message.MSG_SUCCESS , Message.MSG_SALVO_ADDED), HttpStatus.CREATED);
            }

        }
        return response;
    }







    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map< String, Object>> getGame_view(@PathVariable long gamePlayerId , Authentication authentication) {
        ResponseEntity<Map< String, Object>> response;
        if (isGuest(authentication)){
            response = new ResponseEntity<>(makeMap(Message.KEY_ERROR , Message.MSG_MUST_BE_LOGGED), HttpStatus.I_AM_A_TEAPOT);
        }else {
            GamePlayer gameplayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findPlayerByUserName(authentication.getName());

            if (gameplayer == null){
                response = new ResponseEntity<>(makeMap(Message.KEY_ERROR , Message.MSG_NO_SUCH_GAME), HttpStatus.FORBIDDEN);
            } else if (gameplayer.getPlayer().getId() != player.getId()){
                response = new ResponseEntity<>(makeMap(Message.KEY_ERROR , Message.MSG_NOT_YOUR_GAME), HttpStatus.FORBIDDEN);
            }else{
                response = new ResponseEntity<>( this.game_viewDTO(gameplayer), HttpStatus.OK);
            }
        }
        return response ;
    }




    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }



    private Boolean isGuest (Authentication authentication){
     return authentication == null || authentication instanceof AnonymousAuthenticationToken;
     }



    private Map<String, Object> makeGameDTO(Game game) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", game.getId());
        dto.put("creationDate", game.getCreationDate());
        dto.put("gamePlayers" , game.getGamePlayers().stream().map(gamePlayer -> gamePlayerDTO(gamePlayer)));
        return dto;
    }




    private Map<String, Object> gamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", gamePlayer.getId());
        dto.put("player", playerDTO(gamePlayer.getPlayer()));
        dto.put("score", scoreDTO(gamePlayer.getScore()));
        return dto;
    }



    public Map<String, Object> scoreDTO(Score score) {
        Map<String, Object> dto = new LinkedHashMap<>();
       if( score != null){
        dto.put("points", score.getScore());
       } else {
           dto.put("points", null);
       }
       return dto;
    }




    public Map<String, Object> ShipDTO(Ship ship) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("locations", ship.getLocations());
        dto.put("type", ship.getShipType());
        return dto;
    }




    public Map<String, Object> game_viewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();

            dto.put("id", gamePlayer.getGame().getId());
            dto.put("creationDate", gamePlayer.getGame().getCreationDate());
            dto.put("gamePlayers", gamePlayer.getGame().getGamePlayers().stream().map(this::gamePlayerDTO));
            dto.put("ships", gamePlayer.getShips().stream().map(this::ShipDTO));
            dto.put("salvoes", gamePlayer.getGame().getGamePlayers().stream().flatMap(gp -> gp.getSalvoes().stream().map(salvo -> salvo.salvoDTO(salvo))));
            dto.put("Hits", gamePlayer.getSalvoes().stream().map(Salvo::hitDTO));
            dto.put("sunken", gamePlayer.getSalvoes().stream().map(Salvo::sunkenDTO));
            dto.put("status" , gamePlayer.getGameStatus());

            GamePlayer opponent = gamePlayer.getOpponent();

            if (opponent != null){
                dto.put("enemyHits", opponent.getSalvoes().stream().map(Salvo::hitDTO));
                dto.put("enemySunken", opponent.getSalvoes().stream().map(Salvo::sunkenDTO));

            }else {
                dto.put("enemyHits", new ArrayList<>());
                dto.put("enemySunken", new ArrayList<>());
            }
        return dto;
    }


    

    private Map<String, Object> playerDTO(Player player) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", player.getId());
        dto.put("mail", player.getUserName());

        return dto;
    }
}