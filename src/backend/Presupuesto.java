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
public class Presupuesto 
{
    
    private Long id;
    private Long idUsuario;
    private String nombreDescriptivo;
    private Integer anioInicio;
    private Integer mesInicio;
    private Integer anioFin;
    private Integer mesFin;
    private BigDecimal totalIngresos;
    private BigDecimal totalGastos;
    private BigDecimal totalAhorro;
    private Timestamp fechaHoraCreacion;
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

    public String getNombreDescriptivo() {
        return nombreDescriptivo;
    }

    public void setNombreDescriptivo(String nombreDescriptivo) {
        this.nombreDescriptivo = nombreDescriptivo;
    }

    public Integer getAnioInicio() {
        return anioInicio;
    }

    public void setAnioInicio(Integer anioInicio) {
        this.anioInicio = anioInicio;
    }

    public Integer getMesInicio() {
        return mesInicio;
    }

    public void setMesInicio(Integer mesInicio) {
        this.mesInicio = mesInicio;
    }

    public Integer getAnioFin() {
        return anioFin;
    }

    public void setAnioFin(Integer anioFin) {
        this.anioFin = anioFin;
    }

    public Integer getMesFin() {
        return mesFin;
    }

    public void setMesFin(Integer mesFin) {
        this.mesFin = mesFin;
    }

    public BigDecimal getTotalIngresos() {
        return totalIngresos;
    }

    public void setTotalIngresos(BigDecimal totalIngresos) {
        this.totalIngresos = totalIngresos;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(BigDecimal totalGastos) {
        this.totalGastos = totalGastos;
    }

    public BigDecimal getTotalAhorro() {
        return totalAhorro;
    }

    public void setTotalAhorro(BigDecimal totalAhorro) {
        this.totalAhorro = totalAhorro;
    }

    public Timestamp getFechaHoraCreacion() {
        return fechaHoraCreacion;
    }

    public void setFechaHoraCreacion(Timestamp fechaHoraCreacion) {
        this.fechaHoraCreacion = fechaHoraCreacion;
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
        return "Presupuesto{" + "id=" + id + ", idUsuario=" + idUsuario + ", nombreDescriptivo=" + nombreDescriptivo + ", anioInicio=" + anioInicio + ", mesInicio=" + mesInicio + ", anioFin=" + anioFin + ", mesFin=" + mesFin + ", totalIngresos=" + totalIngresos + ", totalGastos=" + totalGastos + ", totalAhorro=" + totalAhorro + ", fechaHoraCreacion=" + fechaHoraCreacion + ", estado=" + estado + ", creadoEn=" + creadoEn + ", modificadoEn=" + modificadoEn + ", creadoPor=" + creadoPor + ", modificadoPor=" + modificadoPor + '}';
    }
    
    
    
}
