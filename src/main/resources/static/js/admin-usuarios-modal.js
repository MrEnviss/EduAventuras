// ===== ADMIN USUARIOS MODAL - EDUAVENTURAS (CORREGIDO) =====

let todosLosUsuarios = [];
let usuarioSeleccionado = null;
let accionPendiente = null;

// ===== ABRIR MODAL DE USUARIOS =====
async function abrirModalUsuarios() {
    console.log('📂 Abriendo modal de usuarios');
    const modal = document.getElementById('usuariosModal');
    modal.classList.add('active');

    // Cargar usuarios
    await cargarUsuariosModal();
}

// ===== CERRAR MODAL DE USUARIOS =====
function cerrarModalUsuarios() {
    console.log('❌ Cerrando modal de usuarios');
    const modal = document.getElementById('usuariosModal');
    modal.classList.remove('active');

    // Limpiar búsqueda
    document.getElementById('searchUsuarios').value = '';
}

// ===== CARGAR USUARIOS EN EL MODAL =====
async function cargarUsuariosModal() {
    const loadingUsuarios = document.getElementById('loadingUsuarios');
    const tablaBody = document.getElementById('tablaUsuariosBody');

    try {
        loadingUsuarios.style.display = 'block';
        tablaBody.innerHTML = '';

        console.log('🔄 Cargando usuarios...');

        const response = await window.fetchAutenticado(`${API_URL}/usuarios`);

        if (!response.ok) {
            throw new Error('Error al cargar usuarios');
        }

        todosLosUsuarios = await response.json();
        console.log('✅ Usuarios cargados:', todosLosUsuarios.length);

        loadingUsuarios.style.display = 'none';
        renderizarUsuarios(todosLosUsuarios);

    } catch (error) {
        console.error('❌ Error al cargar usuarios:', error);
        loadingUsuarios.style.display = 'none';
        tablaBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2rem; color: #dc3545;">
                    ❌ Error al cargar usuarios. Intenta nuevamente.
                </td>
            </tr>
        `;
    }
}

// ===== RENDERIZAR USUARIOS EN LA TABLA =====
function renderizarUsuarios(usuarios) {
    const tablaBody = document.getElementById('tablaUsuariosBody');

    if (usuarios.length === 0) {
        tablaBody.innerHTML = `
            <tr>
                <td colspan="6" style="text-align: center; padding: 2rem; color: #666;">
                    No se encontraron usuarios
                </td>
            </tr>
        `;
        return;
    }

    tablaBody.innerHTML = usuarios.map(usuario => {
        const badgeRol = obtenerBadgeRol(usuario.rol);
        const badgeEstado = usuario.activo
            ? '<span class="badge-activo">Activo</span>'
            : '<span class="badge-inactivo">Inactivo</span>';

        const fecha = formatearFecha(usuario.fechaRegistro);

        return `
            <tr data-user-id="${usuario.id}">
                <td data-label="Nombre">${usuario.nombre} ${usuario.apellido || ''}</td>
                <td data-label="Email">${usuario.email}</td>
                <td data-label="Rol">${badgeRol}</td>
                <td data-label="Estado">${badgeEstado}</td>
                <td data-label="Fecha">${fecha}</td>
                <td data-label="Acciones">
                    <button class="btn-table-action btn-editar" 
                            onclick="editarUsuario(${usuario.id})">
                        ✏️ Editar
                    </button>
                    <button class="btn-table-action btn-toggle" 
                            onclick="toggleEstadoUsuario(${usuario.id}, ${usuario.activo})">
                        ${usuario.activo ? '🔒 Desactivar' : '✅ Activar'}
                    </button>
                    <button class="btn-table-action btn-eliminar" 
                            onclick="confirmarEliminarUsuario(${usuario.id}, '${usuario.nombre}')">
                        🗑️ Eliminar
                    </button>
                </td>
            </tr>
        `;
    }).join('');
}

// ===== OBTENER BADGE DE ROL =====
function obtenerBadgeRol(rol) {
    const badges = {
        'ADMIN': '<span class="badge-rol badge-admin">👑 Admin</span>',
        'DOCENTE': '<span class="badge-rol badge-docente">👨‍🏫 Docente</span>',
        'ESTUDIANTE': '<span class="badge-rol badge-estudiante">👤 Estudiante</span>'
    };
    return badges[rol] || `<span class="badge-rol">${rol}</span>`;
}

// ===== FORMATEAR FECHA =====
function formatearFecha(fecha) {
    if (!fecha) return 'N/A';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// ===== FILTRAR USUARIOS =====
function filtrarUsuarios() {
    const searchTerm = document.getElementById('searchUsuarios').value.toLowerCase();

    const usuariosFiltrados = todosLosUsuarios.filter(usuario => {
        const nombre = `${usuario.nombre} ${usuario.apellido || ''}`.toLowerCase();
        const email = usuario.email.toLowerCase();
        const rol = usuario.rol.toLowerCase();

        return nombre.includes(searchTerm) ||
            email.includes(searchTerm) ||
            rol.includes(searchTerm);
    });

    renderizarUsuarios(usuariosFiltrados);
}

// ===== EDITAR USUARIO =====
function editarUsuario(id) {
    const usuario = todosLosUsuarios.find(u => u.id === id);
    if (!usuario) return;

    const nuevoRol = prompt(
        `Cambiar rol de ${usuario.nombre}\n\nRoles disponibles:\n- ADMIN\n- DOCENTE\n- ESTUDIANTE\n\nRol actual: ${usuario.rol}`,
        usuario.rol
    );

    if (!nuevoRol) return;

    const rolesValidos = ['ADMIN', 'DOCENTE', 'ESTUDIANTE'];
    if (!rolesValidos.includes(nuevoRol.toUpperCase())) {
        alert('Rol inválido. Debe ser: ADMIN, DOCENTE o ESTUDIANTE');
        return;
    }

    cambiarRolUsuario(id, nuevoRol.toUpperCase());
}

// ===== CAMBIAR ROL DE USUARIO =====
async function cambiarRolUsuario(id, nuevoRol) {
    try {
        console.log(`🔄 Cambiando rol de usuario ${id} a ${nuevoRol}`);

        const response = await window.fetchAutenticado(`${API_URL}/usuarios/${id}/rol`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ rol: nuevoRol })
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Error al cambiar rol');
        }

        console.log('✅ Rol cambiado exitosamente');

        // Actualizar en el array local
        const usuario = todosLosUsuarios.find(u => u.id === id);
        if (usuario) {
            usuario.rol = nuevoRol;
        }

        // Actualizar UI sin recargar todo
        actualizarRolEnUI(id, nuevoRol);

        alert('✅ Rol actualizado correctamente');

    } catch (error) {
        console.error('❌ Error al cambiar rol:', error);
        alert('❌ Error al cambiar el rol: ' + error.message);
    }
}

// ===== ACTUALIZAR ROL EN LA UI =====
function actualizarRolEnUI(id, nuevoRol) {
    const fila = document.querySelector(`tr[data-user-id="${id}"]`);
    if (!fila) return;

    const celdaRol = fila.querySelector('td[data-label="Rol"]');
    if (celdaRol) {
        celdaRol.innerHTML = obtenerBadgeRol(nuevoRol);
    }
}

// ===== TOGGLE ESTADO USUARIO (CORREGIDO) =====
async function toggleEstadoUsuario(id, estadoActual) {
    const nuevoEstado = !estadoActual;
    const accion = nuevoEstado ? 'activar' : 'desactivar';

    try {
        console.log(`🔄 Intentando ${accion} usuario ${id}`);

        const response = await window.fetchAutenticado(`${API_URL}/usuarios/${id}/estado`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ activo: nuevoEstado })
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error al ${accion} usuario`);
        }

        console.log('✅ Estado cambiado exitosamente');

        // Actualizar en el array local
        const usuario = todosLosUsuarios.find(u => u.id === id);
        if (usuario) {
            usuario.activo = nuevoEstado;
        }

        // Actualizar UI sin recargar todo
        actualizarEstadoEnUI(id, nuevoEstado);

        alert(`✅ Usuario ${accion === 'activar' ? 'activado' : 'desactivado'} correctamente`);

    } catch (error) {
        console.error(`❌ Error al ${accion} usuario:`, error);
        alert(`❌ Error al ${accion} el usuario: ${error.message}`);
    }
}

// ===== ACTUALIZAR ESTADO EN LA UI (NUEVO) =====
function actualizarEstadoEnUI(id, nuevoEstado) {
    const fila = document.querySelector(`tr[data-user-id="${id}"]`);
    if (!fila) {
        console.warn(`No se encontró la fila del usuario ${id}`);
        return;
    }

    // Actualizar badge de estado
    const celdaEstado = fila.querySelector('td[data-label="Estado"]');
    if (celdaEstado) {
        celdaEstado.innerHTML = nuevoEstado
            ? '<span class="badge-activo">Activo</span>'
            : '<span class="badge-inactivo">Inactivo</span>';
    }

    // Actualizar botón de toggle
    const btnToggle = fila.querySelector('.btn-toggle');
    if (btnToggle) {
        btnToggle.textContent = nuevoEstado ? '🔒 Desactivar' : '✅ Activar';
        btnToggle.onclick = function() { toggleEstadoUsuario(id, nuevoEstado); };
    }
}

// ===== CONFIRMAR ELIMINAR USUARIO =====
function confirmarEliminarUsuario(id, nombre) {
    usuarioSeleccionado = id;
    accionPendiente = 'eliminar';

    const confirmModal = document.getElementById('confirmModal');
    document.getElementById('confirmTitle').textContent = '⚠️ Confirmar Eliminación';
    document.getElementById('confirmMessage').textContent =
        `¿Estás seguro de que deseas eliminar a ${nombre}? Esta acción no se puede deshacer.`;

    confirmModal.classList.add('active');

    // Configurar botón de confirmación
    const btnConfirmar = document.getElementById('btnConfirmarAccion');
    btnConfirmar.onclick = eliminarUsuario;
}

// ===== ELIMINAR USUARIO (CORREGIDO) =====
async function eliminarUsuario() {
    if (!usuarioSeleccionado) {
        console.warn('No hay usuario seleccionado para eliminar');
        return;
    }

    const idUsuario = usuarioSeleccionado;

    try {
        console.log(`🗑️ Eliminando usuario ${idUsuario}`);

        const response = await window.fetchAutenticado(`${API_URL}/usuarios/${idUsuario}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Error al eliminar usuario');
        }

        console.log('✅ Usuario eliminado exitosamente');

        // Eliminar del array local
        todosLosUsuarios = todosLosUsuarios.filter(u => u.id !== idUsuario);

        // Eliminar de la UI inmediatamente
        const fila = document.querySelector(`tr[data-user-id="${idUsuario}"]`);
        if (fila) {
            // Animación de fade out
            fila.style.transition = 'opacity 0.3s ease';
            fila.style.opacity = '0';

            setTimeout(() => {
                fila.remove();

                // Si no quedan usuarios, mostrar mensaje
                const tablaBody = document.getElementById('tablaUsuariosBody');
                if (tablaBody.children.length === 0) {
                    tablaBody.innerHTML = `
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 2rem; color: #666;">
                                No se encontraron usuarios
                            </td>
                        </tr>
                    `;
                }
            }, 300);
        }

        alert('✅ Usuario eliminado correctamente');

        // Cerrar modal de confirmación
        cerrarConfirmacion();

    } catch (error) {
        console.error('❌ Error al eliminar usuario:', error);
        alert('❌ Error al eliminar el usuario: ' + error.message);
        cerrarConfirmacion();
    }
}

// ===== CERRAR MODAL DE CONFIRMACIÓN =====
function cerrarConfirmacion() {
    const confirmModal = document.getElementById('confirmModal');
    confirmModal.classList.remove('active');
    usuarioSeleccionado = null;
    accionPendiente = null;
}

// ===== DESCARGAR REPORTE PDF (CORREGIDO - USA BACKEND) =====
async function descargarReporte() {
    try {
        console.log('📊 Descargando reporte PDF del servidor...');

        // Mostrar indicador de carga
        const btnReporte = document.querySelector('[onclick="descargarReporte()"]');
        const textoOriginal = btnReporte?.textContent;
        if (btnReporte) {
            btnReporte.disabled = true;
            btnReporte.textContent = '⏳ Generando PDF...';
        }

        // Llamar al endpoint del backend que genera el PDF
        const response = await window.fetchAutenticado(`${API_URL}/reportes/estadisticas/descargar`);

        if (!response.ok) {
            throw new Error('Error al generar el reporte');
        }

        // Obtener el blob del PDF
        const blob = await response.blob();

        // Crear nombre del archivo con fecha actual
        const fecha = new Date().toLocaleDateString('es-ES').replace(/\//g, '-');
        const nombreArchivo = `Reporte-EduAventuras-${fecha}.pdf`;

        // Crear enlace de descarga
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        document.body.appendChild(a);
        a.click();

        // Limpiar
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        console.log('✅ Reporte PDF descargado exitosamente');
        alert('✅ Reporte PDF descargado exitosamente');

    } catch (error) {
        console.error('❌ Error al descargar reporte:', error);
        alert('❌ Error al generar el reporte: ' + error.message);
    } finally {
        // Restaurar botón
        const btnReporte = document.querySelector('[onclick="descargarReporte()"]');
        if (btnReporte) {
            btnReporte.disabled = false;
            if (textoOriginal) {
                btnReporte.textContent = textoOriginal;
            }
        }
    }
}

// ===== DESCARGAR REPORTE DE MATERIA ESPECÍFICA =====
async function descargarReporteMateria(materiaId, materiaNombre) {
    try {
        console.log(`📊 Descargando reporte de materia ${materiaId}...`);

        const response = await window.fetchAutenticado(`${API_URL}/reportes/materia/${materiaId}/descargar`);

        if (!response.ok) {
            throw new Error('Error al generar el reporte de la materia');
        }

        const blob = await response.blob();

        const fecha = new Date().toLocaleDateString('es-ES').replace(/\//g, '-');
        const nombreArchivo = `Reporte-${materiaNombre}-${fecha}.pdf`;

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = nombreArchivo;
        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        console.log('✅ Reporte de materia descargado exitosamente');
        alert(`✅ Reporte de ${materiaNombre} descargado exitosamente`);

    } catch (error) {
        console.error('❌ Error al descargar reporte de materia:', error);
        alert('❌ Error al generar el reporte: ' + error.message);
    }
}

// ===== CERRAR MODALES AL HACER CLIC FUERA =====
document.addEventListener('click', (e) => {
    const usuariosModal = document.getElementById('usuariosModal');
    const confirmModal = document.getElementById('confirmModal');

    if (e.target === usuariosModal) {
        cerrarModalUsuarios();
    }

    if (e.target === confirmModal) {
        cerrarConfirmacion();
    }
});

// ===== EXPONER FUNCIONES GLOBALMENTE =====
window.abrirModalUsuarios = abrirModalUsuarios;
window.cerrarModalUsuarios = cerrarModalUsuarios;
window.filtrarUsuarios = filtrarUsuarios;
window.editarUsuario = editarUsuario;
window.toggleEstadoUsuario = toggleEstadoUsuario;
window.confirmarEliminarUsuario = confirmarEliminarUsuario;
window.cerrarConfirmacion = cerrarConfirmacion;
window.descargarReporte = descargarReporte;
window.descargarReporteMateria = descargarReporteMateria;

console.log('✅ Módulo de gestión de usuarios cargado (v2.1 - Con reportes PDF)');