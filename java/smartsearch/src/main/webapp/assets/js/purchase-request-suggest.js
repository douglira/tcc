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
      page: 1,
      perPage: 15,
      countdown: '',
      selectedProducts: [],
      inputProductQuantity: null,
      productsInventory: [],
      productsQuote: [],
      purchaseRequest: null,
      quoteCustomCost: false,
      quoteAdditionalData: '',
    };
  },
  watch: {
    productsQuote(oldValues, newValues) {
      console.log(newValues);
    }
  },
  async created() {
    await this.loadData();
    this.initCountdown();
    $(function() {
      $('.list-item-row_tooltip').tooltip({
        placement: 'top',
        trigger: 'hover',
        title: 'Exibir',
      })
    })
  },
  methods: {
    onClickPushQuotation() {

    },
    onSelectAddProduct(product) {
      const isSelected = this.productsQuote.find(prod => prod.id === product.id);

      if (!isSelected) {
        this.productsQuote.push(product);
      }
    },
    onSelectRemoveProject(product) {
      const isSelectedIndex = this.productsQuote.findIndex(prod => prod.id === product.id);

      if (isSelectedIndex !== -1) {
        this.productsQuote.splice(isSelectedIndex, 1);
      }
    },
    showMessage(msg, type = 'success') {
      toastr[type.toLowerCase()](msg);
    },
    async getProductsInventory() {
      const { page, perPage } = this;
      return axios.get(`/account/products?page=${page}&perPage=${perPage}`);
    },
    async loadData() {
      const purchaseRequestId = window.location.search.split('?pr=')[1];

      const [responsePurchaseRequest, responseProducts] = await Promise.all([
        axios.get(`/account/purchase_request/suggest?pr=${purchaseRequestId || ''}`),
        this.getProductsInventory(),
      ]);

      if (responsePurchaseRequest.data.cause && responsePurchaseRequest.data.cause === 'INVALID_PURCHASE_REQUEST_ID') {
        return this.showMessage(responsePurchaseRequest.data.content, responsePurchaseRequest.data.type);
      }

      this.purchaseRequest = responsePurchaseRequest.data;
      this.productsInventory = responseProducts.data;
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
          this.countdown = '';
        }

        this.countdown = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      });
    }
  }
});
