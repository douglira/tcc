let formatterMixin = {
  methods: {
    formatDatetime: Formatter.datetime,
    formatFullDate: Formatter.fullDate,
    formatDate: Formatter.date,
    formatCurrency: Formatter.currency,
    formatTel: Formatter.telephone,
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
      selectedShipment: null,
      quoteReason: '',
    }
  },
  created() {
    this.loadData();
  },
  updated() {
    if (this.quote.id && this.quote.shipmentOptions.length === 1 && this.quote.status === 'UNDER_REVIEW') {
      try {
        const shipmentEl = document.getElementsByClassName('shipment-options').item(0);
        const shipment = this.quote.shipmentOptions[0];

        this.selectedShipment = shipment.id;
        shipmentEl.classList.remove('text-muted');
        shipmentEl.classList.add('bg-info');
        shipmentEl.classList.add('text-white');
      } catch(err) {

      }
    }
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
    async onClickAccept() {
      const quoteId = this.quote.id;
      const shipmentId = this.selectedShipment;

      if (!shipmentId) {
        return this.showMessage('Escolha uma opção de frete', 'WARNING');
      }

      try {
        const { data } = await axios.post(
          '/account/quote/accept',
          Qs.stringify({ quoteId, shipmentId }),
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }
        );

        this.selectedShipment = null;
        this.quote.status = 'ACCEPTED';
        const { content, type } = data;
        this.showToast(content, type);
      } catch (err) {
        if (err.response.data.cause === 'UNAVAILABLE_QUANTITY') {
          this.selectedShipment = null;
          this.quote.status = 'DECLINED';
        }
        this.showMessage(err.response.data.content, err.response.data.type);
      }
    },
    async onClickRefuse() {
      const quoteId = this.quote.id;
      const quoteReason = this.quoteReason;

      if (!String(quoteReason).trim()) {
        return this.showMessage('Por favor descreva o motivo', 'WARNING');
      }

      try {
        const { data } = await axios.post(
          '/account/quote/refuse',
          Qs.stringify({ quoteId, quoteReason }),
          {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded'
            }
          }
        );

        this.quoteReason = '';
        this.selectedShipment = null;
        this.quote.reason = quoteReason;
        this.quote.status = 'DECLINED';
        const { content, type } = data;
        this.showToast(content, type);
      } catch (err) {
        this.showMessage(err.response.data.content, err.response.data.type);
      }
    },
    onSelectShipment(shipmentIndex, shipmentId) {
      if (this.quote.status !== 'UNDER_REVIEW') {
        return null;
      }

      this.selectedShipment = shipmentId;
      this.quote.shipmentOptions.forEach((shipment, index) => {
        const el = document.getElementsByClassName('shipment-options').item(index);
        if (index !== shipmentIndex) {
          if (el.classList.contains('bg-info')) {
            el.classList.remove('bg-info');
            el.classList.add('text-muted');
          }
        } else {
          el.classList.remove('text-muted');
          el.classList.add('bg-info');
          el.classList.add('text-white');
        }
      });
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
    },
    getShipmentMethod(shipmentMethod) {
      switch (shipmentMethod) {
        case 'FREE': {
          return 'Grátis';
        }
        case 'CUSTOM': {
          return 'Normal';
        }
        default: {
          return 'Retirada no local';
        }
      }
    },
  }
});