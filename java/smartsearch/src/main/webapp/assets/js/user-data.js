new Vue({
	el: '#userData',
	data() {
		return {
			person: null,
			ufs: [],
		};
	},
	created() {
		this.loadProvinceCodes()
	},
	methods: {
		async loadProvinceCodes() {
			const [personResponse, ufsResponse] = [
				await axios.get('/account/me/data'),
				// await axios.get('https://servicodados.ibge.gov.br/api/v1/localidades/estados'),
        await axios.get('http://www.geonames.org/childrenJSON?geonameId=3469034&callback=listPlaces&style=long&noCacheIE=1540136223992'),
			];
			this.person = { user: {}, address: { id: null, postalCode: null }, ...personResponse.data};
			this.ufs = JSON.parse(ufsResponse.data.substring(ufsResponse.data.indexOf('(') + 1, ufsResponse.data.lastIndexOf(')'))).geonames
				.map(geo => ({ nome: geo.name, sigla: geo.adminCodes1.ISO3166_2 }));
		},
		async searchByCep() {
			// UMC Cep - 08780911 Nº 200
			let { postalCode } = this.person.address;
			
			if (!postalCode) {
				return null;
			}
			
			try {
				const { data } = await axios.get(`https://viacep.com.br/ws/${postalCode}/json/`);
				
				let address = {};
				
				address.postalCode = data.cep.replace('-', '');
				address.street = data.logradouro;
				address.district = data.bairro;
				address.city = data.localidade;
				address.provinceCode = data.uf;
				
				this.person.address = { ...this.person.address, ...address };
			} catch (err) {
				this.showMessage('Não foi possível carregar os dados pelo CEP. Por favor preencha manualmente', 'error');
			}
		},
		async save() {
			let isValid = true;
			let payload = {
				accountOwner: this.person.accountOwner,
				tel: this.person.tel,
				cnpj: this.person.cnpj,
				corporateName: this.person.corporateName,
				stateRegistration: this.person.stateRegistration,
				street: this.person.address.street,
				additionalData: this.person.address.additionalData,
				district: this.person.address.district,
				buildingNumber: this.person.address.buildingNumber,
				city: this.person.address.city,
				provinceCode: this.person.address.provinceCode,
				postalCode: this.person.address.postalCode,
			}
			
			Object.keys(payload).forEach((key) => {
				if (!payload[key] && key !== 'additionalData') {
					isValid = false;
					$(`#${key}`).addClass('border-danger');
					return;
				}
				
				$(`#${key}`).hasClass('border-danger') && $(`#${key}`).removeClass('border-danger');
			})
			
			if (!isValid) {
				return null;
			}

			payload.personId = this.person.id;
			payload.addressId = this.person.address.id;
			
			$.post(
				'/account/me/data', 
				{...payload},
				(data, status) => {
					data = JSON.parse(data);
					if (status === 'success') {
						this.person = data;
						this.showMessage('Dados alterados com sucesso');		
						return;
					}
					
					this.showMessage(response.error, 'error');
				}
			);
			
		},
		showMessage(msg, topic = 'success') {
			toastr[topic](msg);
		}
	}
});
