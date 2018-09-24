new Vue({
	el: '#productNew',
	data() {
		return {
			product: {
				title: '',
				price: null,
				availableQuantity: null,
				description: '',
				category: {},
			},
			productsPredict: [],
			categories: [],
			breadcrumbCategories: [{ id: 0, title: 'Geral' }],
		};
	},
	created() {
		this.loadData();
	},
	methods: {
		async onChangeCategory(event) {
			const categoryId = Number(event.target.value);
			const category = this.categories.find(cat => cat.id === categoryId);

			if (category.isLastChild) {
				this.product.category = { ...category };
				return;
			}
			
			this.categories = await this.getCategories(categoryId);
			
			const index = this.breadcrumbCategories.findIndex(cat => cat.id === category.id);
			
			if (index === -1) {
				this.breadcrumbCategories = [...this.breadcrumbCategories, category];
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
			this.productsPredict = [];
		},
		onKeyupProductTitle: _.debounce(async function(event) {
			const productTitle = event.target.value.trim();
			
			this.product.title = productTitle;
			if (!productTitle || productTitle.length <= 1) {
				this.productsPredict = [];
				return;
			}
			
			// buscar no elasticsearch
			const { data } = await axios.get(`/products/search?productPredictTitle=${productTitle}`);
			this.productsPredict = data;
		}, 300),
		save() {
			console.log('Salvando novo produto');
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
		showToast(msg, topic) {
			toastr[topic](msg);
		}
	},
})