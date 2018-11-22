<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/signin.css">
</head>
<body>

	<jsp:include page="header.jsp"/>

	<div class="container">
		<div class="content">
			<h1 class="text-secondary">Login</h1>
			<%String error = (String) request.getAttribute("error");%>
			<c:if test="<%=error != null%>">
				<div class="div-error">
					<div class="alert alert-danger"><%= error %></div>
				</div>
			</c:if>
			<form action="/auth/signin" method="POST">
				<div class="form-group">
					<label for="emailInput">Email</label>
					<input class="form-control" type="email" id="emailInput" name="email" placeholder="Seu email de acesso" autofocus />
				</div>
				<div class="form-group">
					<label for="passwordInput">Senha</label>
					<input class="form-control" type="password" id="passwordInput" name="password" placeholder="Sua senha" />
					<a class="link-forgot_pass" href="<%=request.getContextPath()%>/form/forgot_pass">Esqueceu sua senha?</a>
				</div>
				<button class="btn btn-primary" type="submit" name="action" >Entrar</button>
				<small>ou</small>
				<p><a href="<%=request.getContextPath() %>/register" class="text-info">Não possui conta? Cadastrar</a></p>
			</form>
		</div>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/popper-1.14.4.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>
