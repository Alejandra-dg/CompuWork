package compuwork;

import java.util.List;

public class Supervisor extends Usuario {

    private Departamento departamento;
    private String contrasena;
    private int contadorReportes;

    public Supervisor(int idUsuario, String nombre, String contrasena, Departamento departamento) {
        super(idUsuario, nombre);
        if (contrasena == null || contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento supervisado no puede ser nulo.");
        }
        this.contrasena = contrasena;
        this.departamento = departamento;
        this.contadorReportes = 0;
    }

    @Override
    public boolean iniciarSesion(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        boolean acceso = this.contrasena.equals(contrasena);
        System.out.println(acceso
            ? "Supervisor " + getNombre() + " ha iniciado sesión."
            : "Credenciales incorrectas para el supervisor " + getNombre());
        return acceso;
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Supervisor " + getNombre() + " ha cerrado sesión.");
    }

    public void visualizarEquipo() {
        System.out.println("\n=== Equipo del Supervisor " + getNombre() + " ===");
        System.out.println("Departamento: " + departamento.getNombre());
        List<Empleado> equipo = departamento.listarEmpleados();
        if (equipo.isEmpty()) {
            System.out.println("No hay empleados en este departamento.");
        } else {
            for (Empleado e : equipo) {
                System.out.println("  -> " + e.obtenerDatos());
            }
        }
        System.out.println("Total: " + equipo.size() + " empleados");
    }

    public ReporteDesempenio generarReporteIndividual(int idEmpleado) {
        Empleado empleado = departamento.buscarEmpleado(idEmpleado);
        contadorReportes++;
        ReporteDesempenio reporte = new ReporteDesempenio(contadorReportes, "INDIVIDUAL");
        reporte.calcularMetricas(empleado);
        System.out.println("Reporte individual generado por supervisor " + getNombre());
        return reporte;
    }

    public ReporteDesempenio generarReporte() {
        if (departamento.listarEmpleados().isEmpty()) {
            throw new IllegalStateException("El departamento no tiene empleados para reportar.");
        }
        contadorReportes++;
        ReporteDesempenio reporte = new ReporteDesempenio(contadorReportes, "DEPARTAMENTO");
        reporte.calcularMetricasDepartamento(departamento);
        System.out.println("Reporte de departamento generado por supervisor " + getNombre());
        return reporte;
    }

    public boolean aprobarReporte(ReporteDesempenio reporte) {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        if (reporte.getMetricas().isEmpty()) {
            System.out.println("El reporte ID " + reporte.getIdReporte() + " no tiene métricas. No puede aprobarse.");
            return false;
        }
        System.out.println("Reporte ID " + reporte.getIdReporte() +
                           " APROBADO por Supervisor " + getNombre());
        return true;
    }

    public Departamento getDepartamento() { return departamento; }

    public void setDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser nulo.");
        }
        this.departamento = departamento;
        System.out.println("Supervisor " + getNombre() + " ahora supervisa: " + departamento.getNombre());
    }

    @Override
    public String toString() {
        return "Supervisor [ID=" + getIdUsuario() + ", Nombre=" + getNombre() +
               ", Departamento=" + departamento.getNombre() + "]";
    }
}
