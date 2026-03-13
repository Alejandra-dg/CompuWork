package compuwork;

import java.util.ArrayList;
import java.util.List;

public class Administrador extends Usuario {

    private int nivelAcceso;
    private String contrasena;
    private List<Departamento> departamentos;

    public Administrador(int idUsuario, String nombre, String contrasena, int nivelAcceso) {
        super(idUsuario, nombre);
        if (nivelAcceso < 1 || nivelAcceso > 5) {
            throw new IllegalArgumentException("El nivel de acceso debe estar entre 1 y 5.");
        }
        if (contrasena == null || contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.nivelAcceso = nivelAcceso;
        this.contrasena = contrasena;
        this.departamentos = new ArrayList<>();
    }

    @Override
    public boolean iniciarSesion(String contrasena) {
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        boolean acceso = this.contrasena.equals(contrasena);
        System.out.println(acceso
            ? "Administrador " + getNombre() + " ha iniciado sesión correctamente."
            : "Credenciales incorrectas para " + getNombre());
        return acceso;
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Administrador " + getNombre() + " ha cerrado sesión.");
    }

    public void crearEmpleado(Empleado empleado, int idDepartamento) {
        if (empleado == null) {
            throw new IllegalArgumentException("El empleado no puede ser nulo.");
        }
        Departamento depto = buscarDepartamento(idDepartamento);
        depto.agregarEmpleado(empleado);
        System.out.println("Empleado " + empleado.getNombre() + " creado y asignado al departamento ID " + idDepartamento);
    }

    public void eliminarEmpleado(int idEmpleado, int idDepartamento) {
        Departamento depto = buscarDepartamento(idDepartamento);
        depto.eliminarEmpleado(idEmpleado);
        System.out.println("Empleado ID " + idEmpleado + " eliminado del departamento ID " + idDepartamento);
    }

    public void asignarEmpleadoADepartamento(int idEmpleado, int idDeptoOrigen, int idDeptoDestino) {
        Departamento origen = buscarDepartamento(idDeptoOrigen);
        Departamento destino = buscarDepartamento(idDeptoDestino);
        Empleado empleado = origen.buscarEmpleado(idEmpleado);
        origen.eliminarEmpleado(idEmpleado);
        destino.agregarEmpleado(empleado);
        System.out.println("Empleado " + empleado.getNombre() + " trasladado de '"
            + origen.getNombre() + "' a '" + destino.getNombre() + "'");
    }

    public void gestionarDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("El departamento no puede ser nulo.");
        }

        for (Departamento d : departamentos) {
            if (d.getIdDepto() == departamento.getIdDepto()) {
                throw new IllegalStateException("Ya existe un departamento con ese ID.");
            }
        }
        departamentos.add(departamento);
        System.out.println("Departamento '" + departamento.getNombre() + "' creado exitosamente.");
    }

    public void modificarDepartamento(int idDepartamento, String nuevoNombre, double nuevoPresupuesto) {
        Departamento depto = buscarDepartamento(idDepartamento);
        depto.setNombre(nuevoNombre);
        depto.setPresupuesto(nuevoPresupuesto);
        System.out.println("Departamento ID " + idDepartamento + " actualizado.");
    }

    public void eliminarDepartamento(int idDepartamento) {
        Departamento depto = buscarDepartamento(idDepartamento);
        if (!depto.listarEmpleados().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar un departamento con empleados asignados.");
        }
        departamentos.remove(depto);
        System.out.println("Departamento '" + depto.getNombre() + "' eliminado.");
    }

    public void visualizarEmpleadosPorDepartamento(int idDepartamento) {
        Departamento depto = buscarDepartamento(idDepartamento);
        System.out.println("\n=== Empleados en: " + depto.getNombre() + " ===");
        List<Empleado> lista = depto.listarEmpleados();
        if (lista.isEmpty()) {
            System.out.println("Sin empleados asignados.");
        } else {
            for (Empleado e : lista) {
                System.out.println("  - " + e.obtenerDatos());
            }
        }
    }

    public List<Departamento> listarDepartamentos() {
        return new ArrayList<>(departamentos);
    }

    private Departamento buscarDepartamento(int idDepartamento) {
        for (Departamento d : departamentos) {
            if (d.getIdDepto() == idDepartamento) {
                return d;
            }
        }
        throw new IllegalArgumentException("Departamento con ID " + idDepartamento + " no encontrado.");
    }

    public int getNivelAcceso() { return nivelAcceso; }

    public void setNivelAcceso(int nivelAcceso) {
        if (nivelAcceso < 1 || nivelAcceso > 5) {
            throw new IllegalArgumentException("El nivel de acceso debe estar entre 1 y 5.");
        }
        this.nivelAcceso = nivelAcceso;
    }

    @Override
    public String toString() {
        return "Administrador [ID=" + getIdUsuario() + ", Nombre=" + getNombre() +
               ", NivelAcceso=" + nivelAcceso + "]";
    }
}
