package compuwork.aplicacion;

public class CompuWork {

    public static void main(String[] args) {
        try {
            new WebServidor(8080).start();
        } catch (Exception e) {
            System.out.println("No se pudo iniciar la aplicación web: " + e.getMessage());
            new MenuSistema().iniciar();
        }
    }
}

