package Model;

import java.time.LocalDate;

public class Molde {
    private int id;
    private String nombre;
    private int cantidad;
    private String estado;
    private LocalDate fechaRegistro;

    public Molde() {}

    public Molde(int id, String nombre, int cantidad, String estado, LocalDate fechaRegistro) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}