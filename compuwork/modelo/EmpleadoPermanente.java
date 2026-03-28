package compuwork.modelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import compuwork.util.Validaciones;

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
        this.fechaVacaciones = new Date(fechaVacaciones.getTime());
        this.beneficios = new ArrayList<>();
    }

    public double calcularBonos() {
        return getSalario() * 0.10;
    }

    public void agregarBeneficio(String beneficio) {
        Validaciones.validarTexto(beneficio, "El beneficio no puede estar vacío.");
        beneficios.add(beneficio);
    }

    public void eliminarBeneficio(String beneficio) {
        if (!beneficios.remove(beneficio)) {
            throw new IllegalArgumentException("El beneficio '" + beneficio + "' no existe.");
        }
    }

    @Override
    public String obtenerDatos() {
        return super.obtenerDatos() +
               "\n  Tipo: Permanente | Vacaciones: " + fechaVacaciones +
               " | Beneficios: " + beneficios;
    }

    public List<String> getBeneficios() {
        return new ArrayList<>(beneficios);
    }

    public Date getFechaVacaciones() {
        return new Date(fechaVacaciones.getTime());
    }

    public void setFechaVacaciones(Date fechaVacaciones) {
        if (fechaVacaciones == null) {
            throw new IllegalArgumentException("La fecha de vacaciones no puede ser nula.");
        }
        this.fechaVacaciones = new Date(fechaVacaciones.getTime());
    }
}
