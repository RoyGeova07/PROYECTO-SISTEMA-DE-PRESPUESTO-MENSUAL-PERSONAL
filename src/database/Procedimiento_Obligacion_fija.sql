/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 24 nov 2025
 */

drop procedure if exists SP_INSERTAR_OBLIGACION;
drop procedure if exists SP_ACTUALIZAR_OBLIGACION;
drop procedure if exists SP_ELIMINAR_OBLIGACION;

create procedure SP_INSERTAR_OBLIGACION
(
    in P_ID_USUARIO bigint,
    in P_ID_SUBCATEGORIA bigint,
    in P_NOMBRE varchar(70),
    in P_DESCRIPCION varchar(500),
    in P_MONTO decimal(14,2),
    in P_DIA_VENCIMIENTO smallint,
    in P_FECHA_INICIO date,
    in P_FECHA_FIN date,
    in P_CREADO_POR varchar(100)
)
modifies sql data
--valida existencia subcategoria y que la categoria sea 'gasto', y valida dia y fechas
insert into OBLIGACION_FIJA
(
    Id_usuario,Id_subcategoria,Nombre,Descripcion_detallada,
    Monto_fijo_mensual,Dia_del_mes_de_vencimiento,Fecha_inicio_de_la_obligacion,
    Fecha_de_finalizacion,Esta_vigente,creado_en,creado_por
)
select
    P_ID_USUARIO,
    P_ID_SUBCATEGORIA,
    P_NOMBRE,
    P_DESCRIPCION,
    P_MONTO,
    P_DIA_VENCIMIENTO,
    P_FECHA_INICIO,
    P_FECHA_FIN,
    true,
    CURRENT_TIMESTAMP,
    P_CREADO_POR
from SUBCATEGORIA sc
join CATEGORIA c on sc.Id_categoria=c.Id_categoria
where sc.Id_subcategoria=P_ID_SUBCATEGORIA
  and c.Tipo_de_categoria='gasto'
  and P_DIA_VENCIMIENTO between 1 and 31
  and(P_FECHA_FIN is null or P_FECHA_FIN>P_FECHA_INICIO);



create procedure SP_ACTUALIZAR_OBLIGACION
(
    in P_ID_OBLIGACION bigint,
    in P_NOMBRE varchar(70),
    in P_DESCRIPCION varchar(500),
    in P_MONTO decimal(14,2),
    in P_DIA_VENCIMIENTO smallint,
    in P_FECHA_FIN date,
    in P_ACTIVO boolean,
    in P_MODIFICADO_POR varchar(100)
)
modifies sql data
update OBLIGACION_FIJA
set
    Nombre=P_NOMBRE,
    Descripcion_detallada=P_DESCRIPCION,
    Monto_fijo_mensual=P_MONTO,
    Dia_del_mes_de_vencimiento=P_DIA_VENCIMIENTO,
    Fecha_de_finalizacion=P_FECHA_FIN,
    Esta_vigente=P_ACTIVO,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por=P_MODIFICADO_POR
where Id_obligacion_fija=P_ID_OBLIGACION
  and(P_DIA_VENCIMIENTO between 1 and 31)
  and(P_FECHA_FIN is null or(select Fecha_inicio_de_la_obligacion from OBLIGACION_FIJA where Id_obligacion_fija=P_ID_OBLIGACION)<P_FECHA_FIN);

create procedure SP_ELIMINAR_OBLIGACION
(
    in P_ID_OBLIGACION bigint
)
modifies sql data
update OBLIGACION_FIJA
set Esta_vigente=false,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por='system'
where Id_obligacion_fija=P_ID_OBLIGACION;


