new Vue({
  el: '#userInventory',
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
      try {
        const response = await axios.get('/account/me/inventory?list=products');

        this.products = response.data;
      } catch (error) {
				console.log(error);
				this.showMessage('Ops, algo inesperado aconteceu', 'error');
      }
    },
    showMessage(msg, topic = 'success') {
      toastr[topic.toLowerCase()](msg);
    }
  }
});