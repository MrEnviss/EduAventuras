// ===== RECUPERAR PASSWORD - EDUAVENTURAS =====

const API_URL = 'http://localhost:8080/api';

// ===== PASO 1: SOLICITAR TOKEN =====
document.getElementById('formSolicitarToken')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const email = document.getElementById('emailRecuperacion').value.trim();
    const btnSubmit = e.target.querySelector('button[type="submit"]');

    try {
        btnSubmit.disabled = true;
        btnSubmit.textContent = 'Enviando...';

        console.log('📧 Solicitando recuperación para:', email);

        const response = await fetch(`${API_URL}/password/recuperar`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email })
        });

        const data = await response.json();

        if (response.ok) {
            console.log('✅ Solicitud exitosa');

            // Ocultar paso 1 y mostrar paso 2
            document.getElementById('paso1').classList.remove('active');
            document.getElementById('paso2').classList.add('active');

            // Para testing: mostrar enlace directo
            if (data.token) {
                const enlace = `${window.location.origin}/recuperar-password.html?token=${data.token}`;
                document.getElementById('enlaceRecuperacion').href = enlace;
            }

        } else {
            mostrarError(data.error || 'Error al enviar solicitud');
        }

    } catch (error) {
        console.error('❌ Error:', error);
        mostrarError('Error de conexión. Por favor, intenta nuevamente.');
    } finally {
        btnSubmit.disabled = false;
        btnSubmit.textContent = 'Enviar Enlace';
    }
});

// ===== PASO 3: VALIDAR TOKEN Y MOSTRAR FORMULARIO =====
async function validarToken() {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (!token) {
        // No hay token, mostrar paso 1
        return;
    }

    try {
        console.log('🔍 Validando token...');

        const response = await fetch(`${API_URL}/password/validar-token`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ token })
        });

        const data = await response.json();

        if (response.ok && data.valido) {
            console.log('✅ Token válido');

            // Ocultar paso 1 y mostrar paso 3
            document.getElementById('paso1').classList.remove('active');
            document.getElementById('paso3').classList.add('active');
            document.getElementById('tokenRecuperacion').value = token;

        } else {
            mostrarError('Token inválido o expirado. Por favor, solicita uno nuevo.');
        }

    } catch (error) {
        console.error('❌ Error al validar token:', error);
        mostrarError('Error al validar el token. Por favor, intenta nuevamente.');
    }
}

// ===== RESTABLECER PASSWORD =====
document.getElementById('formNuevaPassword')?.addEventListener('submit', async (e) => {
    e.preventDefault();

    const token = document.getElementById('tokenRecuperacion').value;
    const password = document.getElementById('passwordNueva').value;
    const passwordConfirmar = document.getElementById('passwordConfirmar').value;
    const btnSubmit = e.target.querySelector('button[type="submit"]');

    // Validar que las contraseñas coincidan
    if (password !== passwordConfirmar) {
        mostrarError('Las contraseñas no coinciden');
        return;
    }

    // Validar longitud mínima
    if (password.length < 6) {
        mostrarError('La contraseña debe tener al menos 6 caracteres');
        return;
    }

    try {
        btnSubmit.disabled = true;
        btnSubmit.textContent = 'Restableciendo...';

        console.log('🔐 Restableciendo contraseña...');

        const response = await fetch(`${API_URL}/password/restablecer`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ token, password })
        });

        // ✅ SOLUCIÓN: Verificar si hay contenido antes de parsear
        let data = {};
        const contentType = response.headers.get('content-type');

        // Solo intentar parsear JSON si hay contenido y es tipo JSON
        if (contentType && contentType.includes('application/json')) {
            const text = await response.text();
            if (text) {
                data = JSON.parse(text);
            }
        }

        if (response.ok) {
            console.log('✅ Contraseña restablecida');

            mostrarExito('¡Contraseña restablecida exitosamente! Redirigiendo...');

            // Redirigir al login después de 2 segundos
            setTimeout(() => {
                window.location.href = 'login.html?mensaje=' +
                    encodeURIComponent('Ya puedes iniciar sesión con tu nueva contraseña');
            }, 2000);

        } else {
            mostrarError(data.error || 'Error al restablecer contraseña');
        }

    } catch (error) {
        console.error('❌ Error:', error);
        mostrarError('Error de conexión. Por favor, intenta nuevamente.');
    } finally {
        btnSubmit.disabled = false;
        btnSubmit.textContent = 'Restablecer Contraseña';
    }
});

// ===== MOSTRAR MENSAJES =====
function mostrarError(mensaje) {
    const container = document.getElementById('alertContainer');
    container.innerHTML = `
        <div class="alert-custom alert-danger" role="alert">
            ❌ ${mensaje}
        </div>
    `;

    // Auto-ocultar después de 5 segundos
    setTimeout(() => {
        container.innerHTML = '';
    }, 5000);
}

function mostrarExito(mensaje) {
    const container = document.getElementById('alertContainer');
    container.innerHTML = `
        <div class="alert-custom alert-success" role="alert">
            ✅ ${mensaje}
        </div>
    `;
}

// ===== INICIALIZAR =====
document.addEventListener('DOMContentLoaded', async () => {
    console.log('🔄 Recuperar password - Inicializando');

    // Validar token si existe en URL
    await validarToken();

    console.log('✅ Recuperar password inicializado');
});

console.log('✅ Módulo recuperar-password.js cargado');