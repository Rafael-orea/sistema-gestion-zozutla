package Model;

public class IncidenciaEnvio {
    private int id;
    private int idEnvio;
    private int faltantes;
    private int rotas;
    private String descripcion;
    private String fecha;

    public IncidenciaEnvio() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEnvio() { return idEnvio; }
    public void setIdEnvio(int idEnvio) { this.idEnvio = idEnvio; }

    public int getFaltantes() { return faltantes; }
    public void setFaltantes(int faltantes) { this.faltantes = faltantes; }

    public int getRotas() { return rotas; }
    public void setRotas(int rotas) { this.rotas = rotas; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }
}