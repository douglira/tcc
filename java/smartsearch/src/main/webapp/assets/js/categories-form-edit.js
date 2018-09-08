let modalActions = {
		toggleStatus: {
			textTitle: status => `${status} categoria`,
			textBody: 'Tem certeza que deseja inativar esta categoria? Todas as subcategorias também serão afetadas',
			action: toggleStatus,
		},
		deleteCategory: {
			textTitle: () => 'Excluir categoria',
			textBody: 'Tem certeza que deseja excluir esta categoria? Esta operação é irreversível e todas as subcategorias serão excluídas',
			action: deleteCategory,
		},
}

$(document).ready(function() {
	$('#btnToggleStatus').click(openModal);
	$('#btnDelete').click(openModal);
	$('#btnModalOk').click(onModalConfirm);
})

function openModal() {
	$('#modalActions').on('show.bs.modal', function(event) {
		let btn = $(event.relatedTarget);
		let action = btn.data('action');
		
		let modal = $(this);
		
		modal.find('.modal-title').text(modalActions[action].textTitle($('#btnToggleStatus').text()));
		modal.find('.modal-body').text(modalActions[action].textBody);
		$('#btnModalOk').data('action', modalActions[action].action);
	})
}

function onModalConfirm(event) {
	let btn = $(this);
	let action = btn.data('action');

	action.call(btn);
	btn.clearQueue();
}


function toggleStatus() {
	const category = {
			id: $('#category-id').val(),
			status: $('#category-status').val(),
	}
	
	$.post(
		'/admin/categories/edit', 
		{
			action: 'toggleStatus',
			['category-id']: category.id,
			['category-status']: category.status,
		},
		function() {
			window.location.replace('/admin/categories/new')
		}
	);
}

function deleteCategory() {
	const category = {
			id: $('#category-id').val(),
	}
	
	$.post(
		'/admin/categories/edit', 
		{
			action: 'delete',
			['category-id']: category.id,
		},
		function() {
			window.location.replace('/admin/categories/new')
		}
	);
}