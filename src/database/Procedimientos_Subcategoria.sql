/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 22 nov 2025
 */

/* 
 * Author:  royum
 * Created: 22 nov 2025
 */

drop procedure if exists SP_INSERTAR_SUBCATEGORIA;
drop procedure if exists SP_ACTUALIZAR_SUBCATEGORIA;
drop procedure if exists SP_ELIMINAR_SUBCATEGORIA;

-- insertar subcategoria
create procedure SP_INSERTAR_SUBCATEGORIA
(
    in P_ID_CATEGORIA   bigint,
    in P_NOMBRE varchar(70),
    in P_DESCRIPCION varchar(500),
    in P_ES_DEFECTO boolean,
    in P_CREADO_POR varchar(100)
)
modifies sql data
insert into SUBCATEGORIA(
    Id_categoria,
    Nombre_subcategoria,
    Descripcion_detallada,
    Estado,
    Por_defecto,
    creado_en,
    creado_por
)
values(
    P_ID_CATEGORIA,
    P_NOMBRE,
    P_DESCRIPCION,
    true,
    P_ES_DEFECTO,
    current_timestamp,
    P_CREADO_POR
);


-- actualizar subcategoria
create procedure SP_ACTUALIZAR_SUBCATEGORIA
(
    in P_ID_SUBCATEGORIA bigint,
    in P_NOMBRE varchar(70),
    in P_DESCRIPCION varchar(500),
    in P_MODIFICADO_POR varchar(100)
)
modifies sql data
update SUBCATEGORIA
set Nombre_subcategoria=P_NOMBRE,
    Descripcion_detallada=P_DESCRIPCION,
    modificado_en=current_timestamp,
    modificado_por=P_MODIFICADO_POR
where Id_subcategoria=P_ID_SUBCATEGORIA;


-- eliminar subcategoria (el uso se valida desde Java)
create procedure SP_ELIMINAR_SUBCATEGORIA
(

    in P_ID_SUBCATEGORIA bigint

)
modifies sql data
delete from SUBCATEGORIA
where Id_subcategoria=P_ID_SUBCATEGORIA;
