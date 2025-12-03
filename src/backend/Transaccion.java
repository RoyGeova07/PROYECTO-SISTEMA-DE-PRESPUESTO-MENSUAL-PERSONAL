/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

/**
 *
 * @author royum
 */
public class Transaccion 
{
    
    
    private long id;
    private long idUsuario;
    private long idPresupuesto;
    private short anio;
    private short mes;
    private long idSubcategoria;
    private Long idObligacionFija; // nullable
    private String tipo; // ingreso|gasto|ahorro
    private String descripcion;
    private BigDecimal monto;
    private Date fecha; // fecha real de la transacción
    private String metodoPago; // efectivo|tarjeta_debito|...
    private Timestamp fechaHoraRegistro;

    private Timestamp creadoEn;
    private Timestamp modificadoEn;
    private String creadoPor;
    private String modificadoPor;

    // Campos "join" / auxiliares (no persistidos en la tabla TRANSACCION,
    // pero devueltos por funciones que hacen JOINs)
    private String nombreSubcategoria;
    private Long idCategoria;
    private String nombreCategoria;

    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getIdPresupuesto() {
        return idPresupuesto;
    }

    public void setIdPresupuesto(long idPresupuesto) {
        this.idPresupuesto = idPresupuesto;
    }

    public short getAnio() {
        return anio;
    }

    public void setAnio(short anio) {
        this.anio = anio;
    }

    public short getMes() {
        return mes;
    }

    public void setMes(short mes) {
        this.mes = mes;
    }

    public long getIdSubcategoria() {
        return idSubcategoria;
    }

    public void setIdSubcategoria(long idSubcategoria) {
        this.idSubcategoria = idSubcategoria;
    }

    public Long getIdObligacionFija() {
        return idObligacionFija;
    }

    public void setIdObligacionFija(Long idObligacionFija) {
        this.idObligacionFija = idObligacionFija;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Timestamp getFechaHoraRegistro() {
        return fechaHoraRegistro;
    }

    public void setFechaHoraRegistro(Timestamp fechaHoraRegistro) {
        this.fechaHoraRegistro = fechaHoraRegistro;
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

    @Override
    public String toString() {
        return "Transaccion{" + "id=" + id + ", idUsuario=" + idUsuario + ", idPresupuesto=" + idPresupuesto + ", anio=" + anio + ", mes=" + mes + ", idSubcategoria=" + idSubcategoria + ", idObligacionFija=" + idObligacionFija + ", tipo=" + tipo + ", descripcion=" + descripcion + ", monto=" + monto + ", fecha=" + fecha + ", metodoPago=" + metodoPago + ", fechaHoraRegistro=" + fechaHoraRegistro + ", creadoEn=" + creadoEn + ", modificadoEn=" + modificadoEn + ", creadoPor=" + creadoPor + ", modificadoPor=" + modificadoPor + ", nombreSubcategoria=" + nombreSubcategoria + ", idCategoria=" + idCategoria + ", nombreCategoria=" + nombreCategoria + '}';
    }
    

    
    
}
