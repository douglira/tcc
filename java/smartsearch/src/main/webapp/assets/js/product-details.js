let FormatterMixin = {
  methods: {
    formatCurrency: Formatter.currency,
    formatDecimal: Formatter.decimal,
    formatDate: Formatter.date,
  }
};

new Vue({
  el: '#productDetails',
  name: 'ProductDetails',
  mixins: [FormatterMixin],
  data() {
    return {
      product: {},
      picturesDropzone: null,
    };
  },
  created() {
    this.loadData();
  },
  updated() {
    if (!document.getElementById('productDropzone')) return;
    this.picturesDropzone = new Dropzone('#productDropzone', {
      url: '/files/s3/upload/product_pictures',
      acceptedFiles: 'image/jpg,image/jpeg,image/png',
      maxFiles: 4,
//			createThumbnails: false,
      parallelUploads: 10,
      uploadMultiple: true,
      maxFileSize: 3,
      thumbnailWidth: 320,
//			dpreviewTemplate: null,
      autoProcessQueue: false,
      addRemoveLinks: true,
      dictDefaultMessage: 'Arraste para cá ou clique para carregar',
      dictFallbackMessage: 'Arquivo não suportado',
      dictFallbackMessage: null,
      dictCancelUpload: 'Cancelar upload',
      dictInvalidFileType: 'Tipo do arquivo inválido',
      dictFileTooBig: 'Arquivo muito grande',
      dictResponseError: 'Algo deu errado, tente novamente',
      dictRemoveFile: 'Remover',
      dictMaxFilesExceeded: 'Número de arquivos excedidos',
//			previewTemplate: null,
//			successmultiple: (files, response) => {
//				this.product.picturesPath = JSON.parse(response);
//			}
    });
  },
  methods: {
    async savePictures() {

      if (this.picturesDropzone.getQueuedFiles() && this.picturesDropzone.getQueuedFiles().length) {
        this.picturesDropzone.processQueue();

        return new Promise((resolve, reject) => {
          this.picturesDropzone.on('successmultiple', (files, response) => {
            resolve(JSON.parse(response));
          });
          this.picturesDropzone.on('error', (files, errorMessage) => {
            reject(errorMessage);
          });
        });
      } else {
        return [];
      }

    },
    async save() {
      let isValid = true;
      let pictures = [];
      const categoryId = this.product.category.id;
      const productItemId = this.product.productItemId;
      const payload = {
        productItemId,
        product: {
          title: this.product.title,
          basePrice: this.product.basePrice,
          availableQuantity: this.product.availableQuantity,
          description: this.product.description,
        }
      };

      const toValidate = { ...payload.product, categoryId };

      Object.keys(toValidate).forEach((key) => {
        if (!toValidate[key] && key !== 'description') {
          isValid = false;
          $(`#${key}`).addClass('border-danger');
          return;
        }

        $(`#${key}`).hasClass('border-danger') && $(`#${key}`).removeClass('border-danger');
      });

      if (!isValid) {
        return null;
      }

      try {
        pictures = await this.savePictures();
        payload.product.pictures = pictures;

        const { data } = await axios.post(
            '/account/products/new',
            Qs.stringify({
              ...payload,
              product: JSON.stringify(payload.product)
            }),
            {
              headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
              }
            }
        );
        const { content, type } = data;
        this.showToast(content, type);
        this.resetData();
      } catch (err) {
        if (pictures.length) {
          await axios.post(
              '/files/s3/delete/product_pictures',
              Qs.stringify({ pictures: JSON.stringify(pictures) }),
              {
                headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
              }
          });
        }
        if (err.response.data && err.response.data.type === 'ERROR') {
            const { content, type } = err.response.data;
            return this.showToast(content, type);
        }
        this.showToast('Erro inesperado, tente novamente', 'ERROR');
      }
    },
    async getProduct() {
        const id = new URLSearchParams(document.location.search.substring(1)).get("id");
        
        const { data } = await axios.get(`/account/products/details?id=${id}`);
        return data;
    },
    async loadData() {
        try {
            const product = await this.getProduct();
            this.product = product;
        } catch(err) {
            if (err.response.data && err.response.data.type) {
                const { content, type } = err.response.data;
                return this.showToast(content, type);
            }
            this.showToast('Erro inesperado, tente novamente', 'ERROR');
        }
    },
    showToast(msg, topic = 'success') {
      topic = topic.toLowerCase();

      toastr[topic](msg);
    },
    async resetData() {
      this.product = {
        title: '',
        basePrice: null,
        availableQuantity: null,
        description: null,
        productItemId: null,
      };
      this.breadcrumbCategories = [{ id: 0, title: 'Geral' }];
      this.picturesDropzone.removeAllFiles(true);
    }
  },
});