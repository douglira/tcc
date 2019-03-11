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

    <div id="shipmentOptionsModal" class="modal" tabindex="-1" role="dialog">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"></h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="shipmentEstimatedTime">Prazo estimado de entrega</label>
                        <input id="shipmentEstimatedTime" type="date" class="form-control">
                    </div>
                    <template v-if="selectedShipmentOption === 'CUSTOM'">
                        <div class="form-group">
                            <label for="shipmentCost">Custo de envio</label>
                            <input id="shipmentCost" type="number" min="0.01" class="form-control">
                        </div>
                    </template>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
                    <button type="button" class="btn btn-success" data-dismiss="modal" @click="onClickAddShipmentOption">Adicionar</button>
                </div>
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
                                                    <span class="text-muted text-uppercase font-italic" style="padding-bottom: 10px;">{{ getDisplayQuoteStatus(quote.status) }}</span>
                                                    <small class="text-muted font-italic">
                                                        {{ quote.reason }}
                                                    </small>
                                                    <div class="d-flex flex-row">
                                                        <div class="d-flex flex-column align-items-start" style="flex: 1;">
                                                            <h6 class="align-self-start">Cotação</h6>
                                                            <span class="text-secondary">Valor:&nbsp;<span class="text-success">{{ formatCurrency(quote.totalAmount) }}</span></span>
                                                            <i class="text-muted" style="margin: 3px 0; font-size: 14px;">Desconto&colon;&nbsp;{{ quote.discount }}&percnt;</i>
                                                        </div>
                                                        <div class="d-flex flex-column align-items-end" style="flex: 1;">
                                                            <h6 class="align-self-end">Prazos</h6>
                                                            <small class="text-monospace text-muted">Criado em&colon;&nbsp;{{ formatDate(quote.createdAt) }}</small>
                                                            <i><small class="text-monospace text-muted">Válida até&colon;&nbsp;{{ formatDate(quote.expirationDate) }}</small></i>
                                                        </div>
                                                    </div>
                                                    <template v-if="quote.shipmentOptions && quote.shipmentOptions.length">
                                                        <div class="d-flex flex-column align-items-between">
                                                            <h6 class="align-self-center">Envios disponíveis</h6>
                                                            <div class="d-flex flex-column justify-content-center flex-md-row align-items-md-center">
                                                                <div v-for="(shipmentOption, shipmentOptionIndex) in quote.shipmentOptions" class="d-flex justify-content-between align-items-center" style="flex: 1;">
                                                                    <template v-if="shipmentOption.method === 'CUSTOM'">
                                                                        <blockquote class="d-flex flex-column blockquote" style="font-size: 14px; flex: 1;">
                                                                            <i>Frete customizado</i>
                                                                            <small>Prazo de entrega:&nbsp;{{ formatDate(shipmentOption.estimatedTime) }}</small>
                                                                            <small>Custo:&nbsp;<span class="text-success">{{ formatCurrency(shipmentOption.cost) }}</span></small>
                                                                        </blockquote>
                                                                    </template>

                                                                    <template v-if="shipmentOption.method === 'FREE'">
                                                                        <blockquote class="d-flex flex-column blockquote" style="font-size: 14px; flex: 1;">
                                                                            <i>Frete grátis</i>
                                                                            <small>Prazo de entrega:&nbsp;{{ formatDate(shipmentOption.estimatedTime) }}</small>
                                                                        </blockquote>
                                                                    </template>

                                                                    <template v-if="shipmentOption.method === 'LOCAL_PICK_UP'">
                                                                        <blockquote class="d-flex flex-column blockquote" style="font-size: 14px; flex: 1;">
                                                                            <i>Retirada no local</i>
                                                                        </blockquote>
                                                                    </template>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </template>
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
                            <p class="card-text font-italic">As cotações para este pedido de compra são restritas e visíveis apenas ao comprador, podendo visualizar somente suas próprias cotações abaixo.</p>
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
                                            <span class="text-muted text-uppercase font-italic" style="padding-bottom: 10px;">{{ getDisplayQuoteStatus(quote.status) }}</span>
                                            <small class="text-muted font-italic">
                                                {{ quote.reason }}
                                            </small>
                                            <div class="d-flex flex-row">
                                                <div class="d-flex flex-column align-items-start" style="flex: 1;">
                                                    <h6 class="align-self-start">Cotação</h6>
                                                    <span class="text-secondary">Valor:&nbsp;<span class="text-success">{{ formatCurrency(quote.totalAmount) }}</span></span>
                                                    <i class="text-muted" style="margin: 3px 0; font-size: 14px;">Desconto&colon;&nbsp;{{ quote.discount }}&percnt;</i>
                                                </div>
                                                <div class="d-flex flex-column align-items-end" style="flex: 1;">
                                                    <h6 class="align-self-end">Prazos</h6>
                                                    <small class="text-monospace text-muted">Criado em&colon;&nbsp;{{ formatDate(quote.createdAt) }}</small>
                                                    <i><small class="text-monospace text-muted">Válida até&colon;&nbsp;{{ formatDate(quote.expirationDate) }}</small></i>
                                                </div>
                                            </div>
                                            <template v-if="quote.shipmentOptions && quote.shipmentOptions.length">
                                                <div class="d-flex flex-column align-items-between">
                                                    <h6 class="align-self-center">Envios disponíveis</h6>
                                                    <div class="d-flex flex-column justify-content-center flex-md-row align-items-md-center">
                                                        <div v-for="(shipmentOption, shipmentOptionIndex) in quote.shipmentOptions" class="d-flex justify-content-between align-items-center" style="flex: 1;">
                                                            <template v-if="shipmentOption.method === 'CUSTOM'">
                                                                <blockquote class="d-flex flex-column blockquote" style="font-size: 14px; flex: 1;">
                                                                    <i>Frete customizado</i>
                                                                    <small>Prazo de entrega:&nbsp;{{ formatDate(shipmentOption.estimatedTime) }}</small>
                                                                    <small>Custo:&nbsp;<span class="text-success">{{ formatCurrency(shipmentOption.cost) }}</span></small>
                                                                </blockquote>
                                                            </template>

                                                            <template v-if="shipmentOption.method === 'FREE'">
                                                                <blockquote class="d-flex flex-column blockquote" style="font-size: 14px; flex: 1;">
                                                                    <i>Frete grátis</i>
                                                                    <small>Prazo de entrega:&nbsp;{{ formatDate(shipmentOption.estimatedTime) }}</small>
                                                                </blockquote>
                                                            </template>

                                                            <template v-if="shipmentOption.method === 'LOCAL_PICK_UP'">
                                                                <blockquote class="d-flex flex-column blockquote" style="font-size: 14px; flex: 1;">
                                                                    <i>Retirada no local</i>
                                                                </blockquote>
                                                            </template>
                                                        </div>
                                                    </div>
                                                </div>
                                            </template>
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
                    <hr>
                    <div class="form-row">
                        <div class="form-group col-md-3">
                            <label class="font-weight-bold" for="shipmentOptions">Envio</label>
                            <select class="form-control" id="shipmentOptions" @change="onChangeShippingOption">
                                <option :value="null">Selecione</option>
                                <option v-for="shipmentOption in shipmentOptionsSelect" :value="shipmentOption.value">{{ shipmentOption.text }}</option>
                            </select>
                        </div>
                        <div class="form-group col-md-9">
                            <label class="font-weight-bold" style="margin-left: 15px;">Métodos de envio</label>
                            <template v-if="quoteShipmentOptions.length">
                                <ul class="list-group list-group-flush">
                                    <li class="d-flex justify-content-between align-items-center list-group-item w-100" v-for="(shipmentOption, shipmentOptionIndex) in quoteShipmentOptions">

                                        <template v-if="shipmentOption.method === 'CUSTOM'">
                                            <div class="d-flex flex-column">
                                                <i>Frete customizado</i>
                                                <small>Prazo de entrega:&nbsp;{{ formatDate(shipmentOption.estimatedTime) }}</small>
                                                <small>Custo:&nbsp;<span class="text-success">{{ formatCurrency(shipmentOption.cost) }}</span></small>
                                            </div>
                                        </template>

                                        <template v-if="shipmentOption.method === 'FREE'">
                                            <div class="d-flex flex-column">
                                                <i>Frete grátis</i>
                                                <small>Prazo de entrega:&nbsp;{{ formatDate(shipmentOption.estimatedTime) }}</small>
                                            </div>
                                        </template>

                                        <template v-if="shipmentOption.method === 'LOCAL_PICK_UP'">
                                            <i>Retirada no local</i>
                                        </template>
                                        <button type="button" class="close" aria-label="Close" @click="onClickRemoveShipmentOption(shipmentOptionIndex)">
                                            <span aria-hidden="true">&times;</span>
                                        </button>
                                    </li>
                                </ul>
                            </template>
                            <template v-else>
                                <div class="text-muted font-italic" style="margin-left: 15px;">Adicione ao menos um método de envio</div>
                            </template>
                        </div>
                    </div>
                    <button :disabled="invalidQuote" type="button" class="btn btn-info btn-lg btn-block" style="padding: 30px auto !important;" @click="onClickPushQuotation">Lançar cotação</button>
                </div>
            </template>
            <template v-else>
                <%--Esse pedido nao se encontra mais sob cotação--%>
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