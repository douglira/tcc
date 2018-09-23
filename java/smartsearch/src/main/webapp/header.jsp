<%@page import="enums.UserRoles"%>
<%@page import="models.User"%>
<%
	User user = (User) session.getAttribute("loggedUser");
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<header id="appHeader" class="header">
	<nav class="navbar navbar-expand-lg navbar-light bg-light">
		<a class="navbar-brand" href="#">LOGOTIPO</a>
		<button class="navbar-toggler" type="button" data-toggle="collapse"
			data-target="#navbarSupportedContent"
			aria-controls="navbarSupportedContent" aria-expanded="false"
			aria-label="Toggle navigation">
			<span class="navbar-toggler-icon"></span>
		</button>

		<div class="collapse navbar-collapse" id="navbarSupportedContent">
			<ul class="navbar-nav mr-auto">
				<li class="nav-item"><a class="nav-link" href="#">Sobre nós</a>
				</li>
				<li class="nav-item active"><a class="nav-link" href="#">Pedidos
						de compra</a></li>
				<li class="nav-item"><a class="nav-link" href="#">Recentes</a>
				</li>

			</ul>


			<div class="form-inline my-2 my-lg-0 dropdown">
				<button
					class="btn btn-light header-btn_dropdown text-secondary dropdown-toggle"
					type="button" id="dropdownOptions" data-toggle="dropdown"
					aria-haspopup="true" aria-expanded="false">
					<c:choose>
						<c:when test="<%=user == null%>">
						Minha conta
					</c:when>
						<c:otherwise>
						Olá, <strong><%=user.getDisplayName()%></strong>
						</c:otherwise>
					</c:choose>
				</button>
				
				
				<template v-if="username">
					<button class="btn-notification">
						<i class="far fa-bell"></i>
					</button>
				</template>
				
				
				
				<div class="dropdown-menu" aria-labelledby="dropdownOptions">
					<c:choose>
						<c:when
							test="<%=user != null && user.getRole() == UserRoles.ADMIN%>">
							<a class="dropdown-item font-weight-bold"
								href="<%=request.getContextPath()%>/admin?page=1&perPage=15">(administrador)</a>
							<div class="dropdown-divider"></div>
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/admin?page=1&perPage=15">Lista
								de usuários</a>
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/admin/categories">Painel
								de categorias</a>
						</c:when>
						<c:otherwise>
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/account">Meus pedidos de
								compra</a>
							<a class="dropdown-item" href="#">Orçamentos lançados</a>
							<a class="dropdown-item" href="#">Meu estoque</a>
							<a class="dropdown-item" href="<%=request.getContextPath()%>/account/me">Meus dados</a>
						</c:otherwise>
					</c:choose>

					<div class="dropdown-divider"></div>

					<c:choose>
						<c:when test="<%=user == null%>">
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/signin">Entrar</a>
							<a class="dropdown-item text-info"
								href="<%=request.getContextPath()%>/register">Não possui
								conta?</a>
						</c:when>
						<c:otherwise>
							<a class="dropdown-item"
								href="<%=request.getContextPath()%>/auth">Sair</a>
						</c:otherwise>
					</c:choose>
				</div>
			</div>

			<form class="form-inline my-2 my-lg-0">
				<input class="form-control mr-sm-2" type="search"
					placeholder="Buscar por pedidos de compra..."
					aria-label="Pesquisar">
				<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Pesquisar</button>
			</form>
		</div>
	</nav>
</header>
<c:if test="<%=user != null && user.getRole() == UserRoles.COMMON%>">
	<input type="hidden" id="inputHeaderUsername" value="<%= user.getUsername() %>"/>
	<script src="<%=request.getContextPath() %>/assets/libs/vuejs/vue-dist.js"></script>
	<script src="<%=request.getContextPath() %>/assets/js/header-websocket.js"></script>
</c:if>