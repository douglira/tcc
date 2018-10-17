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
            <div v-if="countdown" class="countdown text-muted">
                {{ countdown }}
            </div>
        </div>
    </div>

    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body d-flex flex-nowrap align-items-center">
            <table class="table table-hover">
                <thead>
                    <tr>
                        <th scope="col">ID</th>
                        <th scope="col">Anúncio</th>
                        <th scope="col" class="text-right">Quantidade</th>
                    </tr>
                </thead>
                <tbody class="table-body">
                    <tr
                        v-for="productList in purchaseRequest.listProducts"
                        :key="productList.product.id">
                        <th scope="row">{{ productList.product.id }}</th>
                        <td>{{ productList.product.title }}</td>
                        <td class="text-right">{{ productList.quantity }}</td>
                    </tr>
                </tbody>
                <tfoot>
                    <tr>
                        <th scope="row" colspan="2" ></th>
                        <td class="text-success text-right font-weight-bold">
                            <span class="text-dark text-uppercase">Total&colon;</span>
                            &nbsp;
                            {{ formatCurrency(purchaseRequest.totalAmount) }}
                        </td>
                    </tr>
                </tfoot>
            </table>
        </div>
    </div>
</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/purchase-request-suggest.js"></script>
</body>
</html>