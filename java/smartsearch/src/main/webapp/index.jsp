<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link
            rel="stylesheet"
            href="<%=request.getContextPath() %>/assets/fonts/montserrat.css"
            type="text/css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/fontawesome/css/all.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/base.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/homepage.css">
</head>
<body>

<jsp:include page="header.jsp"/>

<div id="homepageContainer" class="container">
    <div class="container-products">
        <template v-if="products.length">
            <div class="card border-light card-product" v-for="product in products"
                 :key="product.id">
                <img class="card-img-product" :src="product.thumbnail.urlPath" :alt="product.thumbnail.name">
                <div class="card-body card-body-product">
                    <a href="javascript: void(0)" class="card-body-product_primary--info text-muted">
                        <h3 class="text-secondary">{{ product.title }}</h3>
                        <span class="text-success">{{ formatCurrency(product.basePrice) }}</span>
                    </a>
                    <div class="card-body-product_secondary--info">
                        <div>
                            <p class="text-dark ">Abrangência: {{ product.relevance }}</p>
                        </div>
                        <small class="text-muted">
                            Flutuação (R$)
                            <br>
                            {{ formatDecimal(product.minPrice) }}&nbsp;&hyphen;&nbsp;{{ formatDecimal(product.maxPrice) }}
                        </small>
                    </div>
                    <button type="button" @click="onClickAddToPR(product)"
                       class="btn btn btn-outline-info btn-block btn-sm btn-product-details">
                        Adicionar ao pedido
                    </button>
                </div>
            </div>
        </template>
        <template v-else>
            <div class="alert alert-light mt-sm-3 mt-md-3 text-center w-100">
                <span class="text-muted w-100">Nenhum produto cadastrado no momento</span>
            </div>
        </template>
    </div>
</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/popper-1.14.4.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/homepage.js"></script>
</body>
</html>
