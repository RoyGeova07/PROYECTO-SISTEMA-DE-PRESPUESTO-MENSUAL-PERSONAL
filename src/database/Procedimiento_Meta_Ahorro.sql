/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 24 nov 2025
 */

drop procedure if exists SP_INSERTAR_META;
drop procedure if exists SP_ACTUALIZAR_META;
drop procedure if exists SP_ELIMINAR_META;


create procedure SP_INSERTAR_META
(

    in P_ID_USUARIO bigint,
    in P_ID_SUBCATEGORIA_AHORRO bigint,
    in P_NOMBRE varchar(70),
    in P_DESCRIPCION varchar(500),
    in P_MONTO_OBJETIVO decimal(14,2),
    in P_FECHA_INICIO date,
    in P_FECHA_OBJETIVO date,
    in P_PRIORIDAD varchar(10), --alta|media|baja
    in P_CREADO_POR varchar(100)

)
modifies sql data
--Si todas las comprobaciones pasan, insertamos. Usamos INSERT ... SELECT para validar en una sola operacion.
insert into META_AHORRO
(
    Id_usuario,
    Id_subcategoria,
    Nombre,
    Descripcion_detallada,
    Monto_total_alcanzar,
    Monto_ahorrado,
    Fecha_inicio,
    Fecha_objetivo,
    Prioridad,
    Estado,
    creado_en,
    creado_por
)
select
    P_ID_USUARIO,
    P_ID_SUBCATEGORIA_AHORRO,
    P_NOMBRE,
    P_DESCRIPCION,
    P_MONTO_OBJETIVO,
    0.00, --monto_ahorrado inicial
    P_FECHA_INICIO,
    P_FECHA_OBJETIVO,
    P_PRIORIDAD,
    'en_progreso', --estado inicial
    CURRENT_TIMESTAMP,
    P_CREADO_POR
from SUBCATEGORIA sc
join CATEGORIA c on sc.Id_categoria=c.Id_categoria
where sc.Id_subcategoria=P_ID_SUBCATEGORIA_AHORRO
  and c.Tipo_de_categoria= 'ahorro'
  and P_FECHA_OBJETIVO>P_FECHA_INICIO
  and not exists (
      select 1 from META_AHORRO m
      where m.Id_subcategoria=P_ID_SUBCATEGORIA_AHORRO
        and m.Estado='en_progreso'
  )
;
