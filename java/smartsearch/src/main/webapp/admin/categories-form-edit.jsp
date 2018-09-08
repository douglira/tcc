<%@page import="enums.Status"%>
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
	
	<div class="modal fade" id="modalActions" tabindex="-1" role="dialog" aria-labelledby="modalActionsLabel" aria-hidden="true">
	  <div class="modal-dialog modal-dialog-centered" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title" id="modalActionsLabel"></h5>
	        <button type="button" class="close" data-dismiss="modal" aria-label="Cancelar">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body p-4">
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">Cancelar</button>
	        <button type="button" class="btn btn-danger" data-dismiss="modal" id="btnModalOk">Concluir</button>
	      </div>
	    </div>
	  </div>
	</div>

	<div class="container" style="margin-bottom: 20px;">
		<h1 style="font-size: 28px;">Categoria:&nbsp;<i><%=category.getTitle()%></i></h1>
		<hr>
		<p class="text-secondary">
			Edite abaixo o título e descrição da categoria.
		</p>
		
		<form action="/admin/categories/edit" method="POST">
			<div class="d-inline-flex justify-content-between w-100">
			
				<div class="input-group mb-3 w-50">
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
				
				<div class="dropdown">
					<button class="btn btn-light text-muted dropdown-toggle" type="button" id="dropdownMore" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
				    	Mais
				  	</button>
				  	<div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMore">
				    	<button 
				    		data-toggle="modal" 
				    		data-target="#modalActions" 
				    		data-action="toggleStatus"
				    		class="dropdown-item"
				    		type="button" 
				    		id="btnToggleStatus" 
				    		style="cursor: pointer;">
				    		<c:choose>
								<c:when test="<%=category.getStatus() == Status.ACTIVE%>">
				    				Inativar
								</c:when>
								<c:otherwise>
									Ativar
								</c:otherwise>
							</c:choose>
			    		</button>
				    	<div class="dropdown-divider"></div>
				    	<button 
				    		data-toggle="modal" 
				    		data-target="#modalActions" 
				    		data-action="deleteCategory"
				    		class="dropdown-item text-danger" 
				    		type="button" 
				    		id="btnDelete" 
				    		style="cursor: pointer;">Excluir</button>
				  	</div>
				</div>
			</div>
			<div class="form-group w-100">
				<label for="category-title">Título</label>
				<input class="form-control" type="text" id="category-title" name="category-title" value="${category.title}">
			</div>
			<div class="form-group">
				<label for="category-description">Descrição</label>
				<textarea class="form-control" id="category-description" rows="3" value="${category.description}"></textarea>
			</div>
			<input type="hidden" id="category-status" value="${category.status}"/>
			<hr>
			<button type="submit" class="btn btn-primary btn-lg btn-block" name="action" value="edit">Salvar</button>
		</form>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/js/categories-form-edit.js"></script>
</body>
</html>
