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
      quoteExpirationDate: '',
      quoteDiscount: null,
      quoteTotalAmount: null,
      quoteAdditionalData: '',
      quoteShipmentOptions: [],
      invalidQuote: true,
      shipmentOptionsSelect: [
        {text: 'Customizado', value: 'CUSTOM'},
        {text: 'Frete grátis', value: 'FREE'},
        {text: 'Retirada no local', value: 'LOCAL_PICK_UP'},
      ],
      selectedShipmentOption: null,
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
        const response = await this.getProductsInventory({page: this.page, perPage: this.perPage});
        this.productsInventory = response.data;
      }
    },
    quoteExpirationDate() {
      if (!this.quoteExpirationDate) {
        this.invalidQuote = true;
        return;
      }
      const inputExpiration = $('#expirationDate');

      const now = moment(new Date());
      const prDueDate = moment(this.getDate(this.purchaseRequest.dueDate));
      const quoteExpiration = moment(new Date(this.quoteExpirationDate));

      const isValid = quoteExpiration.isBetween(now, prDueDate);
      if (isValid) {
        if (inputExpiration.hasClass('border border-danger')) {
          inputExpiration.removeClass('border border-danger');
        }
        this.invalidQuote = false;
        return;
      }

      this.invalidQuote = true;
      this.showMessage('Data de expiração inválida', 'error', { preventDuplicates: true });
      inputExpiration.addClass('border border-danger');
    },
  },
  methods: {
    onClickPushQuotation() {
      if (!this.productsQuote.length) {
        this.showMessage('Nenhum item selecionado', 'error', { preventDuplicates: true });
        return
      };

      if (!this.quoteShipmentOptions.length) {
        this.showMessage('Adicione ao menos um método de envio', 'error', { preventDuplicates: true });
        return;
      }

      const quote = {
        purchaseRequest: {
          id: this.purchaseRequest.id,
        },
        customListProduct: this.productsQuote,
        quoteAdditionalData: this.quoteAdditionalData,
        discount: this.quoteDiscount,
        expirationDate: this.getCalendar(new Date(this.quoteExpirationDate)),
        shipmentOptions: this.quoteShipmentOptions,
      };

      $.post(
        '/account/quote/new',
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
            this.quoteExpirationDate = '';
            this.quoteShipmentOptions = [];
            this.invalidQuote = true;
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

      const {searchProduct: searchTitle} = this;

      const responseSearchProducts = await this.getProductsInventory({searchTitle});

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
    onChangeShippingOption(event) {
      const shipmentOptionValue = event.target.value;

      if (!shipmentOptionValue) return;

      if (
        this.quoteShipmentOptions.map(ship => ship.method)
          .includes(shipmentOptionValue)
      ) {
        return;
      }

      const shipmentOption = this.shipmentOptionsSelect.find(ship => ship.value === shipmentOptionValue);

      if (!shipmentOption) return;

      this.selectedShipmentOption = shipmentOption.value;

      switch(shipmentOption.value) {
        case 'FREE': {
          const customIndex = this.quoteShipmentOptions.findIndex(ship => ship.method === 'CUSTOM');

          if (customIndex !== -1) {
            this.quoteShipmentOptions.splice(customIndex, 1);
          }

          $('#shipmentOptionsModal .modal-title').text(shipmentOption.text);
          $('#shipmentOptionsModal').modal('toggle');
          break;
        }
        case 'LOCAL_PICK_UP': {
          this.quoteShipmentOptions.push({
            method: shipmentOption.value,
            cost: 0,
          });
          break;
        }
        case 'CUSTOM': {
          const freeIndex = this.quoteShipmentOptions.findIndex(ship => ship.method === 'FREE');

          if (freeIndex !== -1) {
            this.quoteShipmentOptions.splice(freeIndex, 1);
          }

          $('#shipmentOptionsModal .modal-title').text(shipmentOption.text);
          $('#shipmentOptionsModal').modal('toggle');
          break;
        }
        default:
          throw new Error('Invalid shipment option selected.');
      }
    },
    onClickAddShipmentOption() {
      const shipmentOptionMethod = this.selectedShipmentOption;
      const shipmentEstimatedDate = $('#shipmentEstimatedTime').val();
      const shipmentCost = $('#shipmentCost').val();

      if (shipmentOptionMethod === 'FREE' && shipmentEstimatedDate) {
        this.quoteShipmentOptions.push({
          method: shipmentOptionMethod,
          estimatedTime: this.getCalendar(new Date(shipmentEstimatedDate)),
          cost: 0,
        });
      }

      if (shipmentOptionMethod === 'CUSTOM' && shipmentEstimatedDate && shipmentCost) {
        this.quoteShipmentOptions.push({
          method: shipmentOptionMethod,
          estimatedTime: this.getCalendar(new Date(shipmentEstimatedDate)),
          cost: shipmentCost,
        });
      }
    },
    onClickRemoveShipmentOption(shipmentOptionIndex) {
      this.quoteShipmentOptions.splice(shipmentOptionIndex, 1);
    },
    getTotalAmount() {
      if (this.quoteDiscount) {
        const discount = 1 - (this.quoteDiscount / 100);
        return this.quoteTotalAmount * discount;
      }

      return this.quoteTotalAmount;
    },
    getDisplayQuoteStatus(status) {
      switch (status) {
        case 'UNDER_REVIEW':
          return 'Em análise';
        case 'ACCEPTED':
          return 'Aceito';
        case 'DECLINED':
          return 'Negado';
        case 'EXPIRED':
          return 'Expirado';
        default:
          throw new Error('Unknown quote status condition.');
      }
    },
    updateQuoteTotalAmount() {
      if (this.productsQuote.length) {
        this.quoteTotalAmount = this.productsQuote.reduce((totalAmount, productList) => {
          return totalAmount + (productList.product.basePrice * productList.quantity);
        }, 0);
        return;
      }

      this.quoteTotalAmount = null;
    },
    async onClickLoadMoreProducts() {
      this.page = this.page + 1;
      const responseProducts = await this.getProductsInventory({page: this.page, perPage: this.perPage});

      this.productsInventory = [...this.productsInventory, ...responseProducts.data];
    },
    showMessage(msg, type = 'success', options) {
      toastr.options = options;
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
      const countdownDate = (this.getDate(this.purchaseRequest.dueDate)).getTime();
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
          return;
        }

        this.countdown = `${days}d ${hours}h ${minutes}m ${seconds}s`;
      });
    },
    async getProductsInventory({page, perPage, searchTitle}) {
      return axios.get(`/account/products?page=${page ? page : ''}&perPage=${perPage ? perPage : ''}&search=${searchTitle ? searchTitle : ''}`);
    },
    async loadData(purchaseRequestId) {
      const {page, perPage} = this;
      purchaseRequestId = window.location.search.split('?pr=')[1];

      const [responseSeller, responsePurchaseRequest, responseProducts] = await Promise.all([
        axios.get('/account/me/data'),
        axios.get(`/account/purchase_request/suggest?pr=${purchaseRequestId || ''}`),
        this.getProductsInventory({page, perPage}),
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
      const wsQuotes = new WebSocket(`ws://localhost:8080/account/purchase_request/${this.purchaseRequest.id}/quotes`);
      wsQuotes.onmessage = this.handleQuotesUpdates
    },
    async handleQuotesUpdates(event) {
      const purchaseRequest = JSON.parse(event.data);
      await this.loadData(purchaseRequest.id);
    },
    inputQuantityEnter(event) {
      event.keyCode === 13 && this.onClickAddProduct();
    },
  },
});
