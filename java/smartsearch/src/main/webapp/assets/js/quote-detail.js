let formatterMixin = {
  methods: {
    formatDatetime: Formatter.datetime,
    formatFullDate: Formatter.fullDate,
    formatDate: Formatter.date,
    formatCurrency: Formatter.currency,
    getCalendar: Formatter.getCalendar,
  },
};

new Vue({
  el: '#userQuoteDetail',
  name: 'QuoteDetail',
  mixins: [formatterMixin],
  data() {
    return {
      quote: {},
      seller: {},
    }
  },
  created() {
    this.loadData();
  },
  methods: {
    async loadData() {
      const quoteId = new URLSearchParams(location.search).get('q');
      try {
        const { data: { quote, seller } } = await axios.get(`/account/quote/detail?q=${quoteId}`);
        this.quote = quote;
        this.seller = seller;
      } catch (err) {
        console.log(err.response);
        if (err.response) {
          this.showMessage(err.response.data.content, err.response.data.type);
        }
      }
    },
    showMessage(msg, type = 'success', options) {
      toastr.options = options;
      toastr[type.toLowerCase()](msg);
    },
    getQuotationBadgeClass(quotationItem) {
      const purchaseItem = this.quote.purchaseRequest.listProducts
        .find(purchaseItem => purchaseItem.product.title === quotationItem.product.title);

      if (purchaseItem.quantity < quotationItem.quantity) {
        return 'badge-warning';
      }

      if (purchaseItem.quantity > quotationItem.quantity) {
        return 'badge-danger';
      }

      return 'badge-light';
    },
    getDisplayQuoteStatus() {
      switch (this.quote.status) {
        case 'UNDER_REVIEW': {
          return 'Sob análise';
        }
        case 'ACCEPTED': {
          return 'Aprovado';
        }
        case 'DECLINED': {
          return 'Reprovado';
        }
        default: {
          return 'Expirado';
        }
      }
    }
  }
});