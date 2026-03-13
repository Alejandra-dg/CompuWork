package compuwork;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class ReporteDesempenio {

    private int idReporte;
    private String tipoReporte; 
    private Date fechaGeneracion;
    private String metricas;

    public ReporteDesempenio(int idReporte, String tipoReporte) { 
        if (tipoReporte == null || tipoReporte.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de reporte no puede estar vacío.");
        }
        
        if (!tipoReporte.equalsIgnoreCase("INDIVIDUAL") && !tipoReporte.equalsIgnoreCase("DEPARTAMENTO")) {
            throw new IllegalArgumentException("El tipo de reporte debe ser 'INDIVIDUAL' o 'DEPARTAMENTO'.");
        }
        this.idReporte = idReporte;
        this.tipoReporte = tipoReporte.toUpperCase();
        this.fechaGeneracion = new Date();
        this.metricas = "";
    }

    public void calcularMetricas(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== MÉTRICAS DE DESEMPEÑO ===\n");
        sb.append("Empleado: ").append(empleado.getNombre()).append("\n");
        sb.append("Cargo: ").append(empleado.getCargo()).append("\n");
        sb.append("Salario: $").append(String.format("%.2f", empleado.getSalario())).append("\n");
        sb.append("Fecha Ingreso: ").append(empleado.getFechaIngreso()).append("\n");

        long diasAntiguedad = (new Date().getTime() - empleado.getFechaIngreso().getTime()) / (1000 * 60 * 60 * 24);
        sb.append("Antigüedad: ").append(diasAntiguedad).append(" días\n");

        String calificacion;
        if (diasAntiguedad > 1825) {      
            calificacion = "EXCELENTE";
        } else if (diasAntiguedad > 730) { 
            calificacion = "BUENO";
        } else if (diasAntiguedad > 365) { 
            calificacion = "REGULAR";
        } else {
            calificacion = "NUEVO";
        }
        sb.append("Calificación: ").append(calificacion).append("\n");

        if (empleado instanceof EmpleadoPermanente) {
            EmpleadoPermanente ep = (EmpleadoPermanente) empleado;
            System.out.println("Bono estimado: $" + String.format("%.2f", ep.calcularBonos()) + "\n");
            System.out.println("Beneficios: " + ep.getBeneficios() + "\n");
        } else if (empleado instanceof EmpleadoTemporal) {
            EmpleadoTemporal et = (EmpleadoTemporal) empleado;
            System.out.println("Agencia: " + et.getAgencia());
            System.out.println("Contrato vigente: " + (et.estaVigente() ? "Sí" : "No"));
        } 

        this.metricas = sb.toString();
        System.out.println("Métricas calculadas para: " + empleado.getNombre());
    }

    public void calcularMetricasDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser nulo.");
        }
        if (departamento.listarEmpleados().isEmpty()) {
            throw new IllegalStateException("El departamento no tiene empleados para generar reporte.");
        }

        StringBuilder sb = new StringBuilder();
        System.out.println("=== REPORTE DE DEPARTAMENTO: " + departamento.getNombre() + " ===");
        System.out.println(departamento.obtenerMetricas());
        System.out.println();
        System.out.println("--- Detalle de Empleados ---");

        for (Empleado e : departamento.listarEmpleados()) {
            System.out.println(e.obtenerDatos());
        }

        this.metricas = sb.toString();
        System.out.println("Métricas de departamento calculadas: " + departamento.getNombre());
    }

    public File exportar() {
        if (metricas.isEmpty()) {
            throw new IllegalStateException("No hay métricas para exportar. Calcule las métricas primero.");
        }
        String nombreArchivo = "Reporte_" + tipoReporte + "_" + idReporte + ".txt";
        File archivo = new File(nombreArchivo);
        try (FileWriter escritor = new FileWriter(archivo)) {
            escritor.write("REPORTE DE DESEMPEÑO - CompuWork\n");
            escritor.write("ID Reporte: " + idReporte + "\n");
            escritor.write("Tipo: " + tipoReporte + "\n");
            escritor.write("Fecha: " + fechaGeneracion + "\n");
            escritor.write("==============================\n\n");
            escritor.write(metricas);
            System.out.println("Reporte exportado a: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Error al exportar el reporte: " + e.getMessage(), e);
        }
        return archivo;
    }

    public void visualizar() {
        if (metricas.isEmpty()) {
            System.out.println("No hay métricas disponibles. Calcule las métricas primero.");
            return;
        }
        System.out.println("====== REPORTE ID: " + idReporte + " ======");
        System.out.println("Tipo: " + tipoReporte);
        System.out.println("Fecha: " + fechaGeneracion);
        System.out.println("------------------------------");
        System.out.println(metricas);
    }

    public int getIdReporte() { return idReporte; }

    public String getTipoReporte() { return tipoReporte; }

    public Date getFechaGeneracion() { return fechaGeneracion; }

    public String getMetricas() { return metricas; }

    @Override
    public String toString() {
        return "ReporteDesempenio [ID=" + idReporte + ", Tipo=" + tipoReporte +
               ", Fecha=" + fechaGeneracion + "]";
    }
}
