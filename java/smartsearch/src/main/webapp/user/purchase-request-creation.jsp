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
                                <input type="number" class="form-control" id="productItemQuantity" autofocus min="1" step="1" v-model="modalData.quantity">
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

    <div class="modal fade" id="modalProductItemRemove" tabindex="-1" role="dialog" aria-labelledby="modalProductItemRemove" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title text-info">{{ modalData.productItemTitle }}</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <form @submit.prevent="confirmRemove">
                    <div class="modal-body">
                        <div class="d-flex align-item-center text-muted">
                            <i class="fas fa-exclamation-triangle" style="font-size: 20px;"></i>
                            &nbsp;
                            <p>Remover este produto?</p>
                        </div>
                        <div class="modal-footer d-flex justify-content-between">
                            <button type="button" class="btn btn-light text-muted" data-dismiss="modal">Cancelar</button>
                            <button type="submit" class="btn btn-success">Remover</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body d-flex flex-nowrap align-items-center">
            <h1 class="text-muted page-title">
                Pedido de Compra&nbsp;&#45;&nbsp;&numero;
                <i>{{ purchaseRequest.id }}</i>
                <br>
                <small class="pr-created_at">
                    <strong>Criado em:</strong>
                    &nbsp;
                    {{ formatFullDate(purchaseRequest.createdAt) }}
                </small>
            </h1>
            <button class="text-secondary pr-btn-delete" type="button" @click="deletePurchaseRequest">
                <i class="fas fa-trash"></i>
            </button>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="bg-dark w-100 d-flex justify-content-center align-items-center">
            <span
                tabindex="0"
                data-toggle="popover"
                class="text-white pr-propagation-text">
                Abrangência atual
                &nbsp;
                <strong class="bg-light text-dark pr-propagation-count">
                    {{ purchaseRequest.propagationCount }}
                </strong>
            </span>
        </div>
        <div class="card-body">
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
                        <span class="text-success pr-item-price">{{ formatCurrency(productList.product.basePrice) }}</span>
                    </div>
                    <p class="pr-item-quantity text-secondary">
                        Qtd.
                        &nbsp;
                        <strong>{{ productList.quantity }}</strong>
                    </p>
                    <button type="button" class="text-secondary pr-item-actions" @click="onClickEditProduct(productList)">
                        <i class="far fa-edit"></i>
                    </button>
                    <button type="button" class="text-danger pr-item-actions" @click="onClickRemoveProduct(productList)">
                        <i class="fas fa-times"></i>
                    </button>
                </li>
            </ul>
            <div class="d-flex flex-nowrap flex-column flex-sm-column flex-md-column flex-lg-row-reverse">
                <div style="flex: 1;">
                    <div class="pr-total_amount">
                        Total:
                        &nbsp;
                        <span class="text-success">{{ formatCurrency(purchaseRequest.totalAmount) }}</span>
                    </div>
                    <p class="text-right text-secondary">
                        Última atualização:&nbsp;
                        <strong>{{ formatDatetime(purchaseRequest.updatedAt) }}</strong>
                    </p>
                </div>
                <div style="flex: 1; margin: 20px 0;">
                    <div class="form-group">
                        <label for="additionalData">Informações adicionais</label>
                        <textarea class="form-control" id="additionalData" aria-describedby="additionalDataInfo" rows="3" v-model="prAdditionalData"></textarea>
                        <small id="additionalDataInfo" class="form-text text-muted">Ex: envio, transportadora, negociação...</small>
                    </div>
                </div>
            </div>
            <button type="button" class="btn btn-info btn-lg btn-block btn-publish" @click="onClickPublish">Publicar pedido</button>
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