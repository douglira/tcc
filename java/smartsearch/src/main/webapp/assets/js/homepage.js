let formatterMixin = {
  methods: {
    formatDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
    formatDecimal: Formatter.decimal,
  },
};

new Vue({
  el: '#homepageContainer',
  name: 'Homepage',
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
        '/account/purchase_request/new',
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

          if (status === 400 && msg.cause === 'ERROR_MISSING_ADDRESS') {
            this.showMessage(msg.content, msg.type);
          }
        })
    },
    showMessage(msg, topic = 'success') {
      toastr[topic.toLowerCase()](msg);
    },
    async loadData() {
      try {
        const response = await axios.get('/product_items/homepage');
        this.products = response.data;
      } catch (err) {
        if (response.status !== 200) {
          this.showMessage(response.data.content, response.data.topic);
        }
      }
    },
  },
});
