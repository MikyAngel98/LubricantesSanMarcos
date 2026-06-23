package org.example.Servicio;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.example.DTO.*;

import java.awt.Color;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfExportService {

    private Font getTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    }

    private Font getSubtitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 12);
    }

    private Font getHeaderFont() {
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    }

    private Font getNormalFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 10);
    }

    private PdfPCell createHeaderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getHeaderFont()));
        cell.setBackgroundColor(new Color(200, 200, 200));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getNormalFont()));
        cell.setPadding(5);
        return cell;
    }

    private PdfPCell createCellRight(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, getNormalFont()));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        return cell;
    }

    // ==================== EXPORTAR REPORTE DE VENTAS ====================

    public void exportarReporteVentas(List<ReporteVentaDTO> ventas, LocalDate fechaInicio, LocalDate fechaFin, String ruta) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph titulo = new Paragraph("REPORTE DE VENTAS", getTitleFont());
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Período: " + fechaInicio + " al " + fechaFin, getSubtitleFont());
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Fecha de emisión: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 3, 1, 2, 2});

        table.addCell(createHeaderCell("ID"));
        table.addCell(createHeaderCell("Fecha"));
        table.addCell(createHeaderCell("Cliente"));
        table.addCell(createHeaderCell("Tipos"));
        table.addCell(createHeaderCell("Total (Bs)"));
        table.addCell(createHeaderCell("Método"));

        double totalGeneral = 0;
        for (ReporteVentaDTO v : ventas) {
            table.addCell(createCell(String.valueOf(v.getId())));
            table.addCell(createCell(v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            table.addCell(createCell(v.getCliente()));
            table.addCell(createCell(String.valueOf(v.getTiposProductos())));
            table.addCell(createCellRight(String.format("%.2f", v.getTotal())));
            table.addCell(createCell(v.getMetodoPago()));
            totalGeneral += v.getTotal();
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph total = new Paragraph("TOTAL GENERAL: Bs " + String.format("%.2f", totalGeneral), getTitleFont());
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }

    // ==================== EXPORTAR REPORTE DE COMPRAS ====================

    public void exportarReporteCompras(List<HistorialCompraDTO> compras, LocalDate fechaInicio, LocalDate fechaFin, String ruta) throws Exception {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph titulo = new Paragraph("REPORTE DE COMPRAS", getTitleFont());
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Período: " + fechaInicio + " al " + fechaFin, getSubtitleFont());
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitulo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Fecha de emisión: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 2, 3, 1, 2});

        table.addCell(createHeaderCell("ID"));
        table.addCell(createHeaderCell("Fecha"));
        table.addCell(createHeaderCell("Proveedor"));
        table.addCell(createHeaderCell("Tipos"));
        table.addCell(createHeaderCell("Total (Bs)"));

        double totalGeneral = 0;
        for (HistorialCompraDTO c : compras) {
            table.addCell(createCell(String.valueOf(c.getId())));
            table.addCell(createCell(c.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
            table.addCell(createCell(c.getProveedor()));
            table.addCell(createCell(String.valueOf(c.getTiposProductos())));
            table.addCell(createCellRight(String.format("%.2f", c.getTotal())));
            totalGeneral += c.getTotal();
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph total = new Paragraph("TOTAL GENERAL: Bs " + String.format("%.2f", totalGeneral), getTitleFont());
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }

    // ==================== EXPORTAR PRODUCTOS MÁS VENDIDOS ====================

    public void exportarProductosMasVendidos(List<ProductoVendidoDTO> productos, LocalDate fechaInicio, LocalDate fechaFin, String ruta) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph titulo = new Paragraph("PRODUCTOS MÁS VENDIDOS", getTitleFont());
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph("Período: " + fechaInicio + " al " + fechaFin));
        document.add(new Paragraph("Fecha de emisión: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 4, 2, 2, 1, 2});

        table.addCell(createHeaderCell("ID"));
        table.addCell(createHeaderCell("Producto"));
        table.addCell(createHeaderCell("Categoría"));
        table.addCell(createHeaderCell("Marca"));
        table.addCell(createHeaderCell("Unidades"));
        table.addCell(createHeaderCell("Facturado (Bs)"));

        for (ProductoVendidoDTO p : productos) {
            table.addCell(createCell(String.valueOf(p.getId())));
            table.addCell(createCell(p.getNombre()));
            table.addCell(createCell(p.getCategoria()));
            table.addCell(createCell(p.getMarca()));
            table.addCell(createCell(String.format("%.0f", p.getTotalVendido())));
            table.addCell(createCellRight(String.format("%.2f", p.getTotalFacturado())));
        }

        document.add(table);
        document.close();
    }

    // ==================== EXPORTAR PRODUCTOS BAJO STOCK ====================

    public void exportarProductosBajoStock(List<ProductoBajoStockDTO> productos, float stockMinimo, String ruta) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph titulo = new Paragraph("PRODUCTOS CON STOCK BAJO", getTitleFont());
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph("Stock mínimo considerado: " + stockMinimo + " unidades"));
        document.add(new Paragraph("Fecha de emisión: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 4, 2, 2, 1, 2});

        table.addCell(createHeaderCell("ID"));
        table.addCell(createHeaderCell("Producto"));
        table.addCell(createHeaderCell("Categoría"));
        table.addCell(createHeaderCell("Marca"));
        table.addCell(createHeaderCell("Stock"));
        table.addCell(createHeaderCell("Precio (Bs)"));

        for (ProductoBajoStockDTO p : productos) {
            table.addCell(createCell(String.valueOf(p.getId())));
            table.addCell(createCell(p.getNombre()));
            table.addCell(createCell(p.getCategoria()));
            table.addCell(createCell(p.getMarca()));
            table.addCell(createCell(String.format("%.2f", p.getStock())));
            table.addCell(createCellRight(String.format("%.2f", p.getPrecio())));
        }

        document.add(table);
        document.close();
    }

    // ==================== EXPORTAR DETALLE DE VENTA ====================

    public void exportarDetalleVenta(int idVenta, List<DetalleVentaDTO> detalles, String fecha, String cliente, String metodoPago, float total, String ruta) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph titulo = new Paragraph("DETALLE DE VENTA #" + idVenta, getTitleFont());
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Fecha: " + fecha));
        document.add(new Paragraph("Cliente: " + cliente));
        document.add(new Paragraph("Método de pago: " + metodoPago));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});

        table.addCell(createHeaderCell("Producto"));
        table.addCell(createHeaderCell("Cantidad"));
        table.addCell(createHeaderCell("Precio"));
        table.addCell(createHeaderCell("Subtotal"));

        for (DetalleVentaDTO d : detalles) {
            table.addCell(createCell(d.getProducto()));
            table.addCell(createCell(String.format("%.2f", d.getCantidad())));
            table.addCell(createCellRight(String.format("%.2f", d.getPrecioVenta())));
            table.addCell(createCellRight(String.format("%.2f", d.getSubtotal())));
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph totalP = new Paragraph("TOTAL: Bs " + String.format("%.2f", total), getTitleFont());
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        document.close();
    }

    // ==================== EXPORTAR DETALLE DE COMPRA ====================

    public void exportarDetalleCompra(int idCompra, List<DetalleCompraDTO> detalles, String fecha, String proveedor, float total, String ruta) throws Exception {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(ruta));
        document.open();

        Paragraph titulo = new Paragraph("DETALLE DE COMPRA #" + idCompra, getTitleFont());
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("Fecha: " + fecha));
        document.add(new Paragraph("Proveedor: " + proveedor));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{4, 1, 2, 2});

        table.addCell(createHeaderCell("Producto"));
        table.addCell(createHeaderCell("Cantidad"));
        table.addCell(createHeaderCell("Precio"));
        table.addCell(createHeaderCell("Subtotal"));

        for (DetalleCompraDTO d : detalles) {
            table.addCell(createCell(d.getProducto()));
            table.addCell(createCell(String.format("%.2f", d.getCantidad())));
            table.addCell(createCellRight(String.format("%.2f", d.getPrecioCompra())));
            table.addCell(createCellRight(String.format("%.2f", d.getSubtotal())));
        }

        document.add(table);

        document.add(new Paragraph(" "));
        Paragraph totalP = new Paragraph("TOTAL: Bs " + String.format("%.2f", total), getTitleFont());
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        document.close();
    }
}