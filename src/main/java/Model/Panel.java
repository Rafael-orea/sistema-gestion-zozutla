package Model;

import java.math.BigDecimal;

public class Panel {
    private int totalMoldes;
    private int totalInventario;
    private BigDecimal ventasDia;
    private BigDecimal ventasMes;
    private BigDecimal gananciaEstimada;

    public Panel() {}

    public int getTotalMoldes() { return totalMoldes; }
    public void setTotalMoldes(int totalMoldes) { this.totalMoldes = totalMoldes; }

    public int getTotalInventario() { return totalInventario; }
    public void setTotalInventario(int totalInventario) { this.totalInventario = totalInventario; }

    public BigDecimal getVentasDia() { return ventasDia; }
    public void setVentasDia(BigDecimal ventasDia) { this.ventasDia = ventasDia; }

    public BigDecimal getVentasMes() { return ventasMes; }
    public void setVentasMes(BigDecimal ventasMes) { this.ventasMes = ventasMes; }

    public BigDecimal getGananciaEstimada() { return gananciaEstimada; }
    public void setGananciaEstimada(BigDecimal gananciaEstimada) { this.gananciaEstimada = gananciaEstimada; }
}