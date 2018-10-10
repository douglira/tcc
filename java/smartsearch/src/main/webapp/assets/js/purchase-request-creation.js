const VueComponent = new Vue({
  el: '#userPRCreation',
  data() {
    return {
      username: null,
      purchaseRequest: null,
      modalData: {
        productItemId: null,
        productItemTitle: '',
        quantity: null,
        additionalSpec: null,
      },
    }
  },
  async created() {
    const username = document.getElementById('inputHeaderUsername').value;
    this.username = username;
    await this.loadData();
    this.wsPurchaseRequestUpdate();
  },
  methods: {
    onClickEditProduct(productList) {
      this.modalData = {
        productItemId: productList.product.id,
        productItemTitle: productList.product.title,
        quantity: productList.quantity,
        additionalSpec: productList.additionalSpec,
      };
      $('#modalProductItemEdit').modal('show');
    },
    saveEditProduct() {
      const payload = {
        quantity: this.modalData.quantity,
        additionalSpec: this.modalData.additionalSpec
      };

      $.post(
        '/account/purchase_request/edit',
        {
          purchaseRequestId: this.purchaseRequest.id,
          productItemId: this.modalData.productItemId,
          productList: JSON.stringify(payload),
        }
      )
        .done(res => {
          console.log(res);
        })
        .always(() => {
          $('#modalProductItemEdit').modal('hide');
        })
    },
    wsPurchaseRequestUpdate() {
      const wsPurchaseRequest = new WebSocket(`ws://localhost:8080/purchase_request/creation/${this.username}`);
      wsPurchaseRequest.onmessage = (event) => {
        const payload = JSON.parse(event.data);

        this.purchaseRequest = payload.purchaseRequest;
      }
    },
    async loadData() {
      const response = await axios.get('/purchase_request/new');
      this.purchaseRequest = response.data;
    },
  },
});

$('#modalProductItemEdit').on('hidden.bs.modal', () => {
  VueComponent.$root.modalData = {
    productItemId: null,
    productItemTitle: '',
    quantity: null,
    additionalSpec: null,
  };
});