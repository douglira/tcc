const FormatterMixin = {
  methods: {
    formatFullDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
    getDate: Formatter.getDate,
  }
};

const VueComponent = new Vue({
  el: '#userPRSuggest',
  mixins: [FormatterMixin],
  data() {
    return {
      purchaseRequest: null,
      countdown: '',
    };
  },
  async created() {
    await this.loadData();
    this.initCountdown()
  },
  methods: {
    showMessage(msg, type = 'success') {
      toastr[type.toLowerCase()](msg);
    },
    async loadData() {
      const purchaseRequestId = window.location.search.split('?pr=')[1];
      const response = await axios.get(`/account/purchase_request/suggest?pr=${purchaseRequestId || ''}`);

      if (response.data.cause && response.data.cause === 'INVALID_PURCHASE_REQUEST_ID') {
        return this.showMessage(response.data.content, response.data.type);
      }

      this.purchaseRequest = response.data;
    },
    initCountdown() {
      const countdownDate = (this.getDate(this.purchaseRequest.dueDateAverage)).getTime();
      const countdownInterval = setInterval(() => {
        const now = new Date().getTime();
        const distance = countdownDate - now;

        const days = Math.floor(distance / (1000 * 60 * 60 * 24));
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        if (distance < 0) {
          clearInterval(countdownInterval);
          this.countdown = 'EXPIRADO!';
        }

        this.countdown = `Expira em: ${days}d ${hours}h ${minutes}m ${seconds}s`;
      });
    }
  }
});
