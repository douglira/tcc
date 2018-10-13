const VueHeader = new Vue({
	el: '#appHeader',
	data() {
		return {
			purchaseRequest: null,
      notifications: [],
			username: '',
		}
	},
	async created() {
    const username = document.getElementById('inputHeaderUsername').value;
    if (username) {
      this.username = username;
      await this.loadNotifications();
      this.initializeConnection();
      await this.loadPurchaseRequest();
    }
	},
	methods: {
		async loadPurchaseRequest() {
				try {
          const response = await axios.get('/account/purchase_request/edit');
          if (response.status === 200) {
            this.purchaseRequest = response.data;
          }
        } catch (err) {
					console.log('loadPurchaseRequest ERROR: ' + err);
				}
		},
		async loadNotifications() {
			const response = await axios.get('/account/me/notifications');
			console.log(response);
			if (response.status === 200) {
				this.notifications = response.data;
			} else if (response.status === 503) {
				console.log(response.data);
			}
		},
		initializeConnection() {
			const wsNotify = new WebSocket(`ws://localhost:8080/notify/${this.username}`);
			wsNotify.onmessage = this.handleNotification;
			const wsPurchaseRequest = new WebSocket(`ws://localhost:8080/account/purchase_request/${this.username}`);
      wsPurchaseRequest.onmessage = this.handlePurchaseRequestCreation
		},
		handleNotification(event) {
			console.log(JSON.parse(event.data));
		},
    handlePurchaseRequestCreation(event) {
		  const purchaseRequest = JSON.parse(event.data);

			if (!purchaseRequest.id) {
				this.purchaseRequest = null;
			}

		  this.purchaseRequest = purchaseRequest;
    }
	}
});
