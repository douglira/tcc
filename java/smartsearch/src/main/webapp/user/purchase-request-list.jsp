<%@ page import="models.Person" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link
            href="<%=request.getContextPath() %>/assets/fonts/montserrat.css"
            rel="stylesheet"
            type="text/css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/fontawesome/css/all.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/toast/toastr.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/purchase-request-list.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="userPRList" class="container mb-sm-5 mb-md-5" v-if="purchaseRequests.length">
    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body d-flex flex-nowrap align-items-center">
            <h1 class="text-muted">
                Histórico: Pedidos de compra
            </h1>
        </div>
    </div>

    <a  v-for="(pr, prIndex) in purchaseRequests" :key="prIndex + '_' + pr.id" :href="'/account/purchase_request/details?id=' + pr.id" style="text-decoration: none;">
        <div class="card text-muted mb-3">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5>&numero;&nbsp;<i>{{ pr.id }}</i></h5>
                <small>Criado em: {{ formatDatetime(pr.createdAt) }}</small>
            </div>
            <div class="card-body">
                <h6 class="card-title text-center text-uppercase">{{ getPRStage(pr.stage) }}</h6>
                <div class="card-text d-flex flex-column flex-md-row justify-content-md-between">
                    <div class="d-flex flex-column align-items-md-start align-items-center">
                        <span>Visualizações: <strong>{{ pr.viewsCount}}</strong></span>
                        <span>Número de cotações: <strong>{{ pr.quotes.length }}</strong></span>
                        <span>Relevância atingida: <strong>{{ pr.propagationCount }}</strong></span>
                    </div>
                    <div class="d-flex flex-column align-items-md-end align-items-center">
                        <span>Total: <strong class="text-success">{{ formatCurrency(pr.totalAmount) }}</strong></span>
                        <span>Visibilidade: <strong>{{ pr.quotesVisibility ? 'Permitida' : 'Restrita' }}</strong></span>
                        <span>Expira em: <strong>{{ formatDatetime(pr.dueDate) }}</strong></span>
                    </div>
                </div>
            </div>
        </div>
    </a>
</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/inputmask/dist/jquery.inputmask.bundle.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/moment.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/purchase-request-list.js"></script>
</body>
</html>