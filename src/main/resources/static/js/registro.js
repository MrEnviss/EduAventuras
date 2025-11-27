// ===== CONFIGURACIÓN =====
const API_BASE_URL = 'http://localhost:8080/api';

// ===== ELEMENTOS DEL DOM =====
const registroForm = document.getElementById('registroForm');
const nombreInput = document.getElementById('nombre');
const apellidoInput = document.getElementById('apellido');
const emailInput = document.getElementById('email');
const passwordInput = document.getElementById('password');
const confirmPasswordInput = document.getElementById('confirmPassword');
const rolSelect = document.getElementById('rol');
const terminosCheckbox = document.getElementById('terminos');
const btnRegistro = document.getElementById('btnRegistro');
const btnText = document.getElementById('btnText');
const btnSpinner = document.getElementById('btnSpinner');
const alertContainer = document.getElementById('alertContainer');

// Botones toggle password
const togglePasswordBtn = document.getElementById('togglePassword');
const toggleConfirmPasswordBtn = document.getElementById('toggleConfirmPassword');
const eyeIcon = document.getElementById('eyeIcon');
const eyeIcon2 = document.getElementById('eyeIcon2');

// ===== TOGGLE PARA MOSTRAR/OCULTAR CONTRASEÑA =====
function inicializarTogglePassword() {
    togglePasswordBtn.addEventListener('click', () => {
        const type = passwordInput.type === 'password' ? 'text' : 'password';
        passwordInput.type = type;
        eyeIcon.textContent = type === 'password' ? '👁️' : '🙈';
    });

    toggleConfirmPasswordBtn.addEventListener('click', () => {
        const type = confirmPasswordInput.type === 'password' ? 'text' : 'password';
        confirmPasswordInput.type = type;
        eyeIcon2.textContent = type === 'password' ? '👁️' : '🙈';
    });
}

// ===== VALIDACIÓN EN TIEMPO REAL =====
function inicializarValidaciones() {
    // Validar nombre
    nombreInput.addEventListener('blur', () => {
        validarNombre(nombreInput);
    });

    nombreInput.addEventListener('input', () => {
        if (nombreInput.classList.contains('is-invalid')) {
            validarNombre(nombreInput);
        }
    });

    // Validar apellido
    apellidoInput.addEventListener('blur', () => {
        validarApellido(apellidoInput);
    });

    apellidoInput.addEventListener('input', () => {
        if (apellidoInput.classList.contains('is-invalid')) {
            validarApellido(apellidoInput);
        }
    });

    // Validar email
    emailInput.addEventListener('blur', () => {
        validarEmail(emailInput);
    });

    emailInput.addEventListener('input', () => {
        if (emailInput.classList.contains('is-invalid')) {
            validarEmail(emailInput);
        }
    });

    // Validar contraseña
    passwordInput.addEventListener('blur', () => {
        validarPassword(passwordInput);
        if (confirmPasswordInput.value) {
            validarPasswordsCoinciden();
        }
    });

    passwordInput.addEventListener('input', () => {
        if (passwordInput.classList.contains('is-invalid')) {
            validarPassword(passwordInput);
        }
        if (confirmPasswordInput.value) {
            validarPasswordsCoinciden();
        }
    });

    // Validar confirmación de contraseña
    confirmPasswordInput.addEventListener('blur', () => {
        validarPasswordsCoinciden();
    });

    confirmPasswordInput.addEventListener('input', () => {
        if (confirmPasswordInput.classList.contains('is-invalid')) {
            validarPasswordsCoinciden();
        }
    });

    // Validar rol
    rolSelect.addEventListener('change', () => {
        validarRol(rolSelect);
    });
}

// ===== FUNCIÓN: VALIDAR NOMBRE =====
function validarNombre(input) {
    const nombre = input.value.trim();

    if (!nombre || nombre.length < 2) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR APELLIDO =====
function validarApellido(input) {
    const apellido = input.value.trim();

    if (!apellido || apellido.length < 2) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR EMAIL =====
function validarEmail(input) {
    const email = input.value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!email || !emailRegex.test(email)) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR PASSWORD =====
function validarPassword(input) {
    const password = input.value;

    if (!password || password.length < 6) {
        input.classList.add('is-invalid');
        input.classList.remove('is-valid');
        return false;
    }

    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR QUE PASSWORDS COINCIDAN =====
function validarPasswordsCoinciden() {
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (!confirmPassword) {
        confirmPasswordInput.classList.add('is-invalid');
        confirmPasswordInput.classList.remove('is-valid');
        return false;
    }

    if (password !== confirmPassword) {
        confirmPasswordInput.classList.add('is-invalid');
        confirmPasswordInput.classList.remove('is-valid');
        document.getElementById('passwordMatchError').textContent = 'Las contraseñas no coinciden';
        return false;
    }

    confirmPasswordInput.classList.remove('is-invalid');
    confirmPasswordInput.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR ROL =====
function validarRol(select) {
    if (!select.value) {
        select.classList.add('is-invalid');
        select.classList.remove('is-valid');
        return false;
    }

    select.classList.remove('is-invalid');
    select.classList.add('is-valid');
    return true;
}

// ===== FUNCIÓN: VALIDAR FORMULARIO COMPLETO =====
function validarFormulario() {
    const nombreValido = validarNombre(nombreInput);
    const apellidoValido = validarApellido(apellidoInput);
    const emailValido = validarEmail(emailInput);
    const passwordValido = validarPassword(passwordInput);
    const passwordsCoinciden = validarPasswordsCoinciden();
    const rolValido = validarRol(rolSelect);

    if (!terminosCheckbox.checked) {
        terminosCheckbox.classList.add('is-invalid');
        return false;
    }

    return nombreValido && apellidoValido && emailValido && passwordValido && passwordsCoinciden && rolValido;
}

// ===== FUNCIÓN: MOSTRAR ALERTA =====
function mostrarAlerta(mensaje, tipo = 'danger') {
    alertContainer.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            <strong>${tipo === 'success' ? '✅' : tipo === 'warning' ? '⚠️' : '❌'}</strong> ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    // Scroll al inicio para ver la alerta
    window.scrollTo({ top: 0, behavior: 'smooth' });

    // Auto-cerrar después de 5 segundos si es éxito
    if (tipo === 'success') {
        setTimeout(() => {
            const alert = alertContainer.querySelector('.alert');
            if (alert) {
                alert.classList.remove('show');
                setTimeout(() => alert.remove(), 300);
            }
        }, 5000);
    }
}

// ===== FUNCIÓN: MOSTRAR ESTADO DE CARGA =====
function mostrarCargando(cargando) {
    if (cargando) {
        btnRegistro.disabled = true;
        btnText.classList.add('d-none');
        btnSpinner.classList.remove('d-none');
    } else {
        btnRegistro.disabled = false;
        btnText.classList.remove('d-none');
        btnSpinner.classList.add('d-none');
    }
}

// ===== FUNCIÓN PRINCIPAL: REGISTRAR USUARIO =====
async function registrarUsuario(datos) {
    console.log('📝 Intentando registrar usuario...');
    console.log('📧 Email:', datos.email);
    console.log('👤 Nombre:', datos.nombre, datos.apellido);
    console.log('🏷️ Rol:', datos.rol);

    try {
        mostrarCargando(true);

        // Hacer petición al backend
        const response = await fetch(`${API_BASE_URL}/usuarios/registro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        console.log('📡 Respuesta del servidor:', response.status);

        // Verificar respuesta
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.mensaje || errorData.error || 'Error al registrar usuario');
        }

        // Obtener datos de la respuesta
        const data = await response.json();
        console.log('✅ Registro exitoso:', data);

        // Mostrar mensaje de éxito
        mostrarAlerta('¡Cuenta creada exitosamente! Redirigiendo al login...', 'success');

        // Redirigir al login después de 2 segundos
        setTimeout(() => {
            window.location.href = 'login.html?mensaje=Cuenta creada. Inicia sesión&tipo=success';
        }, 2000);

    } catch (error) {
        console.error('❌ Error en registro:', error);

        // Mensajes de error específicos
        let mensajeError = error.message;

        if (mensajeError.includes('email') || mensajeError.includes('correo')) {
            mensajeError = 'Este correo electrónico ya está registrado.';
        } else if (mensajeError.includes('400')) {
            mensajeError = 'Datos inválidos. Verifica el formulario.';
        } else if (mensajeError.includes('500')) {
            mensajeError = 'Error del servidor. Intenta nuevamente más tarde.';
        }

        mostrarAlerta(mensajeError, 'danger');
        mostrarCargando(false);
    }
}

// ===== EVENTO: SUBMIT DEL FORMULARIO =====
function inicializarFormulario() {
    registroForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        console.log('📝 Formulario enviado');

        // Validar formulario
        if (!validarFormulario()) {
            console.warn('⚠️ Formulario inválido');
            mostrarAlerta('Por favor, completa todos los campos correctamente.', 'warning');
            return;
        }

        // Obtener valores
        const datos = {
            nombre: nombreInput.value.trim(),
            apellido: apellidoInput.value.trim(),
            email: emailInput.value.trim(),
            password: passwordInput.value,
            rol: rolSelect.value
        };

        // Registrar usuario
        await registrarUsuario(datos);
    });
}

// ===== INICIALIZACIÓN AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('🚀 Página de registro cargada');

    // Inicializar funcionalidades
    inicializarTogglePassword();
    inicializarValidaciones();
    inicializarFormulario();

    console.log('✅ Inicialización completada');
});

// ===== MANEJO DE ERRORES GLOBALES =====
window.addEventListener('unhandledrejection', (event) => {
    console.error('❌ Error no manejado:', event.reason);
    mostrarAlerta('Ocurrió un error inesperado. Por favor, intenta nuevamente.', 'danger');
});