package compuwork;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmpleadoPermanente extends Empleado {

    private List<String> beneficios;
    private Date fechaVacaciones;

    public EmpleadoPermanente(int idUsuario, String nombre, String contrasena,
                               int idEmpleado, String cargo, Date fechaIngreso,
                               double salario, Date fechaVacaciones) {
        super(idUsuario, nombre, contrasena, idEmpleado, cargo, fechaIngreso, salario);
        if (fechaVacaciones == null) {
            throw new IllegalArgumentException("La fecha de vacaciones no puede ser nula.");
        }
        this.fechaVacaciones = fechaVacaciones;
        this.beneficios = new ArrayList<>();
    }

    public double calcularBonos() {
        double bono = getSalario() * 0.10;
        System.out.println("Bono calculado para " + getNombre() + ": $" + bono);
        return bono;
    }

    public void agregarBeneficio(String beneficio) {
        if (beneficio == null || beneficio.trim().isEmpty()) {
            throw new IllegalArgumentException("El beneficio no puede estar vacío.");
        }
        beneficios.add(beneficio);
        System.out.println("Beneficio '" + beneficio + "' agregado a " + getNombre());
    }

    public void eliminarBeneficio(String beneficio) {
        if (!beneficios.remove(beneficio)) {
            throw new IllegalArgumentException("El beneficio '" + beneficio + "' no existe.");
        }
        System.out.println("Beneficio '" + beneficio + "' eliminado de " + getNombre());
    }

    @Override
    public String obtenerDatos() {
        return super.obtenerDatos() +
               "\n  Tipo: Permanente | Vacaciones: " + fechaVacaciones +
               " | Beneficios: " + beneficios;
    }

    public List<String> getBeneficios() { return new ArrayList<>(beneficios); }

    public Date getFechaVacaciones() { return fechaVacaciones; }

    public void setFechaVacaciones(Date fechaVacaciones) {
        if (fechaVacaciones == null) {
            throw new IllegalArgumentException("La fecha de vacaciones no puede ser nula.");
        }
        this.fechaVacaciones = fechaVacaciones;
    }
}
