var app = new Vue({
    el: '#app',
    data: {
        message: 'BATALLA NAVAL',
        message1: 'START THE GAME',
        gameData: [],
        letters: ['A', "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        numbers: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        player: {},
        oponent: {}

    },
    methods: {
        printShips() {
            this.gameData.ships.forEach(ship => {
                ship.locations.forEach(location => {
                    document.getElementById(location).style.backgroundColor = "red";
                })
            })
        },
        printSalvoes() {
            this.gameData.salvoes.forEach(salvo => {
                if (salvo.players == this.player.id) {
                    salvo.location.forEach(location => {
                        document.getElementById(location + 'salvo').style.backgroundColor = "blue";
                    })
                } else {
                    salvo.location.forEach(location => {
                        this.gameData.ships.forEach(ship => {
                            if (ship.locations.includes(location)) {
                                document.getElementById(location).style.backgroundColor = "yellow";
                            } else {
                                document.getElementById(location).style.backgroundColor = "green";
                            }

                        })
                    })
                }

            })
        },
        getPlayersInfo() {
            this.gameData.gamePlayers.forEach(gp => {
                if (gpId.gp == gp.id) {
                    this.player = gp.player
                } else {
                    this.oponent = gp.player
                }
            })
        }
    },

})

var url = "http://localhost:8080/api/game_view/";
var gpId = paramObj(location.search).gp;
fetch(url + gpId)
    .then(function(resp) {
        return resp.json()
    })
    .then(function(json) {
        app.gameData = json
        app.getPlayersInfo();
        app.printShips();
        app.printSalvoes();
    })


function paramObj(search) {
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function(match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });

    return obj;
}