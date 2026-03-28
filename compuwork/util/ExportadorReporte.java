package compuwork.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import compuwork.modelo.ReporteDesempenio;

public final class ExportadorReporte {

    private ExportadorReporte() {
        // Clase de utilidad estática.
    }

    public static File exportar(ReporteDesempenio reporte) {
        Validaciones.validarObjeto(reporte, "El reporte no puede ser nulo.");
        if (reporte.getMetricas().isEmpty()) {
            throw new IllegalStateException("No hay métricas para exportar. Calcule las métricas primero.");
        }

        String nombreArchivo = "Reporte_" + reporte.getTipoReporte() + "_" + reporte.getIdReporte() + ".txt";
        File archivo = new File(nombreArchivo);

        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write("REPORTE DE DESEMPEÑO - CompuWork\n");
            escritor.write("ID Reporte: " + reporte.getIdReporte() + "\n");
            escritor.write("Tipo: " + reporte.getTipoReporte() + "\n");
            escritor.write("Fecha: " + reporte.getFechaGeneracion() + "\n");
            escritor.write("==============================\n\n");
            escritor.write(reporte.getMetricas());
            System.out.println("Reporte exportado a: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar el reporte: " + e.getMessage(), e);
        }

        return archivo;
    }
}
