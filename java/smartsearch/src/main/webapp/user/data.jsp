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
<link href="/assets/libs/toast/toastr.css" rel="stylesheet"/>
</head>
<body>

	<jsp:include page="/header.jsp"/>

	<div id="userData" class="container mb-sm-5 mb-md-5">
		<h1 class="text-muted">Meus dados</h1>
		<form @submit.prevent="save" v-if="person && person.id" >
			<div class="form-group w-100">
				<label for="email">Email</label>
				<input class="form-control" type="email" id="email" name="email" disabled :value="person.user && person.user.email">
			</div>
			<div class="card mb-sm-4 mb-md-4">
				<div class="card-body">
					<h2 class="text-muted">Dados jurídicos</h2>
					<div class="form-group w-100">
						<label for="accountOwner">Responsável</label>
						<input class="form-control" type="text" id="accountOwner" name="accountOwner" :value="person && person.accountOwner" @keyup="person.accountOwner = $event.target.value">
					</div>
					<div class="form-group w-100">
						<label for="tel">Telefone</label>
						<input class="form-control" type="tel" id="tel" name="tel" :value="person && person.tel" @keyup="person.tel = $event.target.value">
					</div>
					<div class="form-group w-100">
						<label for="cnpj">CNPJ</label>
						<input class="form-control" type="text" id="cnpj" name="cnpj" :value="person && person.cnpj" @keyup="person.cnpj = $event.target.value">
					</div>
					<div class="form-group w-100">
						<label for="corporateName">Razão social</label>
						<input class="form-control" type="text" id="corporateName" name="corporateName" :value="person && person.corporateName" @keyup="person.corporateName = $event.target.value">
					</div>
					<div class="form-group w-100">
						<label for="stateRegistration">Inscrição estadual</label>
						<input class="form-control" type="number" id="stateRegistration" name="stateRegistration" :value="person && person.stateRegistration" @keyup="person.stateRegistration = $event.target.value">
					</div>
				</div>
			</div>
			<div class="card mb-sm-4 mb-md-4">
				<div class="card-body">
					<h2 class="text-muted">Meu endereço</h2>
					<div class="form-group">
						<label for="street">Endereço</label>
						<input class="form-control" type="text" id="street" name="street" :value="person.address && person.address.street" @keyup="person.address.street = $event.target.value">
					</div>
					<div class="form-group">
						<label for="additionalData">Complemento</label>
						<input class="form-control" type="text" id="additionalData" name="additionalData" :value="person.address && person.address.additionalData" @keyup="person.address.additionalData = $event.target.value">
					</div>
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="district">Bairro</label>
							<input class="form-control" type="text" id="district" name="district" :value="person.address && person.address.district" @keyup="person.address.district = $event.target.value">
						</div>
						<div class="form-group col-md-6">
							<label for="buildingNumber">Número</label>
							<input class="form-control" type="number" id="buildingNumber" name="buildingNumber" :value="person.address && person.address.buildingNumber" @keyup="person.address.buildingNumber = $event.target.value">
						</div>
					</div>
					<div class="form-row">
						<div class="form-group col-md-6">
							<label for="city">Cidade</label>
							<input class="form-control" type="text" id="city" name="city" :value="person.address && person.address.city" @keyup="person.address.city = $event.target.value">
						</div>
						<div class="form-group col-md-2">
							<label for="provinceCode">Estado</label>
							<select id="provinceCode" name="provinceCode" class="custom-select" @change="person.address.provinceCode = $event.target.value">
								<option :selected="!!person.address">Selecione</option>
								<template v-for="(uf, index) in ufs">
									<option :value="uf.sigla" :selected="person.address && person.address.provinceCode === uf.sigla ? true : false">{{ uf.nome }}</option>
								</template>
							</select>
						</div>
						<div class="form-group col-md-4">
							<label for="postalCode">CEP</label>
							<div class="input-group">
								<input class="form-control" type="number" id="postalCode" name="postalCode" :value="person.address && person.address.postalCode" @keyup="person.address.postalCode = $event.target.value">
								<div class="input-group-append">
									<button class="btn btn-light border" type="button" @click="searchByCep">
										<i class="fas fa-search" style="font-size: 24px; color: #666;"></i>
									</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="d-flex justify-content-between">
				<a role="button" class="btn btn-link px-md-5 py-md-2 font-weight-bold" href="<%=request.getContextPath() %>/account" >Voltar</a>
				<button class="btn btn-success btn-submit px-md-5 py-md-2 font-weight-bold" type="submit" value="edit" name="action">Salvar alterações</button>
			</div>
		</form>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/popper-1.14.4.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.min.js"></script>
	<script src="<%=request.getContextPath() %>/assets/libs/toast/toastr.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/js/user-data.js"></script>
</body>
</html>
