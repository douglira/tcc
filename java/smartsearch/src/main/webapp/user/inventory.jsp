<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/user-inventory.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="userInventory" class="container mb-sm-5 mb-md-5">
    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body">
            <h1 class="card-title text-muted text-uppercase">Estoque</h1>
        </div>
    </div>

    <div class="container-actions">
        <div class="dropdown" style="display: flex; justify-content: flex-end;">
            <button
                    class="btn btn-light header-btn_dropdown text-info dropdown-toggle"
                    style="margin: 0 !important; padding: 0 !important;"
                    type="button"
                    id="dropdownProductActions"
                    data-toggle="dropdown"
                    aria-haspopup="true"
                    aria-expanded="false">
                Ações
            </button>
            <div class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownProductActions">
                <a
                        class="dropdown-item"
                        role="button"
                        href="<%=request.getContextPath()%>/account/new_product">
                    Novo produto
                </a>
            </div>
        </div>
    </div>

    <div class="container-products">
        <template v-if="products.length">
            <div class="card border-light card-product" v-for="product in products"
                 :key="product.id">
                <img class="card-img-product" :src="product.thumbnail.urlPath" :alt="product.thumbnail.name">
                <div class="card-body card-body-product">
                    <div href="javascript: void(0)" class="card-body-product_primary--info">
                        <h3 class="text-secondary">{{ product.title }}</h3>
                        <span class="text-success">R$ {{ product.basePrice.toFixed(2) }}</span>
                    </div>
                    <div class="card-body-product_secondary--info">
                        <small v-if="product.situation === 'LINKED'" class="text-muted">Vinculado</small>
                        <small class="text-warning" v-else>Desvinculado</small>
                        <div>
                            <p class="text-dark ">Vendidos: {{ product.soldQuantity }}</p>
                            <p :class="[product.availableQuantity > 5 ? 'text-dark' : 'text-danger']">Em estoque: {{
                                product.availableQuantity }}</p>
                        </div>
                    </div>
                    <a role="button" aria-pressed="true"
                   	   :href="'/account/products/details?id=' + product.id"
                       class="btn btn btn-outline-info btn-block btn-sm btn-product-details">
                        Ver detalhes
                    </a>
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
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/user-inventory.js"></script>
</body>
</html>
