const VueHeader = new Vue({
	el: '#appHeader',
	name: 'VueHeader',
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
      try {
        const response = await axios.get('/account/me/notifications');
        this.notifications = response.data;
      } catch (err) {
      	console.log(err.response.data);
			}
		},
		initializeConnection() {
			const wsNotify = new WebSocket(`ws://localhost:8080/notify/${this.username}`);
			wsNotify.onmessage = this.handleNotification;
			const wsPurchaseRequest = new WebSocket(`ws://localhost:8080/account/purchase_request/${this.username}`);
      wsPurchaseRequest.onmessage = this.handlePurchaseRequestCreation
		},
		handleNotification(event) {
			this.notifications = JSON.parse(event.data)
		},
    handlePurchaseRequestCreation(event) {
		  const purchaseRequest = JSON.parse(event.data);

			if (!purchaseRequest.id) {
				this.purchaseRequest = null;
			}

		  this.purchaseRequest = purchaseRequest;
    },
    getNotificationUrl(notification) {
			switch (notification.resourceType) {
				case 'PURCHASE_REQUEST':
					return `/account/purchase_request/suggest?pr=${notification.resourceId}`;
				default:
					return 'javascript:void(0)';
			}
		},
		getIconNotification(notification) {
			switch(notification.resourceType) {
				case 'PURCHASE_REQUEST':
					return 'fas fa-file-invoice-dollar';
				default:
					return null;
			}
		}
	}
});
