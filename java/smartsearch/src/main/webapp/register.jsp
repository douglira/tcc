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
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/fontawesome/css/all.css" >
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/register.css">
</head>
<body>

	<jsp:include page="header.jsp"/>

	<div class="container">
		<div class="content">
			<h1 class="display-4 text-muted">Cadastrar-se</h1>
			<small>Preencha o fomulário abaixo</small>
			<form action="/auth/signup" method="POST">
				<div class="form-group w-100">
					<label for="email">Email</label>
					<input class="form-control" type="email" id="email" name="email">
				</div>
				<div class="form-group w-100">
					<label for="password">Senha</label>
					<input class="form-control" type="password" id="password" name="password">
				</div>
				<div class="form-group w-100">
					<label for="username">Nome de usuário</label>
					<input class="form-control" type="text" id="username" name="username">
				</div>
				<h2 class="text-muted">Dados jurídicos</h2>
				<div class="form-group w-100">
					<label for="accountOwner">Responsável</label>
					<input class="form-control" type="text" id="accountOwner" name="accountOwner">
				</div>
				<div class="form-group w-100">
					<label for="tel">Telefone</label>
					<input class="form-control" type="tel" id="tel" name="tel">
				</div>
				<div class="form-group w-100">
					<label for="cnpj">CNPJ</label>
					<input class="form-control" type="text" id="cnpj" name="cnpj">
				</div>
				<div class="form-group w-100">
					<label for="corporateName">Razão social</label>
					<input class="form-control" type="text" id="corporateName" name="corporateName">
				</div>
				<div class="form-group w-100">
					<label for="stateRegistration">Inscrição estadual</label>
					<input class="form-control" type="number" id="stateRegistration" name="stateRegistration">
				</div>
				<button class="btn btn-success btn-submit" type="submit" value="register" name="action">Finalizar cadastro</button>
				<p class="text-muted p-link-signin">
					Já tem um cadastro?	
					<a class="text-info" href="<%=request.getContextPath()%>/signin">
						Entrar
					</a>
				</p>
			</form>
		</div>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/popper-1.14.4.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/inputmask/dist/jquery.inputmask.bundle.js"></script>
	<script src="<%=request.getContextPath()%>/assets/js/register.js"></script>
</body>
</html>
