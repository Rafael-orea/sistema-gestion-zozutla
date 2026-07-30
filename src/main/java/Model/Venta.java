package Model;

import java.time.LocalDate;
import java.util.List;

public class Venta {
    private int id;
    private int idCliente;
    private String nombreCliente;
    private String folio;
    private LocalDate fecha;
    private double total;
    private String tipoPrecio;
    private boolean compradorNormal;
    private List<DetalleVenta> detalles;

    public Venta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getTipoPrecio() { return tipoPrecio; }
    public void setTipoPrecio(String tipoPrecio) { this.tipoPrecio = tipoPrecio; }

    public boolean isCompradorNormal() { return compradorNormal; }
    public void setCompradorNormal(boolean compradorNormal) { this.compradorNormal = compradorNormal; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}