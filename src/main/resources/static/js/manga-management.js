document.addEventListener('DOMContentLoaded', function () {
    const deleteModal = document.getElementById('deleteModal');
    if (deleteModal) {
        deleteModal.addEventListener('show.bs.modal', function (event) {
            // Botón que disparó el modal
            const button = event.relatedTarget;

            // Extraer información de los atributos data-*
            const mangaId = button.getAttribute('data-manga-id');
            const mangaTitulo = button.getAttribute('data-manga-titulo');

            // Actualizar el título en el modal
            const modalTitleElement = deleteModal.querySelector('#mangaTitulo');
            modalTitleElement.textContent = mangaTitulo;

            // Actualizar la acción del formulario con el ID del manga
            const deleteForm = deleteModal.querySelector('#deleteForm');
            // La acción en Thymeleaf es: th:action="@{/admin/mangas/delete/{id}(id=0)}"
            // En el frontend, construimos la URL:
            deleteForm.action = `/admin/mangas/delete/${mangaId}`;
        });
    }
});