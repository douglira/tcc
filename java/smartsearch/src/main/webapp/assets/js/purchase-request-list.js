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
    showMessage(msg, type = 'success', options) {
      toastr.options = options;
      toastr[type.toLowerCase()](msg);
    },
    async loadData() {

    }
  },
});
