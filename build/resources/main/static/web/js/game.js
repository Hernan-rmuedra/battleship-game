var app = new Vue({
    el: '#app',
    data: {
        message1: 'BATTLESHIP',

        gameData: [],
        letters: ['A', "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        numbers: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        player: {},
        oponent: {},
        shots: [],
        match: 0,
        puntation: [],
        enemyPuntation: [],
        grid: null,
        options: {
            //grilla de 10 x 10
            column: 10,
            row: 10,
            //separacion entre elementos (les llaman widgets)
            verticalMargin: 0,
            //altura de las celdas
            disableOneColumnMode: true,
            //altura de las filas/celdas
            cellHeight: 40,
            //necesario
            float: true,
            //desabilitando el resize de los widgets
            disableResize: true,
            //false permite mover los widgets, true impide
            staticGrid: false
        }
    },
    methods: {
        puntationHits() {
            function turnPos(a, b) {
                if (a.turn < b.turn) {
                    return -1;
                } else if (a.turn > b.turn) {
                    return 1;
                } else {
                    return 0;
                }
            };

            this.gameData.Hits.sort(turnPos).forEach(hit => {

                let sunk = this.gameData.sunken.filter(sunk => sunk.turn == hit.turn)[0].sunken.map(sunk => sunk.type);

                let golpe = {

                    turn: hit.turn,
                    hits: hit.hits,
                    sunken: sunk,
                    left: 5 - sunk.length,
                }

                app.puntation.push(golpe);

            })

            this.gameData.enemyHits.sort(turnPos).forEach(hit => {

                let sunk = this.gameData.enemySunken.filter(sunk => sunk.turn == hit.turn)[0].sunken.map(sunk => sunk.type);

                let golpe = {

                    turn: hit.turn,
                    hits: hit.hits,
                    sunken: sunk,
                    left: 5 - sunk.length,
                }

                app.enemyPuntation.push(golpe);

            })



        },
        printHits() {
            this.gameData.enemyHits.forEach(hit => {
                hit.hits.forEach(hited => {
                    var letter = hited[0];
                    var number = hited.substring(1);
                    var y = (app.letterToNumber(letter) + 1) * 40;
                    var x = (parseInt(number)) * 40;

                    document.getElementById('grid-ships').innerHTML += "<div class='hitter' style='top:" + y + "px; left:" + x + "px;'></div>";

                })
            })
        },


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
                        document.getElementById(location + 'salvo').classList.add('shootblack');
                    })
                }
                /*else {
                    salvo.location.forEach(location => {
                        this.gameData.ships.forEach(ship => {
                            if (ship.locations.includes(location.concat('salvo'))) {
                                document.getElementById(location.concat('salvo')).style.backgroundColor = "orange";
                            } else {
                                document.getElementById(location.concat('salvo')).style.backgroundColor = "green";
                            }

                        })
                    })
                }*/
                this.gameData.Hits.forEach(hit => {
                    hit.hits.forEach(hited => {
                        document.getElementById(hited.concat('salvo')).style.backgroundColor = "orange";

                    })
                });

            })
        },
        getPlayersInfo() {
            this.gameData.gamePlayers.forEach(gp => {
                if (gpId == gp.id) {
                    this.player = gp.player
                } else {
                    this.oponent = gp.player
                }
            })
        },
        getShipPosition() {
            let ships = document.querySelectorAll(".grid-stack-item")
            let shipsToSend = [];
            ships.forEach(ship => {
                let shipToSend = {};
                shipToSend.locations = [];
                shipToSend.shipType = ship.children[0].id;
                let x = parseInt(ship.dataset.gsX) + 1;
                let y = parseInt(ship.dataset.gsY);
                let width = parseInt(ship.dataset.gsWidth);
                let height = parseInt(ship.dataset.gsHeight);
                if (width > height) {
                    for (let i = 0; i < width; i++) {
                        shipToSend.locations.push(this.numberToLetter(y) + (x + i))
                    }

                } else {
                    for (let i = 0; i < height; i++) {
                        shipToSend.locations.push(this.numberToLetter(y + i) + (x))
                    }
                }
                shipsToSend.push(shipToSend);

            })
            return shipsToSend;
        },
        numberToLetter(number) {
            if (number == 0) {
                return 'A';
            } else if (number == 1) {
                return 'B'
            } else if (number == 2) {
                return 'C'
            } else if (number == 3) {
                return 'D'
            } else if (number == 4) {
                return 'E'
            } else if (number == 5) {
                return 'F'
            } else if (number == 6) {
                return 'G'
            } else if (number == 7) {
                return 'H'
            } else if (number == 8) {
                return 'I'
            } else if (number == 9) {
                return 'J'
            }
        },
        letterToNumber(letter) {
            if (letter == "A") {
                return 0;
            } else if (letter == "B") {
                return 1
            } else if (letter == "C") {
                return 2
            } else if (letter == "D") {
                return 3
            } else if (letter == "E") {
                return 4
            } else if (letter == "F") {
                return 5
            } else if (letter == "G") {
                return 6
            } else if (letter == "H") {
                return 7
            } else if (letter == "I") {
                return 8
            } else if (letter == "J") {
                return 9
            }
        },
        sendShips(ships, gamePlayerId) {
            let url = '/api/games/players/' + gamePlayerId + '/ships';
            let init = {
                method: 'POST',
                headers: {
                    "Content-type": "application/json"
                },
                body: JSON.stringify(ships)
            }
            fetch(url, init)
                .then(res => {
                    if (res.ok) {
                        return res.json()
                    } else {
                        return Promise.reject(res.json())
                    }
                }).then(json => {
                    location.reload()
                }).catch(error => console.log(error))

        },
        shoot(shots) {
            let url = '/api/games/players/' + gpId + '/salvoes';
            let init = {
                method: 'POST',
                headers: {
                    "Content-type": "application/json"
                },
                body: JSON.stringify(shots)
            }
            fetch(url, init)
                .then(res => {
                    if (res.ok) {
                        return res.json()
                    } else {
                        return Promise.reject(res.json())
                    }
                }).then(json => {

                    location.reload()
                }).catch(error => error)
                .then(error => console.log(error))

        },
        shipsSave() {
            let getShipPosition = app.getShipPosition();
            this.sendShips(getShipPosition, gpId);
        },
        shooting() {
            var x = event.target["id"];

            var x = x.length == 8 ? x.slice(0, 3) : x.slice(0, 2);

            if (app.shots.includes(x)) {
                app.shots = app.shots.filter(function(ele) { return ele != x; });
                document.getElementById(x + 'salvo').classList.remove('tiros');

            } else {
                if (app.shots.length < 5) {
                    app.shots.push(x);
                    document.getElementById(x + 'salvo').classList.add('tiros');
                } else {
                    alert("Nene son solo 5 disparos por turno !!!!!");
                }
            }
        },
        printWidge() {
            this.gameData.ships.forEach(ship => {
                var firsLocation = ship.locations[0];
                var secondLocation = ship.locations[1];
                var letter = firsLocation[0];
                var number = firsLocation.substring(1);
                var spaces = ship.locations.length;

                var y = app.letterToNumber(letter);
                var x = parseInt(number) - 1;

                var typeShip = ship.type;

                if (firsLocation[0] == secondLocation[0]) {
                    this.grid.addWidget('<div><div id="' + typeShip + '" class="grid-stack-item-content ' + typeShip + 'Horizontal"></div><div/>',
                        x, y, spaces, 1);
                } else {
                    this.grid.addWidget('<div><div id="' + typeShip + '" class="grid-stack-item-content ' + typeShip + 'Vertical"></div><div/>',
                        x, y, 1, spaces);
                }


            })

        },
        insertShips() {
            this.grid.addWidget('<div><div id="submarine" class="grid-stack-item-content submarineHorizontal"></div><div/>',
                1, 1, 3, 1);

            this.grid.addWidget('<div><div id="carrier" class="grid-stack-item-content carrierVertical"></div><div/>',
                9, 1, 1, 4);

            this.grid.addWidget('<div><div id="patrol" class="grid-stack-item-content patrolHorizontal"></div><div/>',
                2, 4, 2, 1);

            this.grid.addWidget('<div><div id="destroyer" class="grid-stack-item-content destroyerVertical"></div><div/>',
                6, 4, 1, 3);

            this.grid.addWidget('<div><div id="battleship" class="grid-stack-item-content battleshipHorizontal"></div><div/>',
                2, 8, 5, 1);
            //rotacion de las naves
            //obteniendo los ships agregados en la grilla
            const ships = document.querySelectorAll("#submarine,#carrier,#patrol,#destroyer,#battleship");
            ships.forEach(ship => {
                //asignando el evento de click a cada nave
                ship.parentElement.onclick = function(event) {
                    //obteniendo el ship (widget) al que se le hace click
                    let itemContent = event.target;
                    //obteniendo valores del widget
                    let itemX = parseInt(itemContent.parentElement.dataset.gsX);
                    let itemY = parseInt(itemContent.parentElement.dataset.gsY);
                    let itemWidth = parseInt(itemContent.parentElement.dataset.gsWidth);
                    let itemHeight = parseInt(itemContent.parentElement.dataset.gsHeight);

                    //si esta horizontal se rota a vertical sino a horizontal
                    if (itemContent.classList.contains(itemContent.id + 'Horizontal')) {
                        //veiricando que existe espacio disponible para la rotacion
                        if (app.grid.isAreaEmpty(itemX, itemY + 1, itemHeight, itemWidth - 1) && (itemY + (itemWidth - 1) <= 9)) {
                            //la rotacion del widget es simplemente intercambiar el alto y ancho del widget, ademas se cambia la clase
                            app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                            itemContent.classList.remove(itemContent.id + 'Horizontal');
                            itemContent.classList.add(itemContent.id + 'Vertical');
                        } else {
                            alert("Espacio no disponible");
                        }
                    } else {
                        if (app.grid.isAreaEmpty(itemX + 1, itemY, itemHeight - 1, itemWidth) && (itemX + (itemHeight - 1) <= 9)) {
                            app.grid.resize(itemContent.parentElement, itemHeight, itemWidth);
                            itemContent.classList.remove(itemContent.id + 'Vertical');
                            itemContent.classList.add(itemContent.id + 'Horizontal');
                        } else {
                            alert("Espacio no disponible");
                        }
                    }
                }
            })
        }
    },
    mounted() {

        //iniciando la grilla en modo libe statidGridFalse

    }

})

var url = "http://localhost:8080/api/game_view/";
var gpId = paramObj(location.search).gp;
fetch(url + gpId)
    .then(function(resp) {
        return resp.json()
    })
    .then(function(json) {
        app.gameData = json
        app.match = app.gameData.Hits.length;
        app.getPlayersInfo();
        app.printHits();

        if (app.gameData.ships.length > 0) {
            app.options.staticGrid = true;
            app.grid = GridStack.init(app.options, '#grid');
            app.printWidge();
        } else {
            app.options.staticGrid = false;
            app.grid = GridStack.init(app.options, '#grid');
            app.insertShips();
        }
        app.printSalvoes();
        app.puntationHits();

        if (app.gameData.status == 'WAIT') {
            setInterval(function() {
                location.reload()
            }, 5000)
        }



    })


function paramObj(search) {
    var obj = {};
    var reg = /(?:[?&]([^?&#=]+)(?:=([^&#]*))?)(?:#.*)?/g;

    search.replace(reg, function(match, param, val) {
        obj[decodeURIComponent(param)] = val === undefined ? "" : decodeURIComponent(val);
    });

    return obj;
}