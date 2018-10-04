let breadcrumbCategories = [{ id: null, title: 'Gerais' }];

$(document).ready(function() {
	let categoriesData = [];
	loadCategories();
	$('#btnRemoveSelectedCategory').click(removeSelectedCategory);
});

function loadCategories(params) {
	$.get('/categories/json', params, function(data) {
		mountBreadcrumb();
		mountTableCategories(JSON.parse(data));
	});
}

function mountTableCategories(categories) {
	categoriesData = categories;
	
	const tbody = $('#tbody-categories');
	tbody.empty();
	categories.forEach(category => {
		const tableRow = mountTableRow(category);
		tbody.prepend(tableRow);
	});
}

function mountTableRow(category) {
	const row = $('<tr>');
	const colId = $('<td>').text(category.id);
	const colTitle = $('<td>').text(category.title);
	const colBtnGroup = $('<td>');
	const divBtnGroup = $('<div>');
	const btnSubcategories = $('<button>');
	const linkBtnEdit = $('<a>');
	
	if (category.status === 'INACTIVE') {
		colTitle.addClass('text-danger');
	}
	
	const btnClasses = 'btn btn-light btn-sm border border-light bg-light text-muted';
	
	linkBtnEdit.attr('role', 'button');
	linkBtnEdit.attr('aria-pressed', 'true');
	linkBtnEdit.attr('href', `/admin/categories/edit?title=${category.title}`);
	linkBtnEdit.addClass(btnClasses);
	linkBtnEdit.text('Editar');
	
	btnSubcategories.attr('type', 'button');
	btnSubcategories.addClass(btnClasses);
	btnSubcategories.text('Ver subcategorias');
	btnSubcategories.click(event => searchChildCategories.call(row, event));
	
	divBtnGroup.attr('role', 'button');
	divBtnGroup.attr('aria-label', 'Ações');
	divBtnGroup.addClass('btn-group');
	divBtnGroup.append(btnSubcategories);
	divBtnGroup.append(linkBtnEdit);
	
	colBtnGroup.addClass('d-flex justify-content-end');
	colBtnGroup.append(divBtnGroup);
	
	row.attr('style', 'cursor: pointer;');
	row.click(event => setParentCategory.call(row, event));
	
	row.append(colId);
	row.append(colTitle);
	row.append(colBtnGroup);
	
	return row
}

function setParentCategory(event) {
	if (event.target.tagName !== 'TD'){
		return null;
	}
	
	const row = $(this);
	row.parent().find('tr').removeClass('table-info');
	row.addClass('table-info');
	
	const categoryId = row.find('td:eq(0)').text();	
	const selectedCategory = categoriesData.find(category => category.id === parseInt(categoryId, 10));
	
	$('#category-id-selected').val(selectedCategory.id);
	$('#category-title-selected').val(selectedCategory.title);
	$('#category-layer-selected').val(selectedCategory.layer);
	$('#category-is_last_child-selected').val(selectedCategory.isLastChild);
}

function searchChildCategories(event) {
	const row = $(this);
	
	const parentId = row.find('td:eq(0)').text();
	const parentCategory = categoriesData.find(category => category.id === parseInt(parentId, 10));
	
	if (!parentCategory.isLastChild) {
		removeSelectedCategory();
		breadcrumbCategories.push({ id: parentCategory.id, title: parentCategory.title });
		loadCategories({ parentId });
		return;
	}
	
	const el = $('div[role=alert]');
	showWarning(el, 'Essa categoria não possui subcategorias');
	hideWarning(el);
}

function removeSelectedCategory() {
	$('#category-id-selected').val(null);
	$('#category-title-selected').val(null);
	$('#category-layer-selected').val(null);
	$('#category-is_last_child-selected').val(null);
	$('#tbody-categories').children().removeClass('table-info');
}

function showWarning(el, msg) {
	el.text(msg);
	el.show();
}

function hideWarning(el, time = 3000) {
	setTimeout(function() {
		el.fadeOut();
	}, time);
}

function mountBreadcrumb() {
	const container = $('#breadcrumb-categories');
	container.empty();
	const nav = $('<nav>');
	const ol = $('<ol>');
	
	nav.attr('aria-label', 'breadcrumb');
	ol.addClass('breadcrumb');
	
	const lastIndex = breadcrumbCategories.length - 1;
	
	breadcrumbCategories.forEach((category, index) => {
		const li = $('<li>');
		if (lastIndex === index) {
			li.addClass('breadcrumb-item active');
			li.attr('aria-current', 'page');
			li.text(category.title);
			ol.append(li);
			return;
		}
		
		const link = $('<a>');
		link.text(category.title);
		link.attr('href', 'javascript:void(0)');
		link.click(handleBreadcrumbClick);
		li.addClass('breadcrumb-item');
		li.append(link);
		ol.append(li);
	});
	
	nav.append(ol);
	container.append(nav);
}

function handleBreadcrumbClick(event) {
	const link = $(event.target);
	const categoryTitle = link.text();
	
	removeSelectedCategory();
	
	if (categoryTitle === 'Gerais') {
		breadcrumbCategories = [{ id: null, title: categoryTitle }];
		loadCategories();
		return;
	}
	
	const category = breadcrumbCategories.find(cat => cat.title === categoryTitle)
	const endIndex = breadcrumbCategories.indexOf(category) + 1;
	
	breadcrumbCategories = [...breadcrumbCategories.slice(0, endIndex)];
	loadCategories({ parentId: category.id });
}