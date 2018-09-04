$(document).ready(function() {
	let categoriesData = [];
	loadCategories();
	$('#btnRemoveSelectedCategory').click(removeSelectedCategory);
});

function loadCategories(params) {
	$.get('/admin/categories/json', params, function(data) {
		mountTableCategories(JSON.parse(data))
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
	
	if (categories[0] && categories[0].layer !== 1) {
		console.log(categories)
		console.log('entrou aqui')
		mountBackButton();
	}
}

function mountTableRow(category) {
	const row = $('<tr>');
	const colId = $('<td>').text(category.id);
	const colTitle = $('<td>').text(category.title);
	
	row.attr('style', 'cursor: pointer;')
	row.click(function(e) {
		const self = this;
		setTimeout(function() {
			const dblclick = parseInt($(self).data('double'), 10);
			if (dblclick > 0) {
				$(self).data('double', dblclick - 1);
			} else {
				setParentCategory.call(self, e);
			}
		}, 300);
	});
	row.dblclick(function(e) {
		$(this).data('double', 2);
		searchChildCategories.call(this, e);
	});
	
	row.append(colId);
	row.append(colTitle);
	
	return row
}

function setParentCategory(event) {
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
	el.text(msg)
	el.show();
}

function hideWarning(el, time = 3000) {
	setTimeout(function() {
		el.text(null);
		el.hide();
	}, time);
}

function mountBackButton() {
	const tbody = $('#tbody-categories');
	const row = $('<tr>');
	const btn = $('<button>');
	
	btn.addClass("btn btn-light text-secondary");
	btn.text('Voltar');
	btn.click(() => loadCategories())
	
	row.attr('colspan', '2');
	row.append(btn);
	
	tbody.append(row);
}