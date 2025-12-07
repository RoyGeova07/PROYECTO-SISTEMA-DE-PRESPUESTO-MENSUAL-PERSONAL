DROP PROCEDURE IF EXISTS PUBLIC.SP_INSERTAR_SUBCATEGORIA;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_SUBCATEGORIA;
DROP PROCEDURE IF EXISTS SP_ELIMINAR_SUBCATEGORIA;
DROP FUNCTION IF EXISTS SP_CONSULTAR_SUBCATEGORIA;
DROP FUNCTION IF EXISTS SP_LISTAR_SUBCATEGORIAS_POR_CATEGORIA;

CREATE PROCEDURE SP_INSERTAR_SUBCATEGORIA
(

	IN p_id_categoria bigint,
	IN p_nombre varchar(70),
	in p_descripcion varchar(500),
	IN p_es_defecto boolean
	

)
MODIFIES SQL DATA
BEGIN ATOMIC

	--si existe la categoria, se inserta la subcategoria 
	IF exists(SELECT 1 FROM CATEGORIA WHERE Id_categoria=p_id_categoria)THEN
		
	
		IF p_es_defecto IS TRUE THEN
			UPDATE SUBCATEGORIA
			SET Por_defecto=FALSE
			WHERE Id_categoria=p_id_categoria AND Por_defecto=true;
		END IF;
		
		INSERT INTO SUBCATEGORIA
		(
		
			Id_categoria,
            Nombre_subcategoria,
            Descripcion_detallada,
            Estado,
            Por_defecto,
            creado_en,
            creado_por
		
		)
		VALUES
		(
		
			p_id_categoria,
            p_nombre,
            p_descripcion,
            TRUE,
            COALESCE(p_es_defecto, FALSE),
            CURRENT_TIMESTAMP,
            'royum'
		
		);
	ELSE 
		SIGNAL SQLSTATE'45000'SET message_text='La categoria indicada no existe';
	END IF;
END;

CREATE PROCEDURE SP_ACTUALIZAR_SUBCATEGORIA
(

	IN p_id_subcategoria bigint,
	IN p_nombre varchar(70),
	IN p_descripcion varchar(500),
	IN p_es_defecto boolean

)
MODIFIES SQL DATA
BEGIN ATOMIC
	DECLARE v_id_categoria BIGINT;
	IF exists(SELECT 1 FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria)THEN
	
		IF p_es_defecto IS TRUE THEN
			  
    	    SELECT Id_categoria INTO v_id_categoria FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria;

	        UPDATE SUBCATEGORIA
	        SET Por_defecto=FALSE
    	    WHERE Id_categoria=v_id_categoria AND Por_defecto=TRUE;
	    END IF;
	
		UPDATE SUBCATEGORIA
		SET Nombre_subcategoria=p_nombre,
			Descripcion_detallada=p_descripcion,
			por_defecto=coalesce(p_es_defecto,por_defecto),
            modificado_por='royum',
            modificado_en=CURRENT_TIMESTAMP
        WHERE Id_subcategoria=p_id_subcategoria;
	ELSE 
		SIGNAL SQLSTATE'45000'SET message_text='la subcategoria indicada no existe';
	END IF;
END;
	
CREATE PROCEDURE SP_ELIMINAR_SUBCATEGORIA
(

	IN p_id_subcategoria bigint

)
MODIFIES SQL DATA
BEGIN ATOMIC
	DECLARE v_id_categoria bigint;
    DECLARE v_cant_activas int;

	--si existe la subcategoria
	IF exists(SELECT 1 FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria)THEN
		SELECT id_categoria into v_id_categoria FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria;
	
		IF exists(SELECT 1 FROM PRESUPUESTO_DETALLE WHERE Id_subcategoria=p_id_subcategoria)THEN
			SIGNAL SQLSTATE '45000'SET message_text='No se puede eliminar la subcategoria, esta usada en presupuestos';
		END IF;
		
		IF exists(SELECT 1 FROM TRANSACCION WHERE Id_subcategoria=p_id_subcategoria)then
			SIGNAL SQLSTATE '45000'SET message_text='No se puede eliminar la subcategoria, esta usada en transacciones';
		END IF;
		
		SELECT Id_categoria INTO v_id_categoria FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria;
		
		--la categoria quedara con subcategorias activas
		SELECT count(*)INTO v_cant_activas
		FROM SUBCATEGORIA
		WHERE Id_categoria=v_id_categoria
			AND Estado=TRUE
			AND Id_subcategoria<>p_id_subcategoria;
		
		IF v_cant_activas>0 THEN
			--borrar todo
			DELETE FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria;
		ELSE
			SIGNAL SQLSTATE'45000'SET MESSAGE_TEXT='No se puede eliminar: la categoria debe tener al menos una subcategoria activa';
		END IF;
		
	ELSE 
		SIGNAL SQLSTATE'45000'SET MESSAGE_TEXT='La subcategoria indicada no existe';
	END IF;
END;
		
		
CREATE FUNCTION SP_CONSULTAR_SUBCATEGORIA(p_id_subcategoria bigint)
RETURNS TABLE
	(
	
		Id_subcategoria BIGINT,
	    Id_categoria BIGINT,
	    Nombre_subcategoria VARCHAR(70),
	    Descripcion_detallada VARCHAR(500),
	    Estado BOOLEAN,
	    Por_defecto BOOLEAN,
	    Categoria_nombre VARCHAR(50),
	    Categoria_creado_en TIMESTAMP,
	    Creado_en TIMESTAMP,
	    Creado_por VARCHAR(100),
	    Modificado_en TIMESTAMP,
	    Modificado_por VARCHAR(100)
		
	)
READS SQL DATA	
BEGIN ATOMIC
	IF exists(SELECT 1 FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria)THEN
		RETURN TABLE
		(
			select
			 s.Id_subcategoria AS Id_subcategoria,
                s.Id_categoria AS Id_categoria,
                s.Nombre_subcategoria AS Nombre_subcategoria,
                s.Descripcion_detallada AS Descripcion_detallada,
                s.Estado AS Estado,
                s.Por_defecto AS Por_defecto,
                c.Nombre AS Categoria_nombre,
                c.creado_en AS Categoria_creado_en,
                s.creado_en AS Creado_en,
                s.creado_por AS Creado_por,
                s.modificado_en AS Modificado_en,
                s.modificado_por AS Modificado_por
            FROM SUBCATEGORIA s
            LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
            WHERE s.Id_subcategoria=p_id_subcategoria
		
		);
	ELSE
		SIGNAL SQLSTATE'45000'SET message_text='la subcategoria no existe';
	END IF;
END;
		

CREATE FUNCTION SP_LISTAR_SUBCATEGORIAS_POR_CATEGORIA(p_id_categoria BIGINT)
RETURNS TABLE
(
    Id_subcategoria BIGINT,
    Id_categoria BIGINT,
    Nombre_subcategoria VARCHAR(70),
    Descripcion_detallada VARCHAR(500),
    Estado BOOLEAN,
    Por_defecto BOOLEAN,
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100),
    Categoria_nombre varchar(100)
)
READS SQL DATA
BEGIN ATOMIC
    IF EXISTS (SELECT 1 FROM CATEGORIA WHERE Id_categoria=p_id_categoria)THEN
        RETURN TABLE(
            SELECT
                s.Id_subcategoria,
                s.Id_categoria,
                s.Nombre_subcategoria,
                s.Descripcion_detallada,
                s.Estado,
                s.Por_defecto,
                s.creado_en,
                s.modificado_en,
                s.creado_por,
                s.modificado_por,
                c.Nombre AS Categoria_nombre
            FROM SUBCATEGORIA s
            LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
            WHERE s.Id_categoria=p_id_categoria
            ORDER BY s.Id_subcategoria
        );
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La categoría no existe';
    END IF;
END;
		
		

		
		
		
		
		

	
	
	