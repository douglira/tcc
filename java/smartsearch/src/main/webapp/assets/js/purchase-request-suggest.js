const FormatterMixin = {
  methods: {
    formatFullDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
    formatDate: Formatter.date,
    formatDatetime: Formatter.datetime,
    getDate: Formatter.getDate,
  }
};

const VueComponent = new Vue({
  el: '#userPRSuggest',
  name: 'PurchaseRequestSuggest',
  mixins: [FormatterMixin],
  data() {
    return {
      loggedSeller: null,
      purchaseRequest: null,
      page: 1,
      perPage: 5,
      countdown: '',
      searchProduct: '',
      selectedProducts: [],
      inputProductQuantity: null,
      productsInventory: [],
      productsQuote: [],
      quoteDiscount: null,
      quoteTotalAmount: null,
      quoteAdditionalData: '',
    };
  },
  async created() {
    await this.loadData();
    this.initCountdown();
    $(() => {
      $('.list-item-row_tooltip').tooltip({
        placement: 'top',
        trigger: 'hover',
        title: 'Exibir',
      });

      $('.list-item-inventory-product').on('click', () => $('#inputQuantity').focus());
    })
  },
  watch: {
    async searchProduct() {
      if (!this.searchProduct) {
        this.page = 1;
        this.perPage = 5;
        const response = await this.getProductsInventory({ page: this.page, perPage: this.perPage });
        this.productsInventory = response.data;
      }
    }
  },
  methods: {
    onClickPushQuotation() {
      if (!this.productsQuote.length) return;

      const quote = {
        purchaseRequest: {
          id: this.purchaseRequest.id,
        },
        customListProduct: this.productsQuote,
        quoteAdditionalData: this.quoteAdditionalData,
        discount: this.quoteDiscount,
      }

      $.post(
        '/account/purchase_request/suggest',
        {
          quote: JSON.stringify(quote),
        },
      )
        .done((res) => {
          const msg = JSON.parse(res);

          if (msg.content) {
            this.showMessage(msg.content, msg.type);
            this.productsQuote = [];
            this.quoteDiscount = null;
            this.quoteAdditionalData = '';
            this.updateQuoteTotalAmount();
          }
        })
        .fail((res) => {
          const msg = JSON.parse(res.responseText);

          if (msg.content) {
            this.showMessage(msg.content, msg.type);
          }
        })
    },
    async onClickSearchProduct() {
      this.page = 1;
      this.perPage = 5;
      if (!this.searchProduct) return;

      const { searchProduct: searchTitle } = this;

      const responseSearchProducts = await this.getProductsInventory({ searchTitle });

      this.productsInventory = responseSearchProducts.data;
    },
    onClickAddProduct() {
      if (!this.selectedProducts.length) return;
      let isValid = true;

      this.selectedProducts.forEach(prod => {
        const badgeElement = $(`#listItemProduct${prod.id} .badge`);

        if (prod.availableQuantity < this.inputProductQuantity) {
          isValid = false;
          badgeElement.removeClass('badge-secondary').addClass('badge-danger');
          return;
        }

        badgeElement.removeClass('badge-danger').addClass('badge-secondary');
      });

      if (!isValid) return;

      $('.list-item-inventory-product .badge').removeClass('badge-danger').addClass('badge-secondary');

      this.productsQuote = this.selectedProducts.reduce((quoteList, selectedProd) => {
        const productInventoryIndex = this.productsInventory.findIndex(p => p.id === selectedProd.id);
        const productInventory = this.productsInventory[productInventoryIndex];
        productInventory.availableQuantity -= this.inputProductQuantity;
        this.productsInventory.splice(productInventoryIndex, 1, productInventory);

        const quoteProductIndex = quoteList.findIndex(productList => productList.product.id === selectedProd.id);

        if (quoteProductIndex !== -1) {
          const quoteProduct = quoteList[quoteProductIndex];
          quoteProduct.quantity = Number.parseInt(quoteProduct.quantity, 10) + Number.parseInt(this.inputProductQuantity, 10);
          quoteList.splice(quoteProductIndex, 1, quoteProduct);
          return quoteList;
        }

        quoteList.push({
          product: {
            id: selectedProd.id,
            title: selectedProd.title,
            basePrice: selectedProd.basePrice,
          },
          quantity: this.inputProductQuantity,
        });
        return quoteList;
      }, this.productsQuote);

      this.inputProductQuantity = null;
      this.selectedProducts = [];
      this.updateQuoteTotalAmount();
    },
    onClickRemoveProject(prodList) {
      const isSelectedIndex = this.productsQuote.findIndex(productList => productList.product.id === prodList.product.id);

      if (isSelectedIndex !== -1) {
        const [removedProductList] = this.productsQuote.splice(isSelectedIndex, 1);

        const productInventoryIndex = this.productsInventory.findIndex(p => p.id === removedProductList.product.id);
        const productInventory = this.productsInventory[productInventoryIndex];
        productInventory.availableQuantity = Number.parseInt(productInventory.availableQuantity, 10) + Number.parseInt(removedProductList.quantity, 10);
        this.productsInventory.splice(productInventoryIndex, 1, productInventory);
      }
      this.updateQuoteTotalAmount();
    },
    getTotalAmount() {
      if (this.quoteDiscount) {
        const discount = 1 - (this.quoteDiscount / 100);
        return this.quoteTotalAmount * discount;
      }

      return this.quoteTotalAmount;
    },
    updateQuoteTotalAmount() {
      if (this.productsQuote.length) {
        this.quoteTotalAmount = this.productsQuote.reduce((totalAmount, productList)=> {
          return totalAmount + (productList.product.basePrice * productList.quantity);
        }, 0);
        return;
      }

      this.quoteTotalAmount = null;
    },
    async onClickLoadMoreProducts() {
      this.page = this.page + 1;
      const responseProducts = await this.getProductsInventory({ page: this.page, perPage: this.perPage });

      this.productsInventory = [...this.productsInventory, ...responseProducts.data];
    },
    showMessage(msg, type = 'success') {
      toastr[type.toLowerCase()](msg);
    },
    fixListProductTitle(productTitle) {
      const blankSpaceIndex = productTitle.indexOf(' ', 25);

      if (blankSpaceIndex === -1) {
        return productTitle;
      }

      return productTitle.substr(0, blankSpaceIndex) + '...';
    },
    initCountdown() {
      const countdownDate = (this.getDate(this.purchaseRequest.dueDateAverage)).getTime();
      const countdownInterval = setInterval(() => {
        const now = new Date().getTime();
        const distance = countdownDate - now;

        const days = Math.floor(distance / (1000 * 60 * 60 * 24));
        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

        if (distance < 0) {
          clearInterval(countdownInterval);
          this.countdown = '';
        }

        this.countdown = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      });
    },
    async getProductsInventory({ page, perPage, searchTitle }) {
      return axios.get(`/account/products?page=${page ? page : ''}&perPage=${perPage ? perPage : ''}&search=${searchTitle ? searchTitle : ''}`);
    },
    async loadData() {
      const { page, perPage } = this;
      const purchaseRequestId = window.location.search.split('?pr=')[1];

      const [responseSeller, responsePurchaseRequest, responseProducts] = await Promise.all([
        axios.get('/account/me/data'),
        axios.get(`/account/purchase_request/suggest?pr=${purchaseRequestId || ''}`),
        this.getProductsInventory({ page, perPage }),
      ]);

      if (responsePurchaseRequest.data.cause && responsePurchaseRequest.data.cause === 'INVALID_PURCHASE_REQUEST_ID') {
        return this.showMessage(responsePurchaseRequest.data.content, responsePurchaseRequest.data.type);
      }

      this.purchaseRequest = responsePurchaseRequest.data;
      this.productsInventory = responseProducts.data;
      this.loggedSeller = responseSeller.data;
      this.initWebsocket();
    },
    initWebsocket() {
      const wsQuotes = new WebSocket(`ws://localhost:8080/account/seller/${VueHeader.$data.username}/purchase_request/${this.purchaseRequest.id}/quotes`);
      wsQuotes.onmessage = this.handleQuotesUpdates
    },
    handleQuotesUpdates(event) {
      this.purchaseRequest.quotes = JSON.parse(event.data);
    },
    inputQuantityEnter(event) {
      event.keyCode === 13 && this.onClickAddProduct();
    },
  },
});
