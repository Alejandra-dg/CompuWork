package compuwork.aplicacion;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebServidor {

    private final int puerto;
    private final Empresa empresa;
    private final Administrador administrador;
    private Supervisor supervisor;
    private final Map<Integer, ReporteDesempenio> reportes;
    private int siguienteIdReporte;

    public WebServidor(int puerto) {
        this.puerto = puerto;
        this.empresa = new Empresa("CompuWork");
        this.administrador = new Administrador(1, "Carlos García", "admin123", 5, empresa);
        this.reportes = new ConcurrentHashMap<>();
        this.siguienteIdReporte = 1;
    }

    public void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(puerto), 0);
            server.createContext("/", new RootHandler());
            server.createContext("/departamentos", new DepartamentosHandler());
            server.createContext("/departamentos/create", new DepartamentoCrearHandler());
            server.createContext("/departamentos/update", new DepartamentoActualizarHandler());
            server.createContext("/departamentos/delete", new DepartamentoEliminarHandler());
            server.createContext("/empleados", new EmpleadosHandler());
            server.createContext("/empleados/create", new EmpleadoCrearHandler());
            server.createContext("/empleados/update", new EmpleadoActualizarHandler());
            server.createContext("/empleados/delete", new EmpleadoEliminarHandler());
            server.createContext("/empleados/trasladar", new EmpleadoTrasladarHandler());
            server.createContext("/empleado", new EmpleadoDetalleHandler());
            server.createContext("/supervisor", new SupervisorHandler());
            server.createContext("/supervisor/create", new SupervisorCrearHandler());
            server.createContext("/reportes", new ReportesHandler());
            server.createContext("/reportes/generar-individual", new GenerarReporteIndividualHandler());
            server.createContext("/reportes/generar-departamento", new GenerarReporteDepartamentoHandler());
            server.createContext("/reportes/exportar", new ExportarReporteHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Servidor iniciado en http://localhost:" + puerto + "/");
            System.out.println("Abra esta dirección en su navegador para utilizar la aplicación.");
        } catch (IOException e) {
            System.out.println("Error al iniciar el servidor web: " + e.getMessage());
        }
    }

    private String render(String titulo, String contenido) {
        return "<!DOCTYPE html>" +
               "<html lang=\"es\">" +
               "<head>" +
               "<meta charset=\"UTF-8\">" +
               "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
               "<title>CompuWork - " + titulo + "</title>" +
               "<style>body{font-family:Inter,system-ui,Segoe UI,Arial,sans-serif;background:linear-gradient(180deg,#eef2ff 0%,#f8fafc 100%);color:#0f172a;margin:0;padding:0;}" +
               "*{box-sizing:border-box;}" +
               "header{background:#1e293b;color:#f8fafc;padding:24px 28px;border-bottom:1px solid rgba(148,163,184,.24);} " +
               "header h1{font-size:2rem;margin:0 0 .25rem;letter-spacing:.03em;}" +
               "header p{margin:0;color:#cbd5e1;max-width:720px;line-height:1.6;}" +
               "main{padding:24px 28px;max-width:1180px;margin:auto;}" +
               "nav{display:flex;flex-wrap:wrap;gap:12px;margin:20px 0 28px;padding:12px;background:rgba(148,163,184,.12);border-radius:24px;box-shadow:0 12px 24px rgba(15,23,42,.08);}" +
               "nav a{padding:12px 18px;background:rgba(255,255,255,.95);border:1px solid rgba(148,163,184,.35);border-radius:999px;color:#334155;text-decoration:none;font-weight:600;transition:all .25s ease;box-shadow:0 6px 18px rgba(15,23,42,.06);}" +
               "nav a:hover{background:#eef2ff;color:#1d4ed8;border-color:#93c5fd;transform:translateY(-1px);}" +
               "nav a.active{background:#4338ca;color:#fff;border-color:#4338ca;box-shadow:0 12px 24px rgba(67,56,202,.24);} " +
               "section{background:#ffffff;border:1px solid rgba(148,163,184,.16);border-radius:22px;padding:24px;box-shadow:0 24px 48px rgba(15,23,42,.08);margin-bottom:24px;}" +
               "h2{margin-top:0;color:#0f172a;}" +
               "h3{margin-bottom:1rem;color:#334155;}" +
               "table{width:100%;border-collapse:separate;border-spacing:0;margin-top:20px;box-shadow:0 10px 30px rgba(15,23,42,.06);overflow:hidden;border-radius:18px;}" +
               "th,td{padding:16px 18px;text-align:left;}" +
               "th{background:#e2e8f0;color:#0f172a;font-weight:700;}" +
               "tbody tr{background:#fcfcfd;transition:background .2s ease;}" +
               "tbody tr:nth-child(even){background:#f8fafc;}" +
               "tbody tr:hover{background:#e2e8f0;}" +
               "td{border-bottom:1px solid rgba(148,163,184,.22);}" +
               "input,select,textarea{width:100%;padding:14px 16px;margin-top:8px;border:1px solid #cbd5e1;border-radius:14px;background:#f8fafc;color:#0f172a;transition:border-color .2s ease,box-shadow .2s ease;}" +
               "input:focus,select:focus,textarea:focus{outline:none;border-color:#6366f1;box-shadow:0 0 0 4px rgba(99,102,241,.12);} " +
               "button{padding:12px 20px;border:none;border-radius:14px;font-weight:700;cursor:pointer;transition:transform .2s ease,box-shadow .2s ease;}" +
               ".btn-primary{background:#4f46e5;color:#fff;box-shadow:0 12px 24px rgba(79,70,229,.24);} " +
               ".btn-primary:hover{transform:translateY(-1px);box-shadow:0 16px 32px rgba(79,70,229,.24);} " +
               ".btn-secondary{background:#f8fafc;color:#334155;border:1px solid #cbd5e1;} " +
               ".btn-secondary:hover{background:#eef2ff;} " +
               ".btn-danger{background:#ef4444;color:#fff;} " +
               ".btn-danger:hover{background:#dc2626;} " +
               "label{display:block;margin-top:18px;font-size:.95rem;color:#334155;font-weight:600;}" +
               "textarea{min-height:120px;}" +
               "form{display:grid;gap:16px;}" +
               ".form-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:18px;margin-top:16px;}" +
               ".form-card{background:#f8fafc;border:1px solid #e2e8f0;border-radius:18px;padding:18px;box-shadow:inset 0 1px 2px rgba(15,23,42,.06);}" +
               ".form-card h3{margin-top:0;color:#334155;}" +
               ".action-group{display:flex;flex-wrap:wrap;gap:10px;}" +
               ".alert{padding:16px 18px;border-radius:14px;margin-bottom:18px;display:flex;align-items:center;gap:12px;font-weight:600;}" +
               ".alert-success{background:#d1fae5;color:#065f46;border:1px solid #a7f3d0;}" +
               ".alert-error{background:#fee2e2;color:#991b1b;border:1px solid #fecaca;}" +
               ".alert-info{background:#dbeafe;color:#1d4ed8;border:1px solid #bfdbfe;}" +
               ".form-note{display:block;color:#475569;font-size:.9rem;margin-top:-8px;}" +
               ".small-note{font-size:.9rem;color:#64748b;}" +
               "@media(max-width:900px){main{padding:18px 16px;}nav{justify-content:center;}table,thead,tbody,tr,th,td{display:block;}th{position:absolute;top:-9999px;left:-9999px;}td{position:relative;padding-left:50%;border:none;}td:before{position:absolute;top:16px;left:16px;width:45%;white-space:nowrap;font-weight:700;color:#475569;}td:nth-of-type(1):before{content:\"ID\";}td:nth-of-type(2):before{content:\"Nombre\";}td:nth-of-type(3):before{content:\"Cargo\";}td:nth-of-type(4):before{content:\"Departamento\";}td:nth-of-type(5):before{content:\"Tipo\";}td:nth-of-type(6):before{content:\"Acciones\";}}</style>" +
               "</head><body><header><h1>CompuWork</h1><p>Sistema web moderno para gestionar empleados, departamentos y reportes con validaciones y feedback visual.</p></header>" +
               "<main><nav><a href=\"/\">Inicio</a><a href=\"/departamentos\">Departamentos</a><a href=\"/empleados\">Empleados</a><a href=\"/supervisor\">Supervisor</a><a href=\"/reportes\">Reportes</a></nav>" +
               contenido +
               "<script>document.addEventListener('DOMContentLoaded', function(){document.querySelectorAll('input[required],select[required],textarea[required]').forEach(function(el){el.addEventListener('invalid', function(event){event.preventDefault();var label=el.previousElementSibling?el.previousElementSibling.textContent:'Campo';el.setCustomValidity('Complete el campo ' + label.toLowerCase());});el.addEventListener('input', function(){el.setCustomValidity('');});});});</script></main></body></html>";
    }

    private void enviarRespuesta(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private void redirect(HttpExchange exchange, String ruta) throws IOException {
        exchange.getResponseHeaders().set("Location", ruta);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }

    private String urlEncode(String valor) {
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }

    private Map<String, String> parseForm(String raw) {
        Map<String, String> datos = new HashMap<>();
        if (raw == null || raw.isEmpty()) {
            return datos;
        }
        String[] pares = raw.split("&");
        for (String par : pares) {
            String[] partes = par.split("=", 2);
            String clave = urlDecode(partes[0]);
            String valor = partes.length > 1 ? urlDecode(partes[1]) : "";
            datos.put(clave, valor);
        }
        return datos;
    }

    private String urlDecode(String valor) {
        return URLDecoder.decode(valor, StandardCharsets.UTF_8);
    }

    private String formatoFecha(Date fecha) {
        return new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "ES")).format(fecha);
    }

    private String listaDepartamentosHtml() {
        StringBuilder sb = new StringBuilder();
        List<Departamento> departamentos = empresa.listarDepartamentos();
        if (departamentos.isEmpty()) {
            sb.append("<p>No hay departamentos registrados aún.</p>");
            return sb.toString();
        }
        sb.append("<table><thead><tr><th>ID</th><th>Nombre</th><th>Presupuesto</th><th>Empleados</th><th>Acciones</th></tr></thead><tbody>");
        for (Departamento d : departamentos) {
            sb.append("<tr>");
            sb.append("<td>").append(d.getIdDepto()).append("</td>");
            sb.append("<td>").append(d.getNombre()).append("</td>");
            sb.append("<td>$").append(String.format("%.2f", d.getPresupuesto())).append("</td>");
            sb.append("<td>").append(d.getEmpleados().size()).append("</td>");
            sb.append("<td><form method=\"get\" action=\"/departamentos\" style=\"display:inline\"><input type=\"hidden\" name=\"editarId\" value=\"")
              .append(d.getIdDepto()).append("\"><button type=\"submit\">Editar</button></form></td>");
            sb.append("</tr>");
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String listaEmpleadosHtml() {
        StringBuilder sb = new StringBuilder();
        List<Departamento> departamentos = empresa.listarDepartamentos();
        if (departamentos.isEmpty()) {
            sb.append("<p>No hay departamentos. Cree primero un departamento para asignar empleados.</p>");
            return sb.toString();
        }
        sb.append("<table><thead><tr><th>ID</th><th>Nombre</th><th>Cargo</th><th>Departamento</th><th>Tipo</th><th>Acciones</th></tr></thead><tbody>");
        for (Departamento d : departamentos) {
            for (Empleado e : d.getEmpleados()) {
                sb.append("<tr>");
                sb.append("<td>").append(e.getIdEmpleado()).append("</td>");
                sb.append("<td>").append(e.getNombre()).append("</td>");
                sb.append("<td>").append(e.getCargo()).append("</td>");
                sb.append("<td>").append(d.getNombre()).append("</td>");
                sb.append("<td>").append(e instanceof EmpleadoPermanente ? "Permanente" : "Temporal").append("</td>");
                sb.append("<td><form method=\"get\" action=\"/empleado\" style=\"display:inline\"><input type=\"hidden\" name=\"id\" value=\"")
                  .append(e.getIdEmpleado()).append("\"><button type=\"submit\">Editar</button></form></td>");
                sb.append("</tr>");
            }
        }
        sb.append("</tbody></table>");
        return sb.toString();
    }

    private String mensajesHtml(Map<String, String> query) {
        StringBuilder sb = new StringBuilder();
        if (query.containsKey("message")) {
            sb.append("<section class=\"alert alert-success\"><span>✔</span> <strong>Éxito:</strong> ")
              .append(urlDecode(query.get("message"))).append("</section>");
        }
        if (query.containsKey("error")) {
            sb.append("<section class=\"alert alert-error\"><span>⚠</span> <strong>Error:</strong> ")
              .append(urlDecode(query.get("error"))).append("</section>");
        }
        return sb.toString();
    }

    private String formSupervisorHtml() {
        StringBuilder sb = new StringBuilder();
        sb.append("<section><h2>Crear Supervisor</h2>");
        if (supervisor != null) {
            sb.append("<p>Supervisor actual: <strong>").append(supervisor.getNombre()).append("</strong> - Departamento: ")
              .append(supervisor.getDepartamento().getNombre()).append("</p>");
        }
        sb.append("<form method=\"post\" action=\"/supervisor/create\">");
        sb.append("<div class=\"form-grid\"><div class=\"form-card\">");
        sb.append("<h3>Datos del supervisor</h3>");
        sb.append("<label>ID de usuario</label><input name=\"idUsuario\" type=\"number\" min=\"1\" placeholder=\"Ej: 10\" required>");
        sb.append("<label>Nombre</label><input name=\"nombre\" type=\"text\" placeholder=\"Ej: Carla Méndez\" required>");
        sb.append("<label>Contraseña</label><input name=\"contrasena\" type=\"password\" minlength=\"6\" placeholder=\"Mínimo 6 caracteres\" required>");
        sb.append("</div><div class=\"form-card\">");
        sb.append("<h3>Asignación</h3>");
        sb.append("<label>Departamento</label>" + departamentosSeleccionables());
        sb.append("<span class=\"form-note\">Seleccione el departamento al que pertenecerá el supervisor.</span>");
        sb.append("</div></div>");
        sb.append("<div class=\"action-group\"><button class=\"btn-primary\" type=\"submit\">Crear Supervisor</button></div></form></section>");
        return sb.toString();
    }

    private String departamentosSeleccionables() {
        StringBuilder sb = new StringBuilder();
        sb.append("<select name=\"idDepartamento\" required>\n");
        if (empresa.listarDepartamentos().isEmpty()) {
            sb.append("<option value=\"\" disabled selected>No hay departamentos disponibles</option>\n");
        } else {
            sb.append("<option value=\"\" disabled selected>Seleccione un departamento</option>\n");
            for (Departamento d : empresa.listarDepartamentos()) {
                sb.append("<option value=\"").append(d.getIdDepto()).append("\">")
                  .append(d.getNombre()).append(" (#").append(d.getIdDepto()).append(")</option>\n");
            }
        }
        sb.append("</select>");
        return sb.toString();
    }

    private String empleadosSeleccionables() {
        StringBuilder sb = new StringBuilder();
        sb.append("<select name=\"idEmpleado\">\n");
        for (Departamento d : empresa.listarDepartamentos()) {
            for (Empleado e : d.getEmpleados()) {
                sb.append("<option value=\"").append(e.getIdEmpleado()).append("\">")
                  .append(e.getNombre()).append(" (#").append(e.getIdEmpleado()).append(")</option>\n");
            }
        }
        sb.append("</select>");
        return sb.toString();
    }

    private class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            StringBuilder contenido = new StringBuilder();
            contenido.append("<section><h2>Bienvenido a CompuWork</h2>");
            contenido.append("<p>Utilice el menú para administrar departamentos, empleados y reportes desde un navegador web.</p>");
            contenido.append("<p>Las acciones de administración requieren la contraseña del administrador: <strong>admin123</strong>.</p>");
            contenido.append(mensajesHtml(query));
            contenido.append("</section>");
            enviarRespuesta(exchange, render("Inicio", contenido.toString()));
        }
    }

    private class DepartamentosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            StringBuilder contenido = new StringBuilder();
            contenido.append(mensajesHtml(query));
            contenido.append("<section><h2>Departamentos</h2>");
            contenido.append(listaDepartamentosHtml());
            contenido.append("</section>");
            contenido.append("<section><h2>Crear departamento</h2>");
            contenido.append("<form method=\"post\" action=\"/departamentos/create\">");
            contenido.append("<div class=\"form-grid\"><div class=\"form-card\">");
            contenido.append("<h3>Datos del departamento</h3>");
            contenido.append("<label>ID del departamento</label><input type=\"number\" name=\"id\" min=\"1\" placeholder=\"Ej: 1\" required>");
            contenido.append("<label>Nombre del departamento</label><input type=\"text\" name=\"nombre\" placeholder=\"Ej: Sistemas\" required>");
            contenido.append("<label>Presupuesto</label><input type=\"number\" step=\"0.01\" min=\"0.01\" name=\"presupuesto\" placeholder=\"Ej: 150000.00\" required>");
            contenido.append("</div><div class=\"form-card\">");
            contenido.append("<h3>Seguridad</h3>");
            contenido.append("<label>Contraseña del administrador</label><input type=\"password\" name=\"contrasena\" minlength=\"6\" placeholder=\"Mínimo 6 caracteres\" required>");
            contenido.append("<span class=\"form-note\">Validación en servidor para administrador.</span>");
            contenido.append("</div></div>");
            contenido.append("<div class=\"action-group\"><button class=\"btn-primary\" type=\"submit\">Crear departamento</button></div></form></section>");
            if (query.containsKey("editarId")) {
                try {
                    int editarId = Integer.parseInt(query.get("editarId"));
                    Departamento depto = empresa.buscarDepartamento(editarId);
                    contenido.append("<section><h2>Actualizar departamento</h2>");
                    contenido.append("<form method=\"post\" action=\"/departamentos/update\">");
                    contenido.append("<input type=\"hidden\" name=\"id\" value=\"").append(depto.getIdDepto()).append("\">");
                    contenido.append("<div class=\"form-grid\"><div class=\"form-card\">");
                    contenido.append("<h3>Datos del departamento</h3>");
                    contenido.append("<label>Nombre</label><input type=\"text\" name=\"nombre\" value=\"").append(depto.getNombre()).append("\" required>");
                    contenido.append("<label>Presupuesto</label><input type=\"number\" step=\"0.01\" name=\"presupuesto\" value=\"")
                              .append(depto.getPresupuesto()).append("\" required>");
                    contenido.append("</div><div class=\"form-card\">");
                    contenido.append("<h3>Confirmación</h3>");
                    contenido.append("<label>Contraseña del administrador</label><input type=\"password\" name=\"contrasena\" minlength=\"6\" placeholder=\"Mínimo 6 caracteres\" required>");
                    contenido.append("<span class=\"form-note\">La contraseña valida la actualización.</span>");
                    contenido.append("</div></div>");
                    contenido.append("<div class=\"action-group\"><button class=\"btn-primary\" type=\"submit\">Actualizar departamento</button></div></form></section>");
                } catch (Exception ignored) {
                }
            }
            enviarRespuesta(exchange, render("Departamentos", contenido.toString()));
        }
    }

    private class DepartamentoCrearHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/departamentos");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                int id = Integer.parseInt(datos.getOrDefault("id", "0"));
                String nombre = datos.getOrDefault("nombre", "");
                double presupuesto = Double.parseDouble(datos.getOrDefault("presupuesto", "0"));
                String contrasena = datos.getOrDefault("contrasena", "");
                if (!administrador.iniciarSesion(contrasena)) {
                    redirect(exchange, "/departamentos?error=" + urlEncode("Contraseña de administrador incorrecta."));
                    return;
                }
                administrador.gestionarDepartamento(new Departamento(id, nombre, presupuesto));
                redirect(exchange, "/departamentos?message=" + urlEncode("Departamento creado correctamente."));
            } catch (Exception e) {
                redirect(exchange, "/departamentos?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class DepartamentoActualizarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/departamentos");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                int id = Integer.parseInt(datos.getOrDefault("id", "0"));
                String nombre = datos.getOrDefault("nombre", "");
                double presupuesto = Double.parseDouble(datos.getOrDefault("presupuesto", "0"));
                String contrasena = datos.getOrDefault("contrasena", "");
                if (!administrador.iniciarSesion(contrasena)) {
                    redirect(exchange, "/departamentos?error=" + urlEncode("Contraseña de administrador incorrecta."));
                    return;
                }
                administrador.modificarDepartamento(id, nombre, presupuesto);
                redirect(exchange, "/departamentos?message=" + urlEncode("Departamento actualizado correctamente."));
            } catch (Exception e) {
                redirect(exchange, "/departamentos?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class DepartamentoEliminarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/departamentos");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                int id = Integer.parseInt(datos.getOrDefault("id", "0"));
                String adminPass = datos.getOrDefault("contrasena", "");
                if (adminPass.isEmpty() || !administrador.iniciarSesion(adminPass)) {
                    redirect(exchange, "/departamentos?error=" + urlEncode("Para eliminar se requiere la contraseña del administrador."));
                    return;
                }
                administrador.eliminarDepartamento(id);
                redirect(exchange, "/departamentos?message=" + urlEncode("Departamento eliminado correctamente."));
            } catch (Exception e) {
                redirect(exchange, "/departamentos?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class EmpleadosHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            StringBuilder contenido = new StringBuilder();
            contenido.append(mensajesHtml(query));
            contenido.append("<section><h2>Empleados</h2>");
            contenido.append(listaEmpleadosHtml());
            contenido.append("</section>");
            contenido.append("<section><h2>Crear empleado</h2>");
            contenido.append("<form method=\"post\" action=\"/empleados/create\">");
            contenido.append("<div class=\"form-grid\"><div class=\"form-card\">");
            contenido.append("<h3>Datos personales</h3>");
            contenido.append("<label>ID usuario</label><input name=\"idUsuario\" type=\"number\" min=\"1\" placeholder=\"Ej: 5\" required>");
            contenido.append("<label>Nombre</label><input name=\"nombre\" type=\"text\" placeholder=\"Ej: Ana Pérez\" required>");
            contenido.append("<label>Contraseña</label><input name=\"contrasena\" type=\"password\" minlength=\"6\" placeholder=\"Mínimo 6 caracteres\" required>");
            contenido.append("<label>ID empleado</label><input name=\"idEmpleado\" type=\"number\" min=\"1\" placeholder=\"Ej: 1001\" required>");
            contenido.append("</div><div class=\"form-card\">");
            contenido.append("<h3>Detalles laborales</h3>");
            contenido.append("<label>Cargo</label><input name=\"cargo\" type=\"text\" placeholder=\"Ej: Analista de datos\" required>");
            contenido.append("<label>Fecha ingreso (dd/MM/yyyy)</label><input name=\"fechaIngreso\" type=\"text\" pattern=\"\\d{2}/\\d{2}/\\d{4}\" placeholder=\"dd/MM/yyyy\" required>");
            contenido.append("<span class=\"form-note\">Formato de fecha: dd/MM/yyyy</span>");
            contenido.append("<label>Salario</label><input name=\"salario\" type=\"number\" step=\"0.01\" min=\"0\" placeholder=\"Ej: 2500.00\" required>");
            contenido.append("<label>Tipo de empleado</label><select name=\"tipo\"><option value=\"PERMANENTE\">Permanente</option><option value=\"TEMPORAL\">Temporal</option></select>");
            contenido.append("</div><div class=\"form-card\">");
            contenido.append("<h3>Contrato y asignación</h3>");
            contenido.append("<label>Fecha vacaciones (dd/MM/yyyy) - permanente</label><input name=\"fechaVacaciones\" type=\"text\" pattern=\"\\d{2}/\\d{2}/\\d{4}\" placeholder=\"dd/MM/yyyy\">" );
            contenido.append("<label>Fecha fin contrato (dd/MM/yyyy) - temporal</label><input name=\"fechaFin\" type=\"text\" pattern=\"\\d{2}/\\d{2}/\\d{4}\" placeholder=\"dd/MM/yyyy\">" );
            contenido.append("<label>Agencia - temporal</label><input name=\"agencia\" type=\"text\" placeholder=\"Ej: Creative Staffing\">" );
            contenido.append("<label>ID departamento</label>" + departamentosSeleccionables());
            contenido.append("</div></div>");
            contenido.append("<div class=\"form-grid\"><div class=\"form-card\">");
            contenido.append("<h3>Seguridad</h3>");
            contenido.append("<label>Contraseña del administrador</label><input name=\"adminContrasena\" type=\"password\" minlength=\"6\" placeholder=\"Contraseña admin\" required>");
            contenido.append("<span class=\"form-note\">Este dato confirma la creación del empleado.</span>");
            contenido.append("</div></div>");
            contenido.append("<div class=\"action-group\"><button class=\"btn-primary\" type=\"submit\">Crear empleado</button></div></form>");
            contenido.append("</section>");
            enviarRespuesta(exchange, render("Empleados", contenido.toString()));
        }
    }

    private class EmpleadoCrearHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/empleados");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                if (!administrador.iniciarSesion(datos.getOrDefault("adminContrasena", ""))) {
                    redirect(exchange, "/empleados?error=" + urlEncode("Contraseña de administrador incorrecta."));
                    return;
                }
                int idUsuario = Integer.parseInt(datos.getOrDefault("idUsuario", "0"));
                String nombre = datos.getOrDefault("nombre", "");
                String contrasena = datos.getOrDefault("contrasena", "");
                int idEmpleado = Integer.parseInt(datos.getOrDefault("idEmpleado", "0"));
                String cargo = datos.getOrDefault("cargo", "");
                Date fechaIngreso = new SimpleDateFormat("dd/MM/yyyy").parse(datos.getOrDefault("fechaIngreso", ""));
                double salario = Double.parseDouble(datos.getOrDefault("salario", "0"));
                String tipo = datos.getOrDefault("tipo", "PERMANENTE");
                int idDepartamento = Integer.parseInt(datos.getOrDefault("idDepartamento", "0"));
                Empleado empleado;
                if (tipo.equalsIgnoreCase("PERMANENTE")) {
                    Date fechaVacaciones = new SimpleDateFormat("dd/MM/yyyy").parse(datos.getOrDefault("fechaVacaciones", datos.getOrDefault("fechaIngreso", "")));
                    empleado = new EmpleadoPermanente(idUsuario, nombre, contrasena, idEmpleado, cargo, fechaIngreso, salario, fechaVacaciones);
                } else {
                    Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse(datos.getOrDefault("fechaFin", datos.getOrDefault("fechaIngreso", "")));
                    String agencia = datos.getOrDefault("agencia", "Agencia General");
                    empleado = new EmpleadoTemporal(idUsuario, nombre, contrasena, idEmpleado, cargo, fechaIngreso, salario, fechaFin, agencia);
                }
                administrador.crearEmpleado(empleado, idDepartamento);
                redirect(exchange, "/empleados?message=" + urlEncode("Empleado creado correctamente."));
            } catch (ParseException e) {
                redirect(exchange, "/empleados?error=" + urlEncode("Formato de fecha inválido. Use dd/MM/yyyy."));
            } catch (Exception e) {
                redirect(exchange, "/empleados?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class EmpleadoDetalleHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            StringBuilder contenido = new StringBuilder();
            contenido.append(mensajesHtml(query));
            try {
                int id = Integer.parseInt(query.getOrDefault("id", "0"));
                Empleado empleado = empresa.buscarEmpleadoPorId(id);
                Departamento departamento = empresa.buscarDepartamentoPorEmpleado(id);
                contenido.append("<section><h2>Editar empleado</h2>");
                contenido.append("<form method=\"post\" action=\"/empleados/update\">");
                contenido.append("<input type=\"hidden\" name=\"idEmpleado\" value=\"").append(empleado.getIdEmpleado()).append("\">");
                contenido.append("<label>Nombre</label><input name=\"nombre\" type=\"text\" value=\"").append(empleado.getNombre()).append("\" required>");
                contenido.append("<label>Contraseña nueva (dejar vacío para no cambiar)</label><input name=\"contrasena\" type=\"password\">");
                contenido.append("<label>Cargo</label><input name=\"cargo\" type=\"text\" value=\"").append(empleado.getCargo()).append("\" required>");
                contenido.append("<label>Fecha ingreso (dd/MM/yyyy)</label><input name=\"fechaIngreso\" type=\"text\" value=\"")
                  .append(formatoFecha(empleado.getFechaIngreso())).append("\" required>");
                contenido.append("<label>Salario</label><input name=\"salario\" type=\"number\" step=\"0.01\" value=\"")
                  .append(empleado.getSalario()).append("\" required>");
                contenido.append("<label>Departamento destino</label>");
                contenido.append(departamentosSeleccionables());
                if (empleado instanceof EmpleadoPermanente) {
                    EmpleadoPermanente ep = (EmpleadoPermanente) empleado;
                    contenido.append("<label>Fecha vacaciones (dd/MM/yyyy)</label><input name=\"fechaVacaciones\" type=\"text\" value=\"")
                      .append(formatoFecha(ep.getFechaVacaciones())).append("\">" );
                } else if (empleado instanceof EmpleadoTemporal) {
                    EmpleadoTemporal et = (EmpleadoTemporal) empleado;
                    contenido.append("<label>Fecha fin de contrato (dd/MM/yyyy)</label><input name=\"fechaFin\" type=\"text\" value=\"")
                      .append(formatoFecha(et.getFechaFin())).append("\">" );
                    contenido.append("<label>Agencia</label><input name=\"agencia\" type=\"text\" value=\"")
                      .append(et.getAgencia()).append("\">" );
                }
                contenido.append("<label>Contraseña del administrador</label><input name=\"adminContrasena\" type=\"password\" required>");
                contenido.append("<button type=\"submit\">Actualizar empleado</button>");
                contenido.append("</form>");
                contenido.append("</section>");
                contenido.append("<section><h2>Eliminar empleado</h2>");
                contenido.append("<form method=\"post\" action=\"/empleados/delete\">");
                contenido.append("<input type=\"hidden\" name=\"id\" value=\"").append(empleado.getIdEmpleado()).append("\">" );
                contenido.append("<label>Contraseña del administrador</label><input name=\"adminContrasena\" type=\"password\" required>");
                contenido.append("<button type=\"submit\">Eliminar empleado</button></form>");
                contenido.append("</section>");
                enviarRespuesta(exchange, render("Editar Empleado", contenido.toString()));
            } catch (Exception e) {
                redirect(exchange, "/empleados?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class EmpleadoActualizarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/empleados");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                if (!administrador.iniciarSesion(datos.getOrDefault("adminContrasena", ""))) {
                    redirect(exchange, "/empleados?error=" + urlEncode("Contraseña de administrador incorrecta."));
                    return;
                }
                int idEmpleado = Integer.parseInt(datos.getOrDefault("idEmpleado", "0"));
                Empleado empleado = empresa.buscarEmpleadoPorId(idEmpleado);
                Departamento origen = empresa.buscarDepartamentoPorEmpleado(idEmpleado);
                String nombre = datos.getOrDefault("nombre", empleado.getNombre());
                String contrasena = datos.getOrDefault("contrasena", "");
                String cargo = datos.getOrDefault("cargo", empleado.getCargo());
                Date fechaIngreso = new SimpleDateFormat("dd/MM/yyyy").parse(datos.getOrDefault("fechaIngreso", formatoFecha(empleado.getFechaIngreso())));
                double salario = Double.parseDouble(datos.getOrDefault("salario", String.valueOf(empleado.getSalario())));
                int idDepartamento = Integer.parseInt(datos.getOrDefault("idDepartamento", String.valueOf(origen.getIdDepto())));
                empleado.setNombre(nombre);
                if (!contrasena.isEmpty()) {
                    empleado.cambiarContrasena(contrasena);
                }
                empleado.setCargo(cargo);
                empleado.setFechaIngreso(fechaIngreso);
                empleado.setSalario(salario);
                if (empleado instanceof EmpleadoPermanente) {
                    EmpleadoPermanente ep = (EmpleadoPermanente) empleado;
                    if (datos.containsKey("fechaVacaciones") && !datos.get("fechaVacaciones").isEmpty()) {
                        ep.setFechaVacaciones(new SimpleDateFormat("dd/MM/yyyy").parse(datos.get("fechaVacaciones")));
                    }
                } else if (empleado instanceof EmpleadoTemporal) {
                    EmpleadoTemporal et = (EmpleadoTemporal) empleado;
                    if (datos.containsKey("fechaFin") && !datos.get("fechaFin").isEmpty()) {
                        et.setFechaFin(new SimpleDateFormat("dd/MM/yyyy").parse(datos.get("fechaFin")));
                    }
                    if (datos.containsKey("agencia") && !datos.get("agencia").isEmpty()) {
                        et.setAgencia(datos.get("agencia"));
                    }
                }
                if (origen.getIdDepto() != idDepartamento) {
                    administrador.trasladarEmpleado(idEmpleado, origen.getIdDepto(), idDepartamento);
                }
                redirect(exchange, "/empleados?message=" + urlEncode("Empleado actualizado correctamente."));
            } catch (ParseException e) {
                redirect(exchange, "/empleados?error=" + urlEncode("Formato de fecha inválido."));
            } catch (Exception e) {
                redirect(exchange, "/empleados?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class EmpleadoEliminarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/empleados");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                if (!administrador.iniciarSesion(datos.getOrDefault("adminContrasena", ""))) {
                    redirect(exchange, "/empleados?error=" + urlEncode("Contraseña de administrador incorrecta."));
                    return;
                }
                int idEmpleado = Integer.parseInt(datos.getOrDefault("id", "0"));
                Departamento depto = empresa.buscarDepartamentoPorEmpleado(idEmpleado);
                administrador.eliminarEmpleado(idEmpleado, depto.getIdDepto());
                redirect(exchange, "/empleados?message=" + urlEncode("Empleado eliminado correctamente."));
            } catch (Exception e) {
                redirect(exchange, "/empleados?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class EmpleadoTrasladarHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/empleados");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                if (!administrador.iniciarSesion(datos.getOrDefault("adminContrasena", ""))) {
                    redirect(exchange, "/empleados?error=" + urlEncode("Contraseña de administrador incorrecta."));
                    return;
                }
                int idEmpleado = Integer.parseInt(datos.getOrDefault("idEmpleado", "0"));
                int origen = Integer.parseInt(datos.getOrDefault("origen", "0"));
                int destino = Integer.parseInt(datos.getOrDefault("destino", "0"));
                administrador.trasladarEmpleado(idEmpleado, origen, destino);
                redirect(exchange, "/empleados?message=" + urlEncode("Empleado trasladado correctamente."));
            } catch (Exception e) {
                redirect(exchange, "/empleados?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class SupervisorHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            StringBuilder contenido = new StringBuilder();
            contenido.append(mensajesHtml(query));
            contenido.append("<section><h2>Supervisor</h2>");
            if (supervisor == null) {
                contenido.append("<p>No hay supervisor creado. Cree uno con el siguiente formulario.</p>");
            } else {
                contenido.append("<p>Supervisor: <strong>").append(supervisor.getNombre()).append("</strong></p>");
                contenido.append("<p>Departamento: <strong>").append(supervisor.getDepartamento().getNombre()).append("</strong></p>");
            }
            contenido.append(formSupervisorHtml());
            contenido.append("</section>");
            enviarRespuesta(exchange, render("Supervisor", contenido.toString()));
        }
    }

    private class SupervisorCrearHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/supervisor");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String, String> datos = parseForm(raw);
            try {
                int idUsuario = Integer.parseInt(datos.getOrDefault("idUsuario", "0"));
                String nombre = datos.getOrDefault("nombre", "");
                String contrasena = datos.getOrDefault("contrasena", "");
                int idDepartamento = Integer.parseInt(datos.getOrDefault("idDepartamento", "0"));
                Departamento depto = empresa.buscarDepartamento(idDepartamento);
                supervisor = new Supervisor(idUsuario, nombre, contrasena, depto);
                redirect(exchange, "/supervisor?message=" + urlEncode("Supervisor creado correctamente."));
            } catch (Exception e) {
                redirect(exchange, "/supervisor?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class ReportesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            StringBuilder contenido = new StringBuilder();
            contenido.append(mensajesHtml(query));
            contenido.append("<section><h2>Reportes</h2>");
            if (supervisor == null) {
                contenido.append("<p>Para generar reportes primero debe crear un supervisor.</p>");
            }
            contenido.append("<form method=\"post\" action=\"/reportes/generar-individual\">");
            contenido.append("<h3>Generar reporte individual</h3>");
            contenido.append("<label>ID del empleado</label>").append(empleadosSeleccionables());
            contenido.append("<label>Contraseña del supervisor</label><input name=\"contrasena\" type=\"password\" required>");
            contenido.append("<button type=\"submit\">Generar reporte</button></form>");
            contenido.append("<form method=\"post\" action=\"/reportes/generar-departamento\">");
            contenido.append("<h3>Generar reporte de departamento</h3>");
            contenido.append("<label>ID del departamento</label>");
            contenido.append(departamentosSeleccionables());
            contenido.append("<label>Contraseña del supervisor</label><input name=\"contrasena\" type=\"password\" required>");
            contenido.append("<button type=\"submit\">Generar reporte de departamento</button></form>");
            contenido.append("</section>");
            contenido.append("<section><h3>Reportes generados</h3>");
            if (reportes.isEmpty()) {
                contenido.append("<p>No se ha generado ningún reporte todavía.</p>");
            } else {
                contenido.append("<table><thead><tr><th>ID</th><th>Tipo</th><th>Fecha</th><th>Acción</th></tr></thead><tbody>");
                for (ReporteDesempenio reporte : reportes.values()) {
                    contenido.append("<tr><td>").append(reporte.getIdReporte()).append("</td>");
                    contenido.append("<td>").append(reporte.getTipoReporte()).append("</td>");
                    contenido.append("<td>").append(reporte.getFechaGeneracion()).append("</td>");
                    contenido.append("<td><a href=\"/reportes/exportar?id=").append(reporte.getIdReporte()).append("\">Exportar</a></td></tr>");
                }
                contenido.append("</tbody></table>");
            }
            contenido.append("</section>");
            enviarRespuesta(exchange, render("Reportes", contenido.toString()));
        }
    }

    private class GenerarReporteIndividualHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/reportes");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String,String> datos = parseForm(raw);
            try {
                if (supervisor == null) {
                    redirect(exchange, "/reportes?error=" + urlEncode("No existe un supervisor. Cree uno primero."));
                    return;
                }
                String clave = datos.getOrDefault("contrasena", "");
                if (!supervisor.iniciarSesion(clave)) {
                    redirect(exchange, "/reportes?error=" + urlEncode("Contraseña de supervisor incorrecta."));
                    return;
                }
                int idEmpleado = Integer.parseInt(datos.getOrDefault("idEmpleado", "0"));
                ReporteDesempenio reporte = supervisor.generarReporteIndividual(idEmpleado);
                reportes.put(reporte.getIdReporte(), reporte);
                redirect(exchange, "/reportes?message=" + urlEncode("Reporte individual generado con éxito."));
            } catch (Exception e) {
                redirect(exchange, "/reportes?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class GenerarReporteDepartamentoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                redirect(exchange, "/reportes");
                return;
            }
            String raw = readRequestBody(exchange);
            Map<String,String> datos = parseForm(raw);
            try {
                if (supervisor == null) {
                    redirect(exchange, "/reportes?error=" + urlEncode("No existe un supervisor. Cree uno primero."));
                    return;
                }
                String clave = datos.getOrDefault("contrasena", "");
                if (!supervisor.iniciarSesion(clave)) {
                    redirect(exchange, "/reportes?error=" + urlEncode("Contraseña de supervisor incorrecta."));
                    return;
                }
                int idDepartamento = Integer.parseInt(datos.getOrDefault("idDepartamento", "0"));
                Departamento depto = empresa.buscarDepartamento(idDepartamento);
                supervisor.setDepartamento(depto);
                ReporteDesempenio reporte = supervisor.generarReporte();
                reportes.put(reporte.getIdReporte(), reporte);
                redirect(exchange, "/reportes?message=" + urlEncode("Reporte de departamento generado con éxito."));
            } catch (Exception e) {
                redirect(exchange, "/reportes?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private class ExportarReporteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Map<String, String> query = parseQuery(exchange.getRequestURI().getRawQuery());
            try {
                int id = Integer.parseInt(query.getOrDefault("id", "0"));
                ReporteDesempenio reporte = reportes.get(id);
                if (reporte == null) {
                    redirect(exchange, "/reportes?error=" + urlEncode("Reporte no encontrado."));
                    return;
                }
                ExportadorReporte.exportar(reporte);
                String nombreArchivo = "Reporte_" + reporte.getTipoReporte() + "_" + reporte.getIdReporte() + ".txt";
                String contenido = "REPORTE DE DESEMPEÑO - CompuWork\n" +
                        "ID Reporte: " + reporte.getIdReporte() + "\n" +
                        "Tipo: " + reporte.getTipoReporte() + "\n" +
                        "Fecha: " + reporte.getFechaGeneracion() + "\n" +
                        "==============================\n\n" +
                        reporte.getMetricas();
                byte[] bytes = contenido.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.getResponseHeaders().set("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
                exchange.sendResponseHeaders(200, bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            } catch (Exception e) {
                redirect(exchange, "/reportes?error=" + urlEncode(e.getMessage()));
            }
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        byte[] bytes = requestBody.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> datos = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return datos;
        }
        String[] pares = query.split("&");
        for (String par : pares) {
            String[] partes = par.split("=", 2);
            datos.put(urlDecode(partes[0]), partes.length > 1 ? urlDecode(partes[1]) : "");
        }
        return datos;
    }
}
