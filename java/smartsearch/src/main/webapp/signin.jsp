<html lang="pt-br">
<head>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, shrink-to-fit=no">
<link
	href="https://fonts.googleapis.com/css?family=Montserrat:400,400i,500,500i,600,700"
	rel="stylesheet">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/css/bootstrap.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/signin.css">
</head>
<body>

	<jsp:include page="header.jsp"/>

	<div class="container">
		<div class="content">
			<h1>Login</h1>
			
			<%
				String error = (String) request.getAttribute("error");
				if (error != null) {
			%>
				<div class="div-error">
					<div class="alert alert-danger"><%= error %></div>
				</div>
			<%
				}
			%>
			<form action="auth" method="POST">
				<div class="form-group">
					<label for="emailInput">Email</label>
					<input class="form-control" type="email" id="emailInput" name="email" placeholder="Seu email de acesso" />
				</div>
				<div class="form-group">
					<label for="passwordInput">Senha</label>
					<input class="form-control" type="password" id="passwordInput" name="password" placeholder="Sua senha" />
				</div>
				<button class="btn btn-primary" type="submit" value="signin" name="action" >Entrar</button>
				<small>ou</small>
				<p><a href="<%=request.getContextPath() %>/register" class="text-info">Não possui conta? Cadastrar</a></p>
			</form>
		</div>
	</div>

	<script src="<%=request.getContextPath()%>/assets/libs/js/jquery-3.3.1.slim.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/js/popper-1.14.4.min.js"></script>
	<script src="<%=request.getContextPath()%>/assets/libs/js/bootstrap.min.js"></script>
</body>
</html>
