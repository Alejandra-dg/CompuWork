package compuwork.servicio;

import java.util.List;
import compuwork.modelo.Departamento;
import compuwork.modelo.Empleado;
import compuwork.modelo.ReporteDesempenio;
import compuwork.modelo.Usuario;
import compuwork.util.Validaciones;

public class Supervisor extends Usuario {

    private Departamento departamento;
    private int contadorReportes;

    public Supervisor(int idUsuario, String nombre, String contrasena, Departamento departamento) {
        super(idUsuario, nombre, contrasena);
        Validaciones.validarObjeto(departamento, "El departamento supervisado no puede ser nulo.");
        this.departamento = departamento;
        this.contadorReportes = 0;
    }

    @Override
    public boolean iniciarSesion(String contrasena) {
        boolean acceso = autenticar(contrasena);
        System.out.println(acceso
            ? "Supervisor " + getNombre() + " ha iniciado sesión correctamente."
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
        List<Empleado> equipo = departamento.getEmpleados();
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
        contadorReportes++;
        ReporteDesempenio reporte = new ReporteDesempenio(contadorReportes, "DEPARTAMENTO");
        reporte.calcularMetricasDepartamento(departamento);
        System.out.println("Reporte de departamento generado por supervisor " + getNombre());
        return reporte;
    }

    public boolean aprobarReporte(ReporteDesempenio reporte) {
        Validaciones.validarObjeto(reporte, "El reporte no puede ser nulo.");
        if (reporte.getMetricas().isEmpty()) {
            System.out.println("El reporte ID " + reporte.getIdReporte() + " no tiene métricas. No puede aprobarse.");
            return false;
        }
        System.out.println("Reporte ID " + reporte.getIdReporte() + " aprobado por supervisor " + getNombre());
        return true;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        Validaciones.validarObjeto(departamento, "El departamento no puede ser nulo.");
        this.departamento = departamento;
        System.out.println("Supervisor " + getNombre() + " ahora supervisa: " + departamento.getNombre());
    }

    @Override
    public String toString() {
        return "Supervisor [ID=" + getIdUsuario() + ", Nombre=" + getNombre() +
               ", Departamento=" + departamento.getNombre() + "]";
    }
}
