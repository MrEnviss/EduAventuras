package com.Eduaventuras.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Utilidad para manejo de archivos (subida, descarga, eliminación)
 */
public class FileUtil {

    // Carpeta base donde se guardarán los archivos
    private static final String UPLOAD_DIR = "uploads/recursos/";

    /**
     * Guardar un archivo PDF
     * @param file Archivo a guardar
     * @param materiaNombre Nombre de la materia (para organizar en carpetas)
     * @return Ruta donde se guardó el archivo
     */
    public static String guardarArchivo(MultipartFile file, String materiaNombre) throws IOException {
        // Validar que sea un PDF
        if (!esPDF(file)) {
            throw new IOException("El archivo debe ser un PDF");
        }

        // Validar tamaño (máximo 10MB)
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("El archivo no puede superar 10MB");
        }

        // Crear carpeta si no existe
        String carpetaMateria = UPLOAD_DIR + normalizarNombre(materiaNombre) + "/";
        File directorio = new File(carpetaMateria);
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

        // Generar nombre único para el archivo
        String nombreOriginal = file.getOriginalFilename();
        String extension = obtenerExtension(nombreOriginal);
        String nombreUnico = UUID.randomUUID().toString() + extension;

        // Guardar archivo
        Path rutaDestino = Paths.get(carpetaMateria + nombreUnico);
        Files.copy(file.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

        return carpetaMateria + nombreUnico;
    }

    /**
     * Leer un archivo como bytes
     * @param rutaArchivo Ruta del archivo
     * @return Contenido del archivo en bytes
     */
    public static byte[] leerArchivo(String rutaArchivo) throws IOException {
        Path ruta = Paths.get(rutaArchivo);
        if (!Files.exists(ruta)) {
            throw new IOException("Archivo no encontrado: " + rutaArchivo);
        }
        return Files.readAllBytes(ruta);
    }

    /**
     * Eliminar un archivo
     * @param rutaArchivo Ruta del archivo a eliminar
     */
    public static void eliminarArchivo(String rutaArchivo) throws IOException {
        Path ruta = Paths.get(rutaArchivo);
        if (Files.exists(ruta)) {
            Files.delete(ruta);
        }
    }

    /**
     * Verificar si un archivo es PDF
     */
    private static boolean esPDF(MultipartFile file) {
        String contentType = file.getContentType();
        String nombreArchivo = file.getOriginalFilename();

        return (contentType != null && contentType.equals("application/pdf")) ||
                (nombreArchivo != null && nombreArchivo.toLowerCase().endsWith(".pdf"));
    }

    /**
     * Obtener extensión de un archivo
     */
    private static String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return ".pdf";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }

    /**
     * Normalizar nombre de carpeta (sin caracteres especiales)
     */
    private static String normalizarNombre(String nombre) {
        return nombre.toLowerCase()
                .replaceAll("[áàäâ]", "a")
                .replaceAll("[éèëê]", "e")
                .replaceAll("[íìïî]", "i")
                .replaceAll("[óòöô]", "o")
                .replaceAll("[úùüû]", "u")
                .replaceAll("[^a-z0-9]", "_");
    }
}