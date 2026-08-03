package Model;

public class Alcancia {
    private int id;
    private int idMolde;
    private String nombre;
    private int existencia;
    private int existenciaMerma;
    private double precio;
    private double precioMayoreo;
    private String estado;
    private double costoProduccion;

    public double getCostoProduccion() { return costoProduccion; }
    public void setCostoProduccion(double costoProduccion) { this.costoProduccion = costoProduccion; }

    public Alcancia() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdMolde() { return idMolde; }
    public void setIdMolde(int idMolde) { this.idMolde = idMolde; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getExistencia() { return existencia; }
    public void setExistencia(int existencia) { this.existencia = existencia; }

    public int getExistenciaMerma() { return existenciaMerma; }
    public void setExistenciaMerma(int existenciaMerma) { this.existenciaMerma = existenciaMerma; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public double getPrecioMayoreo() { return precioMayoreo; }
    public void setPrecioMayoreo(double precioMayoreo) { this.precioMayoreo = precioMayoreo; }

    public double getPrecioEspecial() { return precio * 0.85; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}