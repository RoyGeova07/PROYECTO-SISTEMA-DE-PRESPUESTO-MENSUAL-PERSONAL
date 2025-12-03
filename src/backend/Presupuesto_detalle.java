/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author royum
 */
public class Presupuesto_detalle 
{
    
    private Long id;
    private Long idPresupuesto;
    private Long idSubcategoria;
    private String nombreSubcategoria;
    private Long idCategoria;
    private String nombreCategoria;
    private BigDecimal montoMensual;
    private String justificacion;
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

    public Long getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(Long idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public Long getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(Long idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
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

    public BigDecimal getMontoMensual() {
        return montoMensual;
    }

    public void setMontoMensual(BigDecimal montoMensual) {
        this.montoMensual = montoMensual;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
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
        return "Presupuesto_detalle{" + "id=" + id + ", idPresupuesto=" + idPresupuesto + ", idSubcategoria=" + idSubcategoria + ", nombreSubcategoria=" + nombreSubcategoria + ", idCategoria=" + idCategoria + ", nombreCategoria=" + nombreCategoria + ", montoMensual=" + montoMensual + ", justificacion=" + justificacion + ", creadoEn=" + creadoEn + ", modificadoEn=" + modificadoEn + ", creadoPor=" + creadoPor + ", modificadoPor=" + modificadoPor + '}';
    }
    
    
    
    
    
}
