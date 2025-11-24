/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 23 nov 2025
 */

/*
usare la funcion  COALESCE porque este devuelve el primer valor no nulo de una lista de expresiones que se le
pasan como argumento

*/

drop procedure if exists SP_INSERTAR_PRESUPUESTO_DETALLE;
drop procedure if exists SP_ACTUALIZAR_PRESUPUESTO_DETALLE;
drop procedure if exists SP_ELIMINAR_PRESUPUESTO_DETALLE;
drop procedure if exists SP_RECALCULAR_TOTALES_PARA_PRESUPUESTO_IDDETALLE;
drop procedure if exists SP_RECALCULAR_TOTALES_PARA_PRESUPUESTO_IDDETALLE_ELIMINADO;


create procedure SP_INSERTAR_PRESUPUESTO_DETALLE
(

    in P_ID_PRESUPUESTO BIGINT,
    in P_ID_SUBCATEGORIA BIGINT,
    in P_MONTO_MENSUAL decimal(14,2),
    in P_JUSTIFICACION varchar(500),
    in P_CREADO_POR varchar(100)

)
modifies sql data
--inserta solo si existen presupuesto y subcategoria (el SELECT impedira insertar si no existen)
insert into PRESUPUESTO_DETALLE
(

    Id_presupuesto,
    Id_subcategoria,
    Monto_mensual,
    Justificacion_del_monto,
    creado_en,
    creado_por

)
select
    P_ID_PRESUPUESTO,
    P_ID_SUBCATEGORIA,
    P_MONTO_MENSUAL,
    P_JUSTIFICACION,
    CURRENT_TIMESTAMP,
    P_CREADO_POR
from PRESUPUESTO pr,SUBCATEGORIA sc
where pr.Id_presupuesto=P_ID_PRESUPUESTO
    and sc.Id_subcategoria=P_ID_SUBCATEGORIA
;


--actualizar detalle y recalcular totales del presupuesto
create procedure SP_ACTUALIZAR_PRESUPUESTO_DETALLE
(

    in P_ID_DETALLE BIGINT,
    in P_MONTO_MENSUAL decimal(14,2),
    in P_JUSTIFICACION varchar(500),
    in P_MODIFICADO_POR varchar(100)

)
modifies sql data
update PRESUPUESTO_DETALLE
set MONTO_MENSUAL=P_MONTO_MENSUAL,
    Justificacion_del_monto=P_JUSTIFICACION,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por=P_MODIFICADO_POR
where Id_presupuesto_detalle=P_ID_DETALLE
;

--obtener id_presupuesto antes de borrar (usamos subconsulta en el UPDATE posterior)
create procedure SP_ELIMINAR_PRESUPUESTO_DETALLE
(

    in P_ID_DETALLE bigint

)
modifies sql data
delete from PRESUPUESTO_DETALLE
where Id_presupuesto_detalle=P_ID_DETALLE
;


create procedure SP_RECALCULAR_TOTALES_PARA_PRESUPUESTO_IDDETALLE
(
    in P_ID_PRESUPUESTO_DETALLE bigint
)
modifies sql data
update PRESUPUESTO
set 
    Total_de_ingresos=coalesce(
        (select sum(pd.Monto_mensual)
         from PRESUPUESTO_DETALLE pd
         join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
         join CATEGORIA c on s.Id_categoria=c.Id_categoria
         where pd.Id_presupuesto=(select Id_presupuesto from PRESUPUESTO_DETALLE where Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE)
           and c.Tipo_de_categoria='ingreso'), 0),
    Total_de_gastos=coalesce(
        (select sum(pd.Monto_mensual)
         from PRESUPUESTO_DETALLE pd
         join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
         join CATEGORIA c on s.Id_categoria=c.Id_categoria
         where pd.Id_presupuesto=(select Id_presupuesto from PRESUPUESTO_DETALLE where Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE)
           and c.Tipo_de_categoria='gasto'),0),
    Total_de_ahorro=coalesce(
        (select sum(pd.Monto_mensual)
         from PRESUPUESTO_DETALLE pd
         join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
         join CATEGORIA c on s.Id_categoria=c.Id_categoria
         where pd.Id_presupuesto=(select Id_presupuesto from PRESUPUESTO_DETALLE where Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE)
           and c.Tipo_de_categoria='ahorro'),0)
where Id_presupuesto=(select Id_presupuesto from PRESUPUESTO_DETALLE where Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE)
;




--recalcular totales para el presupuesto donde estaba ese detalle
create procedure SP_RECALCULAR_TOTALES_PARA_PRESUPUESTO_IDDETALLE_ELIMINADO
(

    in P_ID_PRESUPUESTO_DETALLE bigint

)
modifies sql data
--la subconsulta usa un LEFT JOIN con una tabla temporal derivada que guarda el id_presupuesto
update PRESUPUESTO p
set 
    Total_de_ingresos=Coalesce(
        (select sum(pd.Monto_mensual)
        from PRESUPUESTO_DETALLE pd
        join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
        join CATEGORIA c on s.Id_categoria=c.Id_categoria
        where pd.Id_presupuesto=(
            select pd2.Id_presupuesto from PRESUPUESTO_DETALLE pd2 where pd2.Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE
        )
        and c.Tipo_de_categoria='ingreso'),0),
    Total_de_gastos=Coalesce(
        (select sum(pd.Monto_mensual)
        from PRESUPUESTO_DETALLE pd 
        join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
        join CATEGORIA c on s.Id_categoria=C.Id_categoria
        where pd.Id_presupuesto=(
            select pd2.Id_presupuesto from PRESUPUESTO_DETALLE pd2 where pd2.Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE
        )
        and c.Tipo_de_categoria='gasto'),0),
    Total_de_ahorro=Coalesce(
        (select sum(pd.Monto_mensual)
        from PRESUPUESTO_DETALLE pd
        join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
        join CATEGORIA c on s.Id_categoria=C.Id_categoria
        where pd.Id_presupuesto=(
            select pd2.Id_presupuesto from PRESUPUESTO_DETALLE pd2 where pd2.Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE
        )
        and c.Tipo_de_categoria='ahorro'),0)
where Id_presupuesto=(
    --si el detalle ya fue borrado, a subconsulta anterior devolver null, por eso se usa coalesce
    coalesce(
        (select  pd2.Id_presupuesto from PRESUPUESTO_DETALLE pd2 where pd2.Id_presupuesto_detalle=P_ID_PRESUPUESTO_DETALLE),
        -1
    )
);
