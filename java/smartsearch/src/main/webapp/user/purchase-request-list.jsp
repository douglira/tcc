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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/purchase-request-list.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="userPRList" class="container mb-sm-5 mb-md-5" v-if="purchaseRequests.length">
</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/inputmask/dist/jquery.inputmask.bundle.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/moment.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/purchase-request-list.js"></script>
</body>
</html>