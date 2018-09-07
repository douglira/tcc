<%@page import="models.Category"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<link
	href="https://fonts.googleapis.com/css?family=Montserrat:400,400i,500,700"
	rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/bootstrap/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/categories-panel.css"/>
</head>
<body>

	<jsp:include page="/header.jsp"/>
	
	<%
		Category category = (Category) request.getAttribute("category");
	%>

	<div class="container" style="margin-bottom: 20px;">
		<h1 style="font-size: 28px;">Categoria:&nbsp;<i><%=category.getTitle()%></i></h1>
		<hr>
		<p class="text-secondary">
			Edite abaixo o título e descrição da categoria.
		</p>
		
		<form action="/admin/categories/edit" method="POST">
			<div class="input-group mb-3 w-25">
				<div class="input-group-prepend">
			    	<span class="input-group-text">
						<strong>ID</strong>
			    	</span>
			  	</div>
				<input 
					class="form-control font-italic" 
					type="text" 
					value="<%=category.getId()%>"
					id="category-id" 
					name="category-id"
					readonly>
			</div>
			<div class="form-group w-100">
				<label for="category-title">Título</label>
				<input class="form-control" type="text" id="category-title" name="category-title">
			</div>
			<div class="form-group">
				<label for="category-description">Descrição</label>
				<textarea class="form-control" id="category-description" rows="3"></textarea>
			</div>
			<hr>
			<button type="submit" class="btn btn-primary btn-lg btn-block" name="action" value="edit">Salvar</button>
		</form>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/js/categories-form-create.js"></script>
</body>
</html>
