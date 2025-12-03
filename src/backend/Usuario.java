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
public class Usuario
{
    
    private long id;
    private String nombre;
    private String apellido;
    private String correo;
    private Timestamp fechaRegistro;
    private BigDecimal salario;
    private Boolean Estado_usuario;
    private Timestamp creadoEn;
    private String creadoPor;
    private Timestamp modificadoEn;
    private String modificadoPor;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Timestamp getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(Timestamp fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    public Boolean getEstado_usuario() {
        return Estado_usuario;
    }

    public void setEstado_usuario(Boolean Estado_usuario) {
        this.Estado_usuario = Estado_usuario;
    }

    

    public Timestamp getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public Timestamp getModificadoEn() {
        return modificadoEn;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }
    
    public void setModficadoEn(Timestamp a)
    {
        this.modificadoEn=a;
        
    }

    
    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", nombre=" + nombre + ", apellido=" + apellido + ", correo=" + correo + ", fechaRegistro=" + fechaRegistro + ", salario=" + salario + ", Estado_usuario=" + Estado_usuario + ", creadoEn=" + creadoEn + ", creadoPor=" + creadoPor + ", modificadoEn=" + modificadoEn + ", modificadoPor=" + modificadoPor + '}';
    }
    
    
}
