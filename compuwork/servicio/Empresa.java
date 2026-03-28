package compuwork.servicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import compuwork.modelo.Departamento;
import compuwork.modelo.Empleado;
import compuwork.util.Validaciones;

public class Empresa {

    private String nombre;
    private List<Departamento> departamentos;

    public Empresa(String nombre) {
        Validaciones.validarTexto(nombre, "El nombre de la empresa no puede estar vacío.");
        this.nombre = nombre;
        this.departamentos = new ArrayList<>();
    }

    public void agregarDepartamento(Departamento departamento) {
        Validaciones.validarObjeto(departamento, "El departamento no puede ser nulo.");
        for (Departamento d : departamentos) {
            if (d.getIdDepto() == departamento.getIdDepto()) {
                throw new IllegalStateException("Ya existe un departamento con el mismo ID.");
            }
        }
        departamentos.add(departamento);
    }

    public void eliminarDepartamento(int idDepartamento) {
        Departamento departamento = buscarDepartamento(idDepartamento);
        if (!departamento.getEmpleados().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un departamento con empleados asignados.");
        }
        departamentos.remove(departamento);
    }

    public void agregarEmpleado(int idDepartamento, Empleado empleado) {
        Departamento departamento = buscarDepartamento(idDepartamento);
        departamento.agregarEmpleado(empleado);
    }

    public void eliminarEmpleado(int idDepartamento, int idEmpleado) {
        Departamento departamento = buscarDepartamento(idDepartamento);
        departamento.eliminarEmpleado(idEmpleado);
    }

    public void trasladarEmpleado(int idEmpleado, int idDeptoOrigen, int idDeptoDestino) {
        Departamento origen = buscarDepartamento(idDeptoOrigen);
        Departamento destino = buscarDepartamento(idDeptoDestino);
        Empleado empleado = origen.buscarEmpleado(idEmpleado);
        origen.eliminarEmpleado(idEmpleado);
        destino.agregarEmpleado(empleado);
    }

    public List<Departamento> listarDepartamentos() {
        return Collections.unmodifiableList(departamentos);
    }

    public Departamento buscarDepartamento(int idDepartamento) {
        for (Departamento d : departamentos) {
            if (d.getIdDepto() == idDepartamento) {
                return d;
            }
        }
        throw new IllegalArgumentException("Departamento con ID " + idDepartamento + " no encontrado.");
    }

    public boolean existeEmpleado(int idEmpleado) {
        for (Departamento d : departamentos) {
            for (Empleado e : d.getEmpleados()) {
                if (e.getIdEmpleado() == idEmpleado) {
                    return true;
                }
            }
        }
        return false;
    }

    public Empleado buscarEmpleadoPorId(int idEmpleado) {
        for (Departamento d : departamentos) {
            for (Empleado e : d.getEmpleados()) {
                if (e.getIdEmpleado() == idEmpleado) {
                    return e;
                }
            }
        }
        throw new IllegalArgumentException("Empleado con ID " + idEmpleado + " no encontrado.");
    }

    public Departamento buscarDepartamentoPorEmpleado(int idEmpleado) {
        for (Departamento d : departamentos) {
            for (Empleado e : d.getEmpleados()) {
                if (e.getIdEmpleado() == idEmpleado) {
                    return d;
                }
            }
        }
        throw new IllegalArgumentException("Departamento para el empleado ID " + idEmpleado + " no encontrado.");
    }

    public String getNombre() {
        return nombre;
    }
}
