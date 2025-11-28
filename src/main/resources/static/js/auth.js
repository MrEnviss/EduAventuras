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
        console.error('❌ Error al parsear usuario:', error);
        return null;
    }
}

// ===== FUNCIÓN: VERIFICAR SI ESTÁ AUTENTICADO =====
function isAuthenticated() {
    const token = getToken();
    const usuario = getCurrentUser();
    const autenticado = !!(token && usuario);

    console.log('🔑 isAuthenticated:', {
        token: !!token,
        usuario: !!usuario,
        autenticado: autenticado
    });

    return autenticado;
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

    // Redirigir al login
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
    const autenticado = isAuthenticated();

    console.log('🔄 Actualizando navbar...');
    console.log('📊 Estado:', {
        autenticado: autenticado,
        usuario: usuario,
        rol: usuario?.rol
    });

    // Elementos del navbar
    const navGuest = document.querySelectorAll('.nav-guest');
    const navAuthenticated = document.querySelectorAll('.nav-authenticated');
    const navAdmin = document.querySelectorAll('.nav-admin');
    const navDocente = document.querySelectorAll('.nav-docente');
    const userName = document.getElementById('userName');

    console.log('📋 Elementos encontrados:', {
        navGuest: navGuest.length,
        navAuthenticated: navAuthenticated.length,
        navAdmin: navAdmin.length,
        navDocente: navDocente.length,
        userName: !!userName
    });

    if (autenticado && usuario) {
        // ✅ USUARIO LOGUEADO
        console.log(`👤 Usuario autenticado: ${usuario.nombre} ${usuario.apellido || ''} (${usuario.rol})`);

        // Ocultar elementos de invitado
        navGuest.forEach(el => {
            el.style.display = 'none';
            console.log('🔒 Ocultando botón guest:', el.textContent);
        });

        // Mostrar elementos de autenticado
        navAuthenticated.forEach(el => {
            el.style.display = 'block';
            console.log('✅ Mostrando elemento autenticado');
        });

        // Actualizar nombre del usuario
        if (userName) {
            userName.textContent = usuario.nombre || 'Usuario';
            console.log('✅ Nombre actualizado:', userName.textContent);
        }

        // Mostrar enlaces según ROL
        if (usuario.rol === 'ADMIN') {
            // ADMIN ve TODO
            navAdmin.forEach(el => el.style.display = 'block');
            console.log('👑 Permisos ADMIN activados');

        } else if (usuario.rol === 'DOCENTE') {
            // DOCENTE ve solo sus enlaces
            navAdmin.forEach(el => el.style.display = 'none');
            navDocente.forEach(el => {
                if (!el.classList.contains('nav-admin')) {
                    el.style.display = 'block';
                }
            });
            console.log('👨‍🏫 Permisos DOCENTE activados');

        } else if (usuario.rol === 'ESTUDIANTE') {
            // ESTUDIANTE solo ve lo básico
            navAdmin.forEach(el => el.style.display = 'none');
            navDocente.forEach(el => el.style.display = 'none');
            console.log('👤 Permisos ESTUDIANTE activados');
        }

    } else {
        // ❌ USUARIO NO LOGUEADO
        console.log('👋 Usuario no autenticado - Mostrando navbar público');

        // Mostrar elementos de invitado
        navGuest.forEach(el => {
            el.style.display = 'block';
            console.log('✅ Mostrando botón guest:', el.textContent);
        });

        // Ocultar elementos de autenticado
        navAuthenticated.forEach(el => el.style.display = 'none');
        navAdmin.forEach(el => el.style.display = 'none');
        navDocente.forEach(el => el.style.display = 'none');
    }

    console.log('✅ Navbar actualizado correctamente');
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
            throw new Error('Sesión expirada');
        }

        return response;

    } catch (error) {
        console.error('❌ Error en petición autenticada:', error);
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

// ===== AUTO-ACTUALIZAR NAVBAR AL CARGAR =====
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        console.log('📄 DOM cargado - Auto-actualizando navbar');
        setTimeout(actualizarNavbar, 100); // Pequeño delay para asegurar que todo esté cargado
    });
} else {
    console.log('📄 DOM ya cargado - Auto-actualizando navbar inmediatamente');
    setTimeout(actualizarNavbar, 100);
}

console.log('✅ Módulo de autenticación cargado');