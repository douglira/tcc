const VueHeader = new Vue({
	el: '#appHeader',
	name: 'Header',
	data() {
		return {
			purchaseRequest: null,
      notifications: [],
      username: '',
			wsNotify: null,
      wsPurchaseRequest: null,
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
	computed: {
		pendingNotificationsCount() {
      return this.notifications.filter(n => n.status === 'PENDING').length
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
			this.wsNotify = new WebSocket(`ws://localhost:8080/notify/${this.username}`);
			this.wsNotify.onmessage = this.handleNotification;

			this.wsPurchaseRequest = new WebSocket(`ws://localhost:8080/account/purchase_request/${this.username}`);
      this.wsPurchaseRequest.onmessage = this.handlePurchaseRequestCreation
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
				case 'QUOTE':
					return `/account/purchase_request/quote?q=${notification.resourceId}`;
				default:
					return 'javascript:void(0)';
			}
		},
		getIconNotification(notification) {
			switch(notification.resourceType) {
				case 'PURCHASE_REQUEST':
					return 'fas fa-file-invoice-dollar';
				case 'QUOTE':
					return 'fas fa-hand-holding-usd';
				default:
					return null;
			}
		},
    onClickNotification(event, notification) {
			event.preventDefault();
			const href = $(event.target).closest('a').get(0).href;

      if (notification.status !== 'PENDING') {
        window.location.replace(href);
        return;
			}

			this.wsNotify.send(JSON.stringify([notification]));

			window.location.replace(href);
		}
	},
});
