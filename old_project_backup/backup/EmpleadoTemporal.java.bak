package compuwork;

import java.util.Date;

public class EmpleadoTemporal extends Empleado {

    private Date fechaFin;
    private String agencia;

    public EmpleadoTemporal(int idUsuario, String nombre, String contrasena,
                             int idEmpleado, String cargo, Date fechaIngreso,
                             double salario, Date fechaFin, String agencia) {
        super(idUsuario, nombre, contrasena, idEmpleado, cargo, fechaIngreso, salario);
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin de contrato no puede ser nula.");
        }
        if (fechaFin.before(fechaIngreso)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de ingreso.");
        }
        if (agencia == null || agencia.trim().isEmpty()) {
            throw new IllegalArgumentException("La agencia no puede estar vacía.");
        }
        this.fechaFin = fechaFin;
        this.agencia = agencia;
    }

    public boolean renovarContrato(Date nuevaFechaFin) {
        if (nuevaFechaFin == null) {
            throw new IllegalArgumentException("La nueva fecha de fin no puede ser nula.");
        }
        if (nuevaFechaFin.before(new Date())) {
            System.out.println("Advertencia: La nueva fecha de fin ya pasó.");
            return false;
        }
        this.fechaFin = nuevaFechaFin;
        System.out.println("Contrato de " + getNombre() + " renovado hasta: " + nuevaFechaFin);
        return true;
    }

    public boolean estaVigente() {
        return fechaFin.after(new Date());
    }

    @Override
    public String obtenerDatos() {
        return super.obtenerDatos() +
               "\n  Tipo: Temporal | Agencia: " + agencia +
               " | Fecha Fin: " + fechaFin +
               " | Vigente: " + (estaVigente() ? "Sí" : "No");
    }

    public Date getFechaFin() { return fechaFin; }

    public void setFechaFin(Date fechaFin) {
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin no puede ser nula.");
        }
        this.fechaFin = fechaFin;
    }

    public String getAgencia() { return agencia; }

    public void setAgencia(String agencia) {
        if (agencia == null || agencia.trim().isEmpty()) {
            throw new IllegalArgumentException("La agencia no puede estar vacía.");
        }
        this.agencia = agencia;
    }
}
