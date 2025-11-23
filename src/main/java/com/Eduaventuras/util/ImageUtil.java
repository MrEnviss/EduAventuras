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
 * Utilidad para manejo de imagenes de perfil
 */
public class ImageUtil {

    private static final String UPLOAD_DIR = "uploads/fotos-perfil";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png"};

    /**
     * Guardar imagen de perfil
     */
    public static String guardarImagen(MultipartFile file) throws IOException {
        // Validar que el archivo no este vacio
        if (file.isEmpty()) {
            throw new RuntimeException("El archivo esta vacio");
        }

        // Validar tamaño
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("El archivo excede el tamaño maximo de 5MB");
        }

        // Validar extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("Nombre de archivo invalido");
        }

        String extension = obtenerExtension(originalFilename);
        if (!esExtensionValida(extension)) {
            throw new RuntimeException("Formato de imagen no permitido. Use JPG, JPEG o PNG");
        }

        // Crear directorio si no existe
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generar nombre unico
        String nombreArchivo = UUID.randomUUID().toString() + "." + extension;
        Path rutaDestino = Paths.get(UPLOAD_DIR, nombreArchivo);

        // Guardar archivo
        Files.copy(file.getInputStream(), rutaDestino, StandardCopyOption.REPLACE_EXISTING);

        return UPLOAD_DIR + "/" + nombreArchivo;
    }

    /**
     * Leer imagen desde el sistema de archivos
     */
    public static byte[] leerImagen(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);

        if (!Files.exists(path)) {
            throw new RuntimeException("Imagen no encontrada");
        }

        return Files.readAllBytes(path);
    }

    /**
     * Eliminar imagen
     */
    public static void eliminarImagen(String rutaArchivo) throws IOException {
        if (rutaArchivo == null || rutaArchivo.isEmpty()) {
            return;
        }

        Path path = Paths.get(rutaArchivo);

        if (Files.exists(path)) {
            Files.delete(path);
        }
    }

    /**
     * Obtener extension del archivo
     */
    private static String obtenerExtension(String nombreArchivo) {
        int lastDot = nombreArchivo.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return nombreArchivo.substring(lastDot + 1).toLowerCase();
    }

    /**
     * Validar si la extension es permitida
     */
    private static boolean esExtensionValida(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equals(extension)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crear directorio de fotos de perfil al iniciar
     */
    public static void inicializarDirectorio() {
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("✅ Directorio de fotos de perfil creado: " + UPLOAD_DIR);
            } else {
                System.err.println("❌ No se pudo crear el directorio: " + UPLOAD_DIR);
            }
        }
    }
}