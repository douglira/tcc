<%@page import="enums.UserRoles"%>
<%@page import="models.User"%>
<% 
	User user = (User) session.getAttribute("loggedUser"); 
%>

<header class="header">
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
				<li class="nav-item active">
					<a class="nav-link" href="#">Categorias</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="#">Destaques</a>
				</li>
				<li class="nav-item">
					<a class="nav-link" href="#">Ofertas da semana</a>
				</li>				
				
			</ul>
			
			<div class="form-inline my-2 my-lg-0 dropdown">
				<button 
				class="btn btn-light header-btn_dropdown text-secondary dropdown-toggle" 
				type="button" 
				id="dropdownOptions" 
				data-toggle="dropdown" 
				aria-haspopup="true" 
				aria-expanded="false"
				>
					<%
						if (user == null) {
					%>
						Minha conta
					<% } else { %>
						Olá, <strong><%= user.getDisplayName() %></strong>
					<% } %>
				</button>
				<div class="dropdown-menu" aria-labelledby="dropdownOptions">
					<% if (user != null && user.getRole() == UserRoles.ADMIN) { %>
					<a class="dropdown-item font-weight-bold" href="<%=request.getContextPath()%>/admin">(administrador)</a>
					<div class="dropdown-divider"></div>
					<a class="dropdown-item" href="<%=request.getContextPath()%>/admin">Lista de usuários</a> 
					<a class="dropdown-item" href="#">Painel de categorias</a>
					<% } else {  %>
					<a class="dropdown-item" href="<%=request.getContextPath()%>/account">Pedidos de compra</a> 
					<a class="dropdown-item" href="#">Orçamentos lançados</a>
					<a class="dropdown-item" href="#">Meus dados</a>
					<% } %>
					
					<div class="dropdown-divider"></div>
					
					<%		
						if (user == null) {
					%>
						<a class="dropdown-item" href="<%=request.getContextPath()%>/signin.jsp">Entrar</a>
					<% } else { %>
						<a class="dropdown-item" href="<%=request.getContextPath()%>/auth">Sair</a>
					<% } %>
				</div>
			</div>
			
			<form class="form-inline my-2 my-lg-0">
				<input class="form-control mr-sm-2" type="search"
					placeholder="Buscar por pedidos de compra..." aria-label="Pesquisar">
				<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Pesquisar</button>
			</form>
		</div>
	</nav>
</header>