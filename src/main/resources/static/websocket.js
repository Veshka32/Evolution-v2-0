const tcp = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
const host = window.location.host;
//const id = new URL(window.location.href).searchParams.get('id');
const id = getId();
const socket = tcp + host+'/chat';
const stompClient = Stomp.client(socket);
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/user/queue/messages', function (message) {
        document.getElementById("message").innerText = JSON.parse(message.body);
        console.log(message);
    });
});

function send() {
    stompClient.send("/app/chat/"+id,{},"test message");
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function getId() {
    let match = document.cookie.match(new RegExp('gameId' + '=([^;]+)'));
    if (match) return match[1];
}
