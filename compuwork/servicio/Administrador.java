package compuwork.servicio;

import java.util.List;
import compuwork.modelo.Departamento;
import compuwork.modelo.Empleado;
import compuwork.modelo.Usuario;
import compuwork.util.Validaciones;

public class Administrador extends Usuario {

    private int nivelAcceso;
    private Empresa empresa;

    public Administrador(int idUsuario, String nombre, String contrasena,
                         int nivelAcceso, Empresa empresa) {
        super(idUsuario, nombre, contrasena);
        Validaciones.validarObjeto(empresa, "La empresa no puede ser nula.");
        if (nivelAcceso < 1 || nivelAcceso > 5) {
            throw new IllegalArgumentException("El nivel de acceso debe estar entre 1 y 5.");
        }
        this.nivelAcceso = nivelAcceso;
        this.empresa = empresa;
    }

    @Override
    public boolean iniciarSesion(String contrasena) {
        boolean acceso = autenticar(contrasena);
        System.out.println(acceso
            ? "Administrador " + getNombre() + " ha iniciado sesión correctamente."
            : "Credenciales incorrectas para administrador " + getNombre());
        return acceso;
    }

    @Override
    public void cerrarSesion() {
        System.out.println("Administrador " + getNombre() + " ha cerrado sesión.");
    }

    public void crearEmpleado(Empleado empleado, int idDepartamento) {
        Validaciones.validarObjeto(empleado, "El empleado no puede ser nulo.");
        empresa.agregarEmpleado(idDepartamento, empleado);
        System.out.println("Empleado " + empleado.getNombre() + " creado y asignado al departamento ID " + idDepartamento);
    }

    public void eliminarEmpleado(int idEmpleado, int idDepartamento) {
        empresa.eliminarEmpleado(idDepartamento, idEmpleado);
        System.out.println("Empleado ID " + idEmpleado + " eliminado del departamento ID " + idDepartamento);
    }

    public void trasladarEmpleado(int idEmpleado, int idDeptoOrigen, int idDeptoDestino) {
        empresa.trasladarEmpleado(idEmpleado, idDeptoOrigen, idDeptoDestino);
        System.out.println("Empleado ID " + idEmpleado + " trasldado de departamento " + idDeptoOrigen + " a " + idDeptoDestino);
    }

    public void gestionarDepartamento(Departamento departamento) {
        Validaciones.validarObjeto(departamento, "El departamento no puede ser nulo.");
        empresa.agregarDepartamento(departamento);
        System.out.println("Departamento '" + departamento.getNombre() + "' creado exitosamente.");
    }

    public void modificarDepartamento(int idDepartamento, String nuevoNombre, double nuevoPresupuesto) {
        Departamento departamento = empresa.buscarDepartamento(idDepartamento);
        departamento.setNombre(nuevoNombre);
        departamento.setPresupuesto(nuevoPresupuesto);
        System.out.println("Departamento ID " + idDepartamento + " actualizado.");
    }

    public void eliminarDepartamento(int idDepartamento) {
        empresa.eliminarDepartamento(idDepartamento);
        System.out.println("Departamento ID " + idDepartamento + " eliminado.");
    }

    public void visualizarEmpleadosPorDepartamento(int idDepartamento) {
        Departamento departamento = empresa.buscarDepartamento(idDepartamento);
        System.out.println("\n=== Empleados en: " + departamento.getNombre() + " ===");
        List<Empleado> lista = departamento.getEmpleados();
        if (lista.isEmpty()) {
            System.out.println("Sin empleados asignados.");
        } else {
            for (Empleado e : lista) {
                System.out.println("  - " + e.obtenerDatos());
            }
        }
    }

    public List<Departamento> listarDepartamentos() {
        return empresa.listarDepartamentos();
    }

    public int getNivelAcceso() {
        return nivelAcceso;
    }

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
