let formatterMixin = {
  methods: {
    formatDatetime: Formatter.datetime,
    formatFullDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
  },
};

const VuePRCreation = new Vue({
  el: '#userPRCreation',
  mixins: [formatterMixin],
  data() {
    return {
      username: null,
      purchaseRequest: null,
      prAdditionalData: '',
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
    this.initPopover();
  },
  methods: {
    onClickPublish() {
      this.purchaseRequest.additionalData = this.prAdditionalData;

      $.post(
        '/account/purchase_request/publish',
        {
          purchaseRequest: JSON.stringify(this.purchaseRequest),
        }
      )
        .done(() => {
          console.log('Deu certo!');
          VueHeader.$data.purchaseRequest = null;
          this.purchaseRequest = null;
          window.location.replace('/');
        });
    },
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
    deletePurchaseRequest() {
      $.post('/account/purchase_request/abort?abortAction=delete', { purchaseRequestId: this.purchaseRequest.id })
    },
    initPopover() {
      $(function () {
        $('[data-toggle="popover"]').popover({
          trigger: 'hover focus',
          placement: 'right',
          title: 'Abrangência',
          content: `Este é a quantidade de fornecedores que possuem os items requisitados abaixo em estoque. 
            Ao lançar este pedido de compra uma notificação será enviada a eles, portanto quanto 
            maior este número maior serão as chances de concluir um orçamento.`,
        });
      });
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
  VuePRCreation.$root.modalData = {
    productItemId: null,
    productItemTitle: '',
    quantity: null,
    additionalSpec: null,
  };
});


$('#modalProductItemRemove').on('hidden.bs.modal', () => {
  VuePRCreation.$root.modalData = {
    productItemId: null,
    productItemTitle: '',
    quantity: null,
    additionalSpec: null,
  };
});
