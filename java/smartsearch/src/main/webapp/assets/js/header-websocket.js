$(document).ready(function() {
	const username = $('#inputHeaderUsername').val();

	const ws = new WebSocket("ws://localhost:8080/notify/" + username);

	ws.onopen = function() {
		console.log('connected');
	}

	ws.onmessage = onmessage;
})

function onmessage(event) {
	console.log('Receiving notification');

	const notification = JSON.parse(event.data);
	console.log(notification);
}
