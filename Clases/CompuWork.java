package compuwork;

import java.util.Calendar;
import java.util.Date;

public class CompuWork {

    public static void main(String[] args) {

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║       SISTEMA COMPUWORK v1.0         ║");
        System.out.println("╚══════════════════════════════════════╝\n");

        System.out.println("── 1. AUTENTICACIÓN ──────────────────");
        Administrador admin = new Administrador(1, "Carlos García", "admin123", 5);
        boolean loginAdmin = admin.iniciarSesion("admin123");
        System.out.println("Login exitoso: " + loginAdmin + "\n");

        System.out.println("── 2. GESTIÓN DE DEPARTAMENTOS ────────");
        Departamento deptoTI   = new Departamento(101, "Tecnología", 150000.0);
        Departamento deptoRRHH = new Departamento(102, "Recursos Humanos", 80000.0);

        admin.gestionarDepartamento(deptoTI);
        admin.gestionarDepartamento(deptoRRHH);

        admin.modificarDepartamento(101, "Tecnología e Innovación", 160000.0);
        System.out.println();

        System.out.println("── 3. CREACIÓN DE EMPLEADOS ───────────");

        Calendar cal = Calendar.getInstance();

        cal.set(2020, Calendar.MARCH, 15);
        Date fechaIngreso1 = cal.getTime();

        cal.set(2022, Calendar.JUNE, 1);
        Date fechaIngreso2 = cal.getTime();

        cal.set(2023, Calendar.JANUARY, 10);
        Date fechaIngreso3 = cal.getTime();
        cal.set(2026, Calendar.DECEMBER, 31);
        Date finContrato = cal.getTime();

        EmpleadoPermanente empleado1 = new EmpleadoPermanente(
            10, "Ana Martínez", "ana123",
            1001, "Desarrolladora Senior", fechaIngreso1,
            5500.0, new Date()
        );
        empleado1.agregarBeneficio("Seguro médico");
        empleado1.agregarBeneficio("Bono anual");

        EmpleadoPermanente luis = new EmpleadoPermanente(
            11, "Luis Pérez", "luis123",
            1002, "Analista de Datos", fechaIngreso2,
            4200.0, new Date()
        );
        luis.agregarBeneficio("Seguro de vida");

        EmpleadoTemporal temporal = new EmpleadoTemporal(
            12, "María López", "maria123",
            1003, "Diseñadora UX", fechaIngreso3,
            3000.0, finContrato, "AgenciaTech"
        );

        admin.crearEmpleado(empleado1, 101);
        admin.crearEmpleado(luis,     101);
        admin.crearEmpleado(temporal, 102);
        System.out.println();

        System.out.println("── 4. VISUALIZACIÓN POR DEPARTAMENTO ─");
        admin.visualizarEmpleadosPorDepartamento(101);
        admin.visualizarEmpleadosPorDepartamento(102);
        System.out.println();

        System.out.println("── 5. TRASLADO DE EMPLEADO ────────────");
        admin.asignarEmpleadoADepartamento(1003, 102, 101);
        System.out.println();

        System.out.println("── 6. SUPERVISOR Y REPORTES ───────────");
        Supervisor supervisor = new Supervisor(20, "Pedro Ruiz", "sup123", deptoTI);
        supervisor.iniciarSesion("sup123");
        supervisor.visualizarEquipo();
        System.out.println();

        ReporteDesempenio reporteEmpleado = supervisor.generarReporteIndividual(1001);
        reporteEmpleado.visualizar();

        ReporteDesempenio reporteDepto = supervisor.generarReporte();
        boolean aprobado = supervisor.aprobarReporte(reporteDepto);
        System.out.println("Reporte aprobado: " + aprobado);
        System.out.println();

        System.out.println("── 7. EXPORTAR REPORTE ────────────────");
        reporteEmpleado.exportar();
        System.out.println();

        System.out.println("── 8. EMPLEADO VE SU REPORTE ──────────");
        empleado1.iniciarSesion("ana123");
        empleado1.verReporte(reporteEmpleado);

        System.out.println("\n── 9. MANEJO DE EXCEPCIONES ───────────");

        try {
            admin.eliminarDepartamento(101);
        } catch (IllegalStateException e) {
            System.out.println("Excepción controlada: " + e.getMessage());
        }

        try {
            boolean loginFallido = supervisor.iniciarSesion("claveErronea");
            System.out.println("Login fallido retornó: " + loginFallido);
        } catch (Exception e) {
            System.out.println("Excepción: " + e.getMessage());
        }

        try {
            cal.set(2020, Calendar.JANUARY, 1);
            boolean renovado = temporal.renovarContrato(cal.getTime());
            System.out.println("Renovación con fecha pasada retornó: " + renovado);
        } catch (Exception e) {
            System.out.println("Excepción: " + e.getMessage());
        }

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║     SISTEMA EJECUTADO CORRECTAMENTE  ║");
        System.out.println("╚══════════════════════════════════════╝");
    }
}
