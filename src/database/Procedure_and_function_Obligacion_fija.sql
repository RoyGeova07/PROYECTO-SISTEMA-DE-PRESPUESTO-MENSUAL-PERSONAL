DROP PROCEDURE IF EXISTS SP_INSERTAR_OBLIGACION;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_OBLIGACION;
DROP PROCEDURE IF EXISTS SP_ELIMINAR_OBLIGACION;
DROP FUNCTION IF EXISTS SP_CONSULTAR_OBLIGACION;
DROP FUNCTION IF EXISTS SP_LISTAR_OBLIGACIONES_USUARIO;

CREATE PROCEDURE SP_INSERTAR_OBLIGACION
(

	IN p_id_usuario BIGINT,
    IN p_id_subcategoria BIGINT,
    IN p_nombre VARCHAR(70),
    IN p_descripcion VARCHAR(500),
    IN p_monto DECIMAL(14,2),
    IN p_dia_vencimiento SMALLINT,
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE

)
MODIFIES SQL DATA
BEGIN ATOMIC 
	DECLARE v_count INT;
    DECLARE v_tipo_categoria VARCHAR(15);
	 SELECT COUNT(*) INTO v_count FROM USUARIOS WHERE Id_usuario=p_id_usuario;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El usuario indicado no existe';
    END IF;

	--validar q la subcategoria existe  y obtener tipo de categoria usando join
	SELECT c.Tipo_de_categoria INTO v_tipo_categoria
    FROM SUBCATEGORIA s
    LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
    WHERE s.Id_subcategoria=p_id_subcategoria;
	
	 IF v_tipo_categoria IS NULL THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La subcategoria indicada no existe';
    END IF;
	 
	 --ahora validar que la subcategoria sea de tipo gasto
	 IF lower(v_tipo_categoria)<>'gasto'THEN
	 	 SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La subcategoroa asociada debe ser de tipo ''gasto'' para una obligacion fija';
	 END IF;
	 
	 --validar dia de vencimiento 
	 IF p_dia_vencimiento<1 OR p_dia_vencimiento>31 THEN
	 	 SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El día de vencimiento debe estar entre 1 y 31';
     END IF;
	 
	 --validar fechas, si fecha_fin no es null debe ser mayor que fecha_inicio
	 IF p_fecha_fin IS NOT NULL THEN
        IF p_fecha_fin<=p_fecha_inicio THEN
            SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La fecha de finalizacion debe ser posterior a la fecha de inicio';
        END IF;
     END IF;
	 
	 IF p_monto<0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El monto no puede ser negativo';
     END IF;
	 
	 --validar unicidaddd
	 SELECT count(*)INTO v_count FROM OBLIGACION_FIJA WHERE Id_subcategoria=p_id_subcategoria;
	  IF v_count>0 THEN
        SIGNAL SQLSTATE'45000'SET MESSAGE_TEXT='Ya existe una obligacion asociada a esa subcategoria';
      END IF;
	 
	 INSERT INTO OBLIGACION_FIJA
	 (
	 
	 	Id_usuario,
        Id_subcategoria,
        Nombre,
        Descripcion_detallada,
        Monto_fijo_mensual,
        Dia_del_mes_de_vencimiento,
        Esta_vigente,
        Fecha_inicio_de_la_obligacion,
        Fecha_de_finalizacion,
        creado_en,
        creado_por
	 
	 )values(
	 
	 	 p_id_usuario,
        p_id_subcategoria,
        p_nombre,
        p_descripcion,
        p_monto,
        p_dia_vencimiento,
        TRUE,
        p_fecha_inicio,
        p_fecha_fin,
        CURRENT_TIMESTAMP,
        'royum'
	 
	 );
END;

CREATE PROCEDURE SP_ACTUALIZAR_OBLIGACION
(

	IN p_id_obligacion BIGINT,
    IN p_nombre VARCHAR(70),
    IN p_descripcion VARCHAR(500),
    IN p_monto DECIMAL(14,2),
    IN p_dia_vencimiento SMALLINT,
    IN p_fecha_fin DATE,
    IN p_activo BOOLEAN,
    IN p_modificado_por VARCHAR(100)

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
	DECLARE v_fecha_inicio DATE;
	 SELECT COUNT(*) INTO v_count FROM OBLIGACION_FIJA WHERE Id_obligacion_fija=p_id_obligacion;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La obligacion indicada no existe';
    END IF;

	IF p_monto<0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El monto no puede ser negativo';
    END IF;
    IF p_dia_vencimiento<1 OR p_dia_vencimiento>31 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El día de vencimiento debe estar entre 1 y 31';
    END IF;
	
	--si fecha_fin no es null, obtener fecha_inicio para comparar
	IF p_fecha_fin IS NOT NULL THEN
        SELECT Fecha_inicio_de_la_obligacion INTO v_fecha_inicio FROM OBLIGACION_FIJA WHERE Id_obligacion_fija=p_id_obligacion;
        IF p_fecha_fin<=v_fecha_inicio THEN
            SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La fecha de finalizacion debe ser posterior a la fecha de inicio';
        END IF;
    END IF;
	
	UPDATE OBLIGACION_FIJA
    SET Nombre=p_nombre,
        Descripcion_detallada=p_descripcion,
        Monto_fijo_mensual=p_monto,
        Dia_del_mes_de_vencimiento=p_dia_vencimiento,
        Fecha_de_finalizacion=p_fecha_fin,
        Esta_vigente=COALESCE(p_activo, Esta_vigente),
        modificado_en=CURRENT_TIMESTAMP,
        modificado_por='royum'
    WHERE Id_obligacion_fija=p_id_obligacion;
END;


CREATE PROCEDURE SP_ELIMINAR_OBLIGACION
(

	IN p_id_obligacion BIGINT

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;

	SELECT count(*) INTO v_count FROM OBLIGACION_FIJA WHERE Id_obligacion_fija=p_id_obligacion;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La obligacion indicada no existe';
    END IF;
	
	--en lugar de borrar hacemos un soft-delete: Esta_vigente = FALSE
	UPDATE OBLIGACION_FIJA
    SET Esta_vigente=FALSE,
        modificado_en=CURRENT_TIMESTAMP,
        modificado_por='royum'
    WHERE Id_obligacion_fija=p_id_obligacion;
END;


CREATE FUNCTION SP_CONSULTAR_OBLIGACION(p_id_obligacion BIGINT)
RETURNS TABLE 
(

	Id_obligacion_fija BIGINT,
    Id_usuario BIGINT,
    Id_subcategoria BIGINT,
    Nombre varchar(70),
    Descripcion_detallada varchar(500),
    Monto_fijo_mensual DECIMAL(14,2),
    Dia_del_mes_de_vencimiento SMALLINT,
    Esta_vigente BOOLEAN,
    Fecha_inicio_de_la_obligacion DATE,
    Fecha_de_finalizacion DATE,
    Nombre_subcategoria VARCHAR(100),
    Id_categoria BIGINT,
    Nombre_categoria VARCHAR(100),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
	 SELECT COUNT(*) INTO v_count FROM OBLIGACION_FIJA WHERE Id_obligacion_fija =p_id_obligacion;
    IF v_count = 0 THEN
        SIGNAL SQLSTATE'45000' SET MESSAGE_TEXT= 'La obligación indicada no existe';
    END IF;

	RETURN TABLE
	(
	
		 SELECT
            o.Id_obligacion_fija,
            o.Id_usuario,
            o.Id_subcategoria,
            o.Nombre,
            o.Descripcion_detallada,
            o.Monto_fijo_mensual,
            o.Dia_del_mes_de_vencimiento,
            o.Esta_vigente,
            o.Fecha_inicio_de_la_obligacion,
            o.Fecha_de_finalizacion,
            s.Nombre_subcategoria,
            s.Id_categoria,
            c.Nombre AS Nombre_categoria,
            o.creado_en,
            o.modificado_en,
            o.creado_por,
            o.modificado_por
         FROM OBLIGACION_FIJA O
         LEFT JOIN SUBCATEGORIA s ON  s.Id_subcategoria=o.Id_subcategoria
         LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
         WHERE o.Id_obligacion_fija=p_id_obligacion
	
	);
END;

CREATE FUNCTION SP_LISTAR_OBLIGACIONES_USUARIO(p_id_usuario BIGINT, p_activo BOOLEAN)
RETURNS table
(

	Id_obligacion_fija BIGINT,
    Id_usuario BIGINT,
    Id_subcategoria BIGINT,
    Nombre varchar(70),
    Descripcion_detallada varchar(500),
    Monto_fijo_mensual DECIMAL(14,2),
    Dia_del_mes_de_vencimiento SMALLINT,
    Esta_vigente BOOLEAN,
    Fecha_inicio_de_la_obligacion DATE,
    Fecha_de_finalizacion DATE,
    Nombre_subcategoria VARCHAR(100),
    Id_categoria BIGINT,
    Nombre_categoria VARCHAR(100),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count FROM USUARIOS WHERE Id_usuario =p_id_usuario;
    IF v_count =0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El usuario indicado no existe';
    END IF;

	RETURN TABLE
	(
	
		 SELECT
            o.Id_obligacion_fija,
            o.Id_usuario,
            o.Id_subcategoria,
            o.Nombre,
            o.Descripcion_detallada,
            o.Monto_fijo_mensual,
            o.Dia_del_mes_de_vencimiento,
            o.Esta_vigente,
            o.Fecha_inicio_de_la_obligacion,
            o.Fecha_de_finalizacion,
            s.Nombre_subcategoria,
            s.Id_categoria,
            c.Nombre AS Nombre_categoria,
            o.creado_en,
            o.modificado_en,
            o.creado_por,
            o.modificado_por
         FROM OBLIGACION_FIJA o
         LEFT JOIN SUBCATEGORIA s ON s.Id_subcategoria=o.Id_subcategoria
         LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
         WHERE o.Id_usuario=p_id_usuario AND (p_activo IS NULL OR o.Esta_vigente=p_activo)
         ORDER BY o.Id_obligacion_fija
         
	);
END;




-- suponiendo subcategoria = 1 no es 'gasto'
CALL SP_INSERTAR_OBLIGACION(
    1,
    1,
    'Prueba fallo tipo',
    'Esto debe fallar si la subcategoria no es gasto',
    100.00,
    10,
    DATE '2025-01-01',
    NULL,
    'royum'
);
































































	 
	 
	 
	 
	 
	 
	 
	 
	 