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

    <template v-for="(pr, prIndex) in purchaseRequests">
        <div class="card d-flex flex-md-row flex-sm-row flex-lg-row text-muted mb-3">


            <div id="carouselProductPictures" class="carousel slide" data-ride="carousel">
                <div class="carousel-inner"
                     style="width: 250px; display: flex; justify-content: center; padding: 10px; -webkit-box-sizing: border-box;-moz-box-sizing: border-box;box-sizing: border-box;">
                    <div v-for="(item, itemIndex) in pr.listProducts"
                         :key="itemIndex + '_' + item.product.id"
                        :class="{'carousel-item': true, active: itemIndex === 0}"
                        style="text-align: center;">
                        <img class="img-carousel" :src="item.product.thumbnail.urlPath" :alt="item.product.thumbnail.name">
                    </div>
                </div>
                <template v-if="pr.listProducts.length > 1">
                    <a class="carousel-control-prev" href="#carouselProductPictures" role="button" data-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="sr-only">Anterior</span>
                    </a>
                    <a class="carousel-control-next" href="#carouselProductPictures" role="button" data-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="sr-only">Próximo</span>
                    </a>
                </template>
            </div>

            <div class="w-100">
                <a :href="'/account/purchase_request/details?id=' + pr.id"
                   style="text-decoration: none; background-color: inherit !important; border-bottom: none !important;"
                   class="card-header d-flex justify-content-between align-items-center">
                    <h5>&numero;&nbsp;<i>{{ pr.id }}</i></h5>
                    <small>Criado em: {{ formatDatetime(pr.createdAt) }}</small>
                </a>
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
        </div>
    </template>
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