// ===== CONFIGURACIÓN =====
const API_BASE_URL = 'http://localhost:8080/api';

// ===== ELEMENTOS DEL DOM =====
const loginForm = document.getElementById('loginForm');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const btnLogin = document.getElementById('btnLogin');
const btnText = document.getElementById('btnText');
const btnSpinner = document.getElementById('btnSpinner');
const togglePasswordBtn = document.getElementById('togglePassword');
const eyeIcon = document.getElementById('eyeIcon');
const rememberMeCheckbox = document.getElementById('rememberMe');
const alertContainer = document.getElementById('alertContainer');

// ===== VERIFICAR SI YA ESTÁ AUTENTICADO =====
function verificarSesionActiva() {
    const token = localStorage.getItem('token');
    const usuario = localStorage.getItem('usuario');

    if (token && usuario) {
        console.log('✅ Usuario ya autenticado. Redirigiendo...');
        const user = JSON.parse(usuario);
        redirigirSegunRol(user.rol);
    }
}

// ===== TOGGLE PARA MOSTRAR/OCULTAR CONTRASEÑA =====
function inicializarTogglePassword() {
    togglePasswordBtn.addEventListener('click', () => {
        const type = passwordInput.type === 'password' ? 'text' : 'password';
        passwordInput.type = type;
        eyeIcon.textContent = type === 'password' ? '👁️' : '🙈';
    });
}

// ===== VALIDACIÓN EN TIEMPO REAL =====
function inicializarValidaciones() {
    emailInput.addEventListener('blur', () => {
        validarEmail(emailInput);
    });

    passwordInput.addEventListener('blur', () => {
        validarCampoRequerido(passwordInput);
    });

    emailInput.addEventListener('input', () => {
        if (emailInput.classList.contains('is-invalid')) {
            validarEmail(emailInput);
        }
    });

    passwordInput.addEventListener('input', () => {
        if (passwordInput.classList.contains('is-invalid')) {
            validarCampoRequerido(passwordInput);
        }
    });
}

// ===== FUNCIÓN: VALIDAR EMAIL =====
function validarEmail(input) {
    const email = input.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    if (!emailRegex.test(email)) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR CAMPO REQUERIDO =====
function validarCampoRequerido(input) {
    const valor = input.value.trim();

    if (!valor) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR FORMULARIO COMPLETO =====
function validarFormulario() {
    const emailValido = validarEmail(emailInput);
    const passwordValido = validarCampoRequerido(passwordInput);

    return emailValido && passwordValido;
}

// ===== FUNCIÓN: MOSTRAR ALERTA =====
function mostrarAlerta(mensaje, tipo = 'danger') {
    alertContainer.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            <strong>${tipo === 'success' ? '✅' : tipo === 'warning' ? '⚠️' : '❌'}</strong> ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            alert.classList.remove('show');
            setTimeout(() => {
                alert.remove();
            }, 300);
        }
    }, 5000);
}

// ===== FUNCIÓN: MOSTRAR ESTADO DE CARGA =====
function mostrarCargando(cargando) {
    if (cargando) {
        btnLogin.disabled = true;
        btnText.classList.add('d-none');
        btnSpinner.classList.remove('d-none');
    } else {
        btnLogin.disabled = false;
        btnText.classList.remove('d-none');
        btnSpinner.classList.add('d-none');
    }
}

// ===== FUNCIÓN PRINCIPAL: LOGIN =====
async function iniciarSesion(email, password) {
    console.log('🔐 Intentando iniciar sesión...');
    console.log('📧 Email:', email);

    try {
        mostrarCargando(true);

        // Hacer petición al backend
        const response = await fetch(`${API_BASE_URL}/usuarios/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        console.log('📡 Respuesta del servidor:', response.status);

        // Verificar respuesta
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.mensaje || errorData.error || 'Credenciales incorrectas');
        }

        // Obtener datos de la respuesta
        const data = await response.json();
        console.log('✅ Respuesta completa del backend:', data);

        // ✅ CORRECCIÓN: El backend devuelve { token, usuario: {...}, mensaje }
        // Verificar que venga el token
        if (!data.token) {
            throw new Error('No se recibió el token de autenticación');
        }

        // ✅ CORRECCIÓN: Extraer datos del objeto "usuario"
        const usuario = data.usuario || data; // Fallback por si cambia estructura

        // Guardar token y usuario en localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('usuario', JSON.stringify({
            id: usuario.id,
            nombre: usuario.nombre,
            apellido: usuario.apellido || '',  // ← Ahora sí accede correctamente
            email: usuario.email,
            rol: usuario.rol
        }));

        console.log('💾 Token guardado en localStorage');
        console.log(`👤 Usuario: ${usuario.nombre} ${usuario.apellido || ''} | Rol: ${usuario.rol}`);

        // Verificar si marcó "Recordarme"
        if (rememberMeCheckbox.checked) {
            localStorage.setItem('recordarme', 'true');
            console.log('✅ Recordar sesión activado');
        }

        // Mostrar mensaje de éxito
        mostrarAlerta('¡Inicio de sesión exitoso! Redirigiendo...', 'success');

        // Redirigir según el rol después de 1 segundo
        setTimeout(() => {
            redirigirSegunRol(usuario.rol);
        }, 1000);

    } catch (error) {
        console.error('❌ Error en login:', error);
        mostrarAlerta(error.message || 'Error al iniciar sesión. Verifica tus credenciales.', 'danger');
        mostrarCargando(false);
    }
}

// ===== FUNCIÓN: REDIRIGIR SEGÚN ROL =====
function redirigirSegunRol(rol) {
    console.log('🔀 Redirigiendo según rol:', rol);

    switch (rol) {
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

// ===== EVENTO: SUBMIT DEL FORMULARIO =====
function inicializarFormulario() {
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        console.log('📝 Formulario enviado');

        // Validar formulario
        if (!validarFormulario()) {
            console.warn('⚠️ Formulario inválido');
            mostrarAlerta('Por favor, completa todos los campos correctamente.', 'warning');
            return;
        }

        // Obtener valores
        const email = emailInput.value.trim();
        const password = passwordInput.value.trim();

        // Iniciar sesión
        await iniciarSesion(email, password);
    });
}

// ===== FUNCIÓN: CARGAR CREDENCIALES GUARDADAS =====
function cargarCredencialesRecordadas() {
    const recordarme = localStorage.getItem('recordarme');
    const usuarioGuardado = localStorage.getItem('usuario');

    if (recordarme === 'true' && usuarioGuardado) {
        try {
            const usuario = JSON.parse(usuarioGuardado);
            emailInput.value = usuario.email || '';
            rememberMeCheckbox.checked = true;
            console.log('✅ Credenciales recordadas cargadas');
        } catch (error) {
            console.error('Error al cargar credenciales:', error);
        }
    }
}

// ===== MANEJO DE PARÁMETROS URL =====

function verificarParametrosURL() {
    const params = new URLSearchParams(window.location.search);
    const mensajeURL = params.get('mensaje'); // Este es el código de error (ej: error.acceso.denegado)
    const tipo = params.get('tipo') || 'info';

    if (mensajeURL) {


        let codigoError = decodeURIComponent(mensajeURL);
        const mensajeFinal = t(codigoError);

        mostrarAlerta(mensajeFinal, tipo);

        // Limpiar URL sin recargar página
        window.history.replaceState({}, document.title, window.location.pathname);
    }
}

// ===== INICIALIZACIÓN AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Login page cargada');

    // Verificar si ya hay sesión activa
    verificarSesionActiva();

    // Inicializar funcionalidades
    inicializarTogglePassword();
    inicializarValidaciones();
    inicializarFormulario();
    cargarCredencialesRecordadas();
    verificarParametrosURL();

    console.log('✅ Inicialización completada');
});

// ===== MANEJO DE ERRORES GLOBALES =====
window.addEventListener('unhandledrejection', (event) => {
    console.error('❌ Error no manejado:', event.reason);
    mostrarAlerta('Ocurrió un error inesperado. Por favor, intenta nuevamente.', 'danger');
});