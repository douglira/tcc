<%@page import="models.Category"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
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
		ArrayList<Category> categories = (ArrayList<Category>) request.getAttribute("categories");
	%>

	<div class="container">
		<h1>Painel de categorias</h1>
		<p class="text-secondary">
			Abaixo estão listadas as categorias gerais do sistema. Clique sobre uma categoria para visualizar suas subcategorias.		
		</p>
		<div class="container-actions">
			<div class="dropdown">
				<button 
				class="btn btn-light header-btn_dropdown text-info dropdown-toggle"
				type="button"
				id="dropdownCategoriesActions"
				data-toggle="dropdown"
				aria-haspopup="true"
				aria-expanded="false">
					Ações
				</button>
				<div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownCategoriesActions">
					<a class="dropdown-item" style="cursor: pointer;" href="<%=request.getContextPath()%>/admin/categories/new" >Gerenciar</a>
				</div>
			</div>
		</div>
		<table class="table table-hover">
		  <thead>
		    <tr>
		      <th scope="col">ID</th>
		      <th scope="col">Título</th>
		      <th scope="col" style="text-align: end;">Data de criação</th>
		    </tr>
		  </thead>
		  <tbody>
		  	
		  	<c:forEach items="${ categories }" var="categoryRow">		  		
			    <tr style="cursor: pointer;">
			      <td>${ categoryRow.id }</td>
			      <td>${ categoryRow.title }</td>
			      <td style="text-align: end;">
			      	<fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${ categoryRow.createdAt.getTime() }" />
			      </td>
			    </tr>
		  	</c:forEach>
		  </tbody>
		</table>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
