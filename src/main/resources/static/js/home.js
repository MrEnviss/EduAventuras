// ===== MAPEO DE IMÁGENES POR MATERIA =====
const imagenesMateria = {
    'Matemáticas y geometria': 'assets/images/subjects/card-matematicas.png',
    'Matemática': 'assets/images/subjects/card-matematicas.png',
    'Ciencias Naturales': 'assets/images/subjects/card-ciencias.png',
    'Ciencias': 'assets/images/subjects/card-ciencias.png',
    'Español': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Lenguaje': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Lengua y Literatura': 'assets/images/subjects/card-lenguajeliteratura.png',
    'Literatura': 'assets/images/subjects/card-lenguajeliteratura.png'
};

// Imagen por defecto si no hay coincidencia
const imagenDefault = 'assets/images/hero/landing-page.png';

// ===== MAPEO DE BADGES POR MATERIA =====
const badgesMateria = {
    'Matemáticas': 'badge-matematicas',
    'Matemática': 'badge-matematicas',
    'Ciencias Naturales': 'badge-ciencias',
    'Ciencias': 'badge-ciencias',
    'Español': 'badge-espanol',
    'Lenguaje': 'badge-espanol',
    'Lengua y Literatura': 'badge-espanol',
    'Literatura': 'badge-espanol'
};

// Badge por defecto
const badgeDefault = 'badge-default';

// ===== FUNCIÓN PRINCIPAL: CARGAR MATERIAS =====
async function cargarMaterias() {
    const loadingElement = document.getElementById('loadingMaterias');
    const errorElement = document.getElementById('errorMaterias');
    const emptyElement = document.getElementById('emptyMaterias');
    const gridElement = document.getElementById('materiasGrid');

    try {
        // Mostrar loading
        loadingElement.classList.remove('d-none');
        errorElement.classList.add('d-none');
        emptyElement.classList.add('d-none');
        gridElement.innerHTML = '';

        console.log('Cargando materias desde:', `${API_BASE_URL}/materias`);

        // Llamada al backend
        const response = await fetch(`${API_BASE_URL}/materias`);

        if (!response.ok) {
            throw new Error(`Error HTTP: ${response.status}`);
        }

        const materias = await response.json();
        console.log('Materias recibidas:', materias);

        // Ocultar loading
        loadingElement.classList.add('d-none');

        // Verificar si hay materias
        if (!materias || materias.length === 0) {
            emptyElement.classList.remove('d-none');
            return;
        }

        // Renderizar materias
        renderizarMaterias(materias);

    } catch (error) {
        console.error('Error al cargar materias:', error);
        loadingElement.classList.add('d-none');
        errorElement.classList.remove('d-none');
    }
}

// ===== FUNCIÓN: RENDERIZAR MATERIAS =====
function renderizarMaterias(materias) {
    const gridElement = document.getElementById('materiasGrid');
    gridElement.innerHTML = '';

    materias.forEach(materia => {
        const card = crearCardMateria(materia);
        gridElement.appendChild(card);
    });
}

// ===== FUNCIÓN: CREAR CARD DE MATERIA =====
function crearCardMateria(materia) {
    const col = document.createElement('div');
    col.className = 'col-lg-4 col-md-6';

    // Obtener imagen según el nombre de la materia
    const imagen = imagenesMateria[materia.nombre] || imagenDefault;

    // Obtener clase de badge según el nombre
    const badgeClass = badgesMateria[materia.nombre] || badgeDefault;

    // Obtener cantidad de recursos (con fallback a 0)
    const cantidadRecursos = materia.cantidadRecursos || materia.recursos?.length || 0;

    col.innerHTML = `
        <a href="materias.html?id=${materia.id}" class="subject-card">
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

    return col;
}

// ===== SMOOTH SCROLL PARA ENLACES DEL NAVBAR =====
function inicializarSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const targetId = this.getAttribute('href');

            if (targetId === '#') return;

            const target = document.querySelector(targetId);
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });
}

// ===== CERRAR NAVBAR EN MÓVIL AL HACER CLICK =====
function inicializarNavbarMobile() {
    const navLinks = document.querySelectorAll('.nav-link');
    const navbarCollapse = document.querySelector('.navbar-collapse');

    navLinks.forEach(link => {
        link.addEventListener('click', () => {
            if (navbarCollapse.classList.contains('show')) {
                navbarCollapse.classList.remove('show');
            }
        });
    });
}

// ===== DESTACAR LINK ACTIVO EN NAVBAR AL HACER SCROLL =====
function inicializarScrollSpy() {
    const sections = document.querySelectorAll('section[id]');
    const navLinks = document.querySelectorAll('.nav-link');

    window.addEventListener('scroll', () => {
        let current = '';

        sections.forEach(section => {
            const sectionTop = section.offsetTop;
            const sectionHeight = section.clientHeight;

            if (window.pageYOffset >= sectionTop - 200) {
                current = section.getAttribute('id');
            }
        });

        navLinks.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href') === `#${current}`) {
                link.classList.add('active');
            }
        });
    });
}

// ===== VERIFICAR SI HAY USUARIO LOGUEADO =====
function verificarSesion() {
    const token = localStorage.getItem('token');
    const usuario = localStorage.getItem('usuario');

    if (token && usuario) {
        console.log('Usuario logueado:', JSON.parse(usuario));

    }
}

// ===== INICIALIZACIÓN AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', () => {
    console.log('Página cargada - Inicializando EduAventuras');

    // Actualizar navbar según autenticación
    if (typeof actualizarNavbar === 'function') {
        actualizarNavbar();
    }

    // Cargar materias del backend
    cargarMaterias();

    // Inicializar funcionalidades
    inicializarSmoothScroll();
    inicializarNavbarMobile();
    inicializarScrollSpy();
    verificarSesion();

    console.log('Inicialización completada');
});

// ===== EXPONER FUNCIÓN PARA REINTENTAR (botón de error) =====
window.cargarMaterias = cargarMaterias;
document.addEventListener('DOMContentLoaded', () => {
    const btnUserDropdown = document.getElementById('btnUserDropdown');
    const userDropdownMenu = document.getElementById('userDropdownMenu');

    // Mostrar/ocultar el dropdown de usuario
    btnUserDropdown.addEventListener('click', (e) => {
        e.preventDefault();
        // Alternar visibilidad del menú
        const isVisible = userDropdownMenu.style.display === 'block';
        userDropdownMenu.style.display = isVisible ? 'none' : 'block';
    });

    // Cerrar el menú si se hace clic fuera del mismo
    document.addEventListener('click', (e) => {
        if (!btnUserDropdown.contains(e.target) && !userDropdownMenu.contains(e.target)) {
            userDropdownMenu.style.display = 'none';
        }
    });
});