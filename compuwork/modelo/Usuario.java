package compuwork.modelo;

import compuwork.util.PasswordUtil;
import compuwork.util.Validaciones;

public abstract class Usuario {

    private int idUsuario;
    private String nombre;
    private String contrasena;

    public Usuario(int idUsuario, String nombre, String contrasena) {
        Validaciones.validarTexto(nombre, "El nombre del usuario no puede estar vacío.");
        Validaciones.validarTexto(contrasena, "La contraseña no puede estar vacía.");
        if (contrasena.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.contrasena = PasswordUtil.encriptar(contrasena);
    }

    protected boolean autenticar(String contrasena) {
        Validaciones.validarTexto(contrasena, "La contraseña no puede estar vacía.");
        return this.contrasena.equals(PasswordUtil.encriptar(contrasena));
    }

    public void cambiarContrasena(String contrasenaNueva) {
        Validaciones.validarTexto(contrasenaNueva, "La nueva contraseña no puede estar vacía.");
        if (contrasenaNueva.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.contrasena = PasswordUtil.encriptar(contrasenaNueva);
    }

    public abstract boolean iniciarSesion(String contrasena);

    public abstract void cerrarSesion();

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        Validaciones.validarTexto(nombre, "El nombre no puede estar vacío.");
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Usuario [idUsuario=" + idUsuario + ", nombre=" + nombre + "]";
    }
}
