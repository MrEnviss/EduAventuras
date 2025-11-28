// ===== SUBIR RECURSO JS =====


// Elementos del DOM
const form = document.getElementById('formSubirRecurso');
const fileInput = document.getElementById('file');
const dropZone = document.getElementById('dropZone');
const btnSelectFile = document.getElementById('btnSelectFile');
const filePreview = document.getElementById('filePreview');
const fileName = document.getElementById('fileName');
const fileSize = document.getElementById('fileSize');
const btnRemoveFile = document.getElementById('btnRemoveFile');
const materiaSelect = document.getElementById('materiaId');
const descripcionTextarea = document.getElementById('descripcion');
const charCount = document.getElementById('charCount');
const btnSubmit = document.getElementById('btnSubmit');
const btnText = document.getElementById('btnText');
const btnSpinner = document.getElementById('btnSpinner');
const alertContainer = document.getElementById('alertContainer');

// Variables
let selectedFile = null;
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

// ===== INICIALIZACIÓN =====
document.addEventListener('DOMContentLoaded', async () => {
    // Proteger página (solo DOCENTE y ADMIN)
    if (typeof protegerPaginaPorRol === 'function') {
        if (!protegerPaginaPorRol(['DOCENTE', 'ADMIN'])) {
            return;
        }
    }

    // Cargar materias
    await cargarMaterias();

    // Inicializar funcionalidades
    inicializarDragAndDrop();
    inicializarFileInput();
    inicializarFormulario();
    inicializarContadorCaracteres();
});

// ===== CARGAR MATERIAS =====
async function cargarMaterias() {
    try {
        const response = await fetch(`${API_BASE_URL}/materias`);

        if (!response.ok) {
            throw new Error('Error al cargar materias');
        }

        const materias = await response.json();

        // Limpiar select
        materiaSelect.innerHTML = '<option value="">Selecciona una materia...</option>';

        // Agregar opciones
        materias.forEach(materia => {
            const option = document.createElement('option');
            option.value = materia.id;
            option.textContent = materia.nombre;
            materiaSelect.appendChild(option);
        });

    } catch (error) {
        console.error('Error al cargar materias:', error);
        mostrarAlerta('Error al cargar las materias. Por favor, recarga la página.', 'danger');
    }
}

// ===== DRAG AND DROP =====
function inicializarDragAndDrop() {
    ['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, preventDefaults, false);
    });

    function preventDefaults(e) {
        e.preventDefault();
        e.stopPropagation();
    }

    ['dragenter', 'dragover'].forEach(eventName => {
        dropZone.addEventListener(eventName, () => {
            dropZone.classList.add('drag-over');
        });
    });

    ['dragleave', 'drop'].forEach(eventName => {
        dropZone.addEventListener(eventName, () => {
            dropZone.classList.remove('drag-over');
        });
    });

    dropZone.addEventListener('drop', (e) => {
        const files = e.dataTransfer.files;
        if (files.length > 0) {
            handleFile(files[0]);
        }
    });
}

// ===== FILE INPUT =====
function inicializarFileInput() {
    // Click en botón "Seleccionar Archivo"
    btnSelectFile.addEventListener('click', () => {
        fileInput.click();
    });

    // Click en toda la zona de drop
    dropZone.addEventListener('click', (e) => {
        if (e.target !== btnSelectFile) {
            fileInput.click();
        }
    });

    // Cambio en input file
    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            handleFile(e.target.files[0]);
        }
    });

    // Remover archivo
    btnRemoveFile.addEventListener('click', removerArchivo);
}

// ===== MANEJAR ARCHIVO =====
function handleFile(file) {
    // Validar tipo
    if (file.type !== 'application/pdf') {
        mostrarAlerta('Solo se permiten archivos PDF', 'danger');
        return;
    }

    // Validar tamaño
    if (file.size > MAX_FILE_SIZE) {
        mostrarAlerta('El archivo es demasiado grande. Máximo 10 MB', 'danger');
        return;
    }

    // Guardar archivo
    selectedFile = file;

    // Mostrar preview
    mostrarPreview(file);

    // Ocultar zona de drop
    dropZone.style.display = 'none';
    filePreview.style.display = 'block';
}

// ===== MOSTRAR PREVIEW =====
function mostrarPreview(file) {
    fileName.textContent = file.name;
    fileSize.textContent = formatearTamaño(file.size);
}

// ===== REMOVER ARCHIVO =====
function removerArchivo() {
    selectedFile = null;
    fileInput.value = '';
    dropZone.style.display = 'block';
    filePreview.style.display = 'none';
    fileName.textContent = '';
    fileSize.textContent = '';
}

// ===== FORMATEAR TAMAÑO =====
function formatearTamaño(bytes) {
    if (bytes === 0) return '0 Bytes';

    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));

    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
}

// ===== CONTADOR DE CARACTERES =====
function inicializarContadorCaracteres() {
    descripcionTextarea.addEventListener('input', () => {
        const length = descripcionTextarea.value.length;
        charCount.textContent = length;

        if (length > 450) {
            charCount.style.color = '#ef4444';
        } else if (length > 400) {
            charCount.style.color = '#f59e0b';
        } else {
            charCount.style.color = '#666';
        }
    });
}

// ===== INICIALIZAR FORMULARIO =====
function inicializarFormulario() {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        // Validar formulario
        if (!validarFormulario()) {
            return;
        }

        await subirRecurso();
    });
}

// ===== VALIDAR FORMULARIO =====
function validarFormulario() {
    let valido = true;

    // Validar título
    const titulo = document.getElementById('titulo');
    if (!titulo.value.trim()) {
        titulo.classList.add('is-invalid');
        valido = false;
    } else {
        titulo.classList.remove('is-invalid');
        titulo.classList.add('is-valid');
    }

    // Validar descripción
    const descripcion = document.getElementById('descripcion');
    if (!descripcion.value.trim()) {
        descripcion.classList.add('is-invalid');
        valido = false;
    } else {
        descripcion.classList.remove('is-invalid');
        descripcion.classList.add('is-valid');
    }

    // Validar materia
    const materia = document.getElementById('materiaId');
    if (!materia.value) {
        materia.classList.add('is-invalid');
        valido = false;
    } else {
        materia.classList.remove('is-invalid');
        materia.classList.add('is-valid');
    }

    // Validar archivo
    if (!selectedFile) {
        mostrarAlerta('Debes seleccionar un archivo PDF', 'danger');
        valido = false;
    }

    return valido;
}

// ===== SUBIR RECURSO =====
async function subirRecurso() {
    // Obtener usuario actual
    const usuario = typeof getCurrentUser === 'function' ? getCurrentUser() : null;

    if (!usuario || !usuario.id) {
        mostrarAlerta('Error: No se pudo obtener la información del usuario', 'danger');
        return;
    }

    // Obtener token
    const token = typeof getToken === 'function' ? getToken() : null;

    if (!token) {
        mostrarAlerta('Error: No estás autenticado', 'danger');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 2000);
        return;
    }

    // Preparar FormData
    const formData = new FormData();
    formData.append('titulo', document.getElementById('titulo').value.trim());
    formData.append('descripcion', document.getElementById('descripcion').value.trim());
    formData.append('materiaId', document.getElementById('materiaId').value);
    formData.append('usuarioId', usuario.id);
    formData.append('file', selectedFile);

    // Mostrar loading
    mostrarLoading(true);

    try {
        const response = await fetch(`${API_BASE_URL}/recursos/subir`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            },
            body: formData
        });

        if (!response.ok) {
            const error = await response.json().catch(() => ({}));
            throw new Error(error.mensaje || 'Error al subir el recurso');
        }

        const data = await response.json();
        console.log('✅ Recurso subido:', data);

        // Mostrar éxito
        mostrarAlerta('¡Recurso subido exitosamente!', 'success');

        // Resetear formulario después de 2 segundos
        setTimeout(() => {
            resetearFormulario();
            // Opcional: redirigir a recursos de esa materia
            // window.location.href = `recursos.html?materiaId=${materiaId}`;
        }, 2000);

    } catch (error) {
        console.error('❌ Error al subir recurso:', error);
        mostrarAlerta(error.message || 'Error al subir el recurso. Intenta de nuevo.', 'danger');
    } finally {
        mostrarLoading(false);
    }
}

// ===== MOSTRAR LOADING =====
function mostrarLoading(mostrar) {
    if (mostrar) {
        btnSubmit.disabled = true;
        btnText.style.display = 'none';
        btnSpinner.style.display = 'inline-block';
    } else {
        btnSubmit.disabled = false;
        btnText.style.display = 'flex';
        btnSpinner.style.display = 'none';
    }
}

// ===== MOSTRAR ALERTA =====
function mostrarAlerta(mensaje, tipo = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${tipo}`;
    alertDiv.innerHTML = `
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            ${tipo === 'success' ? '<polyline points="20 6 9 17 4 12"/>' :
        tipo === 'danger' ? '<circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/>' :
            '<circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/>'}
        </svg>
        <span>${mensaje}</span>
    `;

    alertContainer.innerHTML = '';
    alertContainer.appendChild(alertDiv);

    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);

    // Scroll al inicio
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ===== RESETEAR FORMULARIO =====
function resetearFormulario() {
    form.reset();
    removerArchivo();

    // Remover clases de validación
    document.querySelectorAll('.form-control').forEach(input => {
        input.classList.remove('is-valid', 'is-invalid');
    });

    // Resetear contador
    charCount.textContent = '0';
    charCount.style.color = '#666';
}