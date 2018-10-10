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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/purchase-request-creation.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="userPRCreation" class="container mb-sm-5 mb-md-5" v-if="purchaseRequest && purchaseRequest.listProducts.length">
    <div class="modal fade" id="modalProductItemEdit" tabindex="-1" role="dialog" aria-labelledby="modalProductItemEdit" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title text-info">{{ modalData.productItemTitle }}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <form @submit.prevent="saveEditProduct">
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="productItemQuantity">Quantidade</label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="productItemQuantity" min="1" step="1" v-model="modalData.quantity">
                                <div class="input-group-append">
                                    <span class="input-group-text">Un.</span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="productItemAdditionalSpec">Especificações adicionais</label>
                            <textarea class="form-control" id="productItemAdditionalSpec" rows="3" v-model="modalData.additionalSpec"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer d-flex justify-content-between">
                        <button type="button" class="btn btn-light text-muted" data-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-success">Salvar alterações</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body">
            <h1 class="card-title text-muted text-uppercase">Pedido de Compra&nbsp;&#45;&nbsp;N&#176;<i>{{ purchaseRequest.id }}</i></h1>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body">
            <p class="text-dark pr-propagation-text">
                <i class="far fa-question-circle text-dark pr-propagation-icon"></i>
                &nbsp;
                Abrangência atual
                &nbsp;
                <span class="badge badge-dark font-weight-bold">{{ purchaseRequest.propagationCount }}</span>
            </p>
            <ul class="list-group list-group-flush">
                <li
                    v-for="productList in purchaseRequest.listProducts"
                    :key="productList.product.id"
                    class="list-group-item pr-item">
                    <img
                        class="pr-item-thumbnail"
                        :src="productList.product.thumbnail.urlPath"
                        :alt="productList.product.thumbnail.name">
                    <div class="pr-item-title">
                        <span class="text-muted">{{ productList.product.title }}</span>
                        <span class="text-success pr-item-price">R$ {{ productList.product.basePrice.toFixed(2) }}</span>
                    </div>
                    <p class="pr-item-quantity text-secondary">Qtd.&nbsp;{{ productList.quantity }}</p>
                    <button type="button" class="text-info pr-item-edit_button" @click="onClickEditProduct(productList)">
                        <i class="far fa-edit"></i>
                    </button>
                </li>
            </ul>
        </div>
    </div>
</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/purchase-request-creation.js"></script>
</body>
</html>