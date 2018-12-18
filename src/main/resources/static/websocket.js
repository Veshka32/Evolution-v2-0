const tcp = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
const host = window.location.host;
const path = window.location.pathname.substring(0, window.location.pathname.lastIndexOf(".")); //url without .html
const id=1;
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
