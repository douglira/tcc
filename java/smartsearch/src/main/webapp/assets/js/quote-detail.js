new Vue({
  el: '#userQuoteDetail',
  data() {
    return {

    }
  },
  created() {
    this.loadData();
  },
  methods: {
    async loadData() {
      const quoteId = new URLSearchParams(location.search).get('q');
      const response = await axios.get(`/account/quote/get?id=${quoteId}`);
    }
  }
});