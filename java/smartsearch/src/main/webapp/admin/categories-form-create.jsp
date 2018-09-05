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

	<div class="container" style="margin-bottom: 20px;">
		<h1 style="font-size: 28px;">Gestão de categorias e subcategorias</h1>
		<hr>
		<p class="text-secondary">
			Para criar uma subcategoria selecione na lista abaixo o grupo na qual ela irá pertencer. 
			Caso contrário, cadastre uma nova categoria geral.
		</p>
		
		<div class="alert alert-warning" style="display: none;" role="alert"></div>
		
		<div id="breadcrumb-categories" style="margin-top: 50px;"></div>
		
		<table class="table table-hover">
		  <thead>
		    <tr>
		      <th scope="col">ID</th>
		      <th scope="col">Título</th>
		      <th scope="col"></th>
		    </tr>
		  </thead>
		  <tbody id="tbody-categories">
		  </tbody>		  
		</table>
		
		<form action="/admin/categories/new" method="POST">
			<div class="form-group w-100">
				<label for="category-title">Título</label>
				<input class="form-control" type="text" id="category-title" name="category-title">
			</div>
			<div class="form-group">
				<label for="category-description">Descrição</label>
				<textarea class="form-control" id="category-description" rows="3"></textarea>
			</div>
			<div class="form-group">
				<label>Grupo selecionado</label>
				<div class="input-group mb-3">
					<input 
						class="form-control" 
						type="text" 
						placeholder="Nenhum grupo de categoria selecionada"
						aria-label="Nenhum grupo de categoria selecionada"
						id="category-title-selected" 
						name="category-title-selected"
						readonly>
					<div class="input-group-append">
						<button 
							class="btn btn-light bg bg-light text-muted border" 
							type="button" 
							id="btnRemoveSelectedCategory">Remover</button>
					</div>
				</div>
			</div>
			<input type="hidden" id="category-id-selected" name="category-id-selected" />
			<input type="hidden" id="category-layer-selected" name="category-layer-selected" />
			<input type="hidden" id="category-is_last_child-selected" name="category-is_last_child-selected" />
			<hr>
			<button type="submit" class="btn btn-primary btn-lg btn-block">Cadastrar</button>
		</form>
		
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/js/categories-form-create.js"></script>
</body>
</html>
