<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
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
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/libs/dropzone/dropzone.css"/>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/product-form-create.css"/>
</head>
<body>

<jsp:include page="/header.jsp"/>

<div id="productNew" class="container mb-sm-5 mb-md-5">
    <div class="card border-light mb-sm-3 mb-md-3">
        <div class="card-body">
            <h1 class="card-title text-muted text-uppercase">Gestão de Produto</h1>
        </div>
    </div>

    <div class="row">

        <form autocomplete="off" @submit.prevent="save" class="col-md-12">

            <div class="row d-flex justify-content-between">

                <div class="col-md-12">
                    <div class="card border-light mb-3 mb-sm-3 mb-md-3">
                        <div class="card-body">
                            <div class="row justify-content-md-center">
                                <div class=" col-12 col-sm-12 col-md-10 col-lg-7">
                                    <h2 class="text-muted">Produto</h2>
                                    <p class="text-muted">
                                        Insira o título do produto no campo abaixo. Um título que não
                                        conste na nossa base de dados resultará na criação de um novo anúncio em nosso
                                        sistema.
                                        Para mais informações&nbsp;<a href="javascript:void(0)">clique aqui.</a>
                                    </p>
                                    <div class="form-group">
                                        <label for="title">Título</label>
                                        <div class="autocomplete">
                                            <input
                                                    id="title"
                                                    name="title"
                                                    class="form-control form-control-lg"
                                                    placeholder="Busque por um produto ou registre um novo"
                                                    type="text"
                                                    :value="product.title"
                                                    @input="onKeyupProductTitle"
                                                    @blur="clearPredictProducts">
                                            <div class="list-group autocomplete-list"
                                                 v-if="productsPredict && productsPredict.length">
                                                <template v-for="(predictProduct, index) in productsPredict">
                                                    <a
                                                            @click="onClickPredictProduct(predictProduct)"
                                                            href="javascript:void(0)"
                                                            style="font-size: 13px !important;"
                                                            class="list-group-item list-group-item-action autocomplete-list-item">
                                                        <div>
                                                            <img :alt="predictProduct.thumbnail.name"
                                                                 :src="predictProduct.thumbnail.urlPath"/>
                                                            <span>{{ predictProduct.title }}</span>
                                                        </div>
                                                        <small class="badge badge-pill badge-info text-light">{{
                                                            predictProduct.relevance }}
                                                        </small>
                                                    </a>
                                                </template>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-row d-flex justify-content-between align-bottom">
                                        <div class="form-group col-md-6">
                                            <label for="basePrice">Preço</label>
                                            <div class="input-group mb-3">
                                                <div class="input-group-prepend">
                                                    <span class="input-group-text">R$</span>
                                                </div>
                                                <input class="form-control" type="number" id="basePrice" name="basePrice"
                                                       step="0.01" min="0.01" v-model="product.basePrice">
                                            </div>
                                        </div>
                                        <div class="form-group col-md-6">
                                            <label for="availableQuantity">Quantidade disponível</label>
                                            <input class="form-control" type="number" id="availableQuantity"
                                                   name="availableQuantity" min="0" v-model="product.availableQuantity">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label for="description">Especificações</label>
                                        <textarea class="form-control" :value="product.description" id="description"
                                                  name="description" rows="3"
                                                  style="white-space: pre-wrap"></textarea>
                                    </div>

                                    <div class="form-group">
                                        <label>Imagens</label>
                                        <div class="dropzone"></div>
                                    </div>
                                </div>
                                <hr>
                                <div class="col-12 col-sm-12 col-md-auto col-lg-auto">
                                    <h2 class="text-muted">Anúncio a vincular</h2>
                                    <template v-if="productItemPreview">
                                        <div class="container-product-item">
                                            <button type="button" class="close text-danger" aria-label="Close"
                                                    @click="onClickRemoveProductItem">
                                                <span aria-hidden="true">&times;</span>
                                            </button>
                                            <img :alt="productItemPreview.thumbnail.name"
                                                 :src="productItemPreview.thumbnail.urlPath"/>
                                            <div class="info_container-product-item">
                                                <div class="info_content-product-item">
                                                    <p class="text-dark">{{ productItemPreview.title }}</p>
                                                    <small class="text-secondary">Relevância: {{
                                                        productItemPreview.relevance
                                                        }}
                                                    </small>
                                                    <br/>
                                                    <small class="text-muted">Visualizações: {{
                                                        productItemPreview.viewsCount
                                                        }}
                                                    </small>
                                                </div>
                                                <div class="price_content-product-item">
                                                    <p class="text-success">R$ {{
                                                        productItemPreview.basePrice.toFixed(2)
                                                        }}</p>
                                                    <small class="text-muted">
                                                        Flutuação (R$)
                                                        <br/>
                                                        {{ productItemPreview.maxPrice.toFixed(2) }} - {{
                                                        productItemPreview.minPrice.toFixed(2) }}
                                                    </small>
                                                </div>
                                            </div>
                                        </div>
                                    </template>
                                    <template v-else>
                                        <p class="text-muted none-linked-text">Nenhum anúncio vinculado...</p>
                                    </template>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-md-12">
                    <div class="d-flex flex-column">
                        <div class="card border-light mb-3 mb-sm-3 mb-md-3 w-100">
                            <div class="card-body">
                                <h2 class="text-muted">Categoria: <i class="text-primary">{{ product.category &&
                                    product.category.id && product.category.title }}</i></h2>
                                <p class="text-muted">Escolha abaixo a categoria na qual este produto irá pertencer.</p>
                                <nav aria-label="breadcrumb">
                                    <ol class="breadcrumb" style="font-size: 13.5px !important;">
                                        <template v-for="(subcategory, index) in breadcrumbCategories">
                                            <li class="breadcrumb-item active"
                                                v-if="index === (breadcrumbCategories.length - 1)">{{ subcategory.title
                                                }}
                                            </li>
                                            <li class="breadcrumb-item" v-else><a href="javascript:void(0)"
                                                                                  @click="onClickBreadcrumbCategory(subcategory.id)">{{
                                                subcategory.title }}</a></li>
                                        </template>
                                    </ol>
                                </nav>
                                <div class="form-group col-md-5 col-sm-8 col-lg-8 col-12">
                                    <label for="categoryId">Categorias</label>
                                    <select id="categoryId" name="categoryId" class="custom-select"
                                            @change="onChangeCategorySelection">
                                        <option selected :value="null">Selecione</option>
                                        <template v-for="(category, categoryIndex) in categories">
                                            <option :value="category.id">{{ category.title }}</option>
                                        </template>
                                    </select>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%--<div class="card border-light mb-3 mb-sm-3 mb-md-3 w-100">
                        <div class="card-body d-flex-column justify-content-center">

                        </div>
                    </div>--%>
                </div>

            </div>

            <div class="d-flex justify-content-between">
                <a role="button" class="btn btn-link bg-white px-md-5 py-md-2 font-weight-bold"
                   href="<%=request.getContextPath() %>/account/inventory">Voltar</a>
                <button class="btn btn-info btn-submit px-md-5 py-md-2 font-weight-bold" type="submit" value="edit"
                        name="action">Salvar
                </button>
            </div>
        </form>

    </div>


</div>

<script src="<%=request.getContextPath()%>/assets/libs/fontawesome/js/all.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/jquery-3.3.1.slim.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="<%=request.getContextPath() %>/assets/libs/toast/toastr.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/axios/axios-dist.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/vuejs/vue-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/lodash/lodash-dist.js"></script>
<script src="<%=request.getContextPath()%>/assets/libs/dropzone/dropzone.js"></script>
<script>Dropzone.autoDiscover = false;</script>
<script src="<%=request.getContextPath()%>/assets/js/product-form-create.js"></script>
</body>
</html>
