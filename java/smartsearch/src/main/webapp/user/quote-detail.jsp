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
                Cotação&nbsp;&#45;&nbsp;&numero;
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
            <h4>Solicitado</h4>
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
                <h4>Fornecido</h4>
                <h5 class="font-italic font-weight-normal text-muted">Desconto: {{ quote.discount }}</h5>
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
    </div>

</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/inputmask/dist/jquery.inputmask.bundle.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/moment.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/quote-detail.js"></script>
</body>
</html>
