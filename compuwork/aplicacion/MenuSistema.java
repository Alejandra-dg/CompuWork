package compuwork.aplicacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import compuwork.modelo.Departamento;
import compuwork.modelo.Empleado;
import compuwork.modelo.EmpleadoPermanente;
import compuwork.modelo.EmpleadoTemporal;
import compuwork.modelo.ReporteDesempenio;
import compuwork.servicio.Administrador;
import compuwork.servicio.Empresa;
import compuwork.servicio.Supervisor;
import compuwork.util.ExportadorReporte;
import compuwork.util.Validaciones;

public class MenuSistema {

    private Empresa empresa;
    private Administrador administrador;
    private Supervisor supervisor;
    private ReporteDesempenio ultimoReporte;
    private Scanner scanner;
    private SimpleDateFormat formatoFecha;

    public MenuSistema() {
        this.empresa = new Empresa("CompuWork");
        this.administrador = new Administrador(1, "Carlos García", "admin123", 5, empresa);
        this.scanner = new Scanner(System.in);
        this.formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    }

    public void iniciar() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       SISTEMA COMPUWORK v2.0         ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        while (true) {
            mostrarMenuPrincipal();
            int opcion = capturarEntero("Seleccione una opción: ");
            switch (opcion) {
                case 1:
                    menuAdministrador();
                    break;
                case 2:
                    menuSupervisor();
                    break;
                case 3:
                    menuEmpleado();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema. ¡Hasta luego!");
                    return;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Administrador");
        System.out.println("2. Supervisor");
        System.out.println("3. Empleado");
        System.out.println("0. Salir");
    }

    private void menuAdministrador() {
        System.out.println("\n--- LOGIN ADMINISTRADOR ---");
        String contrasena = capturarTexto("Ingrese su contraseña: ");
        if (!administrador.iniciarSesion(contrasena)) {
            System.out.println("Acceso denegado.");
            return;
        }

        while (true) {
            mostrarMenuAdministrador();
            int opcion = capturarEntero("Seleccione una acción: ");
            try {
                switch (opcion) {
                    case 1:
                        crearDepartamento();
                        break;
                    case 2:
                        modificarDepartamento();
                        break;
                    case 3:
                        eliminarDepartamento();
                        break;
                    case 4:
                        crearEmpleadoPermanente();
                        break;
                    case 5:
                        crearEmpleadoTemporal();
                        break;
                    case 6:
                        eliminarEmpleado();
                        break;
                    case 7:
                        trasladarEmpleado();
                        break;
                    case 8:
                        visualizarEmpleadosPorDepartamento();
                        break;
                    case 9:
                        crearSupervisor();
                        break;
                    case 0:
                        administrador.cerrarSesion();
                        return;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void mostrarMenuAdministrador() {
        System.out.println("\n--- MENÚ ADMINISTRADOR ---");
        System.out.println("1. Crear departamento");
        System.out.println("2. Modificar departamento");
        System.out.println("3. Eliminar departamento");
        System.out.println("4. Crear empleado permanente");
        System.out.println("5. Crear empleado temporal");
        System.out.println("6. Eliminar empleado");
        System.out.println("7. Trasladar empleado");
        System.out.println("8. Visualizar empleados por departamento");
        System.out.println("9. Crear supervisor");
        System.out.println("0. Cerrar sesión");
    }

    private void menuSupervisor() {
        if (supervisor == null) {
            System.out.println("No hay supervisor asignado. Cree uno desde el menú del administrador.");
            return;
        }
        System.out.println("\n--- LOGIN SUPERVISOR ---");
        String contrasena = capturarTexto("Ingrese su contraseña: ");
        if (!supervisor.iniciarSesion(contrasena)) {
            System.out.println("Acceso denegado.");
            return;
        }

        while (true) {
            mostrarMenuSupervisor();
            int opcion = capturarEntero("Seleccione una acción: ");
            try {
                switch (opcion) {
                    case 1:
                        supervisor.visualizarEquipo();
                        break;
                    case 2:
                        generarReporteIndividual();
                        break;
                    case 3:
                        generarReporteDepartamento();
                        break;
                    case 4:
                        aprobarReporte();
                        break;
                    case 0:
                        supervisor.cerrarSesion();
                        return;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void mostrarMenuSupervisor() {
        System.out.println("\n--- MENÚ SUPERVISOR ---");
        System.out.println("1. Visualizar equipo");
        System.out.println("2. Generar reporte individual");
        System.out.println("3. Generar reporte de departamento");
        System.out.println("4. Aprobar reporte");
        System.out.println("0. Cerrar sesión");
    }

    private void menuEmpleado() {
        System.out.println("\n--- LOGIN EMPLEADO ---");
        int idEmpleado = capturarEntero("Ingrese su ID de empleado: ");
        Empleado empleado;
        try {
            empleado = empresa.buscarEmpleadoPorId(idEmpleado);
        } catch (Exception e) {
            System.out.println("Empleado no encontrado.");
            return;
        }
        String contrasena = capturarTexto("Ingrese su contraseña: ");
        if (!empleado.iniciarSesion(contrasena)) {
            System.out.println("Acceso denegado.");
            return;
        }

        while (true) {
            System.out.println("\n--- MENÚ EMPLEADO ---");
            System.out.println("1. Ver reporte");
            System.out.println("0. Cerrar sesión");
            int opcion = capturarEntero("Seleccione una acción: ");
            if (opcion == 1) {
                if (ultimoReporte == null) {
                    System.out.println("No hay ningún reporte generado aún.");
                } else {
                    empleado.verReporte(ultimoReporte);
                }
            } else if (opcion == 0) {
                empleado.cerrarSesion();
                return;
            } else {
                System.out.println("Opción no válida.");
            }
        }
    }

    private void crearDepartamento() {
        int id = capturarEntero("ID del departamento: ");
        String nombre = capturarTexto("Nombre del departamento: ");
        double presupuesto = capturarDouble("Presupuesto del departamento: ");
        Departamento departamento = new Departamento(id, nombre, presupuesto);
        administrador.gestionarDepartamento(departamento);
    }

    private void modificarDepartamento() {
        int id = capturarEntero("ID del departamento a modificar: ");
        String nombre = capturarTexto("Nuevo nombre: ");
        double presupuesto = capturarDouble("Nuevo presupuesto: ");
        administrador.modificarDepartamento(id, nombre, presupuesto);
    }

    private void eliminarDepartamento() {
        int id = capturarEntero("ID del departamento a eliminar: ");
        administrador.eliminarDepartamento(id);
    }

    private void crearEmpleadoPermanente() throws ParseException {
        int idUsuario = capturarEntero("ID de usuario: ");
        String nombre = capturarTexto("Nombre del empleado: ");
        String contrasena = capturarTexto("Contraseña: ");
        int idEmpleado = capturarEntero("ID de empleado: ");
        String cargo = capturarTexto("Cargo: ");
        Date fechaIngreso = capturarFecha("Fecha de ingreso (dd/MM/yyyy): ");
        double salario = capturarDouble("Salario: ");
        Date fechaVacaciones = capturarFecha("Fecha de vacaciones (dd/MM/yyyy): ");
        EmpleadoPermanente empleado = new EmpleadoPermanente(idUsuario, nombre, contrasena,
                idEmpleado, cargo, fechaIngreso, salario, fechaVacaciones);
        int idDepartamento = capturarEntero("ID del departamento a asignar: ");
        administrador.crearEmpleado(empleado, idDepartamento);
    }

    private void crearEmpleadoTemporal() throws ParseException {
        int idUsuario = capturarEntero("ID de usuario: ");
        String nombre = capturarTexto("Nombre del empleado: ");
        String contrasena = capturarTexto("Contraseña: ");
        int idEmpleado = capturarEntero("ID de empleado: ");
        String cargo = capturarTexto("Cargo: ");
        Date fechaIngreso = capturarFecha("Fecha de ingreso (dd/MM/yyyy): ");
        double salario = capturarDouble("Salario: ");
        Date fechaFin = capturarFecha("Fecha fin de contrato (dd/MM/yyyy): ");
        String agencia = capturarTexto("Agencia: ");
        EmpleadoTemporal empleado = new EmpleadoTemporal(idUsuario, nombre, contrasena,
                idEmpleado, cargo, fechaIngreso, salario, fechaFin, agencia);
        int idDepartamento = capturarEntero("ID del departamento a asignar: ");
        administrador.crearEmpleado(empleado, idDepartamento);
    }

    private void eliminarEmpleado() {
        int idDepto = capturarEntero("ID del departamento del empleado: ");
        int idEmpleado = capturarEntero("ID del empleado a eliminar: ");
        administrador.eliminarEmpleado(idEmpleado, idDepto);
    }

    private void trasladarEmpleado() {
        int idEmpleado = capturarEntero("ID del empleado a trasladar: ");
        int idOrigen = capturarEntero("ID del departamento origen: ");
        int idDestino = capturarEntero("ID del departamento destino: ");
        administrador.trasladarEmpleado(idEmpleado, idOrigen, idDestino);
    }

    private void visualizarEmpleadosPorDepartamento() {
        int idDepto = capturarEntero("ID del departamento: ");
        administrador.visualizarEmpleadosPorDepartamento(idDepto);
    }

    private void crearSupervisor() {
        int idUsuario = capturarEntero("ID de usuario supervisor: ");
        String nombre = capturarTexto("Nombre del supervisor: ");
        String contrasena = capturarTexto("Contraseña: ");
        int idDepartamento = capturarEntero("ID del departamento a supervisar: ");
        Departamento departamento = empresa.buscarDepartamento(idDepartamento);
        supervisor = new Supervisor(idUsuario, nombre, contrasena, departamento);
        System.out.println("Supervisor " + nombre + " creado correctamente.");
    }

    private void generarReporteIndividual() {
        int idEmpleado = capturarEntero("ID del empleado para el reporte: ");
        ultimoReporte = supervisor.generarReporteIndividual(idEmpleado);
        ultimoReporte.visualizar();
    }

    private void generarReporteDepartamento() {
        ultimoReporte = supervisor.generarReporte();
        ultimoReporte.visualizar();
    }

    private void aprobarReporte() {
        if (ultimoReporte == null) {
            System.out.println("No hay ningún reporte generado para aprobar.");
            return;
        }
        boolean aprobado = supervisor.aprobarReporte(ultimoReporte);
        System.out.println("Reporte aprobado: " + aprobado);
        if (aprobado) {
            ExportadorReporte.exportar(ultimoReporte);
        }
    }

    private int capturarEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Ingrese un número entero.");
            }
        }
    }

    private double capturarDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Ingrese un número válido.");
            }
        }
    }

    private String capturarTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private Date capturarFecha(String mensaje) throws ParseException {
        while (true) {
            try {
                System.out.print(mensaje);
                String linea = scanner.nextLine().trim();
                return formatoFecha.parse(linea);
            } catch (ParseException e) {
                System.out.println("Formato de fecha inválido. Use dd/MM/yyyy.");
            }
        }
    }
}
