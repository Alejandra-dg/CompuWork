package compuwork.prueba;

import java.util.Calendar;
import java.util.Date;
import compuwork.modelo.Departamento;
import compuwork.modelo.EmpleadoPermanente;
import compuwork.modelo.EmpleadoTemporal;
import compuwork.modelo.ReporteDesempenio;
import compuwork.servicio.Administrador;
import compuwork.servicio.Empresa;
import compuwork.servicio.Supervisor;
import compuwork.util.ExportadorReporte;

public class PruebasSistema {

    public static void main(String[] args) {
        testCrearDepartamento();
        testCrearEmpleadoYAsignacion();
        testGenerarReporte();
        System.out.println("\nTodas las pruebas de sistema han finalizado.");
    }

    private static void testCrearDepartamento() {
        Empresa empresa = new Empresa("Prueba");
        Administrador admin = new Administrador(1, "Admin", "admin123", 5, empresa);
        Departamento depto = new Departamento(1, "TI", 10000.0);
        admin.gestionarDepartamento(depto);
        assert empresa.listarDepartamentos().size() == 1 : "No se creó el departamento";
        System.out.println("Prueba crear departamento: OK");
    }

    private static void testCrearEmpleadoYAsignacion() {
        Empresa empresa = new Empresa("Prueba");
        Administrador admin = new Administrador(1, "Admin", "admin123", 5, empresa);
        Departamento depto = new Departamento(1, "TI", 10000.0);
        admin.gestionarDepartamento(depto);

        Calendar cal = Calendar.getInstance();
        cal.set(2023, Calendar.JANUARY, 1);
        Date fechaIngreso = cal.getTime();

        EmpleadoPermanente empleado = new EmpleadoPermanente(2, "Ana", "pass123",
                101, "Desarrolladora", fechaIngreso, 3500.0, fechaIngreso);
        admin.crearEmpleado(empleado, 1);

        assert empresa.buscarDepartamento(1).getEmpleados().size() == 1 : "El empleado no se asignó correctamente";
        System.out.println("Prueba crear empleado y asignar: OK");
    }

    private static void testGenerarReporte() {
        Empresa empresa = new Empresa("Prueba");
        Administrador admin = new Administrador(1, "Admin", "admin123", 5, empresa);
        Departamento depto = new Departamento(1, "TI", 10000.0);
        admin.gestionarDepartamento(depto);

        Calendar cal = Calendar.getInstance();
        cal.set(2020, Calendar.JANUARY, 1);
        Date fechaIngreso = cal.getTime();
        Date fechaVacaciones = cal.getTime();

        EmpleadoPermanente empleado = new EmpleadoPermanente(2, "Ana", "pass123",
                101, "Desarrolladora", fechaIngreso, 3500.0, fechaVacaciones);
        admin.crearEmpleado(empleado, 1);

        Supervisor supervisor = new Supervisor(3, "Luis", "sup123", depto);
        ReporteDesempenio reporte = supervisor.generarReporteIndividual(101);
        assert reporte.getMetricas().contains("Empleado: Ana") : "Reporte individual no contiene datos correctos";
        ExportadorReporte.exportar(reporte);
        System.out.println("Prueba generar reporte y exportar: OK");
    }
}
