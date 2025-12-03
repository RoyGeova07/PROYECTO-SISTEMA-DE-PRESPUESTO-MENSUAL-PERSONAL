DROP PROCEDURE IF EXISTS SP_INSERTAR_META;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_META;
DROP PROCEDURE IF EXISTS SP_ELIMINAR_META;
DROP FUNCTION IF EXISTS SP_CONSULTAR_META;
DROP FUNCTION IF EXISTS SP_LISTAR_METAS_USUARIO;


CREATE PROCEDURE SP_INSERTAR_META
(

	IN p_id_usuario BIGINT,
    IN p_id_subcategoria BIGINT,
    IN p_nombre VARCHAR(70),
    IN p_descripcion VARCHAR(500),
    IN p_monto_objetivo DECIMAL(14,2),
    IN p_fecha_inicio DATE,
    IN p_fecha_objetivo DATE,
    IN p_prioridad VARCHAR(10),
    IN p_creado_por VARCHAR(100)

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    DECLARE v_tipo_categoria VARCHAR(15);

	SELECT COUNT(*) INTO v_count FROM USUARIOS WHERE Id_usuario=p_id_usuario;
    IF v_count =0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El usuario indicado no existe';
    END IF;
	
	--validar subcategoria existe y es de tipo ahorro
	SELECT c.Tipo_de_categoria INTO v_tipo_categoria
	FROM SUBCATEGORIA s
	LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
	WHERE s.Id_subcategoria=p_id_subcategoria;
	
	IF v_tipo_categoria IS NULL THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La subcategoria indicada no existe';
    END IF;
	
	IF lower(v_tipo_categoria)<>'ahorro'THEN
		  SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'La subcategoria debe ser de tipo ''ahorro''';
    END IF;
	
	IF p_fecha_objetivo<=p_fecha_inicio THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La fecha objetivo debe ser posterior a la fecha de inicio';
    END IF;
	
	IF p_monto_objetivo<0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El monto objetivo debe ser mayor o igual a 0';
    END IF;
	
	--asegurar que no exista ya una meta ACTIVA/en_progreso para esa subcategoria
	IF exists(SELECT 1 FROM META_AHORRO m
	WHERE m.Id_subcategoria=p_id_subcategoria AND m.Estado='en_progreso')then
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ya existe una meta activa para esa subcategoría';
    END IF;
	
	
	INSERT INTO META_AHORRO
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
	
	)values(
	
		p_id_usuario,
        p_id_subcategoria,
        p_nombre,
        p_descripcion,
        p_monto_objetivo,
        0.00,--inicio con 0 ahorrado
        p_fecha_inicio,
        p_fecha_objetivo,
        LOWER(COALESCE(p_prioridad,'media')),
        'en_progreso',--al crear queda en_progreso por defecto
        CURRENT_TIMESTAMP,
        COALESCE(p_creado_por,'system')
        
    );
END;
	
	
CREATE PROCEDURE SP_ACTUALIZAR_META
(

	IN p_id_meta BIGINT,
    IN p_nombre VARCHAR(70),
    IN p_descripcion VARCHAR(500),
    IN p_monto_objetivo DECIMAL(14,2),
    IN p_fecha_objetivo DATE,
    IN p_prioridad VARCHAR(10),
    IN p_estado VARCHAR(10),
    IN p_modificado_por VARCHAR(100)

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    DECLARE v_monto_ahorrado DECIMAL(14,2);
	DECLARE v_fecha_inicio DATE;
	DECLARE v_subcategoria BIGINT;
	
	 SELECT COUNT(*)INTO v_count FROM META_AHORRO WHERE Id_Ahorro=p_id_meta;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La meta indicada no existe';
    END IF;
	 
	--aqui obtengo el monto_ahorrado actual para validar
	SELECT Monto_ahorrado INTO v_monto_ahorrado FROM META_AHORRO WHERE Id_Ahorro=p_id_meta;
	
	--si cambian montos, validar que monto_ahorrado <= nuevo objetivo
	 IF p_monto_objetivo IS NOT NULL AND v_monto_ahorrado>p_monto_objetivo THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='No se puede disminuir el objetivo por debajo del monto ya ahorrado';
     END IF;
	
	--si cambia fecha objetivo, validar que sea posterior a inicio actual
	 IF p_fecha_objetivo IS NOT NULL THEN
        SELECT Fecha_inicio INTO v_fecha_inicio FROM META_AHORRO WHERE Id_Ahorro=p_id_meta;
        IF p_fecha_objetivo<=v_fecha_inicio THEN
            SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La fecha objetivo debe ser posterior a la fecha de inicio';
        END IF;
    END IF;
	
	--si cambian estado a 'en_progreso' tengo q asegurarme que no hay otra meta en_progreso para la misma subcategoria
	IF p_estado='en_progreso'then
		SELECT Id_subcategoria INTO v_subcategoria FROM META_AHORRO WHERE Id_Ahorro = p_id_meta;
		IF exists(SELECT 1 FROM META_AHORRO m 
			WHERE m.Id_subcategoria=v_subcategoria
			AND m.Estado='en_progreso'
			AND m.Id_ahorro<>p_id_meta)THEN
	     SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Ya existe otra meta en progreso para la misma subcategoria';
        END IF;
    END IF;
	
	--ahora si actualizacionnnnnnnnnnnnnnnnnnnnnnnnnnnnnn,usare coalesce para para no sobrescribir con NULL si el parametro es NULL
	 UPDATE META_AHORRO
     SET
        Nombre=COALESCE(p_nombre, Nombre),
        Descripcion_detallada=COALESCE(p_descripcion, Descripcion_detallada),
        Monto_total_alcanzar=COALESCE(p_monto_objetivo, Monto_total_alcanzar),
        Fecha_objetivo=COALESCE(p_fecha_objetivo, Fecha_objetivo),
        Prioridad=COALESCE(LOWER(p_prioridad), Prioridad),
        Estado=COALESCE(p_estado, Estado),
        modificado_en=CURRENT_TIMESTAMP,
        modificado_por=COALESCE(p_modificado_por, modificado_por)
     WHERE Id_Ahorro=p_id_meta;
	
	--si despues de la actualizacion el monto_ahorrado >= objetivo, forzar estado completed
	UPDATE META_AHORRO
    SET Estado='completada'
    WHERE Id_Ahorro=p_id_meta
      AND Monto_ahorrado>= Monto_total_alcanzar;
END;

	

CREATE PROCEDURE SP_ELIMINAR_META
(

	IN p_id_meta BIGINT

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    DECLARE v_monto_ahorrado DECIMAL(14,2);
	
	SELECT COUNT(*) INTO v_count FROM META_AHORRO WHERE Id_Ahorro=p_id_meta;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La meta indicada no existe';
    END IF;
	
	SELECT Monto_ahorrado INTO v_monto_ahorrado FROM META_AHORRO WHERE Id_Ahorro = p_id_meta;
	
	--para prevenir eliminacion si ya tiene ahorro acumulado (evitar perder historial)
	 IF v_monto_ahorrado>0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='No se puede eliminar una meta que ya tiene monto ahorrado; cancelela en su lugar';
     END IF;
	
	DELETE FROM META_AHORRO WHERE Id_Ahorro=p_id_meta;
	
END;
	


CREATE FUNCTION SP_CONSULTAR_META(p_id_meta BIGINT)
RETURNS table
(

	Id_Ahorro BIGINT,
    Id_usuario BIGINT,
    Id_subcategoria BIGINT,
    Nombre VARCHAR(70),
    Descripcion_detallada VARCHAR(500),
    Monto_total_alcanzar DECIMAL(14,2),
    Monto_ahorrado DECIMAL(14,2),
    Porcentaje_avance DECIMAL(5,2),
    Fecha_inicio DATE,
    Fecha_objetivo DATE,
    Prioridad VARCHAR(10),
    Estado VARCHAR(10),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count FROM META_AHORRO WHERE Id_Ahorro=p_id_meta;

    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La meta indicada no existe';
    END IF;
    
    RETURN TABLE
    (
    
    	SELECT
            Id_Ahorro,
            Id_usuario,
            Id_subcategoria,
            Nombre,
            Descripcion_detallada,
            Monto_total_alcanzar,
            Monto_ahorrado,
            CASE WHEN Monto_total_alcanzar=0 THEN 0 
            ELSE round((Monto_ahorrado/Monto_total_alcanzar)*100,2)
            END AS Porcentaje_avance,
            Fecha_inicio,
            Fecha_objetivo,
            Prioridad,
            Estado,
            creado_en,
            modificado_en,
            creado_por,
            modificado_por
        FROM META_AHORRO
        WHERE Id_Ahorro=p_id_meta
        
    );
    
END;
    
   
	

CREATE FUNCTION SP_LISTAR_METAS_USUARIO(p_id_usuario BIGINT, p_estado VARCHAR(10))
RETURNS table
(

	Id_Ahorro BIGINT,
    Id_usuario BIGINT,
    Id_subcategoria BIGINT,
    Nombre VARCHAR(70),
    Descripcion_detallada VARCHAR(500),
    Monto_total_alcanzar DECIMAL(14,2),
    Monto_ahorrado DECIMAL(14,2),
    Porcentaje_avance DECIMAL(5,2),
    Fecha_inicio DATE,
    Fecha_objetivo DATE,
    Prioridad VARCHAR(10),
    Estado VARCHAR(10),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count FROM USUARIOS WHERE Id_usuario= p_id_usuario;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El usuario indicado no existe';
    END IF;

	RETURN TABLE
	(
	
		SELECT
            Id_Ahorro,
            Id_usuario,
            Id_subcategoria,
            Nombre,
            Descripcion_detallada,
            Monto_total_alcanzar,
            Monto_ahorrado,
            CASE WHEN Monto_total_alcanzar=0 THEN 0 
            ELSE round((Monto_ahorrado/Monto_total_alcanzar)*100,2)
            END AS Porcentaje_avance,
			Fecha_inicio,
            Fecha_objetivo,
            Prioridad,
            Estado,
            creado_en,
            modificado_en,
            creado_por,
            modificado_por
        FROM META_AHORRO
        WHERE Id_usuario=p_id_usuario
          AND(p_estado IS NULL OR Estado=p_estado)
        ORDER BY creado_en DESC
        
    );
    
END;

ALTER TABLE META_AHORRO ALTER COLUMN Estado SET DATA TYPE VARCHAR(20);
	
	
	

	
	
	
	
	
	-- Ejemplo: usuario 1, subcategoria de tipo 'ahorro' con id 10 
CALL SP_INSERTAR_META(
  2,                     -- p_id_usuario
  6,                    -- p_id_subcategoria tipo 'ahorro')
  'Ahorro Vacaciones',   -- p_nombre
  'Ahorro para vacaciones 2025', -- p_descripcion
  15000.00,              -- p_monto_objetivo
  DATE '2025-01-01',     -- p_fecha_inicio
  DATE '2025-12-31',     -- p_fecha_objetivo
  'alta',                -- p_prioridad (alta|media|baja)
  'royum'                -- p_creado_por
);

	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	