const FormatterMixin = {
  methods: {
    formatFullDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
    formatDate: Formatter.date,
    formatDatetime: Formatter.datetime,
    getDate: Formatter.getDate,
    getCalendar: Formatter.getCalendar,
  }
};

const VueComponent = new Vue({
  el: '#userPRList',
  name: 'PurchaseRequestList',
  mixins: [FormatterMixin],
  data() {
    return {
      purchaseRequests: [],
    };
  },
  async created() {
    await this.loadData();
  },
  methods: {
    getPRStage(stage){
      switch (stage) {
        case 'UNDER_QUOTATION':
          return 'Sob cotações';
        case 'CREATION':
          return 'Em criação';
        case 'CLOSED':
          return 'Encerrado';
        case 'EXPIRED':
          return 'Expirado';
        case 'CANCELED':
            return 'Cancelado';
        default:
          throw new Error('Unknown purchase request stage condition.');
      }
    },
    showMessage(msg, type = 'success', options) {
      toastr.options = options;
      toastr[type.toLowerCase()](msg);
    },
    async loadData() {
      try {
        const response = await axios.get('/account/purchase_request/all');
        this.purchaseRequests = response.data;
      } catch (err) {
        const error = JSON.parse(err);
        this.showMessage(error.content, error.type);
      }
    }
  },
});
