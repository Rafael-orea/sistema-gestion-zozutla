package Model;

public class DetalleVenta {
    private int id;
    private int idVenta;
    private int idAlcancia;
    private String nombreAlcancia;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public int getIdAlcancia() { return idAlcancia; }
    public void setIdAlcancia(int idAlcancia) { this.idAlcancia = idAlcancia; }

    public String getNombreAlcancia() { return nombreAlcancia; }
    public void setNombreAlcancia(String nombreAlcancia) { this.nombreAlcancia = nombreAlcancia; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}
