new Vue({
	el: '#productNew',
	data() {
		return {
			product: {
				title: '',
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

			console.log(category.isLastChild);
			if (category.isLastChild) {
				console.log('selecionada', category);
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
		onKeyupProductTitle: _.debounce(function(event) {
			const productTitle = event.target.value.trim();
			
			this.product.title = productTitle;
			if (!productTitle) {
				this.productsPredict = [];
				return;
			}
			
			// buscar no elasticsearch
			// this.productsPredict = [{ id: 1, title: 'Notebook Gamer', relevance: '450' }, { id: 2, title: 'Computador de escritório', relevance: '1245' }];
		}, 300),
		onClickSuggestedProduct(suggestedProduct) {
			this.product.title = suggestedProduct.title;
			this.productsPredict = [];
		},
		save() {
			console.log('Salvando novo produto');
		},
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