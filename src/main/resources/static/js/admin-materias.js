// ===== ADMIN MATERIAS - EDUAVENTURAS =====

const API_URL = 'http://localhost:8080/api';
let todasLasMaterias = [];
let materiaEditando = null;
let materiaAEliminar = null;

// ===== CARGAR MATERIAS =====
async function cargarMaterias() {
    const loadingEl = document.getElementById('loadingMaterias');
    const tableEl = document.getElementById('materiasTable');
    const emptyEl = document.getElementById('emptyState');

    loadingEl.style.display = 'flex';
    tableEl.style.display = 'none';
    emptyEl.style.display = 'none';

    try {
        const response = await fetch(`${API_URL}/materias`);

        if (!response.ok) {
            throw new Error('Error al cargar materias');
        }

        todasLasMaterias = await response.json();

        loadingEl.style.display = 'none';

        if (todasLasMaterias.length === 0) {
            emptyEl.style.display = 'flex';
        } else {
            tableEl.style.display = 'block';
            renderizarMaterias(todasLasMaterias);
        }

        actualizarContador(todasLasMaterias.length);

    } catch (error) {
        console.error('Error al cargar materias:', error);
        loadingEl.innerHTML = `
            <div class="alert alert-danger">
                <h4>⚠️ Error al cargar materias</h4>
                <p>No se pudieron cargar las materias. Verifica tu conexión.</p>
                <button class="btn-cta" onclick="cargarMaterias()">Reintentar</button>
            </div>
        `;
    }
}

// ===== RENDERIZAR MATERIAS EN LA TABLA =====
function renderizarMaterias(materias) {
    const tbody = document.getElementById('materiasTableBody');

    tbody.innerHTML = materias.map(materia => {
        // Imagen por defecto (placeholder SVG)
        const imagenDefault = materia.imagenUrl ||
            'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="60" height="60"%3E%3Crect width="60" height="60" fill="%230D5957"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-family="Arial" font-size="24" fill="white"%3E📚%3C/text%3E%3C/svg%3E';

        return `
            <tr data-id="${materia.id}">
                <td>
                    <img 
                        src="${imagenDefault}" 
                        alt="${materia.nombre}"
                        class="materia-image"
                    >
                </td>
                <td>
                    <div class="materia-nombre">${materia.nombre}</div>
                </td>
                <td>
                    <div class="materia-descripcion" title="${materia.descripcion}">
                        ${materia.descripcion}
                    </div>
                </td>
                <td class="text-center">
                    <span class="recursos-count">
                        📄 ${materia.cantidadRecursos || 0}
                    </span>
                </td>
                <td>
                    <div class="table-actions">
                        <button class="btn-icon btn-edit" onclick="editarMateria(${materia.id})" title="Editar">
                            ✏️
                        </button>
                        <button class="btn-icon btn-delete" onclick="confirmarEliminar(${materia.id})" title="Eliminar">
                            🗑️
                        </button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// ===== ACTUALIZAR CONTADOR =====
function actualizarContador(total) {
    document.getElementById('totalMaterias').textContent = total;
}

// ===== BÚSQUEDA EN TIEMPO REAL =====
function configurarBusqueda() {
    const searchInput = document.getElementById('searchInput');

    searchInput.addEventListener('input', (e) => {
        const termino = e.target.value.toLowerCase().trim();

        if (termino === '') {
            renderizarMaterias(todasLasMaterias);
            actualizarContador(todasLasMaterias.length);
        } else {
            const materiasFiltradas = todasLasMaterias.filter(materia =>
                materia.nombre.toLowerCase().includes(termino) ||
                materia.descripcion.toLowerCase().includes(termino)
            );

            renderizarMaterias(materiasFiltradas);
            actualizarContador(materiasFiltradas.length);
        }
    });
}

// ===== ABRIR MODAL NUEVA MATERIA =====
function abrirModalNueva() {
    materiaEditando = null;
    document.getElementById('modalTitle').textContent = 'Nueva Materia';
    document.getElementById('materiaForm').reset();
    document.getElementById('materiaId').value = '';
    document.getElementById('alertModal').style.display = 'none';
    document.getElementById('materiaModal').classList.add('show');
}

// ===== EDITAR MATERIA =====
window.editarMateria = async function(id) {
    materiaEditando = todasLasMaterias.find(m => m.id === id);

    if (!materiaEditando) {
        alert('Materia no encontrada');
        return;
    }

    document.getElementById('modalTitle').textContent = 'Editar Materia';
    document.getElementById('materiaId').value = materiaEditando.id;
    document.getElementById('nombre').value = materiaEditando.nombre;
    document.getElementById('descripcion').value = materiaEditando.descripcion;
    document.getElementById('imagenUrl').value = materiaEditando.imagenUrl || '';
    document.getElementById('alertModal').style.display = 'none';
    document.getElementById('materiaModal').classList.add('show');
};

// ===== CERRAR MODAL =====
function cerrarModal() {
    document.getElementById('materiaModal').classList.remove('show');
    materiaEditando = null;
}

// ===== GUARDAR MATERIA (CREAR O ACTUALIZAR) =====
async function guardarMateria(e) {
    e.preventDefault();

    const btnGuardar = document.getElementById('btnGuardar');
    const btnText = document.getElementById('btnGuardarText');
    const btnSpinner = document.getElementById('btnGuardarSpinner');
    const alertModal = document.getElementById('alertModal');

    // Deshabilitar botón
    btnGuardar.disabled = true;
    btnText.textContent = 'Guardando...';
    btnSpinner.style.display = 'inline-block';
    alertModal.style.display = 'none';

    const materiaData = {
        nombre: document.getElementById('nombre').value.trim(),
        descripcion: document.getElementById('descripcion').value.trim(),
        imagenUrl: document.getElementById('imagenUrl').value.trim() || null
    };

    const materiaId = document.getElementById('materiaId').value;
    const esEdicion = !!materiaId;

    try {
        const url = esEdicion
            ? `${API_URL}/materias/${materiaId}`
            : `${API_URL}/materias`;

        const method = esEdicion ? 'PUT' : 'POST';

        console.log(`📤 ${method} ${url}`, materiaData);

        // Verificar token y usuario
        const token = window.getToken();
        const usuario = window.getCurrentUser();
        console.log('🔐 Token presente:', !!token);
        console.log('👤 Usuario:', usuario?.nombre, '- Rol:', usuario?.rol);

        const response = await window.fetchAutenticado(url, {
            method: method,
            body: JSON.stringify(materiaData)
        });

        console.log('📡 Response status:', response.status);
        console.log('📡 Response headers:', [...response.headers.entries()]);

        // Manejo específico de errores
        if (response.status === 403) {
            const usuario = window.getCurrentUser();
            throw new Error(
                `⛔ Acceso denegado.\n\n` +
                `Tu rol actual: ${usuario?.rol || 'Desconocido'}\n` +
                `Roles permitidos: ADMIN, DOCENTE\n\n` +
                `Verifica:\n` +
                `1. Que tu usuario tenga rol ADMIN o DOCENTE en la base de datos\n` +
                `2. Que el token no haya expirado\n` +
                `3. Que el backend esté configurado correctamente`
            );
        }

        if (response.status === 401) {
            throw new Error('🔒 Tu sesión ha expirado. Por favor, inicia sesión nuevamente.');
        }

        if (!response.ok) {
            // Intentar leer el mensaje de error del servidor
            let errorMsg = 'Error al guardar la materia';
            try {
                const contentType = response.headers.get('content-type');
                if (contentType && contentType.includes('application/json')) {
                    const errorData = await response.json();
                    errorMsg = errorData.mensaje || errorData.message || errorData.error || errorMsg;
                } else {
                    const errorText = await response.text();
                    errorMsg = errorText || `Error ${response.status}: ${response.statusText}`;
                }
            } catch (e) {
                console.error('Error al parsear respuesta:', e);
                errorMsg = `Error ${response.status}: ${response.statusText}`;
            }
            throw new Error(errorMsg);
        }

        // Éxito
        const resultado = await response.json();
        console.log('✅ Materia guardada:', resultado);

        alertModal.className = 'alert alert-success';
        alertModal.textContent = esEdicion
            ? '✅ Materia actualizada exitosamente'
            : '✅ Materia creada exitosamente';
        alertModal.style.display = 'block';

        // Recargar materias
        setTimeout(async () => {
            await cargarMaterias();
            cerrarModal();
        }, 1500);

    } catch (error) {
        console.error('❌ Error al guardar materia:', error);
        alertModal.className = 'alert alert-danger';
        alertModal.textContent = error.message;
        alertModal.style.display = 'block';

        // Rehabilitar botón
        btnGuardar.disabled = false;
        btnText.textContent = 'Guardar';
        btnSpinner.style.display = 'none';
    }
}

// ===== CONFIRMAR ELIMINACIÓN =====
window.confirmarEliminar = function(id) {
    materiaAEliminar = todasLasMaterias.find(m => m.id === id);

    if (!materiaAEliminar) {
        alert('Materia no encontrada');
        return;
    }

    document.getElementById('deleteModal').classList.add('show');
};

// ===== CERRAR MODAL DELETE =====
function cerrarModalDelete() {
    document.getElementById('deleteModal').classList.remove('show');
    materiaAEliminar = null;
}

// ===== ELIMINAR MATERIA =====
async function eliminarMateria() {
    if (!materiaAEliminar) return;

    const btnDelete = document.getElementById('btnConfirmarDelete');
    btnDelete.disabled = true;
    btnDelete.textContent = 'Eliminando...';

    try {
        const response = await window.fetchAutenticado(
            `${API_URL}/materias/${materiaAEliminar.id}`,
            { method: 'DELETE' }
        );

        if (!response.ok) {
            throw new Error('Error al eliminar la materia');
        }

        // Éxito
        cerrarModalDelete();
        await cargarMaterias();

        // Mostrar notificación
        alert('✅ Materia eliminada exitosamente');

    } catch (error) {
        console.error('Error al eliminar:', error);
        alert('❌ Error al eliminar la materia: ' + error.message);
        btnDelete.disabled = false;
        btnDelete.textContent = 'Eliminar';
    }
}

// ===== EVENT LISTENERS =====
document.getElementById('btnNuevaMateria').addEventListener('click', abrirModalNueva);
document.getElementById('materiaForm').addEventListener('submit', guardarMateria);
document.getElementById('btnConfirmarDelete').addEventListener('click', eliminarMateria);

// Cerrar modal al hacer clic fuera
document.getElementById('materiaModal').addEventListener('click', (e) => {
    if (e.target.id === 'materiaModal') {
        cerrarModal();
    }
});

document.getElementById('deleteModal').addEventListener('click', (e) => {
    if (e.target.id === 'deleteModal') {
        cerrarModalDelete();
    }
});

// Cerrar modal con ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        cerrarModal();
        cerrarModalDelete();
    }
});

// ===== INICIALIZAR =====
async function inicializar() {
    // Debug: Verificar usuario
    const usuario = window.getCurrentUser();
    console.log('🔍 Usuario actual:', usuario);
    console.log('👤 Rol:', usuario?.rol);

    // Proteger página (ADMIN o DOCENTE)
    if (!window.protegerPaginaPorRol(['ADMIN', 'DOCENTE'])) {
        return;
    }

    // Actualizar navbar
    window.actualizarNavbar();

    // Cargar materias
    await cargarMaterias();

    // Configurar búsqueda
    configurarBusqueda();
}

// Ejecutar al cargar
document.addEventListener('DOMContentLoaded', inicializar);

// Exponer funciones globales
window.abrirModalNueva = abrirModalNueva;
window.cerrarModal = cerrarModal;
window.cerrarModalDelete = cerrarModalDelete;

console.log('✅ Admin Materias JS cargado');