<%@page import="models.Person"%>
<%@page import="models.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/fontawesome/css/all.css" >
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/toast/toastr.css"/>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/product-form-create.css"/>
</head>
<body>

	<jsp:include page="/header.jsp"/>
	
	<div id="productNew" class="container mb-sm-5 mb-md-5">
		<div class="card mb-sm-3 mb-md-3">
			<div class="card-body">
				<h1 class="card-title text-muted text-uppercase">Novo produto</h1>
			</div>
		</div>
		<form autocomplete="off" @submit.prevent="save">
			<div class="card mb-3 mb-sm-3 mb-md-3">
				<div class="card-body">				
					<h2 class="text-muted">Categoria: <i class="text-primary">{{ product.category && product.category.id && product.category.title }}</i></h2>
					<p class="text-muted">Escolha abaixo a categoria na qual este produto irá pertencer.</p>
					<nav aria-label="breadcrumb">
						<ol class="breadcrumb">
							<template v-for="(subcategory, index) in breadcrumbCategories">
								<li class="breadcrumb-item active" v-if="index === (breadcrumbCategories.length - 1)">{{ subcategory.title }}</li>
								<li class="breadcrumb-item" v-else><a href="javascript:void(0)" @click="onClickBreadcrumbCategory(subcategory.id)">{{ subcategory.title }}</a></li>
							</template>
						</ol>
					</nav>
					<div class="form-group col-md-5 col-sm-8 col-lg-8 col-12">
						<label for="categoryId">Categorias</label>
						<select id="categoryId" name="categoryId" class="custom-select" @change="onChangeCategorySelection">
							<template v-for="(category, index) in categories">
								<option :value="category.id">{{ category.title }}</option>
							</template>
						</select>
					</div>
				</div>
			</div>
			
			<div class="card mb-3 mb-sm-3 mb-md-3" >
				<div class="card-body">
					<h2 class="text-muted">Produto</i></h2>
					<p class="text-muted">
						Insira o título do produto no campo abaixo, ao inserir um título que não 
						conste na nossa base de dados você estará criando um novo produto em nosso sistema. 
						Para mais informações&nbsp;<a href="javascript:void(0)">clique aqui.</a>
					</p>
					<div class="form-group">
						<label for="title">Título</label>
						<div class="autocomplete">
							<input 
								id="title" 
								name="title" 
								class="form-control form-control-lg" 
								placeholder="Busque por um produto ou registre um novo" 
								type="text" 
								:value="product.title" 
								@keyup="onKeyupProductTitle"
								@blur="clearPredictProducts">
							<div class="list-group autocomplete-list" v-if="productsPredict && productsPredict.length">
								<template v-for="(predictProduct, index) in productsPredict">
									<a 
										@click="onClickPredictProduct(predictProduct)"
										href="javascript:void(0)" 
										class="list-group-item list-group-item-action d-flex justify-content-between align-items-center">
										{{ predictProduct.title }}
										<span class="badge badge-pill badge-info text-light">{{ predictProduct.relevance }}</span>
									</a>
								</template>
							</div>
						</div>
					</div>
					
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="price">Preço</label>
							<div class="input-group mb-3">
								<div class="input-group-prepend">
									<span class="input-group-text">R$</span>
								</div>								
								<input class="form-control" type="number" id="price" name="price" step="0.01" min="0.01" v-model="product.price">
							</div>
						</div>
						<div class="form-group col-md-6">
							<label for="availableQuantity">Quantidade disponível</label>
							<input class="form-control" type="number" id="availableQuantity" name="availableQuantity" min="0" v-model="product.availableQuantity">
						</div>
					</div>
					
					<div class="form-group">
						<label for="description">Especificações</label>
						<textarea class="form-control" id="description" name="description" rows="3" style="white-space: pre-wrap">{{ product.description }}</textarea>
					</div>
				</div>
			</div>
			
			<div class="d-flex justify-content-between">
				<a role="button" class="btn btn-link px-md-5 py-md-2 font-weight-bold" href="<%=request.getContextPath() %>/account/inventory" >Voltar</a>
				<button class="btn btn-success btn-submit px-md-5 py-md-2 font-weight-bold" type="submit" value="edit" name="action">Salvar</button>
			</div>
		</form>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
	<script src="<%=request.getContextPath() %>/assets/libs/toast/toastr.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/lodash/lodash-dist.js"></script>
	<script src="<%=request.getContextPath()%>/assets/js/product-form-create.js"></script>
</body>
</html>
