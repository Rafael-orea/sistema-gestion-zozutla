package Model;

public class Alcancia {
    private int id;
    private int idMolde;
    private String nombre;
    private String descripcion;
    private int existencia;
    private double precio;
    private String estado;

    public Alcancia() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdMolde() { return idMolde; }
    public void setIdMolde(int idMolde) { this.idMolde = idMolde; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}