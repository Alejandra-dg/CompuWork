package compuwork.util;

public final class Validaciones {

    private Validaciones() {
        // Utilidades estáticas, no instanciable.
    }

    public static void validarTexto(String valor, String mensaje) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static void validarObjeto(Object objeto, String mensaje) {
        if (objeto == null) {
            throw new IllegalArgumentException(mensaje);
        }
    }

    public static void validarNoNegativo(double valor, String mensaje) {
        if (valor < 0) {
            throw new IllegalArgumentException(mensaje);
        }
    }
}
