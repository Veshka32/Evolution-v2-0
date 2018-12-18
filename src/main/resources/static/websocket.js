const tcp = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
const host = window.location.host;
const url = window.location.href;
const id = new URL(url).searchParams.get('id');
const socket = tcp + host+'/chat';
const stompClient = Stomp.client(socket);
function send() {
    stompClient.send("/app/chat/"+id,{},"test message");
}

function connect() {
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages/'+id, function(message) {
            console.log(message);
        });
    });
}

function disconnect() {
    if(stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}
