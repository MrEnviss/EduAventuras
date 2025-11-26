// ===== MÓDULO DE AUTENTICACIÓN - EDUAVENTURAS =====

const API_BASE_URL = 'http://localhost:8080/api';

// ===== FUNCIÓN: OBTENER TOKEN =====
function getToken() {
    return localStorage.getItem('token');
}

// ===== FUNCIÓN: OBTENER USUARIO ACTUAL =====
function getCurrentUser() {
    const usuarioString = localStorage.getItem('usuario');
    if (!usuarioString) return null;

    try {
        return JSON.parse(usuarioString);
    } catch (error) {
        console.error('Error al parsear usuario:', error);
        return null;
    }
}

// ===== FUNCIÓN: VERIFICAR SI ESTÁ AUTENTICADO =====
function isAuthenticated() {
    const token = getToken();
    const usuario = getCurrentUser();
    return !!(token && usuario);
}

// ===== FUNCIÓN: VERIFICAR ROL DEL USUARIO =====
function hasRole(rolRequerido) {
    const usuario = getCurrentUser();
    if (!usuario) return false;

    return usuario.rol === rolRequerido;
}

// ===== FUNCIÓN: CERRAR SESIÓN =====
function logout() {
    console.log('🚪 Cerrando sesión...');

    // Limpiar localStorage
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    localStorage.removeItem('recordarme');

    console.log('✅ Sesión cerrada');

    // Redirigir al login con mensaje
    window.location.href = 'login.html?mensaje=Sesión cerrada correctamente&tipo=success';
}

// ===== FUNCIÓN: PROTEGER PÁGINA (Requiere autenticación) =====
function protegerPagina() {
    if (!isAuthenticated()) {
        console.warn('⚠️ Acceso no autorizado - Redirigiendo al login');
        window.location.href = 'login.html?mensaje=Debes iniciar sesión para acceder&tipo=warning';
        return false;
    }
    return true;
}

// ===== FUNCIÓN: PROTEGER PÁGINA POR ROL =====
function protegerPaginaPorRol(rolesPermitidos) {
    if (!isAuthenticated()) {
        window.location.href = 'login.html?mensaje=Debes iniciar sesión para acceder&tipo=warning';
        return false;
    }

    const usuario = getCurrentUser();
    if (!rolesPermitidos.includes(usuario.rol)) {
        console.warn('⚠️ Acceso denegado - Rol insuficiente');
        window.location.href = 'home.html';
        return false;
    }

    return true;
}

// ===== FUNCIÓN: ACTUALIZAR NAVBAR SEGÚN AUTENTICACIÓN =====
function actualizarNavbar() {
    const usuario = getCurrentUser();
    const navbarNav = document.querySelector('#navbarNav .navbar-nav');

    if (!navbarNav) return; // Si no hay navbar, salir

    // Buscar los botones de login/registro
    const botonesAuth = navbarNav.querySelectorAll('.btn-login, .btn-register');

    if (isAuthenticated()) {
        // Usuario autenticado: Mostrar perfil y logout
        botonesAuth.forEach(btn => btn.parentElement.remove());

        // Agregar items del usuario
        const userItems = `
            <li class="nav-item">
                <a class="nav-link" href="materias.html">Materias</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="perfil.html">Mi Perfil</a>
            </li>
            ${usuario.rol === 'ADMIN' ? `
                <li class="nav-item">
                    <a class="nav-link" href="/admin-dashboard.html">Dashboard</a>
                </li>
            ` : ''}
            ${usuario.rol === 'DOCENTE' || usuario.rol === 'ADMIN' ? `
                <li class="nav-item">
                    <a class="nav-link" href="/subir-recurso.html">Subir Recurso</a>
                </li>
            ` : ''}
            <li class="nav-item ms-3">
                <span class="navbar-text me-2">
                    👤 ${usuario.nombre}
                </span>
            </li>
            <li class="nav-item">
                <button onclick="logout()" class="btn btn-outline-danger btn-sm">
                    Cerrar Sesión
                </button>
            </li>
        `;

        navbarNav.insertAdjacentHTML('beforeend', userItems);

    } else {
        // Usuario no autenticado: Mostrar login y registro (ya están en el HTML)
        console.log('Usuario no autenticado - Navbar por defecto');
    }
}

// ===== FUNCIÓN: OBTENER HEADERS CON AUTENTICACIÓN =====
function getAuthHeaders() {
    const token = getToken();
    const headers = {
        'Content-Type': 'application/json'
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    return headers;
}

// ===== FUNCIÓN: HACER PETICIÓN AUTENTICADA =====
async function fetchAutenticado(url, opciones = {}) {
    const token = getToken();

    if (!token) {
        throw new Error('No hay token de autenticación');
    }

    const opcionesConAuth = {
        ...opciones,
        headers: {
            ...opciones.headers,
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    };

    try {
        const response = await fetch(url, opcionesConAuth);

        // Si el token expiró o es inválido (401)
        if (response.status === 401) {
            console.error('❌ Token inválido o expirado');
            logout();
            throw new Error('Sesión expirada. Por favor, inicia sesión nuevamente.');
        }

        return response;

    } catch (error) {
        console.error('Error en petición autenticada:', error);
        throw error;
    }
}

// ===== FUNCIÓN: REDIRIGIR SEGÚN ROL =====
function redirectByRole() {
    const usuario = getCurrentUser();

    if (!usuario) {
        window.location.href = 'login.html';
        return;
    }

    switch (usuario.rol) {
        case 'ADMIN':
            window.location.href = 'admin-dashboard.html';
            break;
        case 'DOCENTE':
            window.location.href = 'materias.html';
            break;
        case 'ESTUDIANTE':
            window.location.href = 'materias.html';
            break;
        default:
            window.location.href = 'home.html';
    }
}

// ===== EXPONER FUNCIONES GLOBALMENTE =====
window.getToken = getToken;
window.getCurrentUser = getCurrentUser;
window.isAuthenticated = isAuthenticated;
window.hasRole = hasRole;
window.logout = logout;
window.protegerPagina = protegerPagina;
window.protegerPaginaPorRol = protegerPaginaPorRol;
window.actualizarNavbar = actualizarNavbar;
window.getAuthHeaders = getAuthHeaders;
window.fetchAutenticado = fetchAutenticado;
window.redirectByRole = redirectByRole;
window.API_BASE_URL = API_BASE_URL;

console.log('✅ Módulo de autenticación cargado');