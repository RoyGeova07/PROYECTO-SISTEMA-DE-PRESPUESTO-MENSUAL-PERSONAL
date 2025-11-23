/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 20 nov 2025
 */

Drop procedure if exists SP_INSERTAR_CATEGORIA;
Drop procedure if exists SP_ACTUALIZAR_CATEGORIA;
Drop procedure if exists SP_ELIMINAR_CATEGORIA;


--insertar categoria
CREATE PROCEDURE SP_INSERTAR_CATEGORIA
(

    in P_NOMBRE varchar(50),
    in P_DESCRIPCION varchar(500),
    in P_TIPO varchar(15),  
    in P_ID_USUARIO bigint,
    in P_CREADO_POR varchar(100)

)
modifies sql data
insert into CATEGORIA(Nombre,Descripcion_detallada,Tipo_de_categoria)values(P_NOMBRE,P_DESCRIPCION,P_TIPO);

create procedure SP_ACTUALIZAR_CATEGORIA
(

    in P_ID_CATEGORIA bigint,
    in P_NOMBRE varchar(50),
    in P_DESCRIPCION varchar(500),
    in P_MODIFICADO_POR varchar(100)

)
modifies sql data
    update CATEGORIA
set Nombre=P_NOMBRE,Descripcion_detallada=P_DESCRIPCION
where Id_categoria=P_ID_CATEGORIA;

--eliminar categoria, con validacion de subcategorias activas 
create procedure SP_ELIMINAR_CATEGORIA
(

    in P_ID_CATEGORIA bigint

)
modifies sql data
delete from CATEGORIA
where Id_categoria=P_ID_CATEGORIA

