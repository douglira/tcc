let modalActions = {
		toggleStatus: {
			textTitle: status => `${status} categoria`,
			textBody: status => `Tem certeza que deseja ${status.toLowerCase()} esta categoria? Todas as subcategorias também serão afetadas`,
			action: toggleStatus,
		},
		deleteCategory: {
			textTitle: () => 'Excluir categoria',
			textBody: () => 'Tem certeza que deseja excluir esta categoria? Esta operação é irreversível e todas as subcategorias serão excluídas',
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
		
		const status = $('#btnToggleStatus').text();
		
		modal.find('.modal-title').text(modalActions[action].textTitle(status));
		modal.find('.modal-body').text(modalActions[action].textBody(status));
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
			action: 'ToggleStatus',
			['category-id']: category.id,
			['category-status']: category.status,
		},
		function(response) {
			response = JSON.parse(response);
			
			if (!response.error && response.error === null) {
				window.location.replace('/admin/categories/new');				
				return;
			}
			
			const divError = $('#divError');
			
			divError.text(response.error);
			divError.show();
			
			setTimeout(() => divError.hide(), 3200);
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
			action: 'Delete',
			['category-id']: category.id,
		},
		function() {
			window.location.replace('/admin/categories/new')
		}
	);
}