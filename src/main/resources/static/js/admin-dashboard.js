// ===== ADMIN DASHBOARD - EDUAVENTURAS =====

const API_URL = 'http://localhost:8080/api';

let usuariosChart = null;
let recursosChart = null;

// ===== CARGAR ESTADÍSTICAS DESDE EL ENDPOINT CORRECTO =====
async function cargarEstadisticas() {
    try {
        // Debug: Verificar usuario y token
        const usuario = window.getCurrentUser();
        const token = window.getToken();
        console.log('🔍 Usuario actual:', usuario);
        console.log('🔑 Token presente:', !!token);
        console.log('👤 Rol del usuario:', usuario?.rol);

        // Usar el endpoint de estadísticas del admin
        const estadisticasRes = await window.fetchAutenticado(`${API_URL}/admin/dashboard/estadisticas`);

        console.log('📡 Response status:', estadisticasRes.status);

        // Verificar si la respuesta es válida
        if (!estadisticasRes.ok) {
            throw new Error(`HTTP ${estadisticasRes.status}: ${estadisticasRes.statusText}`);
        }

        const stats = await estadisticasRes.json();
        console.log('✅ Estadísticas recibidas:', stats);

        // 🔴 CRÍTICO: Extraer datos de la estructura correcta
        const totalUsuarios = stats.usuarios?.totalUsuarios || stats.totalUsuarios || 0;
        const totalMaterias = stats.contenido?.totalMaterias || stats.totalMaterias || 0;
        const totalRecursos = stats.contenido?.totalRecursos || stats.totalRecursos || 0;
        const totalDescargas = stats.contenido?.totalDescargas || stats.totalDescargas || 0;

        console.log('📊 Valores extraídos:', {
            totalUsuarios,
            totalMaterias,
            totalRecursos,
            totalDescargas
        });

        // Actualizar cards con los datos del backend
        console.log('📊 Actualizando valores en el DOM...');

        const totalUsuariosEl = document.getElementById('totalUsuarios');
        const totalMateriasEl = document.getElementById('totalMaterias');
        const totalRecursosEl = document.getElementById('totalRecursos');
        const totalDescargasEl = document.getElementById('totalDescargas');

        if (totalUsuariosEl) {
            totalUsuariosEl.textContent = totalUsuarios;
            console.log('✅ totalUsuarios actualizado a:', totalUsuarios);
        }

        if (totalMateriasEl) {
            totalMateriasEl.textContent = totalMaterias;
            console.log('✅ totalMaterias actualizado a:', totalMaterias);
        }

        if (totalRecursosEl) {
            totalRecursosEl.textContent = totalRecursos;
            console.log('✅ totalRecursos actualizado a:', totalRecursos);
        }

        if (totalDescargasEl) {
            totalDescargasEl.textContent = totalDescargas.toLocaleString();
            console.log('✅ totalDescargas actualizado a:', totalDescargas);
        }

        // Generar gráficos con los datos recibidos
        // Construir usuariosPorRol desde stats.usuarios
        if (stats.usuarios) {
            const usuariosPorRol = {
                ESTUDIANTE: stats.usuarios.totalEstudiantes || 0,
                DOCENTE: stats.usuarios.totalDocentes || 0,
                ADMIN: stats.usuarios.totalAdmins || 0
            };
            console.log('📊 Generando gráfico de usuarios con:', usuariosPorRol);
            generarGraficoUsuarios(usuariosPorRol);
        }

        // Construir recursosPorMateria desde stats.recursosPopulares
        if (stats.recursosPopulares && stats.recursosPopulares.length > 0) {
            const recursosPorMateria = {};
            stats.recursosPopulares.forEach(recurso => {
                const materiaNombre = recurso.materiaNombre || 'Sin materia';
                recursosPorMateria[materiaNombre] = (recursosPorMateria[materiaNombre] || 0) + 1;
            });
            console.log('📊 Generando gráfico de recursos con:', recursosPorMateria);
            generarGraficoRecursos(recursosPorMateria);
        }

        // Cargar actividad reciente desde el resumen
        await cargarActividadReciente();

        // Mostrar contenido
        document.getElementById('loadingDashboard').style.display = 'none';
        document.getElementById('dashboardContent').style.display = 'block';

    } catch (error) {
        console.error('❌ Error al cargar estadísticas:', error);

        // Si el endpoint de estadísticas falla, intentar con endpoints individuales
        try {
            console.log('⚠️ Intentando método alternativo...');
            await cargarEstadisticasAlternativas();
        } catch (altError) {
            console.error('❌ Error en método alternativo:', altError);
            mostrarError();
        }
    }
}

// ===== MÉTODO ALTERNATIVO SI EL ENDPOINT DE ESTADÍSTICAS NO EXISTE =====
async function cargarEstadisticasAlternativas() {
    console.log('Intentando cargar estadísticas con endpoints individuales...');

    // Cargar usuarios
    const usuariosRes = await window.fetchAutenticado(`${API_URL}/usuarios`);
    const usuarios = await usuariosRes.json();

    // Cargar materias
    const materiasRes = await fetch(`${API_URL}/materias`);
    const materias = await materiasRes.json();

    // Cargar recursos
    const recursosRes = await fetch(`${API_URL}/recursos`);
    const recursos = await recursosRes.json();

    // Calcular estadísticas manualmente
    const totalDescargas = recursos.reduce((sum, r) => sum + (r.cantidadDescargas || 0), 0);

    console.log('📊 Estadísticas calculadas:', {
        usuarios: usuarios.length,
        materias: materias.length,
        recursos: recursos.length,
        descargas: totalDescargas
    });

    // Actualizar cards
    document.getElementById('totalUsuarios').textContent = usuarios.length;
    document.getElementById('totalMaterias').textContent = materias.length;
    document.getElementById('totalRecursos').textContent = recursos.length;
    document.getElementById('totalDescargas').textContent = totalDescargas.toLocaleString();

    // Procesar datos para gráficos
    const usuariosPorRol = usuarios.reduce((acc, user) => {
        acc[user.rol] = (acc[user.rol] || 0) + 1;
        return acc;
    }, {});

    const recursosPorMateria = recursos.reduce((acc, rec) => {
        const materia = materias.find(m => m.id === rec.materiaId);
        const nombreMateria = materia ? materia.nombre : 'Sin materia';
        acc[nombreMateria] = (acc[nombreMateria] || 0) + 1;
        return acc;
    }, {});

    // Generar gráficos
    generarGraficoUsuarios(usuariosPorRol);
    generarGraficoRecursos(recursosPorMateria);

    // Generar actividad reciente
    generarActividadRecienteManual(recursos);

    // Mostrar contenido
    document.getElementById('loadingDashboard').style.display = 'none';
    document.getElementById('dashboardContent').style.display = 'block';
}

// ===== CARGAR ACTIVIDAD RECIENTE DESDE RESUMEN =====
async function cargarActividadReciente() {
    try {
        const resumenRes = await window.fetchAutenticado(`${API_URL}/admin/dashboard/resumen`);
        const resumen = await resumenRes.json();

        const activityList = document.getElementById('activityList');

        if (resumen.recursosRecientes && resumen.recursosRecientes.length > 0) {
            activityList.innerHTML = resumen.recursosRecientes.map(recurso => {
                const fecha = new Date(recurso.fechaSubida);
                const tiempoRelativo = obtenerTiempoRelativo(fecha);

                return `
                    <div class="activity-item">
                        <div class="activity-icon">📄</div>
                        <div class="activity-content">
                            <div class="activity-title">
                                Nuevo recurso: ${recurso.titulo}
                            </div>
                            <div class="activity-time">
                                Subido por ${recurso.subidoPorNombre} • ${tiempoRelativo}
                            </div>
                        </div>
                    </div>
                `;
            }).join('');
        } else {
            activityList.innerHTML = `
                <div class="text-center py-4 text-muted">
                    <p>No hay actividad reciente</p>
                </div>
            `;
        }
    } catch (error) {
        console.error('Error al cargar actividad reciente:', error);
        document.getElementById('activityList').innerHTML = `
            <div class="text-center py-4 text-muted">
                <p>No se pudo cargar la actividad reciente</p>
            </div>
        `;
    }
}

// ===== GENERAR ACTIVIDAD RECIENTE MANUAL =====
function generarActividadRecienteManual(recursos) {
    const activityList = document.getElementById('activityList');

    // Ordenar recursos por fecha (más recientes primero)
    const recursosRecientes = recursos
        .sort((a, b) => new Date(b.fechaSubida) - new Date(a.fechaSubida))
        .slice(0, 10);

    if (recursosRecientes.length === 0) {
        activityList.innerHTML = `
            <div class="text-center py-4 text-muted">
                <p>No hay actividad reciente</p>
            </div>
        `;
        return;
    }

    activityList.innerHTML = recursosRecientes.map(recurso => {
        const fecha = new Date(recurso.fechaSubida);
        const tiempoRelativo = obtenerTiempoRelativo(fecha);

        return `
            <div class="activity-item">
                <div class="activity-icon">📄</div>
                <div class="activity-content">
                    <div class="activity-title">
                        Nuevo recurso: ${recurso.titulo}
                    </div>
                    <div class="activity-time">
                        Subido por ${recurso.subidoPorNombre} • ${tiempoRelativo}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

// ===== MOSTRAR ERROR =====
function mostrarError() {
    document.getElementById('loadingDashboard').innerHTML = `
        <div class="alert alert-danger" style="max-width: 600px; margin: 0 auto;">
            <h4>⚠️ Error al cargar estadísticas</h4>
            <p>No se pudieron cargar los datos del dashboard. Verifica que:</p>
            <ul class="text-start">
                <li>El servidor esté funcionando en <code>http://localhost:8080</code></li>
                <li>Tengas permisos de administrador</li>
                <li>El endpoint <code>/api/admin/dashboard/estadisticas</code> esté disponible</li>
            </ul>
            <button class="btn btn-outline-danger mt-3" onclick="location.reload()">
                🔄 Reintentar
            </button>
        </div>
    `;
}

// ===== GRÁFICO DE USUARIOS POR ROL =====
function generarGraficoUsuarios(usuariosPorRol) {
    const ctx = document.getElementById('usuariosChart');

    if (usuariosChart) {
        usuariosChart.destroy();
    }

    // Mapear nombres de roles
    const nombresRoles = {
        'ESTUDIANTE': 'Estudiantes',
        'DOCENTE': 'Docentes',
        'ADMIN': 'Administradores'
    };

    const labels = Object.keys(usuariosPorRol).map(rol => nombresRoles[rol] || rol);
    const data = Object.values(usuariosPorRol);

    usuariosChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: [
                    'rgba(45, 212, 191, 0.8)',
                    'rgba(13, 89, 87, 0.8)',
                    'rgba(244, 196, 48, 0.8)'
                ],
                borderColor: [
                    'rgba(45, 212, 191, 1)',
                    'rgba(13, 89, 87, 1)',
                    'rgba(244, 196, 48, 1)'
                ],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: {
                        padding: 15,
                        font: {
                            size: 13,
                            family: 'Poppins'
                        }
                    }
                }
            }
        }
    });
}

// ===== GRÁFICO DE RECURSOS POR MATERIA =====
function generarGraficoRecursos(recursosPorMateria) {
    const ctx = document.getElementById('recursosChart');

    if (recursosChart) {
        recursosChart.destroy();
    }

    // Convertir objeto a array y ordenar
    const materiasArray = Object.entries(recursosPorMateria)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 5); // Top 5

    const labels = materiasArray.map(([nombre]) => nombre);
    const data = materiasArray.map(([, count]) => count);

    recursosChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Recursos',
                data: data,
                backgroundColor: 'rgba(45, 212, 191, 0.6)',
                borderColor: 'rgba(45, 212, 191, 1)',
                borderWidth: 2,
                borderRadius: 8
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1,
                        font: {
                            family: 'Poppins'
                        }
                    }
                },
                x: {
                    ticks: {
                        font: {
                            family: 'Poppins'
                        }
                    }
                }
            }
        }
    });
}

// ===== OBTENER TIEMPO RELATIVO =====
function obtenerTiempoRelativo(fecha) {
    const ahora = new Date();
    const diferencia = ahora - fecha;

    const minutos = Math.floor(diferencia / 60000);
    const horas = Math.floor(diferencia / 3600000);
    const dias = Math.floor(diferencia / 86400000);

    if (minutos < 1) return 'Hace un momento';
    if (minutos < 60) return `Hace ${minutos} minuto${minutos > 1 ? 's' : ''}`;
    if (horas < 24) return `Hace ${horas} hora${horas > 1 ? 's' : ''}`;
    if (dias < 30) return `Hace ${dias} día${dias > 1 ? 's' : ''}`;

    return fecha.toLocaleDateString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// ===== ACTUALIZAR NOMBRE DEL ADMIN =====
function actualizarNombreAdmin() {
    const usuario = window.getCurrentUser();
    if (usuario) {
        document.getElementById('adminName').textContent = usuario.nombre;
        const nombreCompleto = `${usuario.nombre} ${usuario.apellido || ''}`.trim();
        document.getElementById('navbarUserName').textContent = `👤 ${nombreCompleto}`;
    }
}

// ===== INICIALIZAR DASHBOARD =====
async function inicializarDashboard() {
    // Proteger página (solo ADMIN)
    if (!window.protegerPaginaPorRol(['ADMIN'])) {
        return;
    }

    // Actualizar navbar
    window.actualizarNavbar();

    // Actualizar nombre
    actualizarNombreAdmin();

    // Cargar estadísticas
    await cargarEstadisticas();
}

// Ejecutar al cargar
document.addEventListener('DOMContentLoaded', inicializarDashboard);

console.log('✅ Admin Dashboard JS cargado');