
package backend;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Representa una meta de ahorro (tabla META_AHORRO)
 */
public class MetaAhorro {

    private Long id;                
    private Long idUsuario;         
    private Long idSubcategoria;    
    private String nombre;          
    private String descripcion;     
    private BigDecimal montoObjetivo;   
    private BigDecimal montoAhorrado;   
    private BigDecimal porcentajeAvance; 
    private Date fechaInicio;       
    private Date fechaObjetivo;     
    private String prioridad;       
    private String estado;          
    private Timestamp creadoEn;     
    private Timestamp modificadoEn; 
    private String creadoPor;       
    private String modificadoPor;       

  public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(Long idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMontoObjetivo() {
        return montoObjetivo;
    }

    public void setMontoObjetivo(BigDecimal montoObjetivo) {
        this.montoObjetivo = montoObjetivo;
    }

    public BigDecimal getMontoAhorrado() {
        return montoAhorrado;
    }

    public void setMontoAhorrado(BigDecimal montoAhorrado) {
        this.montoAhorrado = montoAhorrado;
    }

    public BigDecimal getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(BigDecimal porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaObjetivo() {
        return fechaObjetivo;
    }

    public void setFechaObjetivo(Date fechaObjetivo) {
        this.fechaObjetivo = fechaObjetivo;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Timestamp getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Timestamp getModificadoEn() {
        return modificadoEn;
    }

    public void setModificadoEn(Timestamp modificadoEn) {
        this.modificadoEn = modificadoEn;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    @Override
    public String toString() {
        return "MetaAhorro{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", idSubcategoria=" + idSubcategoria +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", montoObjetivo=" + montoObjetivo +
                ", montoAhorrado=" + montoAhorrado +
                ", porcentajeAvance=" + porcentajeAvance +
                ", fechaInicio=" + fechaInicio +
                ", fechaObjetivo=" + fechaObjetivo +
                ", prioridad='" + prioridad + '\'' +
                ", estado='" + estado + '\'' +
                ", creadoEn=" + creadoEn +
                ", modificadoEn=" + modificadoEn +
                ", creadoPor='" + creadoPor + '\'' +
                ", modificadoPor='" + modificadoPor + '\'' +
                '}';
    }
}