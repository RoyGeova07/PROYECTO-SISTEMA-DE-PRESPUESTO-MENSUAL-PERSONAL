/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 22 nov 2025
 */

drop procedure if exists SP_INSERTAR_PRESUPUESTO;
drop procedure if exists SP_ACTUALIZAR_PRESUPUESTO;
drop procedure if exists SP_ELIMINAR_PRESUPUESTO;
drop procedure if exists SP_RECALCULA_TOTALES_PARA_PRESUPUESTO;


create procedure SP_INSERTAR_PRESUPUESTO
(

    IN P_ID_USUARIO BIGINT,
    IN P_NOMBRE VARCHAR(500),
    IN P_DESCRIPCION VARCHAR(500),
    IN P_ANIO_INICIO SMALLINT,
    IN P_MES_INICIO TINYINT,
    IN P_ANIO_FIN SMALLINT,
    IN P_MES_FIN TINYINT,
    IN P_CREADO_POR VARCHAR(100)

)
modifies sql data
insert into PRESUPUESTO
(

    Id_usuario,
    Nombre_descriptivo,
    Anio_de_inicio,
    Mes_de_inicio,
    Anio_de_fin,
    Mes_de_fin,
    Total_de_ingresos,
    Total_de_gastos,
    Total_de_ahorro,
    Fecha_hora_creacion,
    Estado_presupuesto,
    creado_en,
    creado_por

)
values
(

    P_ID_USUARIO,
    P_NOMBRE,
    P_ANIO_INICIO,
    P_MES_INICIO,
    P_ANIO_FIN,
    P_MES_FIN,
    0,--Total_de_ingresos(se ajustare despues)
    0,--Total_de_gastos
    0,--Total_de_ahorro
    CURRENT_TIMESTAMP,
    'activo',--siempre se crea en estado ACTIVO
    CURRENT_TIMESTAMP,
    P_CREADO_POR

);


create procedure SP_ACTUALIZAR_PRESUPUESTO
(

    IN P_ID_PRESUPUESTO BIGINT,
    IN P_NOMBRE VARCHAR(500),
    IN P_DESCRIPCION VARCHAR(500),
    IN P_ANIO_INICIO SMALLINT,
    IN P_MES_INICIO TINYINT,
    IN P_ANIO_FIN SMALLINT,
    IN P_MES_FIN TINYINT,
    IN P_MODIFICADO_POR VARCHAR(100)

)
modifies sql data
update PRESUPUESTO
set Nombre_descriptivo=P_NOMBRE,
    Anio_de_inicio=P_ANIO_INICIO,
    Mes_de_inicio=P_MES_INICIO,
    Anio_de_fin=P_ANIO_FIN,
    Mes_de_fin=P_MES_FIN,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por=P_MODIFICADO_POR
where Id_presupuesto=P_ID_PRESUPUESTO;


create procedure SP_ELIMINAR_PRESUPUESTO
(

    IN P_ID_PRESUPUESTO BIGINT

)
modifies sql data
delete from PRESUPUESTO
where Id_presupuesto=P_ID_PRESUPUESTO;

--con esto recalcula los 3 totales del presupuesto (ingresos,gastos,ahorros)
create procedure SP_RECALCULA_TOTALES_PARA_PRESUPUESTO
(

    in P_ID_PRESUPUESTO bigint

)
modifies sql data
update PRESUPUESTO
set 
    Total_de_ingresos=Coalesce(
        (select sum(pd.Monto_mensual)
        from PRESUPUESTO_DETALLE pd
        join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
        join CATEGORIA c on s.Id_categoria=c.Id_categoria
        where pd.Id_presupuesto=P_ID_PRESUPUESTO
            and c.Tipo_de_categoria='ingreso'),0),
    Total_de_gastos=Coalesce(
        (select sum(pd.Monto_mensual)
        from PRESUPUESTO_DETALLE pd 
        join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
        join CATEGORIA c on s.Id_categoria=C.Id_categoria
        where pd.Id_presupuesto=P_ID_PRESUPUESTO
            and c.Tipo_de_categoria='gasto'),0),
    Total_de_ahorro=Coalesce(
        (select sum(pd.Monto_mensual)
        from PRESUPUESTO_DETALLE pd
        join SUBCATEGORIA s on pd.Id_subcategoria=s.Id_subcategoria
        join CATEGORIA c on s.Id_categoria=C.Id_categoria
        where pd.Id_presupuesto=P_ID_PRESUPUESTO
            and c.Tipo_de_categoria='ahorro'),0)
where Id_presupuesto=P_ID_PRESUPUESTO
;