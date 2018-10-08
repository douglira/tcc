new Vue({
	el: '#appHeader',
	data() {
		return {
			purchaseRequest: null,
			username: '',
		}
	},
	async created() {
    const username = document.getElementById('inputHeaderUsername').value;
    if (username) {
      this.username = username;
      this.initializeConnection();
      await this.loadPurchaseRequest();
    }
	},
	computed: {
		allNotifications() {
			return null;
		}
	},
	methods: {
		async loadPurchaseRequest() {
				try {
          const response = await axios.get('/purchase_request/new');
          if (response.status === 200) {
            this.purchaseRequest = response.data;
          }
        } catch (err) {
					console.log('loadPurchaseRequest ERROR: ' + err);
				}
		},
		loadNotifications() {
			
		},
		initializeConnection() {
			const wsNotify = new WebSocket(`ws://localhost:8080/notify/${this.username}`);
			wsNotify.onmessage = this.handleNotification;
			const wsPurchaseRequest = new WebSocket(`ws://localhost:8080/purchase_request/creation/${this.username}`)
      wsPurchaseRequest.onmessage = this.handlePurchaseRequestCreation
		},
		handleNotification(event) {
			console.log('Receiving notification!');
			
			const notification = JSON.parse(event.data);
			console.log(notification);
		},
    handlePurchaseRequestCreation(event) {
		  const payload = JSON.parse(event.data);

		  this.purchaseRequest = payload.purchaseRequest;
    }
	}
});
