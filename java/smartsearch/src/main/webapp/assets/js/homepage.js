new Vue({
  el: '#homepageContainer',
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
          actionCreation: 'single',
          productItemId: productItem.id,
          productItemQuantity: 1,
        }
      )
    },
    showMessage(msg, topic = 'success') {
      toastr[topic.toLowerCase()](msg);
    },
    async loadData() {
      const response = await axios.get('/products/homepage');
      if (response.status !== 200) {
        this.showMessage(response.data.content, response.data.topic);
      }

      this.products = response.data;
    },
  },
});
