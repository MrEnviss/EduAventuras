package com.eduaventuras.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para generar reportes PDF con iTextPDF
 */
public class PdfGenerator {

    // Colores
    private static final BaseColor COLOR_HEADER = new BaseColor(41, 128, 185);
    private static final BaseColor COLOR_SECTION = new BaseColor(52, 152, 219);

    // Fuentes
    private static Font fontTitle;
    private static Font fontHeader;
    private static Font fontSection;
    private static Font fontNormal;
    private static Font fontBold;

    static {
        try {
            fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.WHITE);
            fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, COLOR_HEADER);
            fontSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, COLOR_SECTION);
            fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
            fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Crear un nuevo documento PDF
     */
    public static Document crearDocumento() {
        return new Document(PageSize.A4, 36, 36, 54, 36);
    }

    /**
     * Agregar encabezado al documento
     */
    public static void agregarEncabezado(Document document, String titulo) throws DocumentException {
        // Tabla para el encabezado
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);

        PdfPCell headerCell = new PdfPCell();
        headerCell.setBackgroundColor(COLOR_HEADER);
        headerCell.setPadding(15);
        headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph headerText = new Paragraph(titulo, fontTitle);
        headerText.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(headerText);

        // Fecha
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        Paragraph fechaText = new Paragraph("Generado: " + fecha,
                FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.WHITE));
        fechaText.setAlignment(Element.ALIGN_CENTER);
        headerCell.addElement(fechaText);

        headerTable.addCell(headerCell);
        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    /**
     * Agregar sección con título
     */
    public static void agregarSeccion(Document document, String titulo) throws DocumentException {
        Paragraph seccion = new Paragraph(titulo, fontSection);
        seccion.setSpacingBefore(15);
        seccion.setSpacingAfter(10);
        document.add(seccion);
    }

    /**
     * Agregar párrafo normal
     */
    public static void agregarParrafo(Document document, String texto) throws DocumentException {
        Paragraph parrafo = new Paragraph(texto, fontNormal);
        parrafo.setSpacingAfter(5);
        document.add(parrafo);
    }

    /**
     * Agregar tabla simple (2 columnas: label - valor)
     */
    public static void agregarTablaDatos(Document document, java.util.Map<String, String> datos)
            throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        try {
            table.setWidths(new float[]{3, 2});
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        for (java.util.Map.Entry<String, String> entry : datos.entrySet()) {
            // Celda label (negrita)
            PdfPCell cellLabel = new PdfPCell(new Phrase(entry.getKey(), fontBold));
            cellLabel.setPadding(8);
            cellLabel.setBorder(Rectangle.NO_BORDER);
            cellLabel.setBackgroundColor(BaseColor.LIGHT_GRAY);

            // Celda valor
            PdfPCell cellValue = new PdfPCell(new Phrase(entry.getValue(), fontNormal));
            cellValue.setPadding(8);
            cellValue.setBorder(Rectangle.NO_BORDER);

            table.addCell(cellLabel);
            table.addCell(cellValue);
        }

        document.add(table);
    }

    /**
     * Agregar tabla con encabezados personalizados
     */
    public static void agregarTabla(Document document, String[] headers,
                                    java.util.List<String[]> rows) throws DocumentException {
        PdfPTable table = new PdfPTable(headers.length);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Agregar encabezados
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontBold));
            cell.setBackgroundColor(COLOR_SECTION);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        // Agregar filas
        for (String[] row : rows) {
            for (String value : row) {
                PdfPCell cell = new PdfPCell(new Phrase(value, fontNormal));
                cell.setPadding(8);
                table.addCell(cell);
            }
        }

        document.add(table);
    }

    /**
     * Agregar línea separadora
     */
    public static void agregarSeparador(Document document) throws DocumentException {
        LineSeparator line = new LineSeparator();
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));
        document.add(new Paragraph("\n"));
    }}