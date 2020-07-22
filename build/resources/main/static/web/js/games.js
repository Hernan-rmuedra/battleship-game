 function getData() {
     var url = "/api/games";

     fetch(url)
         .then(function(resp) {
             return resp.json()
         })
         .then(function(json) {
             app.games = json.game
             app.player = json.player
             app.tableScore()
         })
 }
 getData()




 var app = new Vue({
     el: '#app',
     data: {
         message: 'BATTLESHIP',
         message1: 'SHIP UBICATION',
         games: [],
         playerscores: [],
         player: {}


     },
     filters: {
         formatDate: function(value) {
             if (value) {
                 return moment(String(value)).format('LLL');
             }
         }
     },
     methods: {
         tableScore() {
             this.games.forEach(game => {
                 game.gamePlayers.forEach(gamePlayer => {
                     if (this.playerscores.findIndex(player => player.name == gamePlayer.player.mail) != -1) {
                         let index = this.playerscores.findIndex(player => player.name == gamePlayer.player.mail)
                         this.playerscores[index].score += gamePlayer.score.points

                         if (gamePlayer.score.points == 3) {
                             this.playerscores[index].won++;
                         } else if (gamePlayer.score.points == 1.5) {
                             this.playerscores[index].tie++;
                         } else if (gamePlayer.score.points == 0) {
                             this.playerscores[index].lose++;
                         }
                     } else {
                         let player = {}
                         player.name = gamePlayer.player.mail
                         player.score = gamePlayer.score.points
                         player.won = 0;
                         player.lose = 0;
                         player.tie = 0;

                         if (gamePlayer.score.points == 3) {
                             player.won++;
                         } else if (gamePlayer.score.points == 1.5) {
                             player.tie++;
                         } else if (gamePlayer.score.points == 0) {
                             player.lose++;
                         }
                         this.playerscores.push(player)

                     }

                 })

             });
         },
         login() {
             var form = document.querySelector('#login');
             $.post("/api/login", {
                 username: form["email"].value,
                 password: form["password"].value
             }).done(function() {
                 alert("logiado");
                 getData();
             }).fail(function() {
                 alert("error");
             })


         },
         signin() {

             var form = document.querySelector('#login');
             $.post("/api/players", {
                 email: form["email"].value,
                 password: form["password"].value
             }).done(function() {
                 app.login()
             }).fail(function() {
                 alert("error");
             })
         },
         logout() {

             $.post("/api/logout").done(function() {
                 alert("estas deslogeado");
                 getData();
             }).fail(function() {
                 alert("error");

             })

         },
         creategame() {
             fetch('/api/games', {
                     method: 'POST'
                 }).then(res => {
                     if (res.ok) {
                         return res.json()
                     } else {
                         return Promise.reject(res.json())
                     }
                 }).then(json => {
                     location.href = '/web/game.html?gp=' + json.gpId
                 }).catch(error => error)
                 .then(error => console.log(error))
         },
         joinGame(gameId) {
             fetch("/api/games/" + gameId + "/players", {
                     method: 'POST'
                 }).then(res => {
                     if (res.ok) {
                         return res.json()
                     } else {
                         return Promise.reject(res.json())
                     }
                 }).then(json => {
                     location.href = '/web/game.html?gp=' + json.gpId
                 }).catch(error => error)
                 .then(error => console.log(error))
         }



     },

 })