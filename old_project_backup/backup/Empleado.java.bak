package compuwork;

import java.util.Date;

public class Empleado extends Usuario {

    private int idEmpleado;
    private String cargo;
    private Date fechaIngreso;
    private double salario;
    private String contrasena; 

    public Empleado(int idUsuario, String nombre, String contrasena,
                    int idEmpleado, String cargo, Date fechaIngreso, double salario) {
        super(idUsuario, nombre);
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("El cargo del empleado no puede estar vacío.");
        }
        if (salario < 0) {
            throw new IllegalArgumentException("El salario no puede ser negativo.");
        }
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser nula.");
        }
        this.idEmpleado = idEmpleado;
        this.cargo = cargo;
        this.fechaIngreso = fechaIngreso;
        this.salario = salario;
        this.contrasena = contrasena;
    }

    @Override
    public boolean iniciarSesion(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        return this.contrasena.equals(contrasena);
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
        System.out.println("=== Reporte del Empleado: " + getNombre() + " ===");
        reporte.visualizar();
    }

    public int getIdEmpleado() { return idEmpleado; }

    public String getCargo() { return cargo; }

    public void setCargo(String cargo) {
        if (cargo == null || cargo.trim().isEmpty()) {
            throw new IllegalArgumentException("El cargo no puede estar vacío.");
        }
        this.cargo = cargo;
    }

    public Date getFechaIngreso() { return fechaIngreso; }

    public void setFechaIngreso(Date fechaIngreso) {
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso no puede ser nula.");
        }
        this.fechaIngreso = fechaIngreso;
    }

    public double getSalario() { return salario; }

    public void setSalario(double salario) {
        if (salario < 0) {
            throw new IllegalArgumentException("El salario no puede ser negativo.");
        }
        this.salario = salario;
    }

    @Override
    public String toString() {
        return obtenerDatos();
    }
}
