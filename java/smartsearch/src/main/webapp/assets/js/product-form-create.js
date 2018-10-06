new Vue({
  el: '#productNew',
  data() {
    return {
      productItemPreview: null,
      product: {
        title: '',
        price: null,
        availableQuantity: null,
        description: '',
        productItemId: null,
        picturesPath: [],
        category: {
          id: null,
        },
      },
      productsPredict: [],
      categories: [],
      breadcrumbCategories: [{ id: 0, title: 'Geral' }],
    };
  },
  created() {
    this.loadData();
  },
  mounted() {
    this.picturesDropzone = new Dropzone('div.dropzone', {
      url: '/products/pictures/upload',
      acceptedFiles: 'image/jpg,image/jpeg',
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
    async onChangeCategorySelection(event) {
      const categoryId = Number(event.target.value);
      const category = this.categories.find(cat => cat.id === categoryId);

      if (category && category.isLastChild) {
        this.product.category = { ...category };
        return;
      }

      this.categories = await this.getCategories(categoryId);

      const index = this.breadcrumbCategories.findIndex(cat => cat.id === categoryId);

      if (index === -1) {
        this.breadcrumbCategories = [...this.breadcrumbCategories, category];
      }

      if (index === 0 ) {
        this.breadcrumbCategories = [this.breadcrumbCategories[index]];
        this.product.category = { id: null };
      }
    },
    async onClickBreadcrumbCategory(categoryId) {
      categoryId = Number(categoryId);
      this.categories = await this.getCategories(categoryId);

      const lastIndex = this.breadcrumbCategories.length - 1;
      const index = this.breadcrumbCategories.findIndex(cat => cat.id === categoryId);

      if (index !== lastIndex) {
        this.breadcrumbCategories.splice((index + 1), lastIndex);
        this.product.category = null;
      }
    },
    onClickPredictProduct(predictProduct) {
      this.product.title = predictProduct.title;
      this.product.productItemId = predictProduct.id;

      this.productItemPreview = predictProduct;

      this.productsPredict = [];
    },
    onKeyupProductTitle(event) {
      this.productItemPreview = null;
      const productTitle = event.target.value;

      this.product.title = productTitle;
      this.product.productItemId = null;
      if (!productTitle || productTitle.length <= 1) {
        this.productsPredict = [];
        return;
      }
      this.getPredictProducts(productTitle);
    },
    onClickRemoveProductItem() {
      this.product.title = '';
      this.product.productItemId = null;

      this.productItemPreview = null;
    },
    getPredictProducts: _.debounce(async function(productTitle) {
      // predict at elasticsearch
      const { data } = await axios.get(`/products/predict?productPredictTitle=${productTitle}`);
      this.productsPredict = data;
    }, 450),
    async savePictures() {

      if (this.picturesDropzone.getQueuedFiles() && this.picturesDropzone.getQueuedFiles().length) {
        this.picturesDropzone.processQueue();

        return new Promise(resolve => {
          this.picturesDropzone.on('successmultiple', (files, response) => {
            resolve(JSON.parse(response));
          });
        });
      } else {
        return [];
      }

    },
    async save() {
      let isValid = true;

      const categoryId = this.product.category.id;
      const productItemId = this.product.productItemId;
      const payload = {
        categoryId,
        productItemId,
        product: {
          title: this.product.title,
          price: this.product.price,
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

      const pictures = await this.savePictures();

      payload.product.pictures= pictures;

      $.post(
        '/account/me/inventory',
        {
          ...payload,
          product: JSON.stringify(payload.product),
        },
        (data, status) => {
          const msg = data && JSON.parse(data);
          if (status === 'success') {
            this.showToast(msg.content, msg.type);
            this.resetData();
            return;
          }

          this.showToast(msg.content, msg.type);
        }
      );
    },
    clearPredictProducts: _.debounce(function() {
      this.productsPredict = [];
    }, 300),
    async getCategories(categoryId) {
      let data;

      try {
        if (categoryId) {
          const response = await axios.get(`/categories/json?parentId=${categoryId}`);
          ({ data } = response);
        } else {
          const response = await axios.get('/categories/json');
          ({ data } = response);
        }

        return data;
      } catch(err) {
        console.log(err);
        this.showToast('Não foi possível carregar as categorias do sistema. Tente mais tarde', 'error');
      }

    },
    async loadData() {
      this.categories = await this.getCategories();
    },
    showToast(msg, topic = 'success') {
      topic = topic.toLowerCase();

      toastr[topic](msg);
    },
    async resetData() {
      this.product = {
        title: '',
        price: null,
        availableQuantity: null,
        description: null,
        productItemId: null,
        category: {
          id: null,
        },
      };
      this.productItemPreview = null;
      this.productsPredict = [];
      this.categories = await this.getCategories();
      this.breadcrumbCategories = [{ id: 0, title: 'Geral' }];
      this.picturesDropzone.removeAllFiles(true);
    }
  },
});