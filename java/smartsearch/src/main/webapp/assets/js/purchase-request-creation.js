let formatterMixin = {
  methods: {
    formatDatetime: Formatter.datetime,
    formatFullDate: Formatter.fullDate,
    formatCurrency: Formatter.currency,
    getCalendar: Formatter.getCalendar,
  },
};

const VuePRCreation = new Vue({
  el: '#userPRCreation',
  name: 'PRCreation',
  mixins: [formatterMixin],
  data() {
    return {
      purchaseRequest: null,
      prQuotesVisibility: false,
      prDueDate: '',
      prAdditionalData: '',
      modalData: {
        productItemId: null,
        productItemTitle: '',
        quantity: null,
        additionalSpec: null,
      },
      invalidPublish: true,
    }
  },
  async created() {
    await this.loadData();
    this.wsPurchaseRequestUpdate();
    this.initPopover();
  },
  watch: {
    prDueDate() {
      if (!this.prDueDate) {
        this.invalidPublish = true;
        return;
      }
      const inputDueDate = $('#expirationDate');

      const now = moment(new Date());
      const dueDate = moment(new Date(this.prDueDate));

      const days = dueDate.diff(now, 'days');
      if (days > 90) {
        this.invalidPublish = true;
        inputDueDate.addClass('border border-primary');
        this.showMessage('Expiração máxima: 90 dias', 'error', { preventDuplicates: true });
      }

      if (days < 0) {
        this.invalidPublish = true;
        inputDueDate.addClass('border border-primary');
        this.showMessage('Data de expiração inválida', 'error', { preventDuplicates: true });
      }

      if (inputDueDate.hasClass('border border-primary')) {
        inputDueDate.removeClass('border border-primary');
      }
      this.invalidPublish = false;
    }
  },
  methods: {
    onClickPublish() {
      $.post(
        '/account/purchase_request/publish',
        {
          purchaseRequest: JSON.stringify({
            ...this.purchaseRequest,
            additionalData: this.prAdditionalData,
            quotesVisibility: this.prQuotesVisibility,
            dueDate: this.getCalendar(new Date(this.prDueDate)),
          }),
        }
      )
        .done(() => {
          VueHeader.$data.purchaseRequest = null;
          this.purchaseRequest = null;
          window.location.replace('/');
        })
        .fail((response) => {
          const msg = JSON.parse(response.responseText);

          if (msg.content) {
            this.showMessage(msg.content, msg.type);
          }
        })
    },
    onClickEditProduct(purchaseItem) {
      this.modalData = {
        productItemId: purchaseItem.product.id,
        productItemTitle: purchaseItem.product.title,
        quantity: purchaseItem.quantity,
        additionalSpec: purchaseItem.additionalSpec,
      };
      $('#modalProductItemEdit').modal('show');
    },
    onClickRemoveProduct(purchaseItem) {
      this.modalData = {
        productItemId: purchaseItem.product.id,
        productItemTitle: purchaseItem.product.title,
      };
      $('#modalProductItemRemove').modal('show');
    },
    confirmRemove(productList) {
      $.post(
        '/account/purchase_request/remove',
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
      $.post(
        '/account/purchase_request/save',
        {
          purchaseRequestId: this.purchaseRequest.id,
          productItemId: this.modalData.productItemId,
          productItemQuantity: this.modalData.quantity,
          productItemAdditionalSpec: this.modalData.additionalSpec
        }
      )
        .always(() => {
          $('#modalProductItemEdit').modal('hide');
        })
    },
    deletePurchaseRequest() {
      $.post('/account/purchase_request/abort', { purchaseRequestId: this.purchaseRequest.id })
    },
    initPopover() {
      $(function () {
        $('#popoverPropagationCount').popover({
          trigger: 'hover focus',
          placement: 'right',
          title: 'Fornecedores',
          content: `Esta é a quantidade de fornecedores que possuem os items requisitados abaixo em estoque. 
            Ao lançar este pedido de compra uma notificação será enviada a eles, portanto quanto 
            maior este número maior serão as chances de concluir um orçamento.`,
        });
      });
    },
    wsPurchaseRequestUpdate() {
      const wsPurchaseRequest = new WebSocket(`ws://localhost:8080/account/purchase_request/${VueHeader.$data.username}`);
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
      const response = await axios.get('/account/purchase_request/creation');
      this.purchaseRequest = response.data;
    },
    showMessage(msg, type = 'success', options) {
      toastr.options = options;
      toastr[type.toLowerCase()](msg);
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
