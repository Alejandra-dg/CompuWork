package compuwork.modelo;

import java.util.Date;
import compuwork.util.Validaciones;

public class Empleado extends Usuario {

    private int idEmpleado;
    private String cargo;
    private Date fechaIngreso;
    private double salario;

    public Empleado(int idUsuario, String nombre, String contrasena,
                    int idEmpleado, String cargo, Date fechaIngreso, double salario) {
        super(idUsuario, nombre, contrasena);
        Validaciones.validarTexto(cargo, "El cargo del empleado no puede estar vacío.");
        Validaciones.validarNoNegativo(salario, "El salario no puede ser negativo.");
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser nula.");
        }
        this.idEmpleado = idEmpleado;
        this.cargo = cargo;
        this.fechaIngreso = new Date(fechaIngreso.getTime());
        this.salario = salario;
    }

    @Override
    public boolean iniciarSesion(String contrasena) {
        boolean acceso = autenticar(contrasena);
        System.out.println(acceso
            ? "Empleado " + getNombre() + " ha iniciado sesión correctamente."
            : "Credenciales incorrectas para el empleado " + getNombre());
        return acceso;
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Empleado " + getNombre() + " ha cerrado sesión.");
    }

    public String obtenerDatos() {
        return "Empleado [ID=" + idEmpleado + ", Nombre=" + getNombre() +
               ", Cargo=" + cargo + ", Fecha Ingreso=" + fechaIngreso +
               ", Salario=" + salario + "]";
    }

    public void verReporte(ReporteDesempenio reporte) {
        if (reporte == null) {
            throw new IllegalArgumentException("El reporte no puede ser nulo.");
        }
        System.out.println("=== Reporte del empleado " + getNombre() + " ===");
        reporte.visualizar();
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        Validaciones.validarTexto(cargo, "El cargo no puede estar vacío.");
        this.cargo = cargo;
    }

    public Date getFechaIngreso() {
        return new Date(fechaIngreso.getTime());
    }

    public void setFechaIngreso(Date fechaIngreso) {
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser nula.");
        }
        this.fechaIngreso = new Date(fechaIngreso.getTime());
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        Validaciones.validarNoNegativo(salario, "El salario no puede ser negativo.");
        this.salario = salario;
    }

    @Override
    public String toString() {
        return obtenerDatos();
    }
}
