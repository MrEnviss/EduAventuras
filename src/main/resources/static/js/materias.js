

// ===== VARIABLES GLOBALES =====
let materiasOriginales = [];
let materiasFiltradas = [];

// ===== MAPEO DE IMÁGENES POR MATERIA =====
const imagenesMateria = {
    'Matemáticas y geometria': 'assets/images/subjects/card-matematicas.png',
    'Matemática': 'assets/images/subjects/card-matematicas.png',
    'Ciencias Naturales': 'assets/images/subjects/card-ciencias.png',
    'Ciencias': 'assets/images/subjects/card-ciencias.png',
    'Español': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Lenguaje': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Lenguaje y Literatura': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Literatura': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Ingles': 'assets/images/subjects/card-ingles.png',
    'Quimica': 'assets/images/subjects/card-quimica.png',

};

const imagenDefault = 'assets/images/hero/landing-page.png';

// ===== MAPEO DE BADGES =====
const badgesMateria = {
    'Matemáticas y geometria': 'badge-matematicas',
    'Matemática': 'badge-matematicas',
    'Ciencias Naturales': 'badge-ciencias',
    'Ciencias y Quimica': 'badge-ciencias',
    'Español': 'badge-espanol',
    'Lenguaje y Literatura': 'badge-espanol',
    'Lengua y Literatura': 'badge-espanol',
    'Literatura': 'badge-espanol'
};

const badgeDefault = 'badge-default';

// ===== ELEMENTOS DEL DOM =====
const loadingElement = document.getElementById('loadingMaterias');
const errorElement = document.getElementById('errorMaterias');
const emptyElement = document.getElementById('emptyMaterias');
const noResultsElement = document.getElementById('noResultsMaterias');
const gridElement = document.getElementById('materiasGrid');
const searchInput = document.getElementById('searchInput');
const resultadosContador = document.getElementById('resultadosContador');

// ===== FUNCIÓN PRINCIPAL: CARGAR MATERIAS =====
async function cargarMaterias() {
    try {
        console.log('📚 Cargando materias...');

        // Mostrar loading
        mostrarEstado('loading');

        // Llamada al backend
        const response = await fetch(`${API_BASE_URL}/materias`);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const materias = await response.json();
        console.log('✅ Materias recibidas:', materias.length);

        // Guardar materias originales
        materiasOriginales = materias;
        materiasFiltradas = materias;

        // Verificar si hay materias
        if (!materias || materias.length === 0) {
            mostrarEstado('empty');
            return;
        }

        // Renderizar materias
        renderizarMaterias(materias);
        mostrarEstado('success');
        actualizarContador(materias.length, materias.length);

    } catch (error) {
        console.error('❌ Error al cargar materias:', error);
        mostrarEstado('error');
    }
}

// ===== FUNCIÓN: RENDERIZAR MATERIAS =====
function renderizarMaterias(materias) {
    gridElement.innerHTML = '';

    materias.forEach(materia => {
        const col = document.createElement('div');
        col.className = 'col-lg-4 col-md-6';

        // Obtener imagen y badge
        const imagen = imagenesMateria[materia.nombre] || imagenDefault;
        const badgeClass = badgesMateria[materia.nombre] || badgeDefault;
        const cantidadRecursos = materia.cantidadRecursos || materia.recursos?.length || 0;

        col.innerHTML = `
            <a href="recursos.html?materiaId=${materia.id}" class="subject-card">
                <img src="${imagen}" 
                     alt="${materia.nombre}" 
                     class="subject-card-image"
                     onerror="this.src='${imagenDefault}'">
                <div class="subject-card-body">
                    <h4>${materia.nombre}</h4>
                    <p>${materia.descripcion || 'Explora los recursos disponibles'}</p>
                    <span class="subject-badge ${badgeClass}">
                        ${cantidadRecursos} Recurso${cantidadRecursos !== 1 ? 's' : ''}
                    </span>
                </div>
            </a>
        `;

        gridElement.appendChild(col);
    });
}

// ===== FUNCIÓN: BUSCAR MATERIAS =====
function buscarMaterias(termino) {
    termino = termino.toLowerCase().trim();

    if (!termino) {
        // Si el término está vacío, mostrar todas
        materiasFiltradas = materiasOriginales;
    } else {
        // Filtrar materias que coincidan con el término
        materiasFiltradas = materiasOriginales.filter(materia => {
            return materia.nombre.toLowerCase().includes(termino) ||
                (materia.descripcion && materia.descripcion.toLowerCase().includes(termino));
        });
    }

    // Actualizar vista
    if (materiasFiltradas.length === 0) {
        mostrarEstado('noResults');
    } else {
        renderizarMaterias(materiasFiltradas);
        mostrarEstado('success');
    }

    actualizarContador(materiasFiltradas.length, materiasOriginales.length);
}

// ===== FUNCIÓN: ACTUALIZAR CONTADOR =====
function actualizarContador(filtradas, totales) {
    if (filtradas === totales) {
        resultadosContador.textContent = `Mostrando ${totales} materia${totales !== 1 ? 's' : ''}`;
    } else {
        resultadosContador.textContent = `Mostrando ${filtradas} de ${totales} materias`;
    }
}

// ===== FUNCIÓN: MOSTRAR ESTADO =====
function mostrarEstado(estado) {
    // Ocultar todos los estados
    loadingElement.classList.add('d-none');
    errorElement.classList.add('d-none');
    emptyElement.classList.add('d-none');
    noResultsElement.classList.add('d-none');
    gridElement.classList.add('d-none');

    // Mostrar el estado correspondiente
    switch (estado) {
        case 'loading':
            loadingElement.classList.remove('d-none');
            break;
        case 'error':
            errorElement.classList.remove('d-none');
            break;
        case 'empty':
            emptyElement.classList.remove('d-none');
            break;
        case 'noResults':
            noResultsElement.classList.remove('d-none');
            break;
        case 'success':
            gridElement.classList.remove('d-none');
            break;
    }
}

// ===== FUNCIÓN: INICIALIZAR BÚSQUEDA =====
function inicializarBusqueda() {
    // Búsqueda en tiempo real con debounce
    let timeoutId;

    searchInput.addEventListener('input', (e) => {
        clearTimeout(timeoutId);

        timeoutId = setTimeout(() => {
            buscarMaterias(e.target.value);
        }, 300); // Esperar 300ms después de que el usuario deje de escribir
    });
}

// ===== INICIALIZACIÓN AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('📄 Página de materias cargada');

    // Actualizar navbar según autenticación
    if (typeof actualizarNavbar === 'function') {
        actualizarNavbar();
    }

    // Cargar materias
    cargarMaterias();

    // Inicializar búsqueda
    inicializarBusqueda();

    console.log('✅ Inicialización completada');
});

// ===== EXPONER FUNCIÓN PARA REINTENTAR =====
window.cargarMaterias = cargarMaterias;