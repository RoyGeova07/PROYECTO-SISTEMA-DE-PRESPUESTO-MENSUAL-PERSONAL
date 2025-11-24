/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 24 nov 2025
 */

drop procedure if exists SP_INSERTAR_TRANSACCION;
drop procedure if exists SP_ACTUALIZAR_TRANSACCION;
drop procedure if exists SP_ELIMINAR_TRANSACCION;

-- SP_INSERTAR_TRANSACCION
create procedure SP_INSERTAR_TRANSACCION
(
    in P_ID_USUARIO bigint,
    in P_ID_PRESUPUESTO bigint,
    in P_ANIO smallint,
    in P_MES smallint,
    in P_ID_SUBCATEGORIA bigint,
    in P_ID_OBLIGACION bigint, -- puede ser NULL
    in P_TIPO varchar(15),
    in P_DESCRIPCION varchar(500),
    in P_MONTO decimal(14,2),
    in P_FECHA date,
    in P_METODO_PAGO varchar(30),
    in P_CREADO_POR varchar(100)
)
modifies sql data
insert into TRANSACCION
(
    Id_usuario,
    Id_presupuesto,
    Anio,
    Mes,
    Id_subcategoria,
    Id_obligacion_fija,
    Tipo_de_transaccion,
    Descripcion,
    Monto,
    Fecha,
    Metodo_de_pago,
    Fecha_hora_de_registro,
    creado_en,
    creado_por
)
select
    P_ID_USUARIO,
    P_ID_PRESUPUESTO,
    P_ANIO,
    P_MES,
    P_ID_SUBCATEGORIA,
    P_ID_OBLIGACION,
    P_TIPO,
    P_DESCRIPCION,
    P_MONTO,
    P_FECHA,
    P_METODO_PAGO,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    P_CREADO_POR
from PRESUPUESTO pr
join SUBCATEGORIA sc on sc.Id_subcategoria =P_ID_SUBCATEGORIA
join CATEGORIA c on sc.Id_categoria = c.Id_categoria
left join OBLIGACION_FIJA o on (o.Id_obligacion_fija = P_ID_OBLIGACION)
where pr.Id_presupuesto = P_ID_PRESUPUESTO
  and pr.Id_usuario = P_ID_USUARIO
  and c.Tipo_de_categoria = P_TIPO
  /* año/mes imputado debe estar dentro de la vigencia del presupuesto */
  and (P_ANIO * 100 + P_MES) between (pr.Anio_de_inicio * 100 + pr.Mes_de_inicio) and (pr.Anio_de_fin * 100 + pr.Mes_de_fin)
  /* la fecha real debe estar dentro de la vigencia del presupuesto */
  and (EXTRACT(YEAR FROM P_FECHA) * 100 + EXTRACT(MONTH FROM P_FECHA))
      between (pr.Anio_de_inicio * 100 + pr.Mes_de_inicio) and (pr.Anio_de_fin * 100 + pr.Mes_de_fin)
  /* si se proporciono obligacion, debe existir, ser del mismo usuario y usar la misma subcategoria */
  and (P_ID_OBLIGACION IS NULL OR (o.Id_obligacion_fija = P_ID_OBLIGACION AND o.Id_usuario = P_ID_USUARIO AND o.Id_subcategoria = P_ID_SUBCATEGORIA))
;

-- SP_ACTUALIZAR_TRANSACCION
create procedure SP_ACTUALIZAR_TRANSACCION
(
    in P_ID_TRANSACCION bigint,
    in P_ANIO smallint,
    in P_MES smallint,
    in P_DESCRIPCION varchar(500),
    in P_MONTO decimal(14,2),
    in P_FECHA date,
    in P_METODO_PAGO varchar(30),
    in P_MODIFICADO_POR varchar(100)
)
modifies sql data
update TRANSACCION
set Anio=P_ANIO,
    Mes=P_MES,
    Descripcion=P_DESCRIPCION,
    Monto=P_MONTO,
    Fecha=P_FECHA,
    Metodo_de_pago=P_METODO_PAGO,
    Fecha_hora_de_registro=CURRENT_TIMESTAMP,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por=P_MODIFICADO_POR
where Id_transaccion=P_ID_TRANSACCION
  and exists (
      select 1
      from TRANSACCION t
      join PRESUPUESTO pr on t.Id_presupuesto = pr.Id_presupuesto
      where t.Id_transaccion = P_ID_TRANSACCION
        /*validar que el nuevo anio/mes este dentro de la vigencia del presupuesto*/
        and (P_ANIO * 100 + P_MES) between (pr.Anio_de_inicio * 100 + pr.Mes_de_inicio) and (pr.Anio_de_fin * 100 + pr.Mes_de_fin)
        /*validar que la nueva fecha este dentro de la vigencia del presupuesto*/
        and (EXTRACT(YEAR FROM P_FECHA) * 100 + EXTRACT(MONTH FROM P_FECHA))
            between (pr.Anio_de_inicio * 100 + pr.Mes_de_inicio) and (pr.Anio_de_fin * 100 + pr.Mes_de_fin)
  )
;

-- SP_ELIMINAR_TRANSACCION
create procedure SP_ELIMINAR_TRANSACCION
(
    in P_ID_TRANSACCION bigint
)
modifies sql data
delete from TRANSACCION
where Id_transaccion = P_ID_TRANSACCION
;