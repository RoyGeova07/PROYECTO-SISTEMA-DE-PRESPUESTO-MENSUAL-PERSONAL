package backend;


import backend.Puente_Sql_Java;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import backend.Usuario;
import java.sql.Date;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
/**
 *
 * @author royum
 */
public class pruebas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

    Puente_Sql_Java pe = new Puente_Sql_Java();

    System.out.println("Monto ejecutado: "
            + pe.fnCalcularMontoEjecutado(3, 2025, 2));

    System.out.println("Porcentaje ejecutado: "
            + pe.fnCalcularPorcentajeEjecutado(3, 1, 2025, 2));

    System.out.println("Balance subcategoria: "
            + pe.fnObtenerBalanceSubcategoria(1, 3, 2025, 2));

    System.out.println("Total categoria (presupuesto): "
            + pe.fnObtenerTotalCategoriaMes(4, 1, 2025, 2));

    System.out.println("Total ejecutado categoria: "
            + pe.fnObtenerTotalEjecutadoCategoriaMes(4, 2025, 2));

    System.out.println("Dias hasta vencimiento obligacion: "
            + pe.fnDiasHastaVencimiento(1));

    System.out.println("Presupuesto vigente (2025-03-15): "
            + pe.fnValidarVigenciaPresupuesto(
                    java.sql.Date.valueOf("2025-03-15"), 1));

    System.out.println("Categoria de subcategoria 3: "
            + pe.fnObtenerCategoriaPorSubcategoria(3));

    System.out.println("Proyeccion gasto mensual: "
            + pe.fnCalcularProyeccionGastoMensual(3, 2025, 2));

    System.out.println("Promedio gasto ultimos 3 meses: "
            + pe.fnObtenerPromedioGastoSubcategoria(1, 3, 3));
}


    private static void imprimirSubcategoria(Subcategoria s) {
        if (s == null) {
            System.out.println("Subcategoria = null");
            return;
        }
        System.out.println("ID:               " + s.getId());
        System.out.println("ID_CATEGORIA:     " + s.getIdCategoria());
        System.out.println("NOMBRE:           " + s.getNombre());
        System.out.println("DESCRIPCION:      " + s.getDescripcion());
        System.out.println("ESTADO:           " + s.getEstado());
        System.out.println("POR_DEFECTO:      " + s.getPorDefecto());
        System.out.println("CATEGORIA_NOMBRE: " + s.getCategoriaNombre());
        System.out.println("CREADO_EN:        " + s.getCreadoEn());
        System.out.println("MODIFICADO_EN:    " + s.getModificadoEn());
        System.out.println("CREADO_POR:       " + s.getCreadoPor());
        System.out.println("MODIFICADO_POR:   " + s.getModificadoPor());
    }
    private static void imprimirCategoria(Categoria c) {
        if (c == null) {
            System.out.println("Categoria = null");
            return;
        }
        System.out.println("ID_CATEGORIA:     " + c.getId());
        System.out.println("NOMBRE:           " + c.getNombre());
        System.out.println("DESCRIPCION:      " + c.getDescripcion());
        System.out.println("TIPO:             " + c.getTipo());
        System.out.println("CREADO_EN:        " + c.getCreadoEn());
        System.out.println("MODIFICADO_EN:    " + c.getModificadoEn());
        System.out.println("CREADO_POR:       " + c.getCreadoPor());
        System.out.println("MODIFICADO_POR:   " + c.getModificadoPor());
    }

    private static void imprimirPresupuestoDetalle(Presupuesto_detalle pd) {
        if (pd == null) {
            System.out.println("PresupuestoDetalle = null");
            return;
        }
        System.out.println("ID:                    " + safe(pd.getId()));
        System.out.println("ID_PRESUPUESTO:        " + safe(pd.getIdPresupuesto()));
        System.out.println("ID_SUBCATEGORIA:       " + safe(pd.getIdSubcategoria()));
        System.out.println("NOMBRE_SUBCATEGORIA:   " + safe(pd.getNombreSubcategoria()));
        System.out.println("ID_CATEGORIA:          " + safe(pd.getIdCategoria()));
        System.out.println("NOMBRE_CATEGORIA:      " + safe(pd.getNombreCategoria()));
        System.out.println("MONTO_MENSUAL:         " + safe(pd.getMontoMensual()));
        System.out.println("JUSTIFICACION:         " + safe(pd.getJustificacion()));
        System.out.println("CREADO_EN:             " + safe(pd.getCreadoEn()));
        System.out.println("MODIFICADO_EN:         " + safe(pd.getModificadoEn()));
        System.out.println("CREADO_POR:            " + safe(pd.getCreadoPor()));
        System.out.println("MODIFICADO_POR:        " + safe(pd.getModificadoPor()));
    }

    private static String safe(Object o) {
        return o == null ? "null" : o.toString();
    }
    
     private static void imprimirTransaccion(Transaccion t) {
        if (t == null) {
            System.out.println("Transaccion = null");
            return;
        }
        System.out.println("ID:                  " + safe(t.getId()));
        System.out.println("ID_USUARIO:          " + safe(t.getIdUsuario()));
        System.out.println("ID_PRESUPUESTO:      " + safe(t.getIdPresupuesto()));
        System.out.println("ANIO:                " + safe(t.getAnio()));
        System.out.println("MES:                 " + safe(t.getMes()));
        System.out.println("ID_SUBCATEGORIA:     " + safe(t.getIdSubcategoria()));
        System.out.println("ID_OBLIGACION:       " + safe(t.getIdObligacionFija()));
        System.out.println("TIPO:                " + safe(t.getTipo()));
        System.out.println("DESCRIPCION:         " + safe(t.getDescripcion()));
        System.out.println("MONTO:               " + safe(t.getMonto()));
        System.out.println("FECHA:               " + safe(t.getFecha()));
        System.out.println("METODO_PAGO:         " + safe(t.getMetodoPago()));
        System.out.println("FECHA_HORA_REGISTRO: " + safe(t.getFechaHoraRegistro()));
        System.out.println("CREADO_EN:           " + safe(t.getCreadoEn()));
        System.out.println("MODIFICADO_EN:       " + safe(t.getModificadoEn()));
        System.out.println("CREADO_POR:          " + safe(t.getCreadoPor()));
        System.out.println("MODIFICADO_POR:      " + safe(t.getModificadoPor()));
        System.out.println("NOMBRE_SUBCATEGORIA: " + safe(t.getNombreSubcategoria()));
        System.out.println("ID_CATEGORIA:        " + safe(t.getIdCategoria()));
        System.out.println("NOMBRE_CATEGORIA:    " + safe(t.getNombreCategoria()));
    }
        
        
        

}
