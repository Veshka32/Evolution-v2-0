const tcp = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
//const id = new URL(window.location.href).searchParams.get('id');
const id = getId();
const stompClient = Stomp.client(tcp + window.location.host + '/chat');

let playerName, draggedProperty, playedCardId, mimicryVictims;
let move = null;
let firstAnimalId = null;
let secondAnimalId = null;
let tailLoss = false;
let mimicry = false;
let doEat = false;

stompClient.connect({}, function (frame) {
    stompClient.subscribe('/user/queue/messages', function (message) {
        onMessage(message);
        console.log(message);
    });
});

function send(message) {
    stompClient.send("/app/chat/" + id, {}, message);
}

function getId() {
    let match = document.cookie.match(new RegExp('gameId' + '=([^;]+)'));
    if (match) return match[1];
}

//------------------//

function eatFood() {
    if (doEat) {
        alert("You can't eat/attack twice during one move");
        return;
    }
    move = "EAT_FOOD";
    document.getElementById("doing").innerText = "Feed ";// set firstAnimalId
}

function endMove() {
    move = "END_MOVE";
    document.getElementById("doing").innerText = "end move";
    let json = buildMessage();
    clearFields();
    send(json);
}

function endPhase() {
    move = "END_PHASE";
    document.getElementById("doing").innerText = "end " + document.getElementById("phase").innerText;
    let json = buildMessage();
    clearFields(); //clear fields after message is built!
    send(json);
}

function onMessage(message) {
    clearFields();
    let game = JSON.parse(message.body);
    if (game.hasOwnProperty("error")) {
        alert(game.error);
        document.getElementById("wrapper").style.pointerEvents = "auto";
        return;
    }

    //build always
    document.getElementById("phase").innerText = game.phase;
    document.getElementById("log").innerText += game.log;

    //build occasionally
    if (game.hasOwnProperty("players")) {
        let common = document.getElementById("common");

        for (let i = 0; i < game.players.length; i++) {
            let player = game.players[i];
            if (document.getElementById(player.name) == null) common.appendChild(buildPlayerBlock(player));
            let span = $('<span/>', {
                text: player.name + "  ",
            });
            if (player.leftGame == true) span.addClass('text-danger');
            else span.addClass('text-success');
            $("#players").append(span);
        }
    }

    if (game.hasOwnProperty("player")) {
        playerName = game.player;
    }
    if (game.hasOwnProperty("playerOnMove")) {
        $('#playerOnMove').text(game.playerOnMove);
    }

    if (game.hasOwnProperty("changedAnimal")) {
        for (let k = 0; k < game.changedAnimal.length; k++) {
            let animal = game.changedAnimal[k];
            let id = animal.id;
            let owner = animal.ownerName;
            let flag = false;
            if (owner == playerName) flag = true;
            let playerBlock = document.getElementById(owner);
            if (document.getElementById(id) == null) playerBlock.appendChild(buildAnimal(animal, flag));
            else
                playerBlock.replaceChild(buildAnimal(animal, flag), document.getElementById(id));
        }
    }

    if (game.hasOwnProperty("deleteAnimal")) {
        for (let k = 0; k < game.deleteAnimal.length; k++) {
            let id = game.deleteAnimal[k];
            document.getElementById(id).remove();
        }
    }

    if (game.hasOwnProperty("player")) {
        playerName = game.player;
        document.getElementById("player").innerText = playerName;
    }
    if (game.hasOwnProperty("id")) document.getElementById("gameId").innerText = game.id;
    if (game.hasOwnProperty("cards")) buildCards(game.cards);
    if (game.hasOwnProperty("newCards")) buildNewCards(game.newCards);
    if (game.hasOwnProperty("deletedCard")) deleteCard(game.deletedCard);

    if (game.phase == "FEED") {
        document.getElementById("End move").style.display = 'inline-block'; //show button
        document.getElementById("feedPanel").style.display = 'block'; //show panel
        setFood(game);
    } else if (game.phase == "EVOLUTION") {
        document.getElementById("movePanel").style.display = 'block'; //evolution phase
        document.getElementById("feedPanel").style.display = 'none'; //hide panel
        document.getElementById("End move").style.display = 'none'; //hide button
    }

    if (game.status == true) {
        document.getElementById("status").innerText = "It's your turn!";
        document.getElementById("wrapper").style.pointerEvents = "auto"; //clickable whole page
    } else {
        document.getElementById("status").innerText = "Please, wait...";
        document.getElementById("wrapper").style.pointerEvents = "none"; //disable whole page
    }

    if (game.hasOwnProperty("extraMessage")) {
        let type = game.extraMessage.type;

        if (type == "MIMICRY") {
            let message = game.extraMessage;
            if (message.playerOnAttack === playerName) wait();
            else if (message.playerUnderAttack === playerName) {
                alert("Animal #" + message.predator + " attack your animal #" + message.victim + " with mimicry property. Choose animal to redirect attack or click animal to die");
                mimicry = true;
                document.getElementById("common").style.pointerEvents = "none";
                document.getElementById(playerName).style.pointerEvents = 'auto';
                mimicryVictims = message.victims;
                document.getElementById("Make move").style.pointerEvents = "auto";
                document.getElementById("Clear").style.pointerEvents = 'auto';
            }
        } else if (type == "TAIL_LOSS") {
            let message = game.TAIL_LOSS;
            if (message.playerOnAttack === playerName) wait();
            else if (message.playerUnderAttack === playerName) {
                alert("Animal #" + message.predator + " attack your animal #" + message.victim + " with tail loss property. Choose property to loose or click animal to die");
                tailLoss = true;
                let animals = Array.from(document.getElementsByClassName("animal"));
                let victim_id = message.victims[0];
                let animal = animals.find(x => x.id == victim_id);
                animal.style.pointerEvents = "auto"; //clickable only animals
                document.getElementById("Make move").style.pointerEvents = "auto";
                document.getElementById("Clear").style.pointerEvents = 'auto';
            }
        }
    }

    if (game.hasOwnProperty("lastRound")) document.getElementById("last").style.display = "block";
    if (game.hasOwnProperty("winners")) {
        alert(game.winners); //show panel
        location.assign("/index");
    }
}

function makeMove() {
    if (document.getElementById("phase").innerText == "EVOLUTION") {
        if (move == null) {
            alert("You haven't made any move");
            return;
        }
    }
    else if (document.getElementById("phase").innerText == "FEED") {

        if (move == null) {
            if (secondAnimalId == null) {
                alert("You haven't made any move");
                return;
            }
            move = "ATTACK";
            if (doEat) {
                alert("You can't eat/attack twice during one move");
                return;
            }
        }
    }
    let json = buildMessage();
    clearFields();//clear fields after message is built!
    send(json);
}

function clearFields() {
    move = null;
    draggedProperty = null;
    firstAnimalId = null;
    secondAnimalId = null;
    playedCardId = null;
    tailLoss = false;
    $("#doing").empty();
    document.getElementById("wrapper").style.pointerEvents = "none";
    $("#players").empty();
}

function wait() {
    document.getElementById("status").innerText = "Please, wait for victim answer...";
    document.getElementById("wrapper").style.pointerEvents = "none"; //disable whole page
}
