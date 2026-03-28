package compuwork.modelo;

import java.util.Date;
import compuwork.util.Validaciones;

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
        if (!fechaFin.after(fechaIngreso)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la fecha de ingreso.");
        }
        Validaciones.validarTexto(agencia, "La agencia no puede estar vacía.");
        this.fechaFin = new Date(fechaFin.getTime());
        this.agencia = agencia;
    }

    public boolean renovarContrato(Date nuevaFechaFin) {
        if (nuevaFechaFin == null) {
            throw new IllegalArgumentException("La nueva fecha de fin no puede ser nula.");
        }
        if (!nuevaFechaFin.after(this.fechaFin)) {
            System.out.println("Advertencia: La nueva fecha de fin debe ser posterior a la fecha actual de contrato.");
            return false;
        }
        this.fechaFin = new Date(nuevaFechaFin.getTime());
        System.out.println("Contrato de " + getNombre() + " renovado hasta: " + this.fechaFin);
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

    public Date getFechaFin() {
        return new Date(fechaFin.getTime());
    }

    public void setFechaFin(Date fechaFin) {
        if (fechaFin == null) {
            throw new IllegalArgumentException("La fecha de fin no puede ser nula.");
        }
        this.fechaFin = new Date(fechaFin.getTime());
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        Validaciones.validarTexto(agencia, "La agencia no puede estar vacía.");
        this.agencia = agencia;
    }
}
