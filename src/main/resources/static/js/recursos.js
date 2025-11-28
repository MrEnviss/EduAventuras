// ===== RECURSOS.JS - EDUAVENTURAS (CORREGIDO) =====

const API_URL = 'http://localhost:8080/api';
let materiaId = null;
let todosLosRecursos = [];
let recursoAEliminar = null;

// ===== OBTENER MATERIA ID DE LA URL =====
function obtenerMateriaId() {
    const params = new URLSearchParams(window.location.search);
    return params.get('materiaId');
}

// ===== FORMATEAR TAMAÑO DE ARCHIVO =====
function formatearTamanio(bytes) {
    if (!bytes || bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// ===== FORMATEAR FECHA =====
function formatearFecha(fecha) {
    if (!fecha) return 'Fecha desconocida';
    const date = new Date(fecha);
    const opciones = { year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('es-ES', opciones);
}

// ===== VERIFICAR SI ES DOCENTE O ADMIN =====
function esDocenteOAdmin() {
    // Usar la función global de auth.js
    if (typeof getCurrentUser !== 'function') {
        console.error('getCurrentUser no está disponible');
        return false;
    }

    const usuario = getCurrentUser();

    if (!usuario) {
        return false;
    }

    return usuario.rol === 'DOCENTE' || usuario.rol === 'ADMIN';
}

// ===== CARGAR INFORMACIÓN DE LA MATERIA =====
async function cargarMateria() {
    try {
        console.log('📚 Cargando materia ID:', materiaId);

        const response = await fetch(`${API_URL}/materias/${materiaId}`);

        if (!response.ok) {
            throw new Error('Error al cargar la materia');
        }

        const materia = await response.json();
        console.log('✅ Materia cargada:', materia);

        // Actualizar breadcrumb y título
        document.getElementById('materiaNombre').textContent = materia.nombre;
        document.getElementById('tituloMateria').textContent = `Recursos de ${materia.nombre}`;
        document.getElementById('descripcionMateria').textContent =
            materia.descripcion || 'Explora y descarga los materiales disponibles';

        // Actualizar título de la página
        document.title = `${materia.nombre} - Recursos | EduAventuras`;

    } catch (error) {
        console.error('❌ Error al cargar materia:', error);
        document.getElementById('materiaNombre').textContent = 'Materia';
        document.getElementById('tituloMateria').textContent = 'Recursos';
    }
}

// ===== CARGAR RECURSOS DE LA MATERIA =====
async function cargarRecursos() {
    const loadingSpinner = document.getElementById('loadingSpinner');
    const recursosGrid = document.getElementById('recursosGrid');
    const emptyState = document.getElementById('emptyState');
    const noResultsState = document.getElementById('noResultsState');

    console.log('📥 Cargando recursos de la materia:', materiaId);

    // Mostrar loading
    loadingSpinner.style.display = 'flex';
    recursosGrid.style.display = 'none';
    emptyState.style.display = 'none';
    noResultsState.style.display = 'none';

    try {
        const response = await fetch(`${API_URL}/recursos/materia/${materiaId}`);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        todosLosRecursos = await response.json();
        console.log(`✅ ${todosLosRecursos.length} recursos cargados`);

        loadingSpinner.style.display = 'none';

        if (todosLosRecursos.length === 0) {
            emptyState.style.display = 'flex';
        } else {
            recursosGrid.style.display = 'grid';
            renderizarRecursos(todosLosRecursos);
        }

    } catch (error) {
        console.error('❌ Error al cargar recursos:', error);
        loadingSpinner.style.display = 'none';
        emptyState.style.display = 'flex';
    }
}

// ===== RENDERIZAR RECURSOS EN EL GRID =====
function renderizarRecursos(recursos) {
    const recursosGrid = document.getElementById('recursosGrid');
    const puedeEliminar = esDocenteOAdmin();

    console.log('🎨 Renderizando recursos. Puede eliminar:', puedeEliminar);

    recursosGrid.innerHTML = recursos.map(recurso => {
        // Generar HTML del botón eliminar SOLO si es DOCENTE o ADMIN
        const botonEliminar = puedeEliminar ? `
            <button class="btn-delete" onclick="confirmarEliminar(${recurso.id})">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="3 6 5 6 21 6"/>
                    <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                </svg>
                Eliminar
            </button>
        ` : '';

        return `
            <div class="recurso-card" data-id="${recurso.id}">
                <div class="recurso-header">
                    <div class="recurso-icon">
                        <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                            <polyline points="14 2 14 8 20 8"/>
                            <line x1="16" y1="13" x2="8" y2="13"/>
                            <line x1="16" y1="17" x2="8" y2="17"/>
                            <polyline points="10 9 9 9 8 9"/>
                        </svg>
                    </div>
                    <div class="recurso-info">
                        <h3>${recurso.titulo}</h3>
                        <p class="recurso-descripcion">${recurso.descripcion || 'Sin descripción'}</p>
                    </div>
                </div>
                
                <div class="recurso-metadata">
                    <div class="metadata-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                            <line x1="16" y1="2" x2="16" y2="6"/>
                            <line x1="8" y1="2" x2="8" y2="6"/>
                            <line x1="3" y1="10" x2="21" y2="10"/>
                        </svg>
                        <span>${formatearFecha(recurso.fechaSubida)}</span>
                    </div>
                    <div class="metadata-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                            <polyline points="7 10 12 15 17 10"/>
                            <line x1="12" y1="15" x2="12" y2="3"/>
                        </svg>
                        <span>${recurso.cantidadDescargas || 0} descargas</span>
                    </div>
                    <div class="metadata-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M13 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V9z"/>
                            <polyline points="13 2 13 9 20 9"/>
                        </svg>
                        <span>${formatearTamanio(recurso.tamanioBytes)}</span>
                    </div>
                    <div class="metadata-item">
                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                            <circle cx="12" cy="7" r="4"/>
                        </svg>
                        <span>${recurso.subidoPorNombre || 'Anónimo'}</span>
                    </div>
                </div>
                
                <div class="recurso-actions">
                    <button class="btn-download" onclick="descargarRecurso(${recurso.id}, '${recurso.nombreArchivo}')">
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                            <polyline points="7 10 12 15 17 10"/>
                            <line x1="12" y1="15" x2="12" y2="3"/>
                        </svg>
                        Descargar
                    </button>
                    ${botonEliminar}
                </div>
            </div>
        `;
    }).join('');
}

// ===== DESCARGAR RECURSO =====
window.descargarRecurso = async function(id, nombreArchivo) {
    console.log('📥 Descargando recurso:', id, nombreArchivo);

    try {
        const token = getToken(); // Función global de auth.js

        if (!token) {
            alert('Debes iniciar sesión para descargar recursos');
            window.location.href = 'login.html';
            return;
        }

        const response = await fetch(`${API_URL}/recursos/${id}/descargar`, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Error al descargar el archivo');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        console.log('✅ Descarga completada');

        // Recargar recursos para actualizar contador de descargas
        setTimeout(() => {
            cargarRecursos();
        }, 1000);

    } catch (error) {
        console.error('❌ Error al descargar:', error);
        alert('Error al descargar el archivo. Intenta nuevamente.');
    }
};

// ===== CONFIRMAR ELIMINACIÓN =====
window.confirmarEliminar = function(id) {
    console.log('⚠️ Confirmando eliminación de recurso:', id);

    // Verificar permiso nuevamente
    if (!esDocenteOAdmin()) {
        alert('No tienes permisos para eliminar recursos');
        return;
    }

    recursoAEliminar = id;
    document.getElementById('deleteModal').style.display = 'flex';
};

// ===== ELIMINAR RECURSO =====
async function eliminarRecurso() {
    if (!recursoAEliminar) return;

    // Verificar permiso nuevamente
    if (!esDocenteOAdmin()) {
        alert('No tienes permisos para eliminar recursos');
        document.getElementById('deleteModal').style.display = 'none';
        return;
    }

    console.log('🗑️ Eliminando recurso:', recursoAEliminar);

    try {
        const token = getToken(); // Función global de auth.js

        const response = await fetch(`${API_URL}/recursos/${recursoAEliminar}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || 'Error al eliminar el recurso');
        }

        console.log('✅ Recurso eliminado exitosamente');

        // Cerrar modal
        document.getElementById('deleteModal').style.display = 'none';
        recursoAEliminar = null;

        // Recargar recursos
        await cargarRecursos();

        // Mostrar mensaje de éxito
        alert('Recurso eliminado exitosamente');

    } catch (error) {
        console.error('❌ Error al eliminar:', error);
        alert('Error al eliminar el recurso: ' + error.message);
        document.getElementById('deleteModal').style.display = 'none';
    }
}

// ===== BÚSQUEDA EN TIEMPO REAL =====
function configurarBusqueda() {
    const searchInput = document.getElementById('searchInput');
    const recursosGrid = document.getElementById('recursosGrid');
    const emptyState = document.getElementById('emptyState');
    const noResultsState = document.getElementById('noResultsState');

    searchInput.addEventListener('input', (e) => {
        const termino = e.target.value.toLowerCase().trim();

        if (termino === '') {
            // Mostrar todos los recursos
            if (todosLosRecursos.length === 0) {
                recursosGrid.style.display = 'none';
                emptyState.style.display = 'flex';
                noResultsState.style.display = 'none';
            } else {
                recursosGrid.style.display = 'grid';
                emptyState.style.display = 'none';
                noResultsState.style.display = 'none';
                renderizarRecursos(todosLosRecursos);
            }
        } else {
            // Filtrar recursos
            const recursosFiltrados = todosLosRecursos.filter(recurso =>
                recurso.titulo.toLowerCase().includes(termino) ||
                (recurso.descripcion && recurso.descripcion.toLowerCase().includes(termino))
            );

            if (recursosFiltrados.length === 0) {
                recursosGrid.style.display = 'none';
                emptyState.style.display = 'none';
                noResultsState.style.display = 'flex';
            } else {
                recursosGrid.style.display = 'grid';
                emptyState.style.display = 'none';
                noResultsState.style.display = 'none';
                renderizarRecursos(recursosFiltrados);
            }
        }
    });
}

// ===== CONFIGURAR BOTÓN DE SUBIR RECURSO =====
function configurarBotonSubir() {
    const btnSubir = document.getElementById('btnSubirRecurso');

    if (esDocenteOAdmin()) {
        console.log('✅ Usuario puede subir recursos - Mostrando botón');
        btnSubir.style.display = 'flex';
        btnSubir.addEventListener('click', () => {
            window.location.href = `subir-recurso.html?materiaId=${materiaId}`;
        });
    } else {
        console.log('⚠️ Usuario NO puede subir recursos - Ocultando botón');
        btnSubir.style.display = 'none';
    }
}

// ===== EVENT LISTENERS DEL MODAL =====
document.getElementById('btnCancelar').addEventListener('click', () => {
    document.getElementById('deleteModal').style.display = 'none';
    recursoAEliminar = null;
});

document.getElementById('btnConfirmarEliminar').addEventListener('click', eliminarRecurso);

// Cerrar modal al hacer clic fuera
document.getElementById('deleteModal').addEventListener('click', (e) => {
    if (e.target.id === 'deleteModal') {
        document.getElementById('deleteModal').style.display = 'none';
        recursoAEliminar = null;
    }
});

// ===== INICIALIZAR PÁGINA =====
async function inicializar() {
    console.log('🚀 Inicializando página de recursos');

    // Verificar autenticación (función global de auth.js)
    if (typeof protegerPagina === 'function') {
        if (!protegerPagina()) {
            return;
        }
    }

    // Obtener ID de materia de la URL
    materiaId = obtenerMateriaId();

    if (!materiaId) {
        alert('No se especificó una materia');
        window.location.href = 'materias.html';
        return;
    }

    // Cargar contenido
    await cargarMateria();
    await cargarRecursos();
    configurarBusqueda();
    configurarBotonSubir();

    console.log('✅ Página de recursos inicializada');
}

// ===== EJECUTAR AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', inicializar);

console.log('✅ Módulo recursos.js cargado');