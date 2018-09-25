new Vue({
	el: '#productNew',
	data() {
		return {
			product: {
				title: '',
				price: null,
				availableQuantity: null,
				description: '',
				productItemId: null,
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
	methods: {
		async onChangeCategorySelection(event) {
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
			this.product.productItemId = predictProduct.id;
			this.productsPredict = [];
		},
		onKeyupProductTitle(event) {
			const productTitle = event.target.value;
			
			this.product.title = productTitle;
			this.product.productItemId = null;
			if (!productTitle || productTitle.length <= 1) {
				this.productsPredict = [];
				return;
			}
			this.getPredictProducts(productTitle);
		},
		getPredictProducts: _.debounce(async function(productTitle) {
			productTitle.trim();
			// predict at elasticsearch
			const { data } = await axios.get(`/products/search?productPredictTitle=${productTitle}`);
			this.productsPredict = data;
		}, 450),
		save() {
			let isValid = true;
			
			const categoryId = this.product.category.id;
			const productItemId = this.product.productItemId;
			const payload = {
				categoryId,
				productItemId,
				product: {
					title: this.product.title.trim(),
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
			})
			
			if (!isValid) {
				return null;
			}
			
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
				description: '',
				productItemId: null,
				category: {
					id: null,
				},
			};
			this.productsPredict = [];
			this.categories = await this.getCategories();
			this.breadcrumbCategories = [{ id: 0, title: 'Geral' }];
		}
	},
})