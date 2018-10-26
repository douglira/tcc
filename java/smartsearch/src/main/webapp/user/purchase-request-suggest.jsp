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
                <span class="font-weight-bold text-muted">Expira em&colon;</span>
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
                <template v-for="item in purchaseRequest.listProducts">
                    <button
                            :class="['list-group-item list-group-item-action list-item-row', item.additionalSpec ? 'font-weight-bold list-item-row_tooltip' : '']"
                            :style="item.additionalSpec ? { cursor: 'pointer' } : null"
                            type="button"
                            data-toggle="collapse"
                            :data-target="'#collapse' + item.product.id"
                            aria-expanded="true"
                            :aria-controls="'collapse' + item.product.id">
                        <img :src="item.product.thumbnail.urlPath" :alt="item.product.thumbnail.name">
                        <span style="flex: 1;">{{ item.product.title }}</span>
                        <span class="text-right">
                            {{ item.quantity }}
                            &nbsp;
                            unidade(s)
                        </span>
                    </button>
                    <div v-if="item.additionalSpec" :id="'collapse' + item.product.id" class="collapse" style="margin-bottom: 5px;" >
                        <div class="card card-body">
                            <strong>Especificações adicionais&colon;&nbsp;</strong>
                            {{ item.additionalSpec }}
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
                        <div class="card w-100 text-center text-muted">
                            <div class="card-header text-uppercase font-weight-bold">
                                Sob cotações
                            </div>
                            <div class="card-body d-flex flex-column align-items-center" style="padding: 15px;">
                                <h2 style="font-size: 16px;" class="card-title">Visualização disponível</h2>
                                <p class="card-text font-italic">Cotações em aberto.</p>
                                <a href="#sendQuotations">
                                    <i class="fas fa-comments text-muted" style="font-size: 32px"></i>
                                </a>
                                <div class="list-group list-group-flush" style="width: 100%; margin-top: 15px;">
                                    <template v-for="quote in purchaseRequest.quotes">
                                        <a
                                                class="list-group-item list-group-item-action flex-row justify-content-start bg-light"
                                                :style="quote.seller.id === loggedSeller.id ? { cursor: 'pointer' } : null"
                                                data-toggle="collapse"
                                                :data-target="'#collapse' + quote.id"
                                                aria-expanded="true"
                                                :aria-controls="'collapse' + quote.id"
                                                href="javascript:void(0)">
                                            <div :class="['d-flex flex-row w-100', quote.seller.id === loggedSeller.id ? 'justify-content-end' : 'justify-content-start']">
                                                <div class="d-flex flex-column w-50">
                                                    <div class="d-flex flex-row w-100 justify-content-between">
                                                        <strong class="text-secondary" style="margin-bottom: 3px; font-size: 15px;">Cotação: <span class="text-success">{{ formatCurrency(quote.totalAmount) }}</span></strong>
                                                        <small class="text-monospace text-muted">Criado em&colon;&nbsp;{{ formatDatetime(quote.createdAt) }}</small>
                                                    </div>
                                                    <div v-if="quote.seller.id === loggedSeller.id" class="d-flex flex-row w-100 justify-content-center align-items-center"><i>- Você -</i></div>
                                                    <br v-else>
                                                    <div class="d-flex flex-row w-100 justify-content-between align-items-center">
                                                        <i class="text-muted" style="margin: 3px 0; font-size: 14px;">Desconto&colon;&nbsp;{{ quote.discount }}&percnt;</i>
                                                        <i><small class="text-monospace text-muted">Até&colon;&nbsp;{{ formatDate(quote.expirationDate) }}</small></i>
                                                    </div>
                                                    <i
                                                            v-if="quote.customListProduct && quote.customListProduct.length"
                                                            class="fas fa-angle-down text-muted d-flex w-100 justify-content-center align-items-center"
                                                            style="font-size: 28px;"></i>
                                                </div>
                                            </div>
                                        </a>
                                        <div v-if="quote.customListProduct && quote.customListProduct.length" :id="'collapse' + quote.id" class="collapse" style="margin-bottom: 5px;" >
                                            <ul class="list-group">
                                                <li
                                                        v-for="listProduct in quote.customListProduct"
                                                        class="list-group-item d-flex justify-content-between align-items-center">
                                                    {{ listProduct.product.title }}
                                                    <span class="badge badge-info badge-pill">{{ listProduct.quantity }}</span>
                                                </li>
                                            </ul>
                                        </div>
                                    </template>
                                </div>
                            </div>
                        </div>
                    </template>
                    <template v-else>
                        <div class="card w-100 text-center text-muted">
                            <div class="card-header text-uppercase font-weight-bold">
                                Sob cotações
                            </div>
                            <div class="card-body">
                                <h2 style="font-size: 16px;" class="card-title">Nenhuma cotação enviada</h2>
                                <p class="card-text">Seja o primeiro a enviar uma cotação para este pedido de compra.</p>
                                <a href="#sendQuotations">
                                    <i class="fas fa-comments text-muted" style="font-size: 32px"></i>
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
                            <p class="card-text font-italic">As cotações para este pedido de compra são restritas apenas ao comprador, podendo visualizar somente suas próprias cotações abaixo.</p>
                            <i class="far fa-eye-slash" style="font-size: 32px"></i>
                            <div class="d-flex flex-row flex justify-content-center w-100" style="padding: 15px;">
                                <div class="list-group list-group-flush" style="width: 100%; max-width: 720px">
                                    <template v-for="quote in purchaseRequest.quotes">
                                        <a
                                                class="list-group-item list-group-item-action flex-column align-items-start bg-light"
                                                style="cursor: pointer;"
                                                href="javascript:void(0)"
                                                data-toggle="collapse"
                                                :data-target="'#collapse' + quote.id"
                                                aria-expanded="true"
                                                :aria-controls="'collapse' + quote.id">
                                            <div class="d-flex flex-row w-100 justify-content-between">
                                                <strong class="text-secondary" style="margin-bottom: 3px; font-size: 15px;">Cotação: <span class="text-success">{{ formatCurrency(quote.totalAmount) }}</span></strong>
                                                <small class="text-monospace text-muted">Criado em&colon;&nbsp;{{ formatDatetime(quote.createdAt) }}</small>
                                            </div>
                                            <div class="d-flex flex-row w-100 justify-content-between align-items-center">
                                                <i class="text-muted" style="margin: 3px 0; font-size: 14px;">Desconto&colon;&nbsp;{{ quote.discount }}&percnt;</i>
                                                <i><small class="text-monospace text-muted">Até&colon;&nbsp;{{ formatDate(quote.expirationDate) }}</small></i>
                                            </div>
                                            <i class="fas fa-angle-down text-muted d-flex w-100 justify-content-center align-items-center" style="font-size: 28px;"></i>
                                        </a>
                                        <div :id="'collapse' + quote.id" class="collapse" style="margin-bottom: 5px;" >
                                            <ul class="list-group">
                                                <li
                                                        v-for="listProduct in quote.customListProduct"
                                                        class="list-group-item d-flex justify-content-between align-items-center">
                                                    {{ listProduct.product.title }}
                                                    <span class="badge badge-info badge-pill">{{ listProduct.quantity }}</span>
                                                </li>
                                            </ul>
                                        </div>
                                    </template>
                                </div>
                            </div>
                        </div>
                    </div>
                </template>
                <div id="sendQuotations" class="w-100" style="margin-top: 15px;">
                    <div class="input-group">
                        <input class="form-control form-control-lg" type="search" v-model="searchProduct" placeholder="Pesquisar em seu estoque...">
                        <div class="input-group-append">
                            <button type="button" class="btn btn-light text-secondary border" @click="onClickSearchProduct">
                                <i class="fas fa-search" style="font-size: 28px; padding: 5px; box-sizing: border-box;"></i>
                            </button>
                        </div>
                    </div>

                    <div class="row d-flex align-items-center">
                        <div class="card col-md-5 border-0" style="align-self: flex-start;">
                            <div class="card-body">
                                <div class="card-title font-weight-bold">Seus produtos</div>
                                <div class="list-group list-overflow">
                                    <div v-for="product in productsInventory" class="custom-control custom-checkbox mb-3 list-item-inventory-product" :id="'listItemProduct' + product.id">
                                        <input type="checkbox" :value="product" v-model="selectedProducts" class="custom-control-input" :id="'checkProduct' + product.id">
                                        <label class="d-flex flex-row align-items-start custom-control-label w-100" :for="'checkProduct' + product.id">
                                            <span
                                                class="list-group-item list-group-item-action border-0 list-item-product-title" style="padding-top: 0;">
                                                {{ fixListProductTitle(product.title) }}
                                            </span>
                                            <span class="badge badge-secondary badge-pill">{{ product.availableQuantity }}</span>
                                        </label>
                                    </div>
                                    <a
                                        @click="onClickLoadMoreProducts"
                                        href="javascript:void(0)"
                                        class="d-flex justify-content-center align-items-center list-group-item list-group-item-action border-0"
                                        style="padding-top: 2px !important; padding-bottom: 2px !important;">
                                        <i class="fas fa-angle-down" style="font-size: 28px; color: #999;"></i>
                                    </a>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-2">
                            <div class="d-flex flex-column align-items-center justify-content-center">
                                <span class="font-weight-bold">Qtd.</span>
                                <input type="number" step="5" min="1" v-model="inputProductQuantity" class="input-product-quantity text-dark" id="inputQuantity" @keydown="inputQuantityEnter">
                                <button type="button" class="button-product-quantity" @click="onClickAddProduct">
                                    <i class="fas fa-plus-circle text-secondary" style="font-size: 28px;"></i>
                                </button>
                            </div>
                        </div>
                        <div class="card col-md-5 border-0" style="align-self: flex-start;">
                            <div class="card-body">
                                <div class="card-title font-weight-bold">Produtos selecionados</div>
                                <div class="list-group list-overflow">
                                    <a
                                        href="javascript:void(0)"
                                        v-for="item in productsQuote"
                                        @click="onClickRemoveProject(item)"
                                        class="d-flex justify-content-between align-items-center list-group-item list-group-item-action list-item-selected-product">
                                        {{ item.product.title }}
                                        <span class="badge badge-info badge-pill">{{ item.quantity }}</span>
                                    </a>
                                </div>
                                <div v-if="quoteTotalAmount" class="card-footer text-success text-right">{{ formatCurrency(getTotalAmount()) }}</div>
                            </div>
                        </div>
                    </div>
                    <div class="form-row">
                        <div class="form-group col-md-6">
                            <div class="form-group">
                                <label for="costInput">Aplicar desconto (%)</label>
                                <input id="costInput" type="number" v-model="quoteDiscount"  min="0.01" max="100" class="form-control">
                            </div>
                            <div class="form-group">
                                <label for="expirationDate">Prazo da cotação</label>
                                <input id="expirationDate" type="date" v-model="quoteExpirationDate" class="form-control">
                            </div>
                        </div>
                        <div class="form-group col-md-6">
                            <label for="additionalData">Informações adicionais</label>
                            <textarea class="form-control" id="additionalData" aria-describedby="additionalDataInfo" rows="5" v-model="quoteAdditionalData"></textarea>
                        </div>
                    </div>
                    <button :disabled="invalidQuote" type="button" class="btn btn-info btn-lg btn-block" style="padding: 30px auto !important;" @click="onClickPushQuotation">Lançar cotação</button>
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
<script src="<%=request.getContextPath()%>/assets/libs/inputmask/dist/jquery.inputmask.bundle.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/moment.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/purchase-request-suggest.js"></script>
</body>
</html>