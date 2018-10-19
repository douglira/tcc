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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/purchase-request-suggest.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="userPRSuggest" class="container mb-sm-5 mb-md-5" v-if="purchaseRequest && purchaseRequest.id">
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
            <div v-if="countdown" class="countdown text-danger">
                <span class="font-weight-bold text-dark">Expira em&colon;</span>
                &nbsp;
                {{ countdown }}
            </div>
            <div v-else class="countdown text-danger text-uppercase">
                EXPIRADO!!
            </div>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body d-flex flex-column flex-nowrap align-items-center">

            <div class="list-group list-group-flush list-group-container">
                <template v-for="productList in purchaseRequest.listProducts">
                    <button
                            :class="['list-group-item list-group-item-action list-item-row', productList.additionalSpec ? 'font-weight-bold list-item-row_tooltip' : '']"
                            :style="productList.additionalSpec ? { cursor: 'pointer' } : null"
                            type="button"
                            data-toggle="collapse"
                            :data-target="'#collapse' + productList.product.id"
                            aria-expanded="true"
                            :aria-controls="'collapse' + productList.product.id">
                        <img :src="productList.product.thumbnail.urlPath" :alt="productList.product.thumbnail.name">
                        <span style="flex: 1;">{{ productList.product.title }}</span>
                        <span class="text-right">
                            {{ productList.quantity }}
                            &nbsp;
                            unidade(s)
                        </span>
                    </button>
                    <div v-if="productList.additionalSpec" :id="'collapse' + productList.product.id" class="collapse" style="margin-bottom: 5px;" >
                        <div class="card card-body">
                            <strong>Especificações adicionais&colon;&nbsp;</strong>
                            {{ productList.additionalSpec }}
                        </div>
                    </div>
                </template>
            </div>
            <div class="w-100 text-success text-right font-weight-bold" style="padding: 15px 20px">
                <span class="text-dark text-uppercase">Total&colon;</span>
                &nbsp;
                {{ formatCurrency(purchaseRequest.totalAmount) }}
            </div>
            <div v-if="purchaseRequest.additionalData" class="card w-100">
                <div class="card-body">
                    <h2 style="font-size: 16px;" class="card-title">Informações gerais adicionais</h2>
                    <div style="font-size: 14px;" class="card-text">{{ purchaseRequest.additionalData }}</div>
                </div>
            </div>
            <hr>
            <br>
            <template v-if="purchaseRequest.stage === 'UNDER_QUOTATION'">
                <template v-if="purchaseRequest.quotesVisibility">
                    <template v-if="purchaseRequest.quotes && purchaseRequest.quotes.length">

                    </template>
                    <template v-else>
                        <div class="card w-100 text-center text-white" style="background-color: #999">
                            <div class="card-header text-uppercase font-weight-bold">
                                Sob cotações
                            </div>
                            <div class="card-body">
                                <h2 style="font-size: 16px;" class="card-title">Nenhuma cotação enviada</h2>
                                <p class="card-text">Seja o primeiro a enviar uma cotação para este pedido de compra.</p>
                                <a href="#sendQuotations">
                                    <i class="fas fa-comments text-white" style="font-size: 32px"></i>
                                </a>
                            </div>
                        </div>
                    </template>
                </template>
                <template v-else>
                    <div class="card w-100 text-center text-muted">
                        <div class="card-header text-uppercase font-weight-bold">
                            Sob cotações
                        </div>
                        <div class="card-body">
                            <h2 style="font-size: 16px;" class="card-title">Visualização indisponível</h2>
                            <p class="card-text font-italic">As cotações para este pedido de compra são restritas. Visualização disponível apenas ao comprador.</p>
                            <i class="far fa-eye-slash" style="font-size: 32px"></i>
                        </div>
                    </div>
                </template>
                <div id="sendQuotations" class="w-100" style="margin-top: 15px;">
                    <div class="input-group">
                        <input class="form-control form-control-lg" type="text" placeholder="Pesquisar em seu estoque...">
                        <div class="input-group-append">
                            <button type="button" class="btn btn-light text-secondary border">
                                <i class="fas fa-search" style="font-size: 28px; padding: 5px; box-sizing: border-box;"></i>
                            </button>
                        </div>
                    </div>

                    <div class="row d-flex align-items-center">
                        <div class="card col-md-5 border-0" style="align-self: flex-start;">
                            <div class="card-body">
                                <div class="card-title font-weight-bold">Seus produtos</div>
                                <div class="list-group">
                                    <div v-for="product in productsInventory" class="custom-control custom-checkbox mb-3">
                                        <input type="checkbox" :value="product" v-model="selectedProducts" class="custom-control-input" :id="'checkProduct' + product.id">
                                        <label class="custom-control-label w-100" :for="'checkProduct' + product.id">
                                            <span
                                                class="list-group-item list-group-item-action border-0" style="padding-top: 0;">
                                                {{ product.title }}
                                            </span>
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="d-flex flex-column align-items-center justify-content-center">
                                <span class="font-weight-bold">Qtd.</span>
                                <input type="number" step="5" min="1" v-model="inputProductQuantity" class="input-product-quantity text-dark">
                                <button type="button" class="button-product-quantity">
                                    <i class="fas fa-plus-circle text-secondary" style="font-size: 28px; margin-top: 10px;"></i>
                                </button>
                            </div>
                        </div>
                        <div class="card col-md-5 border-0" style="align-self: flex-start;">
                            <div class="card-body">
                                <div class="card-title font-weight-bold">Produtos selecionados</div>
                                <div class="list-group">
                                    <a
                                        href="javascript:void(0)"
                                        v-for="product in productsQuote"
                                        @click="onSelectRemoveProject(product)"
                                        class="list-group-item list-group-item-action">
                                        {{ product.title }}
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6"></div>
                        <div class="col-md-6"></div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <div class="form-check" style="margin: 20px 0;">
                                <input class="form-check-input" type="checkbox" v-model="quoteCustomCost" id="customCostCheckbox">
                                <label class="form-check-label" for="customCostCheckbox">
                                    Editar custo total:
                                </label>
                            </div>
                            <label for="costInput">Custo total</label>
                            <input id="costInput" type="number" :disabled="!quoteCustomCost" class="form-control text-success" min="0.01" step="100">
                        </div>
                        <div class="form-group col-md-6">
                            <label for="additionalData">Informações adicionais</label>
                            <textarea class="form-control" id="additionalData" aria-describedby="additionalDataInfo" rows="4" v-model="quoteAdditionalData"></textarea>
                        </div>
                    </div>
                    <button type="button" class="btn btn-info btn-lg btn-block" style="padding: 30px auto !important;" @click="onClickPushQuotation">Lançar cotação</button>
                </div>
            </template>
            <template v-else>

            </template>


        </div>
    </div>
</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/purchase-request-suggest.js"></script>
</body>
</html>