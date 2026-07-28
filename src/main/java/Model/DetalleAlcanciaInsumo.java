package Model;

public class DetalleAlcanciaInsumo {
    private int idAlcancia;
    private int idInsumo;
    private String nombreInsumo;
    private String unidad;
    private double cantidad;
    private double costoUnitario;
    private double subtotal;

    public DetalleAlcanciaInsumo() {}

    public int getIdAlcancia() { return idAlcancia; }
    public void setIdAlcancia(int idAlcancia) { this.idAlcancia = idAlcancia; }

    public int getIdInsumo() { return idInsumo; }
    public void setIdInsumo(int idInsumo) { this.idInsumo = idInsumo; }

    public String getNombreInsumo() { return nombreInsumo; }
    public void setNombreInsumo(String nombreInsumo) { this.nombreInsumo = nombreInsumo; }

    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public double getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(double costoUnitario) { this.costoUnitario = costoUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
}