var app = new Vue({
    el: '#app',
    data: {
        message: 'BATALLA NAVAL',
        message1: 'SHIP UBICATION',
        games: [],
        playerscores: []


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
                    gamePlayer.score.points
                    let player = {}
                    player.name = gamePlayer.player.mail
                    player.score = gamePlayer.score.points
                    if (score == 3) {
                        player.win += player

                    } else if (score == 1, 5) {
                        player.tied
                    } else {
                        player.lose
                    }

                    this.playerscores.push(player)

                })

            });
        },
        matchResult() {
            if (player1.score > player2.score) {
                return player1.won
            } else if (player1.score < player2.score) {
                return player1.lose
            } else {
                return tied
            }


        }
    },

})
var url = "/api/games";

fetch(url)
    .then(function(resp) {
        return resp.json()
    })
    .then(function(json) {
        app.games = json
        app.tableScore()


    })