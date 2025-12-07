Drop procedure if exists SP_INSERTAR_CATEGORIA;
Drop procedure if exists SP_ACTUALIZAR_CATEGORIA;
Drop procedure if exists SP_ELIMINAR_CATEGORIA;
Drop function if exists SP_CONSULTAR_CATEGORIA;

--insertar categoria
CREATE PROCEDURE SP_INSERTAR_CATEGORIA

(

    in P_NOMBRE varchar(50),
    in P_DESCRIPCION varchar(500),
    in P_TIPO varchar(15),  
    in P_ID_USUARIO bigint

)
modifies sql DATA
BEGIN ATOMIC 
	INSERT INTO CATEGORIA
	(
	
        Nombre,
        Descripcion_detallada,
        Tipo_de_categoria,
        creado_en,
        creado_por
        
    )
    VALUES
    (
    
        P_NOMBRE,
        P_DESCRIPCION,
        LOWER(P_TIPO),      
        CURRENT_TIMESTAMP,
        'royum'
        
    );
END;

create procedure SP_ACTUALIZAR_CATEGORIA
(

    in P_ID_CATEGORIA bigint,
    in P_NOMBRE varchar(50),
    in P_DESCRIPCION varchar(500),
    in P_MODIFICADO_POR varchar(100)

)
modifies sql data
    update CATEGORIA
set Nombre=P_NOMBRE,Descripcion_detallada=P_DESCRIPCION,modificado_en=CURRENT_TIMESTAMP,modificado_por='royum'
where Id_categoria=P_ID_CATEGORIA;

--eliminar categoria, con validacion de subcategorias activas 
create procedure SP_ELIMINAR_CATEGORIA
(

    in P_ID_CATEGORIA bigint

)
modifies sql DATA
BEGIN ATOMIC 
	IF exists(SELECT 1 FROM CATEGORIA WHERE Id_categoria=p_id_categoria)THEN
	
		 IF exists(SELECT 1 FROM SUBCATEGORIA WHERE Id_categoria=p_id_Categoria)THEN
		 	SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='No se puede eliminar la categoria: tiene subcategorias. Eliminelas o reasignelas primero.';
        ELSE
            DELETE FROM CATEGORIA WHERE Id_categoria=p_id_categoria;
        END IF;
	ELSE 
		SIGNAL SQLSTATE '45000'SET message_text='La categoria no existe';
	END IF;
END;
	
	

CREATE FUNCTION SP_CONSULTAR_CATEGORIA(p_id_categoria bigint)
RETURNS TABLE
(

	Id_categoria BIGINT,
    Nombre VARCHAR(50),
    Descripcion_detallada VARCHAR(500),
    Tipo_de_categoria VARCHAR(15),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
	IF exists(SELECT 1 FROM CATEGORIA WHERE Id_categoria=p_id_categoria)THEN
		RETURN TABLE
		(
		
			SELECT 
				Id_categoria,
                Nombre,
                Descripcion_detallada,
                Tipo_de_categoria,
                creado_en,
                modificado_en,
                creado_por,
                modificado_por
            FROM CATEGORIA
            WHERE Id_categoria=p_id_categoria
		
		);
	ELSE
	 SIGNAL SQLSTATE '45000'
	 	SET message_text='La categoria no existe';
	END IF;
END;

CALL SP_INSERTAR_CATEGORIA('Limpieza','Gastos de limpieza','gasto', 1);


TRUNCATE TABLE META_AHORRO RESTART IDENTITY;--para empezar de nuevo el conteo
