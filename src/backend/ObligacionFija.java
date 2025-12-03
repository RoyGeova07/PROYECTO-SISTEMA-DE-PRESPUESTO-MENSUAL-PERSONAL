/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author royum
 */
public class ObligacionFija
{
    
    private Long id;
    private Long idUsuario;
    private Long idSubcategoria;
    private String nombre;
    private String descripcion;
    private BigDecimal montoMensual;
    private Integer diaVencimiento;
    private Boolean estaVigente;
    private Date fechaInicio;
    private Date fechaFin;
    private String nombreSubcategoria;
    private Long idCategoria;
    private String nombreCategoria;
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

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    public Integer getDiaVencimiento() {
        return diaVencimiento;
    }

    public void setDiaVencimiento(Integer diaVencimiento) {
        this.diaVencimiento = diaVencimiento;
    }

    public Boolean getEstaVigente() {
        return estaVigente;
    }

    public void setEstaVigente(Boolean estaVigente) {
        this.estaVigente = estaVigente;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getNombreSubcategoria() {
        return nombreSubcategoria;
    }

    public void setNombreSubcategoria(String nombreSubcategoria) {
        this.nombreSubcategoria = nombreSubcategoria;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
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
        return "ObligacionFija{" + "id=" + id + ", idUsuario=" + idUsuario + ", idSubcategoria=" + idSubcategoria + ", nombre=" + nombre + ", descripcion=" + descripcion + ", montoMensual=" + montoMensual + ", diaVencimiento=" + diaVencimiento + ", estaVigente=" + estaVigente + ", fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + ", nombreSubcategoria=" + nombreSubcategoria + ", idCategoria=" + idCategoria + ", nombreCategoria=" + nombreCategoria + ", creadoEn=" + creadoEn + ", modificadoEn=" + modificadoEn + ", creadoPor=" + creadoPor + ", modificadoPor=" + modificadoPor + '}';
    }
    
    
    
    
}
