<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
	<meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link
            href="<%=request.getContextPath() %>/assets/fonts/montserrat.css"
            rel="stylesheet"
            type="text/css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/fontawesome/css/all.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/toast/toastr.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/dropzone/dropzone.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/product-details.css"/>
</head>
<body>
	<jsp:include page="/header.jsp"/>
	
	<div id="productDetails" class="container mb-sm-5 mb-md-5">
		<div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body">
            <h1 class="card-title text-muted text-uppercase">Gestão de Produto</h1>
        </div>
    </div>

    <div class="row" v-if="product && product.id && product.productItem">

        <form autocomplete="off" @submit.prevent="save" class="col-md-12">

            <div class="row d-flex justify-content-between">

                <div class="col-md-12">
                    <div class="card border-light mb-3 mb-sm-3 mb-md-3">
                        <div class="card-body">
                            <div class="row justify-content-md-center">
                                <div class=" col-12 col-sm-12 col-md-10 col-lg-7">
                                    <h2 class="text-muted">Produto 	&#35;{{ product.id }}</h2>
                                    <div class="form-group">
                                        <label for="title">Título</label>
                                        <div class="autocomplete">
                                            <input
                                               id="title"
                                               name="title"
                                               class="form-control form-control-lg"
                                               type="text"
                                               disabled
                                               :value="product.title">
                                        </div>
                                    </div>

                                    <div class="form-row d-flex justify-content-between align-bottom">
                                        <div class="form-group col-md-6">
                                            <label for="basePrice">Preço</label>
                                            <div class="input-group mb-3">
                                                <div class="input-group-prepend">
                                                    <span class="input-group-text">R$</span>
                                                </div>
                                                <input class="form-control" type="number" id="basePrice" name="basePrice"
                                                       step="0.01" min="0.01" v-model="product.basePrice">
                                            </div>
                                        </div>
                                        <div class="form-group col-md-6">
                                            <label for="availableQuantity">Quantidade disponível</label>
                                            <input class="form-control" type="number" id="availableQuantity"
                                                   name="availableQuantity" min="0" v-model="product.availableQuantity">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="description">Especificações</label>
                                        <textarea class="form-control" v-model="product.description" id="description"
                                                  name="description" rows="3"
                                                  style="white-space: pre-wrap"></textarea>
                                    </div>

                                    <div class="form-group">
                                        <label>Carregar Imagens</label>
                                        <div id="productDropzone" class="dropzone"></div>
                                    </div>
                                </div>
                                <hr>
                                <div class="col-12 col-sm-12 col-md-auto col-lg-auto">
                                    <h2 class="text-muted">Anúncio vinculado</h2>
                                    <div class="container-product-item">
                                        <img
                                        	class="img-thumbnail" 
	                                        :alt="product.productItem.thumbnail.name"
	                                        :src="product.productItem.thumbnail.urlPath"/>
                                        <div class="info_container-product-item">
                                            <div class="info_content-product-item">
                                                <p class="text-dark">{{ product.productItem.title }}</p>
                                                <small class="text-secondary">
                                                    Relevância:
                                                    &nbsp;
                                                    {{ product.productItem.relevance }}
                                                </small>
                                                <br/>
                                                <small class="text-muted">Visualizações: {{
                                                    product.productItem.viewsCount
                                                    }}
                                                </small>
                                            </div>
                                            <div class="price_content-product-item">
                                                <p class="text-success">
                                                    {{ formatCurrency(product.productItem.basePrice) }}
                                                </p>
                                                <small class="text-muted">
                                                    Flutuação (R$)
                                                    <br/>
                                                    {{ formatDecimal(product.productItem.maxPrice) }}
                                                    &nbsp;&hyphen;&nbsp;
                                                    {{ formatDecimal(product.productItem.minPrice) }}
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="d-flex justify-content-between">
                <a role="button" class="btn btn-link bg-white px-md-5 py-md-2 font-weight-bold"
                   href="<%=request.getContextPath() %>/account/inventory">Voltar</a>
                <button class="btn btn-info btn-submit px-md-5 py-md-2 font-weight-bold" type="submit" value="edit"
                        name="action">Salvar
                </button>
            </div>
        </form>

    </div>
	</div>
	
	<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/dropzone/dropzone.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/qs/index.js"></script>
	<script>Dropzone.autoDiscover = false;</script>
	<script src="<%=request.getContextPath()%>/assets/js/product-details.js"></script>
</body>
</html>