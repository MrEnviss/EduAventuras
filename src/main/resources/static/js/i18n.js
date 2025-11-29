// ===== I18N.JS - SISTEMA DE INTERNACIONALIZACIÓN =====

const API_URL = 'http://localhost:8080/api';

class I18n {
    constructor() {
        this.currentLang = this.obtenerIdiomaGuardado();
        this.mensajes = {};
        this.idiomasDisponibles = {
            'es': { nombre: 'Español', bandera: '🇪🇸' },
            'en': { nombre: 'English', bandera: '🇺🇸' },
            'fr': { nombre: 'Français', bandera: '🇫🇷' }
        };
    }

    /**
     * Obtener idioma guardado en localStorage o detectar del navegador
     */
    obtenerIdiomaGuardado() {
        const guardado = localStorage.getItem('idioma-eduaventuras');
        if (guardado && ['es', 'en', 'fr'].includes(guardado)) {
            return guardado;
        }

        // Detectar idioma del navegador
        const navegador = navigator.language.toLowerCase();
        if (navegador.startsWith('es')) return 'es';
        if (navegador.startsWith('en')) return 'en';
        if (navegador.startsWith('fr')) return 'fr';

        return 'es'; // Por defecto español
    }

    /**
     * Cargar mensajes desde el backend
     */
    async cargarMensajes(idioma = this.currentLang) {
        try {
            const response = await fetch(`${API_URL}/idioma/mensajes?lang=${idioma}`);

            if (!response.ok) {
                throw new Error('Error al cargar mensajes');
            }

            this.mensajes = await response.json();
            this.currentLang = idioma;

            console.log(`✅ Mensajes cargados para idioma: ${idioma}`);
            return this.mensajes;

        } catch (error) {
            console.error('❌ Error al cargar mensajes:', error);
            return this.mensajesPorDefecto();
        }
    }

    /**
     * Mensajes por defecto si falla la carga
     */
    mensajesPorDefecto() {
        return {
            'app.nombre': 'EduAventuras',
            'app.bienvenida': 'Bienvenido',
            'error.general': 'Ha ocurrido un error'
        };
    }

    /**
     * Obtener un mensaje traducido por su clave
     */
    t(clave, parametros = {}) {
        let mensaje = this.mensajes[clave] || clave;

        // Reemplazar parámetros {nombre}, {valor}, etc.
        Object.keys(parametros).forEach(key => {
            mensaje = mensaje.replace(`{${key}}`, parametros[key]);
        });

        return mensaje;
    }

    /**
     * Cambiar idioma
     */
    async cambiarIdioma(idioma) {
        if (!['es', 'en', 'fr'].includes(idioma)) {
            console.error('Idioma no soportado:', idioma);
            return;
        }

        await this.cargarMensajes(idioma);
        localStorage.setItem('idioma-eduaventuras', idioma);

        // Aplicar traducciones a la página actual
        this.aplicarTraducciones();

        // Disparar evento personalizado
        window.dispatchEvent(new CustomEvent('idiomaChanged', {
            detail: { idioma }
        }));

        console.log(`✅ Idioma cambiado a: ${idioma}`);
    }

    /**
     * Aplicar traducciones a elementos con data-i18n
     */
    aplicarTraducciones() {
        // Traducir textos
        document.querySelectorAll('[data-i18n]').forEach(elemento => {
            const clave = elemento.getAttribute('data-i18n');
            elemento.textContent = this.t(clave);
        });

        // Traducir placeholders
        document.querySelectorAll('[data-i18n-placeholder]').forEach(elemento => {
            const clave = elemento.getAttribute('data-i18n-placeholder');
            elemento.placeholder = this.t(clave);
        });

        // Traducir títulos (tooltips)
        document.querySelectorAll('[data-i18n-title]').forEach(elemento => {
            const clave = elemento.getAttribute('data-i18n-title');
            elemento.title = this.t(clave);
        });

        // Traducir valores de botones
        document.querySelectorAll('[data-i18n-value]').forEach(elemento => {
            const clave = elemento.getAttribute('data-i18n-value');
            elemento.value = this.t(clave);
        });
    }

    /**
     * Obtener idiomas disponibles
     */
    async obtenerIdiomasDisponibles() {
        try {
            const response = await fetch(`${API_URL}/idioma/idiomas-disponibles`);

            if (response.ok) {
                return await response.json();
            }

            return this.idiomasDisponibles;
        } catch (error) {
            console.error('Error al obtener idiomas:', error);
            return this.idiomasDisponibles;
        }
    }

    /**
     * Crear selector de idioma
     */
    crearSelectorIdioma(contenedorId = 'selector-idioma') {
        const contenedor = document.getElementById(contenedorId);
        if (!contenedor) return;

        const html = `
            <div class="language-selector">
                <button class="language-btn" id="language-dropdown-btn">
                    <span class="flag">${this.idiomasDisponibles[this.currentLang].bandera}</span>
                    <span class="lang-name">${this.idiomasDisponibles[this.currentLang].nombre}</span>
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="currentColor">
                        <path d="M2 4l4 4 4-4H2z"/>
                    </svg>
                </button>
                <div class="language-dropdown" id="language-dropdown">
                    ${Object.entries(this.idiomasDisponibles).map(([codigo, info]) => `
                        <button class="language-option ${codigo === this.currentLang ? 'active' : ''}" 
                                data-lang="${codigo}">
                            <span class="flag">${info.bandera}</span>
                            <span>${info.nombre}</span>
                        </button>
                    `).join('')}
                </div>
            </div>
        `;

        contenedor.innerHTML = html;
        this.configurarEventosSelector();
    }

    /**
     * Configurar eventos del selector
     */
    configurarEventosSelector() {
        const btn = document.getElementById('language-dropdown-btn');
        const dropdown = document.getElementById('language-dropdown');

        if (!btn || !dropdown) return;

        // Toggle dropdown
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            dropdown.classList.toggle('show');
        });

        // Cerrar al hacer click fuera
        document.addEventListener('click', () => {
            dropdown.classList.remove('show');
        });

        // Cambiar idioma
        document.querySelectorAll('.language-option').forEach(option => {
            option.addEventListener('click', async (e) => {
                e.stopPropagation();
                const idioma = option.getAttribute('data-lang');
                await this.cambiarIdioma(idioma);

                // Actualizar selector
                this.crearSelectorIdioma();
            });
        });
    }

    /**
     * Obtener idioma actual
     */
    getIdioma() {
        return this.currentLang;
    }
}

// ===== INSTANCIA GLOBAL =====
const i18n = new I18n();

// ===== INICIALIZAR AL CARGAR LA PÁGINA =====
document.addEventListener('DOMContentLoaded', async () => {
    console.log('🌍 Inicializando i18n...');

    await i18n.cargarMensajes();
    i18n.aplicarTraducciones();

    console.log('✅ i18n inicializado');
});

// ===== EXPORTAR PARA USO GLOBAL =====
window.i18n = i18n;

console.log('✅ Módulo i18n.js cargado');