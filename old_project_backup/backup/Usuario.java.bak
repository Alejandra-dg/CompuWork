package compuwork;

public abstract class Usuario {

    private int idUsuario;
    private String nombre;

    public Usuario(int idUsuario, String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío.");
        }
        this.idUsuario = idUsuario;
        this.nombre = nombre;
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
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Usuario [idUsuario=" + idUsuario + ", nombre=" + nombre + "]";
    }
}
