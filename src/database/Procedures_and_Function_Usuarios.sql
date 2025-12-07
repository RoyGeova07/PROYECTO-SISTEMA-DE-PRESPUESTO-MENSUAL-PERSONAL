

DROP PROCEDURE IF EXISTS sp_insertar_usuario;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_USUARIO;
DROP PROCEDURE IF EXISTS sp_eliminar_usuario;
DROP FUNCTION  IF EXISTS sp_consultar_usuario;
DROP FUNCTION IF EXISTS sp_listar_usuarios;
DROP PROCEDURE IF EXISTS sp_reactivar_usuario;


--TRUNCATE TABLE USUARIOS RESTART IDENTITY;

--FUNCIONES PARA LOS USUARIOSSSS
CREATE PROCEDURE sp_insertar_usuario(IN p_nombre VARCHAR(70),
IN p_apellido VARCHAR(70),
IN p_email VARCHAR(70),
IN p_salario_mensual DECIMAL(14,2))
MODIFIES SQL DATA
BEGIN ATOMIC 
	IF EXISTS(SELECT 1 FROM USUARIOS WHERE Correo_electronico=p_email)THEN 
	SIGNAL SQLSTATE '45000' 
		SET message_text='El correo ya existe en la tabla de usuarios. Por favor escoja otro.';
	ELSE 
		INSERT INTO USUARIOS(Nombre_usuario,Apellido_usuario,Correo_electronico,Fecha_registro,Salario_mensual_base,Estado_usuario,creado_en,creado_por)
		values(p_nombre,p_apellido,p_email,CURRENT_TIMESTAMP,p_salario_mensual,TRUE,CURRENT_TIMESTAMP,'royum');
	END IF;
END; 


CREATE PROCEDURE SP_ACTUALIZAR_USUARIO
(

  IN p_id_usuario BIGINT,
  IN p_nombre VARCHAR(70),
  IN p_apellido VARCHAR(70),
  IN p_salario_mensual DECIMAL(14,2)
  
)
MODIFIES SQL DATA
BEGIN ATOMIC
  UPDATE USUARIOS
  SET Nombre_usuario=p_nombre,
      Apellido_usuario=p_apellido,
      Salario_mensual_base=p_salario_mensual,
      modificado_en=CURRENT_TIMESTAMP,
      modificado_por='royum'
  WHERE Id_usuario=p_id_usuario;
END;


CREATE PROCEDURE sp_eliminar_usuario(IN p_id_usuario bigint) 
MODIFIES SQL DATA 
BEGIN ATOMIC 
IF EXISTS(SELECT 1 FROM USUARIOS WHERE Id_usuario=p_id_usuario)THEN
	UPDATE USUARIOS
	SET Estado_usuario=FALSE,
		modificado_en=current_timestamp,
		modificado_por='sp_eliminar_usuario'
	WHERE Id_usuario=p_id_usuario;
ELSE 
	SIGNAL SQLSTATE '45000' SET message_text='El usuario que se quiere eliminar no existe';
	
END IF;
END; 


CREATE FUNCTION sp_consultar_usuario(p_id_usuario bigint)
RETURNS table(

	Id_usuario bigint,
	Nombre_usuario varchar(70),
	Apellido_usuario varchar(70),
	Correo_electronico varchar(70),
	Fecha_registro timestamp,
	Salario_mensual_base decimal(14,2)
	
)
READS SQL DATA 
BEGIN ATOMIC 

	IF exists(SELECT 1 FROM USUARIOS WHERE Id_usuario=p_id_usuario)THEN
		RETURN TABLE(
		SELECT
			Id_usuario,
			Nombre_usuario,
            Apellido_usuario,
            Correo_electronico,
            Fecha_registro,
            Salario_mensual_base
            FROM USUARIOS
            WHERE Id_usuario=p_id_usuario);
	ELSE
		SIGNAL SQLSTATE '45000'
			SET message_text='El id de usuario que ingreso no existe';
	END IF;
	
END;

CREATE FUNCTION sp_listar_usuarios()
RETURNS table(

	Id_usuario bigint,
	Nombre_usuario varchar(70),
	Apellido_usuario varchar(70),
	Correo_electronico varchar(70),
	Fecha_registro timestamp,
	Salario_mensual_base decimal(14,2),
	Estado_usuario boolean,
	creado_en timestamp,
	creado_por varchar(100),
	modificado_en timestamp,
	modificado_por varchar(100)
	
)
READS SQL DATA 
BEGIN ATOMIC 
	RETURN TABLE(
	SELECT 
	Id_usuario,
	Nombre_usuario,
	Apellido_usuario,
	Correo_electronico,
	Fecha_registro,
	Salario_mensual_base,
	Estado_usuario,
	creado_en,
	creado_por,
	modificado_en,
	modificado_por
	
	FROM USUARIOS 
	ORDER BY Id_usuario
);
END;

CREATE PROCEDURE sp_reactivar_usuario
(

	IN p_id_usuario bigint,
	IN p_modificado_por varchar(100)

)
MODIFIES SQL DATA
BEGIN atomic
	IF exists(SELECT 1 FROM USUARIOS WHERE Id_usuario=p_id_usuario)THEN
		UPDATE USUARIOS
		SET Estado_usuario=TRUE,
		modificado_en=current_timestamp,
		modificado_por='royum'
		WHERE Id_usuario=p_id_usuario;
	ELSE
		SIGNAL SQLSTATE '45000'
			SET message_text='el id usuario que se intenta reactivar no existe';
	END IF;
END;

---------------------------------FIN DE FUNCIONES PARA LOS USUARIOS--------------------------------------------------



--DROP FUNCTION IF EXISTS sp_consultar_usuarioA;
--DROP FUNCTION IF EXISTS sp_listar_usuariosA;

/*
--FUNCTION: consultar usuario (solo devuelve si Estado_usuario = TRUE)
CREATE FUNCTION sp_consultar_usuarioA(p_id_usuario BIGINT)
RETURNS TABLE(
    Id_usuario BIGINT,
    Nombre_usuario VARCHAR(70),
    Apellido_usuario VARCHAR(70),
    Correo_electronico VARCHAR(70),
    Fecha_registro TIMESTAMP,
    Salario_mensual_base DECIMAL(14,2)
)
READS SQL DATA
BEGIN ATOMIC
    --Comprobar existencia del usuario activo
    IF EXISTS (SELECT 1 FROM USUARIOS WHERE Id_usuario = p_id_usuario AND Estado_usuario = TRUE) THEN
        RETURN TABLE(
            SELECT
                Id_usuario,
                Nombre_usuario,
                Apellido_usuario,
                Correo_electronico,
                Fecha_registro,
                Salario_mensual_base
            FROM USUARIOS
            WHERE Id_usuario = p_id_usuario
              AND Estado_usuario = TRUE
        );
    ELSE
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El id de usuario que ingresó no existe o está desactivado';
    END IF;
END;
/


--FUNCTION: listar usuarios (solo activos)
CREATE FUNCTION sp_listar_usuariosA()
RETURNS TABLE(
    Id_usuario BIGINT,
    Nombre_usuario VARCHAR(70),
    Apellido_usuario VARCHAR(70),
    Correo_electronico VARCHAR(70),
    Fecha_registro TIMESTAMP,
    Salario_mensual_base DECIMAL(14,2),
    Estado_usuario BOOLEAN,
    creado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_en TIMESTAMP,
    modificado_por VARCHAR(100)
)
READS SQL DATA
BEGIN ATOMIC
    RETURN TABLE(
        SELECT
            Id_usuario,
            Nombre_usuario,
            Apellido_usuario,
            Correo_electronico,
            Fecha_registro,
            Salario_mensual_base,
            Estado_usuario,
            creado_en,
            creado_por,
            modificado_en,
            modificado_por
        FROM USUARIOS
        WHERE Estado_usuario = TRUE
        ORDER BY Id_usuario
    );
END;
/

*/


