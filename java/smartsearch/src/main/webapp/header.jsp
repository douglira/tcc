<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@page import="enums.UserRoles" %>
<%@page import="models.User" %>
<%
    User user = (User) session.getAttribute("loggedUser");
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/header.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.css">
<header id="appHeader" class="header">
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="/">LOGOTIPO</a>
        <button class="navbar-toggler" type="button" data-toggle="collapse"
                data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false"
                aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item"><a class="nav-link" href="#">Sobre nós</a>
                </li>
                <li class="nav-item active"><a class="nav-link" href="#">Pedidos
                    de compra</a></li>
                <li class="nav-item"><a class="nav-link" href="#">Recentes</a>
                </li>

            </ul>


            <div class="form-inline my-2 my-lg-0 dropdown">
                <button
                        class="btn btn-light header-btn_dropdown text-secondary dropdown-toggle"
                        type="button" id="dropdownOptions" data-toggle="dropdown"
                        aria-haspopup="true" aria-expanded="false">
                    <c:choose>
                        <c:when test="<%=user == null%>">
                            Minha conta
                        </c:when>
                        <c:otherwise>
                            Olá, <strong><%=user.getDisplayName()%>
                        </strong>
                        </c:otherwise>
                    </c:choose>
                </button>

                <div class="dropdown-menu" aria-labelledby="dropdownOptions">
                    <c:choose>
                        <c:when
                                test="<%=user != null && user.getRole() == UserRoles.ADMIN%>">
                            <a class="dropdown-item font-weight-bold"
                               href="<%=request.getContextPath()%>/admin?page=1&perPage=15">(administrador)</a>
                            <div class="dropdown-divider"></div>
                            <a class="dropdown-item"
                               href="<%=request.getContextPath()%>/admin?page=1&perPage=15">Lista
                                de usuários</a>
                            <a class="dropdown-item"
                               href="<%=request.getContextPath()%>/admin/categories">Painel
                                de categorias</a>
                        </c:when>
                        <c:otherwise>
                            <a class="dropdown-item"
                               href="<%=request.getContextPath()%>/account/purchase_request/list">Meus pedidos de
                                compra</a>
                            <a class="dropdown-item" href="#">Orçamentos lançados</a>
                            <a class="dropdown-item" href="<%=request.getContextPath()%>/account/inventory">Estoque</a>
                            <a class="dropdown-item" href="<%=request.getContextPath()%>/account/me">Meus dados</a>
                        </c:otherwise>
                    </c:choose>

                    <div class="dropdown-divider"></div>

                    <c:choose>
                        <c:when test="<%=user == null%>">
                            <a class="dropdown-item"
                               href="<%=request.getContextPath()%>/signin">Entrar</a>
                            <a class="dropdown-item text-info"
                               href="<%=request.getContextPath()%>/register">Não possui
                                conta?</a>
                        </c:when>
                        <c:otherwise>
                            <a class="dropdown-item"
                               href="<%=request.getContextPath()%>/auth/signout">Sair</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <c:if test="<%=user != null && user.getRole() == UserRoles.COMMON%>">
                <template v-if="username">
                    <div class="dropdown">
                        <button
                                class="btn-notification dropdown-toggle"
                                type="button"
                                id="btnHeaderPR"
                                aria-haspopup="true"
                                data-toggle="dropdown">
                            <template v-if="purchaseRequest && purchaseRequest.listProducts.length">
                                <span class="btn-badge-count bg-danger">{{ purchaseRequest.listProducts.length }}</span>
                            </template>
                            <i class="fas fa-shopping-cart"></i>
                        </button>
                        <div class="dropdown-menu dropdown-menu-right" aria-labelledby="btnHeaderPR">
                            <template v-if="purchaseRequest && purchaseRequest.listProducts.length">
                                <ul class="list-group list-group-flush pr-header-container">
                                    <h6
                                        style="display: flex !important; justify-content: space-between !important; align-items: center !important;"
                                        class="text-info dropdown-header text-justify d-flex align-items-center justify-content-center text-uppercase">
                                        Abrangência
                                        &nbsp;
                                        <span :class="['badge', 'text-white', purchaseRequest.propagationCount <= 1 ? 'badge-danger' : 'badge-info']">
                                            {{ purchaseRequest.propagationCount }}
                                        </span>
                                    </h6>
                                    <li
                                            role="button"
                                            v-for="(prItem, prIndex) in purchaseRequest.listProducts"
                                            :key="prItem.product.id"
                                            class="list-group-item list-group-item-action pr-header-item">
                                        <img class="pr-header-item-thumbnail" :src="prItem.product.thumbnail.urlPath"
                                             :alt="prItem.product.thumbnail.name">
                                        <div class="pr-header-item-title text-muted">{{ prItem.product.title }}</div>
                                    </li>
                                    <a class="dropdown-item text-center text-danger p-3 p-sm-3 p-md-3 p-lg-3"
                                       :href="'/account/purchase_request/new?pr=' + purchaseRequest.id">Ver pedido</a>
                                </ul>
                            </template>
                            <template v-else>
                                <div
                                    style="font-size: 12px;"
                                    class="p-md-3 p-lg-3 p-sm-3 alert alert-light text-center font-italic text-muted"
                                    role="alert">
                                    Nenhum pedido de compra criado...
                                </div>
                            </template>
                        </div>
                    </div>

                    <div class="dropdown">
                        <button
                            type="button"
                            id="btnHeaderNotification"
                            aria-haspopup="true"
                            data-toggle="dropdown"
                            class="btn-notification dropdown-toggle">
                            <template v-if="pendingNotificationsCount">
                                <span class="btn-badge-count bg-danger">{{ pendingNotificationsCount }}</span>
                            </template>
                            <i class="far fa-bell"></i>
                        </button>
                        <div class="dropdown-menu dropdown-menu-left" aria-labelledby="btnHeaderNotification">
                            <template v-if="notifications && notifications.length">
                                <ul class="list-group list-group-flush">
                                    <a
                                        v-for="(notification, index) in notifications"
                                        :key="notification.id"
                                        class="list-group-item list-group-item-action notification-item"
                                        @click="onClickNotification($event, notification)"
                                        :href="getNotificationUrl(notification)">
                                        <i v-if="getIconNotification" :class="['notification-icon', getIconNotification(notification)]"></i>
                                        <span :class="[
                                            'text-muted',
                                             'notification-content',
                                             notification.status === 'PENDING' && 'font-weight-bold',
                                            ]">{{ notification.content }}</span>
                                    </a>
                                </ul>
                            </template>
                            <template v-else>
                                <div
                                    style="font-size: 12px;"
                                    class="p-md-3 p-lg-3 p-sm-3 alert alert-light text-center font-italic text-muted"
                                    role="alert">
                                    Nenhuma notificação recente...
                                </div>
                            </template>
                        </div>
                    </div>
                </template>
            </c:if>

            <form class="form-inline my-2 my-lg-0">
                <input class="form-control mr-sm-2" type="search"
                       placeholder="Buscar por pedidos de compra..."
                       aria-label="Pesquisar">
                <button class="btn btn-outline-success my-2 my-sm-0" type="submit">Pesquisar</button>
            </form>
        </div>
    </nav>
</header>
<script src="<%=request.getContextPath() %>/assets/libs/helper.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/toast/toastr.min.js"></script>
<c:if test="<%=user != null && user.getRole() == UserRoles.COMMON%>">
    <input type="hidden" id="inputHeaderUsername" value="<%= user.getUsername() %>"/>
    <script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
    <script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
    <script src="<%=request.getContextPath() %>/assets/libs/vuejs/vue-dist.js"></script>
    <script src="<%=request.getContextPath() %>/assets/js/header-websocket.js"></script>
</c:if>