let formatterMixin = {
  methods: {
    formatDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
    formatDecimal: Formatter.decimal,
  },
};

new Vue({
  el: '#homepageContainer',
  mixins: [formatterMixin],
  data() {
    return {
      products: [],
    };
  },
  created() {
    this.loadData();
  },
  methods: {
    onClickAddToPR(productItem) {
      $.post(
        '/purchase_request/new',
        {
          productItemId: productItem.id,
          productItemQuantity: 1,
        },
      )
        .fail(({status, responseText}) => {
          const msg = JSON.parse(responseText);
          if (status === 401 && msg.cause === 'WARNING_MISSING_LOGGED_USER') {
            // console.log(msg);
            window.location.replace('/signin');
          }

          if (status === 400 && msg.cause === 'PRODUCT_AVAILABLE') {
            this.showMessage(msg.content, msg.type);
          }
        })
    },
    showMessage(msg, topic = 'success') {
      toastr[topic.toLowerCase()](msg);
    },
    async loadData() {
      try {
        const response = await axios.get('/products/homepage');
        this.products = response.data;
      } catch (err) {
        if (response.status !== 200) {
          this.showMessage(response.data.content, response.data.topic);
        }
      }
    },
  },
});
