// ===== PERFIL JS - EDUAVENTURAS =====

const API_URL = 'http://localhost:8080/api';
let datosOriginales = null;
let fotoSeleccionada = null;
let todasLasMaterias = [];

// ===== CARGAR MATERIAS PARA EL SELECT =====
async function cargarMaterias() {
    try {
        const response = await fetch(`${API_URL}/materias`);
        if (!response.ok) return;

        todasLasMaterias = await response.json();

        const select = document.getElementById('materiaFavorita');
        select.innerHTML = '<option value="">Sin materia favorita</option>';

        todasLasMaterias.forEach(materia => {
            const option = document.createElement('option');
            option.value = materia.id;
            option.textContent = materia.nombre;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('❌ Error al cargar materias:', error);
    }
}

// ===== CARGAR DATOS DEL PERFIL =====
async function cargarPerfil() {
    const loadingEl = document.getElementById('loadingPerfil');
    const contentEl = document.getElementById('perfilContent');

    loadingEl.style.display = 'flex';
    contentEl.style.display = 'none';

    try {
        const response = await window.fetchAutenticado(`${API_URL}/perfil`);

        if (!response.ok) {
            throw new Error('Error al cargar el perfil');
        }

        const usuario = await response.json();
        console.log('✅ Perfil cargado:', usuario);

        // Guardar datos originales
        datosOriginales = { ...usuario };

        // Cargar materias primero
        await cargarMaterias();

        // Mostrar datos en la interfaz
        mostrarDatosPerfil(usuario);

        // Mostrar actividad reciente
        mostrarActividadReciente(usuario);

        loadingEl.style.display = 'none';
        contentEl.style.display = 'block';

    } catch (error) {
        console.error('❌ Error al cargar perfil:', error);
        loadingEl.innerHTML = `
            <div class="alert alert-danger" style="max-width: 500px;">
                <h4>⚠️ Error al cargar perfil</h4>
                <p>${error.message}</p>
                <button class="btn-primary" onclick="location.reload()">Reintentar</button>
            </div>
        `;
    }
}

// ===== MOSTRAR DATOS EN LA INTERFAZ =====
function mostrarDatosPerfil(usuario) {
    // Avatar y nombre
    const nombreCompleto = `${usuario.nombre} ${usuario.apellido || ''}`.trim();
    document.getElementById('avatarNombre').textContent = nombreCompleto;
    document.getElementById('avatarRol').textContent = usuario.rol;

    // Avatar imagen - CON MANEJO CORRECTO DE ERRORES
    const avatarImg = document.getElementById('avatarImg');
    const avatarDefault = `https://ui-avatars.com/api/?name=${encodeURIComponent(nombreCompleto)}&size=200&background=0D5957&color=fff&bold=true`;

    if (usuario.id && usuario.foto) {
        console.log('📸 Usuario tiene foto:', usuario.foto);

        
        let fotoUrl;

        if (usuario.foto.startsWith('/')) {
            // Ya tiene la barra, solo agregar API_URL y timestamp
            fotoUrl = `${API_URL}${usuario.foto}?t=${Date.now()}`;
        } else if (usuario.foto.startsWith('uploads/')) {
            // Agregar barra y API_URL
            fotoUrl = `${API_URL}/${usuario.foto}?t=${Date.now()}`;
        } else {
            // Asumir que es solo el nombre del archivo
            fotoUrl = `${API_URL}/uploads/${usuario.foto}?t=${Date.now()}`;
        }

        console.log('🔗 URL de foto completa:', fotoUrl);

        // Crear nueva imagen para probar si carga
        const testImg = new Image();
        testImg.onload = () => {
            console.log('✅ Foto cargada exitosamente desde:', fotoUrl);
            avatarImg.src = fotoUrl;
        };

        testImg.onerror = () => {
            console.warn('⚠️ No se pudo cargar foto desde:', fotoUrl);
            console.warn('Probando con GET /api/perfil/foto/' + usuario.id);

            // Intentar endpoint alternativo
            const altFotoUrl = `${API_URL}/perfil/foto/${usuario.id}?t=${Date.now()}`;
            console.log('🔄 Intentando URL alternativa:', altFotoUrl);
            avatarImg.src = altFotoUrl;

            avatarImg.onerror = () => {
                console.warn('❌ Ninguna URL funcionó, usando avatar por defecto');
                avatarImg.src = avatarDefault;
            };
        };

        testImg.src = fotoUrl;

    } else {
        console.log('ℹ️ Sin foto de perfil (usuario.foto es:', usuario.foto, '), usando avatar por defecto');
        avatarImg.src = avatarDefault;
    }

    // Información lateral
    document.getElementById('infoEmail').textContent = usuario.email;
    document.getElementById('infoRol').textContent = traducirRol(usuario.rol);
    document.getElementById('infoFecha').textContent = formatearFecha(usuario.fechaRegistro);

    // Materia favorita
    const materiaFavEl = document.getElementById('infoMateriaFav');
    materiaFavEl.textContent = usuario.materiaFavoritaNombre || 'Sin materia favorita';

    // Formulario
    document.getElementById('nombre').value = usuario.nombre || '';
    document.getElementById('apellido').value = usuario.apellido || '';
    document.getElementById('email').value = usuario.email || '';
    document.getElementById('biografia').value = usuario.biografia || '';
    document.getElementById('materiaFavorita').value = usuario.materiaFavoritaId || '';

    // Contador de biografía
    actualizarContadorBiografia();
}

// ===== MOSTRAR ACTIVIDAD RECIENTE =====
function mostrarActividadReciente(usuario) {
    const actividadList = document.getElementById('actividadList');

    const actividades = [];

    // Fecha de registro
    actividades.push({
        icono: '🎉',
        texto: 'Te uniste a EduAventuras',
        fecha: usuario.fechaRegistro
    });

    // Última actualización de perfil
    if (usuario.ultimaActualizacion && usuario.ultimaActualizacion !== usuario.fechaRegistro) {
        actividades.push({
            icono: '✏️',
            texto: 'Actualizaste tu perfil',
            fecha: usuario.ultimaActualizacion
        });
    }

    // Ordenar por fecha (más reciente primero)
    actividades.sort((a, b) => new Date(b.fecha) - new Date(a.fecha));

    // Tomar las últimas 5
    const ultimasActividades = actividades.slice(0, 5);

    if (ultimasActividades.length === 0) {
        actividadList.innerHTML = '<p class="text-muted">Sin actividad reciente</p>';
        return;
    }

    actividadList.innerHTML = ultimasActividades.map(act => `
        <div class="actividad-item">
            <span class="actividad-icono">${act.icono}</span>
            <div class="actividad-content">
                <div class="actividad-text">${act.texto}</div>
                <div class="actividad-time">${obtenerTiempoRelativo(act.fecha)}</div>
            </div>
        </div>
    `).join('');
}

// ===== TIEMPO RELATIVO =====
function obtenerTiempoRelativo(fecha) {
    const ahora = new Date();
    const fechaDate = new Date(fecha);
    const diferencia = ahora - fechaDate;

    const minutos = Math.floor(diferencia / 60000);
    const horas = Math.floor(diferencia / 3600000);
    const dias = Math.floor(diferencia / 86400000);
    const meses = Math.floor(dias / 30);
    const años = Math.floor(dias / 365);

    if (minutos < 1) return 'Hace un momento';
    if (minutos < 60) return `Hace ${minutos} minuto${minutos > 1 ? 's' : ''}`;
    if (horas < 24) return `Hace ${horas} hora${horas > 1 ? 's' : ''}`;
    if (dias < 30) return `Hace ${dias} día${dias > 1 ? 's' : ''}`;
    if (meses < 12) return `Hace ${meses} mes${meses > 1 ? 'es' : ''}`;
    return `Hace ${años} año${años > 1 ? 's' : ''}`;
}

// ===== CONTADOR DE BIOGRAFÍA =====
document.getElementById('biografia').addEventListener('input', actualizarContadorBiografia);

function actualizarContadorBiografia() {
    const biografia = document.getElementById('biografia').value;
    document.getElementById('bioCount').textContent = biografia.length;
}

// ===== TRADUCIR ROL =====
function traducirRol(rol) {
    const roles = {
        'ADMIN': 'Administrador',
        'DOCENTE': 'Docente',
        'ESTUDIANTE': 'Estudiante'
    };
    return roles[rol] || rol;
}

// ===== FORMATEAR FECHA =====
function formatearFecha(fecha) {
    if (!fecha) return '-';
    const date = new Date(fecha);
    const opciones = { year: 'numeric', month: 'long', day: 'numeric' };
    return date.toLocaleDateString('es-ES', opciones);
}

// ===== ACTUALIZAR PERFIL =====
async function actualizarPerfil(e) {
    e.preventDefault();

    const btnGuardar = document.getElementById('btnGuardar');
    const btnText = document.getElementById('btnGuardarText');
    const btnSpinner = document.getElementById('btnGuardarSpinner');
    const alertEl = document.getElementById('alertPerfil');

    // Deshabilitar botón
    btnGuardar.disabled = true;
    btnText.textContent = '💾 Guardando...';
    btnSpinner.style.display = 'inline-block';
    alertEl.style.display = 'none';

    const datosActualizados = {
        nombre: document.getElementById('nombre').value.trim(),
        apellido: document.getElementById('apellido').value.trim(),
        biografia: document.getElementById('biografia').value.trim() || null,
        materiaFavoritaId: document.getElementById('materiaFavorita').value || null
    };

    // Convertir a número si existe
    if (datosActualizados.materiaFavoritaId) {
        datosActualizados.materiaFavoritaId = parseInt(datosActualizados.materiaFavoritaId);
    }

    try {
        console.log('🔤 Actualizando perfil:', datosActualizados);

        const response = await window.fetchAutenticado(`${API_URL}/perfil`, {
            method: 'PUT',
            body: JSON.stringify(datosActualizados)
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || 'Error al actualizar perfil');
        }

        const resultado = await response.json();
        console.log('✅ Perfil actualizado:', resultado);

        // Actualizar localStorage con nuevos datos
        const usuarioActual = window.getCurrentUser();
        usuarioActual.nombre = datosActualizados.nombre;
        usuarioActual.apellido = datosActualizados.apellido;
        localStorage.setItem('usuario', JSON.stringify(usuarioActual));

        // Mostrar éxito
        alertEl.className = 'alert alert-success';
        alertEl.textContent = '✅ ' + (resultado.mensaje || 'Perfil actualizado exitosamente');
        alertEl.style.display = 'block';

        // Recargar datos
        setTimeout(() => {
            cargarPerfil();
            window.actualizarNavbar();
        }, 1500);

    } catch (error) {
        console.error('❌ Error al actualizar:', error);
        alertEl.className = 'alert alert-danger';
        alertEl.textContent = '❌ ' + error.message;
        alertEl.style.display = 'block';

        // Rehabilitar botón
        btnGuardar.disabled = false;
        btnText.textContent = '💾 Guardar Cambios';
        btnSpinner.style.display = 'none';
    }
}

// ===== CANCELAR EDICIÓN =====
function cancelarEdicion() {
    if (datosOriginales) {
        document.getElementById('nombre').value = datosOriginales.nombre || '';
        document.getElementById('apellido').value = datosOriginales.apellido || '';
        document.getElementById('biografia').value = datosOriginales.biografia || '';
        document.getElementById('materiaFavorita').value = datosOriginales.materiaFavoritaId || '';
        actualizarContadorBiografia();
    }
    document.getElementById('alertPerfil').style.display = 'none';
}

// ===== SELECCIONAR FOTO =====
document.getElementById('fotoInput').addEventListener('change', (e) => {
    const file = e.target.files[0];

    if (!file) return;

    console.log('📸 Archivo seleccionado:', file.name);

    // Validar tipo de archivo
    if (!file.type.startsWith('image/')) {
        alert('❌ Por favor selecciona una imagen válida');
        return;
    }

    // Validar tamaño (máximo 5MB)
    if (file.size > 5 * 1024 * 1024) {
        alert('❌ La imagen es demasiado grande. Máximo 5MB');
        return;
    }

    fotoSeleccionada = file;

    // Mostrar preview
    const reader = new FileReader();
    reader.onload = (e) => {
        document.getElementById('previewImg').src = e.target.result;
        document.getElementById('previewModal').classList.add('show');
    };
    reader.readAsDataURL(file);
});

// ===== CONFIRMAR SUBIDA DE FOTO =====
document.getElementById('btnConfirmarFoto').addEventListener('click', async () => {
    if (!fotoSeleccionada) return;

    const btnConfirmar = document.getElementById('btnConfirmarFoto');
    const btnFotoText = document.getElementById('btnFotoText');
    const btnFotoSpinner = document.getElementById('btnFotoSpinner');

    // Deshabilitar botón
    btnConfirmar.disabled = true;
    btnFotoText.textContent = '🔤 Subiendo...';
    btnFotoSpinner.style.display = 'inline-block';

    try {
        console.log('📤 Preparando carga de foto...');

        const formData = new FormData();
        formData.append('foto', fotoSeleccionada);

        console.log('📤 Enviando solicitud POST /api/perfil/foto');

        const token = window.getToken();
        const response = await fetch(`${API_URL}/perfil/foto`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        console.log('📡 Response status:', response.status);

        if (!response.ok) {
            const errorData = await response.json();
            console.error('❌ Error response:', errorData);
            throw new Error(errorData.error || 'Error al subir foto');
        }

        const resultado = await response.json();
        console.log('✅ Foto subida exitosamente:', resultado);

        // Cerrar modal y recargar perfil
        cerrarPreview();
        await cargarPerfil();

        alert('✅ Foto actualizada exitosamente');

    } catch (error) {
        console.error('❌ Error al subir foto:', error);
        alert('❌ ' + error.message);

        // Rehabilitar botón
        btnConfirmar.disabled = false;
        btnFotoText.textContent = '🔤 Subir Foto';
        btnFotoSpinner.style.display = 'none';
    }
});

// ===== CERRAR PREVIEW =====
function cerrarPreview() {
    document.getElementById('previewModal').classList.remove('show');
    document.getElementById('fotoInput').value = '';
    fotoSeleccionada = null;
}

// ===== ELIMINAR FOTO =====
document.getElementById('btnEliminarFoto').addEventListener('click', async () => {
    if (!confirm('¿Estás seguro de eliminar tu foto de perfil?')) return;

    try {
        const response = await window.fetchAutenticado(`${API_URL}/perfil/foto`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Error al eliminar foto');
        }

        await cargarPerfil();
        alert('✅ Foto eliminada exitosamente');

    } catch (error) {
        console.error('❌ Error al eliminar foto:', error);
        alert('❌ ' + error.message);
    }
});

// ===== CERRAR MODAL AL HACER CLIC FUERA =====
document.getElementById('previewModal').addEventListener('click', (e) => {
    if (e.target.id === 'previewModal') {
        cerrarPreview();
    }
});

// ===== EVENT LISTENERS =====
document.getElementById('perfilForm').addEventListener('submit', actualizarPerfil);

// ===== INICIALIZAR =====
async function inicializar() {
    // Proteger página (requiere autenticación)
    if (!window.protegerPagina()) {
        return;
    }

    // Actualizar navbar
    window.actualizarNavbar();

    // Cargar perfil
    await cargarPerfil();
}

// Ejecutar al cargar
document.addEventListener('DOMContentLoaded', inicializar);

// Exponer funciones globales
window.cancelarEdicion = cancelarEdicion;
window.cerrarPreview = cerrarPreview;

console.log('✅ perfil.js cargado');