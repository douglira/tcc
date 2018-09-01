<%@page import="models.User"%>
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
</head>
<body>

	<jsp:include page="/header.jsp"/>
	
	<%
		ArrayList<User> users = (ArrayList<User>) request.getAttribute("users");
	%>

	<div class="container">
		<h1>Área de administrador</h1>
		<table class="table table-hover">
		  <thead>
		    <tr>
		      <th scope="col">Nome</th>
		      <th scope="col">Email</th>
		      <th scope="col" style="text-align: end;">Data de cadastro</th>
		    </tr>
		  </thead>
		  <tbody>
		  	
		  	<c:forEach items="${ users }" var="userRow">		  		
			    <tr>
			      <td>${ userRow.displayName }</td>
			      <td>${ userRow.email }</td>
			      <td style="text-align: end;">
			      	<fmt:formatDate pattern="dd/MM/yyyy" value="${ userRow.createdAt }" />
			      </td>
			    </tr>
		  	</c:forEach>
		  </tbody>
		</table>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/popper-1.14.4.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>
