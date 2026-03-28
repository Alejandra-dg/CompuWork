package compuwork.modelo;

import java.util.Date;
import compuwork.util.Validaciones;

public class ReporteDesempenio {

    private int idReporte;
    private String tipoReporte;
    private Date fechaGeneracion;
    private String metricas;

    public ReporteDesempenio(int idReporte, String tipoReporte) {
        Validaciones.validarTexto(tipoReporte, "El tipo de reporte no puede estar vacío.");
        String tipo = tipoReporte.trim().toUpperCase();
        if (!tipo.equals("INDIVIDUAL") && !tipo.equals("DEPARTAMENTO")) {
            throw new IllegalArgumentException("El tipo de reporte debe ser 'INDIVIDUAL' o 'DEPARTAMENTO'.");
        }
        this.idReporte = idReporte;
        this.tipoReporte = tipo;
        this.fechaGeneracion = new Date();
        this.metricas = "";
    }

    public void calcularMetricas(Empleado empleado) {
        Validaciones.validarObjeto(empleado, "El empleado no puede ser nulo.");

        StringBuilder sb = new StringBuilder();
        sb.append("=== MÉTRICAS DE DESEMPEÑO INDIVIDUAL ===\n");
        sb.append("Empleado: ").append(empleado.getNombre()).append("\n");
        sb.append("Cargo: ").append(empleado.getCargo()).append("\n");
        sb.append("Salario: $").append(String.format("%.2f", empleado.getSalario())).append("\n");
        sb.append("Fecha de ingreso: ").append(empleado.getFechaIngreso()).append("\n");

        long diasAntiguedad = (new Date().getTime() - empleado.getFechaIngreso().getTime()) / (1000L * 60 * 60 * 24);
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
            sb.append("Bono estimado: $").append(String.format("%.2f", ep.calcularBonos())).append("\n");
            sb.append("Beneficios: ").append(ep.getBeneficios()).append("\n");
        } else if (empleado instanceof EmpleadoTemporal) {
            EmpleadoTemporal et = (EmpleadoTemporal) empleado;
            sb.append("Agencia: ").append(et.getAgencia()).append("\n");
            sb.append("Contrato vigente: ").append(et.estaVigente() ? "Sí" : "No").append("\n");
        }

        this.metricas = sb.toString();
    }

    public void calcularMetricasDepartamento(Departamento departamento) {
        Validaciones.validarObjeto(departamento, "El departamento no puede ser nulo.");
        if (departamento.getEmpleados().isEmpty()) {
            throw new IllegalStateException("El departamento no tiene empleados para generar reporte.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE DE DEPARTAMENTO ===\n");
        sb.append(departamento.obtenerMetricas()).append("\n");
        sb.append("--- Detalle de empleados ---\n");

        for (Empleado e : departamento.getEmpleados()) {
            sb.append(e.obtenerDatos()).append("\n");
        }

        this.metricas = sb.toString();
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

    public int getIdReporte() {
        return idReporte;
    }

    public String getTipoReporte() {
        return tipoReporte;
    }

    public Date getFechaGeneracion() {
        return new Date(fechaGeneracion.getTime());
    }

    public String getMetricas() {
        return metricas;
    }

    @Override
    public String toString() {
        return "ReporteDesempenio [ID=" + idReporte + ", Tipo=" + tipoReporte + "]";
    }
}
