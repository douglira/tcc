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
    async loadData() {
      const response = await axios.get('/products/homepage');
      if (response.status !== 200) {
        this.showMessage(response.data.content, response.data.topic);
      }

      this.products = response.data;
    },
    showMessage(msg, topic = 'success') {
      toastr[topic.toLowerCase()](msg);
    },
  },
});
