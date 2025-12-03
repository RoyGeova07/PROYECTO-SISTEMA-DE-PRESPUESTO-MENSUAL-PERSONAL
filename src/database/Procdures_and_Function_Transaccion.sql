DROP PROCEDURE IF EXISTS SP_INSERTAR_TRANSACCION;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_TRANSACCION;
DROP PROCEDURE IF EXISTS SP_ELIMINAR_TRANSACCION;
DROP FUNCTION IF EXISTS SP_CONSULTAR_TRANSACCION;
DROP FUNCTION IF EXISTS SP_LISTAR_TRANSACCIONES_PRESUPUESTO;

CREATE PROCEDURE SP_INSERTAR_TRANSACCION
(

	IN p_id_usuario BIGINT,
    IN p_id_presupuesto BIGINT,
    IN p_anio SMALLINT,
    IN p_mes SMALLINT,
    IN p_id_subcategoria BIGINT,
    IN p_id_obligacion BIGINT,--nullable
    IN p_tipo VARCHAR(15),
    IN p_descripcion VARCHAR(500),
    IN p_monto DECIMAL(14,2),
    IN p_fecha DATE,
    IN p_metodo_pago VARCHAR(30),
    IN p_creado_por VARCHAR(100)

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    DECLARE v_tipo_categoria VARCHAR(15);
    DECLARE v_presu_anio SMALLINT;
    DECLARE v_presu_mes SMALLINT;
    DECLARE v_presu_anio_fin SMALLINT;
    DECLARE v_presu_mes_fin SMALLINT;

    
    SELECT COUNT(*)INTO v_count FROM USUARIOS WHERE Id_usuario=p_id_usuario;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT ='Usuario no existe';
    END IF;

    
    SELECT COUNT(*)INTO v_count FROM PRESUPUESTO WHERE Id_presupuesto =p_id_presupuesto;
    IF v_count =0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT ='Presupuesto no existe';
    END IF;

    --validar subcategoria y obtener tipo de categoria padre
    SELECT c.Tipo_de_categoria
      INTO v_tipo_categoria
      FROM SUBCATEGORIA s
      LEFT JOIN CATEGORIA c ON c.Id_categoria =s.Id_categoria
     WHERE s.Id_subcategoria= p_id_subcategoria;

    IF v_tipo_categoria IS NULL THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Subcategoria no existe';
    END IF;

    --validar que p_tipo coincide con tipo de categoria padre
    IF  lower(p_tipo)<>lower(v_tipo_categoria) THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Tipo de transaccion no coincide con tipo de la categoria padre';
    END IF;

    --si viene id_obligacion, validar que existe y que usa la misma subcategoria
    IF p_id_obligacion IS NOT NULL THEN
        SELECT COUNT(*) INTO v_count FROM OBLIGACION_FIJA WHERE Id_obligacion_fija=p_id_obligacion;
        IF v_count=0 THEN
            SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Obligacion indicada no existe';
        END IF;

        SELECT COUNT(*)INTO v_count
        FROM OBLIGACION_FIJA
        WHERE Id_obligacion_fija=p_id_obligacion
          AND Id_subcategoria =p_id_subcategoria;

        IF v_count=0 THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='La obligacion no corresponde a la subcategoria indicada';
        END IF;
    END IF;

    
    IF p_monto<0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El monto no puede ser negativo';
    END IF;

    IF p_mes<1 OR p_mes>12 THEN
        SIGNAL SQLSTATE'45000'SET MESSAGE_TEXT ='Mes invalido (1..12)';
    END IF;

    --validar vigencia del presupuesto (obtener periodos)
    SELECT Anio_de_inicio,Mes_de_inicio,Anio_de_fin,Mes_de_fin
      INTO v_presu_anio,v_presu_mes,v_presu_anio_fin,v_presu_mes_fin
    FROM PRESUPUESTO
    WHERE Id_presupuesto= p_id_presupuesto;

    IF NOT((p_anio>v_presu_anio OR(p_anio= v_presu_anio AND p_mes >=v_presu_mes))AND
        (p_anio<v_presu_anio_fin OR(p_anio=v_presu_anio_fin AND p_mes<=v_presu_mes_fin)))THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT ='Año/Mes fuera del periodo de vigencia del presupuesto';
    END IF;
	
	--ahora si insertar
	INSERT INTO TRANSACCION
	(
	
		Id_usuario,
        Id_presupuesto,
        Anio,
        Mes,
        Id_subcategoria,
        Id_obligacion_fija,
        Tipo_de_transaccion,
        Descripcion,
        Monto,
        Fecha,
        Metodo_de_pago,
        Fecha_hora_de_registro,
        creado_en,
        creado_por
	
	)values(
	
		p_id_usuario,
        p_id_presupuesto,
        p_anio,
        p_mes,
        p_id_subcategoria,
        p_id_obligacion,
        LOWER(p_tipo),
        p_descripcion,
        p_monto,
        p_fecha,
        LOWER(p_metodo_pago),
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        COALESCE(p_creado_por,'system')
	
	);
END;
	 
	 
	 

CREATE PROCEDURE SP_ACTUALIZAR_TRANSACCION
(

	IN p_id_transaccion BIGINT,
    IN p_anio SMALLINT,
    IN p_mes SMALLINT,
    IN p_descripcion VARCHAR(500),
    IN p_monto DECIMAL(14,2),
    IN p_fecha DATE,
    IN p_metodo_pago VARCHAR(30),
    IN p_modificado_por VARCHAR(100)

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    DECLARE v_presu_id BIGINT;
    DECLARE v_presu_anio SMALLINT;
    DECLARE v_presu_mes SMALLINT;
    DECLARE v_presu_anio_fin SMALLINT;
    DECLARE v_presu_mes_fin SMALLINT;

	SELECT COUNT(*) INTO v_count FROM TRANSACCION WHERE Id_transaccion=p_id_transaccion;
    IF v_count =0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Transacción no existe';
    END IF;
	
	--aqui obtengo el presupuesto asociado
	SELECT Id_presupuesto INTO v_presu_id FROM TRANSACCION WHERE Id_transaccion=p_id_transaccion;
	
	IF p_monto<0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El monto no puede ser negativo';
    END IF;
	
    IF p_mes<1 OR p_mes>12 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Mes invalido (1..12)';
    END IF;
    
    --validar que p_anio/p_mes estn dentro de vigencia del presupuesto
     SELECT Anio_de_inicio,Mes_de_inicio,Anio_de_fin,Mes_de_fin
      INTO v_presu_anio,v_presu_mes,v_presu_anio_fin,v_presu_mes_fin
    FROM PRESUPUESTO
    WHERE Id_presupuesto=v_presu_id;
    
    IF NOT((p_anio>v_presu_anio OR(p_anio= v_presu_anio AND p_mes >=v_presu_mes))AND
        (p_anio<v_presu_anio_fin OR(p_anio=v_presu_anio_fin AND p_mes<=v_presu_mes_fin)))THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT ='Año/Mes fuera del periodo de vigencia del presupuesto';
    END IF;
    
    --ahora si actualizar
     UPDATE TRANSACCION
     SET Anio=p_anio,
        Mes=p_mes,
        Descripcion=p_descripcion,
        Monto=p_monto,
        Fecha=p_fecha,
        Metodo_de_pago=LOWER(p_metodo_pago),
        modificado_en=CURRENT_TIMESTAMP,
        modificado_por=COALESCE(p_modificado_por,'system')
    WHERE Id_transaccion=p_id_transaccion;
END;


CREATE PROCEDURE SP_ELIMINAR_TRANSACCION
(

	IN p_id_transaccion BIGINT

)
MODIFIES SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    SELECT COUNT(*)INTO v_count FROM TRANSACCION WHERE Id_transaccion=p_id_transaccion;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Transaccion no existe';
    END IF;

    DELETE FROM TRANSACCION WHERE Id_transaccion=p_id_transaccion;
END;


CREATE FUNCTION SP_CONSULTAR_TRANSACCION(p_id_transaccion BIGINT)
RETURNS table
(

	Id_transaccion BIGINT,
    Id_usuario BIGINT,
    Id_presupuesto BIGINT,
    Anio SMALLINT,
    Mes SMALLINT,
    Id_subcategoria BIGINT,
    Id_obligacion_fija BIGINT,
    Tipo_de_transaccion VARCHAR(15),
    Descripcion VARCHAR(500),
    Monto DECIMAL(14,2),
    Fecha DATE,
    Metodo_de_pago VARCHAR(30),
    Fecha_hora_de_registro TIMESTAMP,
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100),
    Nombre_subcategoria VARCHAR(100),
    Id_categoria BIGINT,
    Nombre_categoria VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
	SELECT COUNT(*) INTO v_count FROM TRANSACCION WHERE Id_transaccion=p_id_transaccion;
    IF v_count=0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='Transacción no existe';
    END IF;

	RETURN TABLE
	(
	
		 SELECT
            t.Id_transaccion,
            t.Id_usuario,
            t.Id_presupuesto,
            t.Anio,
            t.Mes,
            t.Id_subcategoria,
            t.Id_obligacion_fija,
            t.Tipo_de_transaccion,
            t.Descripcion,
            t.Monto,
            t.Fecha,
            t.Metodo_de_pago,
            t.Fecha_hora_de_registro,
            t.creado_en,
            t.modificado_en,
            t.creado_por,
            t.modificado_por,
            s.Nombre_subcategoria,
            s.Id_categoria,
            c.Nombre AS Nombre_categoria
        FROM TRANSACCION t
        LEFT JOIN SUBCATEGORIA s ON s.Id_subcategoria=t.Id_subcategoria
        LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
        WHERE t.Id_transaccion=p_id_transaccion
	
	);
END;



	
CREATE FUNCTION SP_LISTAR_TRANSACCIONES_PRESUPUESTO
(

	p_id_presupuesto BIGINT,
    p_anio SMALLINT,
    p_mes SMALLINT,
    p_tipo VARCHAR(15)

)
RETURNS TABLE
(

	 Id_transaccion BIGINT,
    Id_usuario BIGINT,
    Id_presupuesto BIGINT,
    Anio SMALLINT,
    Mes SMALLINT,
    Id_subcategoria BIGINT,
    Id_obligacion_fija BIGINT,
    Tipo_de_transaccion VARCHAR(15),
    Descripcion VARCHAR(500),
    Monto DECIMAL(14,2),
    Fecha DATE,
    Metodo_de_pago VARCHAR(30),
    Fecha_hora_de_registro TIMESTAMP,
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por VARCHAR(100),
    modificado_por VARCHAR(100),
    Nombre_subcategoria VARCHAR(100),
    Id_categoria BIGINT,
    Nombre_categoria VARCHAR(100)

)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;
    SELECT COUNT(*) INTO v_count FROM PRESUPUESTO WHERE Id_presupuesto = p_id_presupuesto;
    IF v_count = 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Presupuesto no existe';
    END IF;

	RETURN TABLE
	(
	
		SELECT
            t.Id_transaccion,
            t.Id_usuario,
            t.Id_presupuesto,
            t.Anio,
            t.Mes,
            t.Id_subcategoria,
            t.Id_obligacion_fija,
            t.Tipo_de_transaccion,
            t.Descripcion,
            t.Monto,
            t.Fecha,
            t.Metodo_de_pago,
            t.Fecha_hora_de_registro,
            t.creado_en,
            t.modificado_en,
            t.creado_por,
            t.modificado_por,
            s.Nombre_subcategoria,
            s.Id_categoria,
            c.Nombre AS Nombre_categoria
        FROM TRANSACCION t
        LEFT JOIN SUBCATEGORIA s ON s.Id_subcategoria=t.Id_subcategoria
        LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
        WHERE t.Id_presupuesto=p_id_presupuesto
          AND(p_anio IS NULL OR t.Anio=p_anio)
          AND(p_mes  IS NULL OR t.Mes=p_mes)
          AND(p_tipo IS NULL OR LOWER(t.Tipo_de_transaccion)=LOWER(p_tipo))
        ORDER BY t.Fecha_hora_de_registro DESC
	
	);
END;


	 
CALL SP_INSERTAR_TRANSACCION(
  1,            -- p_id_usuario
  1,            -- p_id_presupuesto
  2025,         -- p_anio
  2,            -- p_mes
  1,            -- p_id_subcategoria  (subcategoria de tipo 'gasto')
  NULL,         -- p_id_obligacion (no vinculada a obligación)
  'gasto',      -- p_tipo (debe coincidir con tipo de la categoria padre)
  'Compra supermercado', -- p_descripcion
  1200.00,      -- p_monto
  DATE '2025-02-10', -- p_fecha (fecha real)
  'efectivo',   -- p_metodo_pago
  'royum'       -- p_creado_por
);

	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 