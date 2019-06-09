<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/quote-detail.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="userQuoteDetail" class="container mb-sm-5 mb-md-5" v-if="quote && quote.id">

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body d-flex flex-nowrap justify-content-between align-items-center">
            <h1 class="text-muted page-title">
                Cotação&nbsp;&#45;&nbsp;
                <i>{{ seller.corporateName }}</i>
                <br>
                <small class="quote-created_at">
                    <strong>Criado em:</strong>
                    &nbsp;
                    {{ formatFullDate(quote.createdAt) }}
                </small>
            </h1>
            <span
                style="font-size: 20px;"
                class="font-weight-bold text-uppercase d-flex flex-column justify-content-between align-items-end"
                :class="{ 'text-danger': (quote.status === 'EXPIRED') || (quote.status === 'DECLINED'), 'text-muted': quote.status === 'UNDER_REVIEW', 'text-info': quote.status === 'ACCEPTED' }">
                {{ getDisplayQuoteStatus() }}
                <small v-if="quote.status === 'UNDER_REVIEW'" class="quote-expiration_date">Até: {{ formatDate(quote.expirationDate) }}</small>
            </span>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body">
            <h4 class="text-muted">Solicitado</h4>
            <div class="list-group list-group-flush">
                <li class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" v-for="purchaseItem in quote.purchaseRequest.listProducts">
                    <span class="d-flex flex-column justify-content-around align-items-start">
                        <span>{{ purchaseItem.product.title }}</span>
                        <span class="text-success font-italic">{{ formatCurrency(purchaseItem.product.basePrice) }} un.</span>
                    </span>
                    <span class="badge badge-light badge-pill">Qtd.&nbsp;{{ purchaseItem.quantity }}</span>
                </li>
            </div>
        </div>

        <div class="card-body">
            <div class="d-flex flex-row justify-content-between align-items-center">
                <h4 class="text-muted">Fornecido</h4>
                <h5 class="font-italic font-weight-normal text-muted">Desconto: {{ quote.discount }}&#37;</h5>
            </div>
            <div class="list-group list-group-flush">
                <li class="list-group-item list-group-item-action d-flex justify-content-between align-items-center" v-for="quotationItem in quote.customListProduct">
                    <span class="d-flex flex-column justify-content-around align-items-start">
                        <span>{{ quotationItem.product.title }}</span>
                        <span class="text-success font-italic">{{ formatCurrency(quotationItem.product.basePrice) }} un.</span>
                    </span>
                    <span class="badge badge-primary badge-pill" :class="[getQuotationBadgeClass(quotationItem)]">Qtd.&nbsp;{{ quotationItem.quantity }}</span>
                </li>
                <li class="list-group-item list-group-item-action d-flex flex-row justify-content-end" >
                    <strong>TOTAL:&nbsp;<span class="text-success">{{ formatCurrency(quote.totalAmount) }}</span></strong>
                </li>
            </div>
        </div>

        <div class="card-body">
            <h4 class="text-muted">Dados do vendedor</h4>
            <span class="text-muted">Empresa: <strong>{{ seller.corporateName }}</strong></span>
            <br>
            <span class="text-muted">Responsável: <strong>{{ seller.accountOwner }}</strong></span>
            <br>
            <span class="text-muted">Telefone: <strong>{{ formatTel(seller.tel) }}</strong></span>
        </div>

        <div v-if="seller.address && seller.address.id" class="card-body">
            <h4 class="text-muted">Endereço do vendedor</h4>
            <span class="text-muted">Endereço: <strong>{{ seller.address.street }}, {{ seller.address.buildingNumber }}</strong></span>
            <br>
            <span class="text-muted">Bairro: <strong>{{ seller.address.district }}</strong></span>
            <br>
            <span class="text-muted">Cidade: <strong>{{ seller.address.city }} - {{ seller.address.provinceCode }}</strong></span>
            <br>
            <span class="text-muted">CEP: <strong>{{ formatCep(seller.address.postalCode) }}</strong></span>
        </div>

        <div class="card-body">
            <h4 class="text-muted">Frete</h4>
            <p class="text-muted">Abaixo escolha uma das opções de frete disponibilizadas pelo vendedor.</p>
            <div class="d-flex flex-sm-column flex-md-row flex-lg-row justify-content-start align-items-stretch">
                <div
                    style="padding: 20px; flex: 1; max-width: fit-content;"
                    class="d-flex flex-column align-items-center justify-content-center text-muted"
                    :class="{'shipment-options': quote.status === 'UNDER_REVIEW'}"
                    v-for="(shipment, index) in quote.shipmentOptions"
                    @click="onSelectShipment(index, shipment.id)">
                    <strong class="text-uppercase font-italic">{{ getShipmentMethod(shipment.method) }}</strong>
                    <span v-if="shipment.estimatedTime">Prazo de entrega: {{ formatDate(shipment.estimatedTime) }}</span>
                    <span v-if="shipment.method === 'CUSTOM'">Custo do frete: <strong>{{ formatCurrency(shipment.cost) }}</strong></span>
                </div>
            </div>
        </div>

        <div v-if="quote.status === 'UNDER_REVIEW'" class="d-flex flex-sm-column flex-md-row flex-lg-row justify-content-between align-items-stretch">
            <button type="button" class="btn btn-block btn-light" data-toggle="modal" data-target="#modalRefuse">Recusar</button>
            <button type="button" class="btn btn-block btn-info" @click="onClickAccept">Aceitar</button>
        </div>
    </div>

    <div class="modal fade" id="modalRefuse" tabindex="-1" role="dialog" aria-labelledby="modalRefuse" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Recusar cotação</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <div class="form-group">
                        <label for="modalRefuseTextarea">Motivo</label>
                        <textarea class="form-control" id="modalRefuseTextarea" rows="3" v-model="quoteReason"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-light" data-dismiss="modal">Voltar</button>
                    <button type="button" class="btn btn-info" data-dismiss="modal" @click="onClickRefuse">Confirmar</button>
                </div>
            </div>
        </div>
    </div>

</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/inputmask/dist/jquery.inputmask.bundle.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/qs/index.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/moment.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/quote-detail.js"></script>
</body>
</html>
