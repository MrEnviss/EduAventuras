// ===== SISTEMA DE INTERNACIONALIZACIÓN - EDUAVENTURAS =====

// Idioma por defecto
const DEFAULT_LANGUAGE = 'es';

// Traducciones
const translations = {
    es: {
        // Navbar
        'inicio': 'Inicio',
        'materias': 'Materias',
        'como funciona': 'Cómo Funciona',
        'contacto': 'Contacto',
        'login': 'Iniciar Sesión',
        'register': 'Registrarse',
        'perfil': 'Mi Perfil',
        'dashboard': 'Dashboard',
        'subir': 'Subir Recurso',
        'logout': 'Cerrar Sesión',

        // Navbar con prefijo nav.
        'nav.iniciarSesion': 'Iniciar Sesión',
        'nav.registrarse': 'Registrarse',

        // Navbar con prefijo navbar.
        'navbar.iniciarSesion': 'Iniciar Sesión',
        'navbar.registrarse': 'Registrarse',

        // Dropdown
        'dropdown.perfil': 'Mi Perfil',
        'dropdown.dashboard': 'Dashboard',
        'dropdown.subir': 'Subir Recurso',
        'dropdown.salir': 'Cerrar Sesión',

        // Home - Hero
        'home.hero.title': 'Educación',
        'home.hero.title-highlight': 'Gratuita',
        'home.hero.title-end': 'para Todos',
        'home.hero.subtitle': 'Accede a recursos educativos de calidad sin costo alguno. Aprende matemáticas, ciencias, español y mucho más. Tu aventura educativa comienza aquí.',
        'home.hero.cta': 'Explorar Materias',

        // Home - Features
        'home.features.title': '¿Por Qué EduAventuras?',
        'home.features.free.title': '100% Gratuito',
        'home.features.free.desc': 'Todos nuestros recursos son completamente gratuitos. Educación accesible para todos sin barreras económicas.',
        'home.features.subjects.title': 'Múltiples Materias',
        'home.features.subjects.desc': 'Encuentra recursos de matemáticas, ciencias, español, historia y más. Todo en un solo lugar.',
        'home.features.access.title': 'Acceso Universal',
        'home.features.access.desc': 'Aprende desde cualquier lugar, en cualquier momento. Solo necesitas conexión a internet.',

        // Home - Materias Section
        'home.subjects.title': 'Explora Nuestras Materias',
        'home.subjects.loading': 'Cargando materias disponibles...',
        'home.subjects.error.title': 'Error al cargar materias',
        'home.subjects.error.desc': 'No se pudieron cargar las materias. Por favor, verifica que el servidor esté corriendo en',
        'home.subjects.error.retry': 'Reintentar',
        'home.subjects.empty.title': 'No hay materias disponibles',
        'home.subjects.empty.desc': 'Aún no se han agregado materias al sistema.',

        // Home - Cómo Funciona
        'home.how.title': '¿Cómo Funciona?',
        'home.how.step1.title': 'Regístrate Gratis',
        'home.how.step1.desc': 'Crea tu cuenta en segundos. Solo necesitas un correo electrónico.',
        'home.how.step2.title': 'Explora Materias',
        'home.how.step2.desc': 'Navega por nuestras materias y encuentra los recursos que necesitas.',
        'home.how.step3.title': 'Descarga y Aprende',
        'home.how.step3.desc': 'Descarga los recursos en PDF y comienza tu aventura educativa.',

        // Home - CTA
        'home.cta.title': '¡Comienza Tu Aventura Educativa Hoy!',
        'home.cta.subtitle': 'Únete a miles de estudiantes que ya están aprendiendo con EduAventuras',
        'home.cta.button': 'Crear Cuenta Gratis',

        // Materias Page
        'materias.title': 'Explora Nuestras Materias',
        'materias.subtitle': 'Encuentra recursos educativos de calidad en todas las áreas',
        'materias.search': 'Buscar materias (ej: Matemáticas, Ciencias...)',
        'materias.loading': 'Cargando materias disponibles...',
        'materias.showing': 'Mostrando',
        'materias.of': 'de',
        'materias.available': 'materias disponibles',
        'materias.resources': 'recursos',
        'materias.view-more': 'Ver más',
        'materias.no-results': 'No se encontraron resultados',
        'materias.try-again': 'Intenta con otros términos de búsqueda',

        // Login
        'login.title': 'Iniciar Sesión',
        'login.subtitle': 'Accede a tu cuenta de EduAventuras',
        'login.email': 'Correo Electrónico',
        'login.password': 'Contraseña',
        'login.remember': 'Recordarme',
        'login.forgot': '¿Olvidaste tu contraseña?',
        'login.button': 'Iniciar Sesión',
        'login.no-account': '¿No tienes cuenta?',
        'login.register': 'Regístrate aquí',
        'login.success': 'Inicio de sesión exitoso! Redirigiendo...',
        'login.loading': 'Iniciando sesión...',
        'usuario.login.exito':'Sesión Cerrada',

        // Registro
        'register.title': 'Crear Cuenta',
        'register.subtitle': 'Únete a EduAventuras gratis',
        'register.name': 'Nombre',
        'register.lastname': 'Apellido',
        'register.email': 'Correo Electrónico',
        'register.password': 'Contraseña',
        'register.confirm': 'Confirmar Contraseña',
        'register.role': 'Soy un(a)',
        'register.role.student': 'Estudiante',
        'register.role.teacher': 'Docente',
        'register.button': 'Registrarse',
        'register.have-account': '¿Ya tienes cuenta?',
        'register.login': 'Inicia sesión aquí',

        // Footer
        'footer.about': 'Plataforma educativa gratuita dedicada a brindar recursos de calidad y libre acceso.',
        'footer.links': 'Enlaces',
        'footer.subjects': 'Materias',
        'footer.contact': 'Contacto',
        'footer.support': 'Soporte',
        'footer.terms': 'Términos y Condiciones',
        'footer.privacy': 'Política de Privacidad',
        'footer.rights': 'Todos los derechos reservados. Hecho con ❤️ para la educación.',

        // Common
        'common.loading': 'Cargando...',
        'common.error': 'Error',
        'common.success': 'Éxito',
        'common.close': 'Cerrar',
        'common.save': 'Guardar',
        'common.cancel': 'Cancelar',
        'common.delete': 'Eliminar',
        'common.edit': 'Editar',
        'common.view': 'Ver',
        'common.download': 'Descargar',

        // ===== ERRORES DEL BACKEND (PARA PARÁMETROS URL) =====
        'error.acceso.denegado': 'Acceso denegado. Por favor, verifica tu email y contraseña.',
        'error.token.expirado': 'Tu sesión ha expirado. Por favor, inicia sesión de nuevo.',
        'error.usuario.inactivo': 'Tu cuenta está inactiva. Contacta al administrador.',
        'error.no.autenticado': 'No estás autenticado. Por favor, inicia sesión.',
        'error.no.autorizado': 'No tienes los permisos necesarios para realizar esta acción.',
        'error.general': 'Ocurrió un error inesperado. Por favor, intenta de nuevo.',
        // =====================================================
    },

    en: {
        // Navbar
        'inicio': 'Home',
        'materias': 'Subjects',
        'como funciona': 'How It Works',
        'contacto': 'Contact',
        'login': 'Login',
        'register': 'Sign Up',
        'perfil': 'My Profile',
        'dashboard': 'Dashboard',
        'subir': 'Upload Resource',
        'logout': 'Logout',

        'nav.iniciarSesion': 'Login',
        'nav.registrarse': 'Sign Up',
        'navbar.iniciarSesion': 'Login',
        'navbar.registrarse': 'Sign Up',
        'dropdown.perfil': 'My Profile',
        'dropdown.dashboard': 'Dashboard',
        'dropdown.subir': 'Upload Resource',
        'dropdown.salir': 'Logout',

        // Home - Hero
        'home.hero.title': 'Education',
        'home.hero.title-highlight': 'Free',
        'home.hero.title-end': 'for Everyone',
        'home.hero.subtitle': 'Access quality educational resources at no cost. Learn mathematics, science, Spanish and much more. Your educational adventure starts here.',
        'home.hero.cta': 'Explore Subjects',

        // Home - Features
        'home.features.title': 'Why EduAventuras?',
        'home.features.free.title': '100% Free',
        'home.features.free.desc': 'All our resources are completely free. Accessible education for everyone without economic barriers.',
        'home.features.subjects.title': 'Multiple Subjects',
        'home.features.subjects.desc': 'Find resources in mathematics, science, Spanish, history and more. All in one place.',
        'home.features.access.title': 'Universal Access',
        'home.features.access.desc': 'Learn from anywhere, anytime. You only need an internet connection.',

        // Home - Materias Section
        'home.subjects.title': 'Explore Our Subjects',
        'home.subjects.loading': 'Loading available subjects...',
        'home.subjects.error.title': 'Error loading subjects',
        'home.subjects.error.desc': 'Could not load subjects. Please verify the server is running at',
        'home.subjects.error.retry': 'Retry',
        'home.subjects.empty.title': 'No subjects available',
        'home.subjects.empty.desc': 'No subjects have been added to the system yet.',

        // Home - Cómo Funciona
        'home.how.title': 'How It Works?',
        'home.how.step1.title': 'Sign Up Free',
        'home.how.step1.desc': 'Create your account in seconds. You only need an email.',
        'home.how.step2.title': 'Explore Subjects',
        'home.how.step2.desc': 'Browse through our subjects and find the resources you need.',
        'home.how.step3.title': 'Download and Learn',
        'home.how.step3.desc': 'Download PDF resources and start your educational adventure.',

        // Home - CTA
        'home.cta.title': 'Start Your Educational Adventure Today!',
        'home.cta.subtitle': 'Join thousands of students already learning with EduAventuras',
        'home.cta.button': 'Create Free Account',

        // Materias Page
        'materias.title': 'Explore Our Subjects',
        'materias.subtitle': 'Find quality educational resources in all areas',
        'materias.search': 'Search subjects (e.g: Mathematics, Science...)',
        'materias.loading': 'Loading available subjects...',
        'materias.showing': 'Showing',
        'materias.of': 'of',
        'materias.available': 'available subjects',
        'materias.resources': 'resources',
        'materias.view-more': 'View more',
        'materias.no-results': 'No results found',
        'materias.try-again': 'Try with other search terms',

        // Login
        'login.title': 'Login',
        'login.subtitle': 'Access your EduAventuras account',
        'login.email': 'Email',
        'login.password': 'Password',
        'login.remember': 'Remember me',
        'login.forgot': 'Forgot your password?',
        'login.button': 'Login',
        'login.no-account': "Don't have an account?",
        'login.register': 'Sign up here',
        'success': 'Login successful! Redirecting...',
        'loading': 'Logging in...',

        // Registro
        'register.title': 'Create Account',
        'register.subtitle': 'Join EduAventuras for free',
        'register.name': 'First Name',
        'register.lastname': 'Last Name',
        'register.email': 'Email',
        'register.password': 'Password',
        'register.confirm': 'Confirm Password',
        'register.role': 'I am a',
        'register.role.student': 'Student',
        'register.role.teacher': 'Teacher',
        'register.button': 'Sign Up',
        'register.have-account': 'Already have an account?',
        'register.login': 'Login here',

        // Footer
        'footer.about': 'Free educational platform dedicated to providing quality and freely accessible resources.',
        'footer.links': 'Links',
        'footer.subjects': 'Subjects',
        'footer.contact': 'Contact',
        'footer.support': 'Support',
        'footer.terms': 'Terms and Conditions',
        'footer.privacy': 'Privacy Policy',
        'footer.rights': 'All rights reserved. Made with ❤️ for education.',

        // Common
        'common.loading': 'Loading...',
        'common.error': 'Error',
        'common.success': 'Success',
        'common.close': 'Close',
        'common.save': 'Save',
        'common.cancel': 'Cancel',
        'common.delete': 'Delete',
        'common.edit': 'Edit',
        'common.view': 'View',
        'common.download': 'Download',

        // ===== ERRORES DEL BACKEND (PARA PARÁMETROS URL) =====
        'error.acceso.denegado': 'Access denied. Please check your email and password.',
        'error.token.expirado': 'Your session has expired. Please log in again.',
        'error.usuario.inactivo': 'Your account is inactive. Contact the administrator.',
        'error.no.autenticado': 'You are not authenticated. Please log in.',
        'error.no.autorizado': 'You do not have the necessary permissions to perform this action.',
        'error.general': 'An unexpected error occurred. Please try again.',
        // =====================================================
    },

    fr: {
        // Navbar
        'inicio': 'Accueil',
        'materias': 'Matières',
        'como funciona': 'Comment Ça Marche',
        'contacto': 'Contact',
        'login': 'Connexion',
        'register': "S'inscrire",
        'perfil': 'Mon Profil',
        'dashboard': 'Tableau de bord',
        'subir': 'Télécharger Ressource',
        'logout': 'Déconnexion',

        'nav.iniciarSesion': 'Connexion',
        'nav.registrarse': "S'inscrire",
        'navbar.iniciarSesion': 'Connexion',
        'navbar.registrarse': "S'inscrire",
        'dropdown.perfil': 'Mon Profil',
        'dropdown.dashboard': 'Tableau de bord',
        'dropdown.subir': 'Télécharger Ressource',
        'dropdown.salir': 'Déconnexion',

        // Home - Hero
        'home.hero.title': 'Éducation',
        'home.hero.title-highlight': 'Gratuite',
        'home.hero.title-end': 'pour Tous',
        'home.hero.subtitle': 'Accédez à des ressources éducatives de qualité sans aucun coût. Apprenez les mathématiques, les sciences, l\'espagnol et bien plus encore. Votre aventure éducative commence ici.',
        'home.hero.cta': 'Explorer les Matières',

        // Home - Features
        'home.features.title': 'Pourquoi EduAventuras?',
        'home.features.free.title': '100% Gratuit',
        'home.features.free.desc': 'Toutes nos ressources sont entièrement gratuites. Éducation accessible pour tous sans barrières économiques.',
        'home.features.subjects.title': 'Matières Multiples',
        'home.features.subjects.desc': 'Trouvez des ressources en mathématiques, sciences, espagnol, histoire et plus. Tout en un seul endroit.',
        'home.features.access.title': 'Accès Universal',
        'home.features.access.desc': 'Apprenez de n\'importe où, n\'importe quand. Vous n\'avez besoin que d\'une connexion internet.',

        // Home - Materias Section
        'home.subjects.title': 'Explorez Nos Matières',
        'home.subjects.loading': 'Chargement des matières disponibles...',
        'home.subjects.error.title': 'Erreur lors du chargement des matières',
        'home.subjects.error.desc': 'Impossible de charger les matières. Veuillez vérifier que le serveur fonctionne à',
        'home.subjects.error.retry': 'Réessayer',
        'home.subjects.empty.title': 'Aucune matière disponible',
        'home.subjects.empty.desc': 'Aucune matière n\'a encore été ajoutée au système.',

        // Home - Cómo Funciona
        'home.how.title': 'Comment Ça Marche?',
        'home.how.step1.title': 'Inscrivez-vous Gratuitement',
        'home.how.step1.desc': 'Créez votre compte en quelques secondes. Vous n\'avez besoin que d\'un email.',
        'home.how.step2.title': 'Explorez les Matières',
        'home.how.step2.desc': 'Parcourez nos matières et trouvez les ressources dont vous avez besoin.',
        'home.how.step3.title': 'Téléchargez et Apprenez',
        'home.how.step3.desc': 'Téléchargez les ressources en PDF et commencez votre aventure éducative.',

        // Home - CTA
        'home.cta.title': 'Commencez Votre Aventure Éducative Aujourd\'hui!',
        'home.cta.subtitle': 'Rejoignez des milliers d\'étudiants qui apprennent déjà avec EduAventuras',
        'home.cta.button': 'Créer un Compte Gratuit',

        // Materias Page
        'materias.title': 'Explorez Nos Matières',
        'materias.subtitle': 'Trouvez des ressources éducatives de qualité dans tous les domaines',
        'materias.search': 'Rechercher des matières (ex: Mathématiques, Sciences...)',
        'materias.loading': 'Chargement des matières disponibles...',
        'materias.showing': 'Affichage',
        'materias.of': 'de',
        'materias.available': 'matières disponibles',
        'materias.resources': 'ressources',
        'materias.view-more': 'Voir plus',
        'materias.no-results': 'Aucun résultat trouvé',
        'materias.try-again': 'Essayez avec d\'autres termes de recherche',

        // Login
        'login.title': 'Connexion',
        'login.subtitle': 'Accédez à votre compte EduAventuras',
        'login.email': 'Email',
        'login.password': 'Mot de passe',
        'login.remember': 'Se souvenir de moi',
        'login.forgot': 'Mot de passe oublié?',
        'login.button': 'Se connecter',
        'login.no-account': "Vous n'avez pas de compte?",
        'login.register': 'Inscrivez-vous ici',
        'login.success': 'Connexion réussie! Redirection...',
        'login.loading': 'Connexion en cours...',

        // Registro
        'register.title': 'Créer un Compte',
        'register.subtitle': 'Rejoignez EduAventuras gratuitement',
        'register.name': 'Prénom',
        'register.lastname': 'Nom',
        'register.email': 'Email',
        'register.password': 'Mot de passe',
        'register.confirm': 'Confirmer le mot de passe',
        'register.role': 'Je suis un(e)',
        'register.role.student': 'Étudiant(e)',
        'register.role.teacher': 'Enseignant(e)',
        'register.button': "S'inscrire",
        'register.have-account': 'Vous avez déjà un compte?',
        'register.login': 'Connectez-vous ici',

        // Footer
        'footer.about': 'Plateforme éducative gratuite dédiée à fournir des ressources de qualité et d\'accès libre.',
        'footer.links': 'Liens',
        'footer.subjects': 'Matières',
        'footer.contact': 'Contact',
        'footer.support': 'Support',
        'footer.terms': 'Termes et Conditions',
        'footer.privacy': 'Politique de Confidentialité',
        'footer.rights': 'Tous droits réservés. Fait avec ❤️ pour l\'éducation.',

        // Common
        'common.loading': 'Chargement...',
        'common.error': 'Erreur',
        'common.success': 'Succès',
        'common.close': 'Fermer',
        'common.save': 'Enregistrer',
        'common.cancel': 'Annuler',
        'common.delete': 'Supprimer',
        'common.edit': 'Modifier',
        'common.view': 'Voir',
        'common.download': 'Télécharger',

        // ===== ERREURS DU BACKEND (POUR PARAMÈTRES URL) =====
        'error.acceso.denegado': 'Accès refusé. Veuillez vérifier votre email et mot de passe.',
        'error.token.expirado': 'Votre session a expiré. Veuillez vous reconnecter.',
        'error.usuario.inactivo': 'Votre compte est inactif. Contactez l\'administrateur.',
        'error.no.autenticado': 'Vous n\'êtes pas authentifié. Veuillez vous connecter.',
        'error.no.autorizado': 'Vous n\'avez pas les permissions nécessaires pour effectuer cette action.',
        'error.general': 'Une erreur inattendue est survenue. Veuillez réessayer.',
        // =====================================================
    }
};

// ===== OBTENER IDIOMA ACTUAL =====
function getCurrentLanguage() {
    return localStorage.getItem('language') || DEFAULT_LANGUAGE;
}

// ===== ESTABLECER IDIOMA =====
function setLanguage(lang) {
    if (!translations[lang]) {
        console.error(`Idioma no soportado: ${lang}`);
        return;
    }

    localStorage.setItem('language', lang);
    document.documentElement.lang = lang;
    translatePage();
    console.log(`✅ Idioma cambiado a: ${lang}`);
}

// ===== OBTENER TRADUCCIÓN =====
function t(key, params = {}) {
    const lang = getCurrentLanguage();
    let translation = translations[lang][key] || translations[DEFAULT_LANGUAGE][key] || key;

    // Reemplazar parámetros
    Object.keys(params).forEach(param => {
        translation = translation.replace(`{${param}}`, params[param]);
    });

    return translation;
}

// ===== TRADUCIR PÁGINA =====
function translatePage() {
    // Traducir todos los elementos con data-i18n
    document.querySelectorAll('[data-i18n]').forEach(element => {
        const key = element.getAttribute('data-i18n');
        const translation = t(key);

        // Si la traducción es diferente a la clave, aplicarla
        if (translation !== key) {
            element.textContent = translation;
        }
    });

    // Traducir placeholders
    document.querySelectorAll('[data-i18n-placeholder]').forEach(element => {
        const key = element.getAttribute('data-i18n-placeholder');
        element.placeholder = t(key);
    });

    // Traducir títulos (title attribute)
    document.querySelectorAll('[data-i18n-title]').forEach(element => {
        const key = element.getAttribute('data-i18n-title');
        element.title = t(key);
    });

    // Actualizar selector de idioma
    updateLanguageSelector();

    console.log('🌍 Página traducida');
}

// ===== ACTUALIZAR SELECTOR DE IDIOMA =====
function updateLanguageSelector() {
    const currentLang = getCurrentLanguage();
    const langButton = document.getElementById('currentLanguage');
    const langFlags = {
        es: '🇪🇸',
        en: '🇬🇧',
        fr: '🇫🇷'
    };

    if (langButton) {
        langButton.textContent = langFlags[currentLang] || '🌍';
    }

    // Actualizar label del idioma
    const langLabel = document.getElementById('langLabel');
    if (langLabel) {
        langLabel.textContent = currentLang.toUpperCase();
    }
}

// =====