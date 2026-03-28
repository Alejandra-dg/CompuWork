package compuwork.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import compuwork.util.Validaciones;

public class Departamento {

    private int idDepto;
    private String nombre;
    private double presupuesto;
    private List<Empleado> empleados;

    public Departamento(int idDepto, String nombre, double presupuesto) {
        Validaciones.validarTexto(nombre, "El nombre del departamento no puede estar vacío.");
        Validaciones.validarNoNegativo(presupuesto, "El presupuesto no puede ser negativo.");
        this.idDepto = idDepto;
        this.nombre = nombre;
        this.presupuesto = presupuesto;
        this.empleados = new ArrayList<>();
    }

    public void agregarEmpleado(Empleado empleado) {
        Validaciones.validarObjeto(empleado, "El empleado no puede ser nulo.");
        for (Empleado e : empleados) {
            if (e.getIdEmpleado() == empleado.getIdEmpleado()) {
                throw new IllegalStateException("El empleado ya está asignado a este departamento.");
            }
        }
        empleados.add(empleado);
    }

    public void eliminarEmpleado(int idEmpleado) {
        Empleado encontrado = null;
        for (Empleado e : empleados) {
            if (e.getIdEmpleado() == idEmpleado) {
                encontrado = e;
                break;
            }
        }
        if (encontrado == null) {
            throw new IllegalArgumentException("Empleado con ID " + idEmpleado + " no encontrado en el departamento.");
        }
        empleados.remove(encontrado);
    }

    public List<Empleado> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }

    public String obtenerMetricas() {
        if (empleados.isEmpty()) {
            return "Departamento " + nombre + ": Sin empleados asignados.";
        }
        double totalSalarios = 0;
        for (Empleado e : empleados) {
            totalSalarios += e.getSalario();
        }
        double promedioSalario = totalSalarios / empleados.size();
        return "Departamento: " + nombre +
               " | Total Empleados: " + empleados.size() +
               " | Salario Promedio: $" + String.format("%.2f", promedioSalario) +
               " | Presupuesto: $" + String.format("%.2f", presupuesto);
    }

    public Empleado buscarEmpleado(int idEmpleado) {
        for (Empleado e : empleados) {
            if (e.getIdEmpleado() == idEmpleado) {
                return e;
            }
        }
        throw new IllegalArgumentException("Empleado con ID " + idEmpleado + " no encontrado.");
    }

    public int getIdDepto() {
        return idDepto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        Validaciones.validarTexto(nombre, "El nombre del departamento no puede estar vacío.");
        this.nombre = nombre;
    }

    public double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(double presupuesto) {
        Validaciones.validarNoNegativo(presupuesto, "El presupuesto no puede ser negativo.");
        this.presupuesto = presupuesto;
    }

    @Override
    public String toString() {
        return "Departamento [ID=" + idDepto + ", Nombre=" + nombre +
               ", Presupuesto=" + presupuesto + ", Empleados=" + empleados.size() + "]";
    }
}
