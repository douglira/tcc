new Vue({
	el: '#appHeader',
	data() {
		return {
			username: '',
		}
	},
	created() {
		const username = document.getElementById('inputHeaderUsername').value;
		this.username = username;
		this.initializeConnection();
		
	},
	computed: {
		allNotifications() {
			return null;
		}
	},
	methods: {
		loadNotifications() {
			
		},
		initializeConnection() {
			const ws = new WebSocket(`ws://localhost:8080/notify/${this.username}`);
			ws.onmessage = this.handleNotification;
		},
		handleNotification(event) {
			console.log('Receiving notification!');
			
			const notification = JSON.parse(event.data);
			console.log(notification);
		}
	}
})
