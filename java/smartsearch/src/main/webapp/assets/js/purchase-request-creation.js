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
  mounted() {
    $('#propagationPopover').popover({
      trigger: 'focus'
    });
  },
  computed: {
    dueDateAverage() {
      return Formatter.getDate(this.purchaseRequest.dueDateAverage)
    }
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
    onClickRemoveProduct(productList) {
      this.modalData = {
        productItemId: productList.product.id,
        productItemTitle: productList.product.title,
      };
      $('#modalProductItemRemove').modal('show');
    },
    confirmRemove(productList) {
      $.post(
        '/account/purchase_request/edit?action=remove',
        {
          purchaseRequestId: this.purchaseRequest.id,
          productItemId: this.modalData.productItemId,
        }
      )
        .always(() => {
          $('#modalProductItemRemove').modal('hide');
        })
    },
    saveEditProduct() {
      const payload = {
        quantity: this.modalData.quantity,
        additionalSpec: this.modalData.additionalSpec
      };

      $.post(
        '/account/purchase_request/edit?action=update',
        {
          purchaseRequestId: this.purchaseRequest.id,
          productItemId: this.modalData.productItemId,
          productList: JSON.stringify(payload),
        }
      )
        .always(() => {
          $('#modalProductItemEdit').modal('hide');
        })
    },
    wsPurchaseRequestUpdate() {
      const wsPurchaseRequest = new WebSocket(`ws://localhost:8080/account/purchase_request/${this.username}`);
      wsPurchaseRequest.onmessage = (event) => {
        const purchaseRequest = JSON.parse(event.data);

        if (purchaseRequest.id && purchaseRequest.stage === 'CREATION') {
          this.purchaseRequest = purchaseRequest;
        } else {
          window.location.replace('/');
          this.purchaseRequest = null;
        }
      }
    },
    async loadData() {
      const response = await axios.get('/account/purchase_request/edit');
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


$('#modalProductItemRemove').on('hidden.bs.modal', () => {
  VueComponent.$root.modalData = {
    productItemId: null,
    productItemTitle: '',
    quantity: null,
    additionalSpec: null,
  };
});
