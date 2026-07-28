package Model;

import java.time.LocalDate;

public class HistorialVenta {
    private int id;
    private LocalDate fecha;
    private String cliente;
    private String modelo;
    private int cantidad;
    private double total;
    private String folio;

    public HistorialVenta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getFolio() { return folio; }
    public void setFolio(String folio) { this.folio = folio; }
}