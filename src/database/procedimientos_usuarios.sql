/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 16 nov 2025
 */

/*

 Procedimientos almacenados para la tabla USUARIOS, en si es una sentencia q sirve para insertar o actulizar datos
 
  sp_insertar_usuario(p_nombre, p_apellido, p_email, p_salario_mensual, p_creado_por)
  sp_actualizar_usuario(p_id_usuario, p_nombre, p_apellido, p_salario_mensual, p_modificado_por)
  sp_consultar_usuario(p_id_usuario)
  sp_listar_usuarios()
        
*/

--por si ya existian, se eliminan primero 
DROP PROCEDURE IF EXISTS sp_insertar_usuario;
DROP PROCEDURE IF EXISTS sp_actualizar_usuario;
DROP PROCEDURE IF EXISTS sp_eliminar_usuario;

--para insertar el usuario
CREATE PROCEDURE sp_insertar_usuario(IN p_nombre VARCHAR(70),IN p_apellido VARCHAR(70),IN p_email VARCHAR(70),IN p_salario_mensual DECIMAL(14,2),IN p_creado_por VARCHAR(100))
MODIFIES SQL DATA

INSERT INTO USUARIOS(Nombre_usuario,Apellido_usuario,Correo_electronico,Fecha_Registro,Salario_mensual_base,Estado_usuario,creado_en,creado_por)
VALUES(p_nombre,p_apellido,p_email,CURRENT_TIMESTAMP,p_salario_mensual,TRUE,CURRENT_TIMESTAMP,p_creado_por);


--para actualizar el usuario
CREATE PROCEDURE sp_actualizar_usuario(IN p_id_usuario BIGINT,IN p_nombre VARCHAR(70),IN p_apellido VARCHAR(70),IN p_salario_mensual DECIMAL(14,2),IN p_modificado_por VARCHAR(100))
MODIFIES SQL DATA
UPDATE USUARIOS
SET Nombre_usuario=p_nombre,
    Apellido_usuario=p_apellido,
    Salario_mensual_base=p_salario_mensual,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por=p_modificado_por
WHERE Id_usuario=p_id_usuario;


--para eliminar usuarios descativando su estado de activo
CREATE PROCEDURE sp_eliminar_usuario(IN p_id_usuario BIGINT)
MODIFIES SQL DATA
UPDATE USUARIOS
SET Estado_usuario=FALSE,
    modificado_en=CURRENT_TIMESTAMP,
    modificado_por='sp_eliminar_usuario'
WHERE Id_usuario=p_id_usuario;
