package Model;

public class IncidenciaEnvio {
    private int id;
    private int idEnvio;
    private int idAlcancia;
    private String nombreAlcancia;
    private int faltantes;
    private int rotas;
    private int cantidadAfectada;
    private String descripcion;
    private String fecha;
    private String responsabilidad;
    private double porcentajeCliente;
    private double porcentajeZozutla;

    public IncidenciaEnvio() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEnvio() { return idEnvio; }
    public void setIdEnvio(int idEnvio) { this.idEnvio = idEnvio; }

    public int getIdAlcancia() { return idAlcancia; }
    public void setIdAlcancia(int idAlcancia) { this.idAlcancia = idAlcancia; }

    public String getNombreAlcancia() { return nombreAlcancia; }
    public void setNombreAlcancia(String nombreAlcancia) { this.nombreAlcancia = nombreAlcancia; }

    public int getFaltantes() { return faltantes; }
    public void setFaltantes(int faltantes) { this.faltantes = faltantes; }

    public int getRotas() { return rotas; }
    public void setRotas(int rotas) { this.rotas = rotas; }

    public int getCantidadAfectada() { return cantidadAfectada; }
    public void setCantidadAfectada(int cantidadAfectada) { this.cantidadAfectada = cantidadAfectada; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getResponsabilidad() { return responsabilidad; }
    public void setResponsabilidad(String responsabilidad) { this.responsabilidad = responsabilidad; }

    public double getPorcentajeCliente() { return porcentajeCliente; }
    public void setPorcentajeCliente(double porcentajeCliente) { this.porcentajeCliente = porcentajeCliente; }

    public double getPorcentajeZozutla() { return porcentajeZozutla; }
    public void setPorcentajeZozutla(double porcentajeZozutla) { this.porcentajeZozutla = porcentajeZozutla; }
}