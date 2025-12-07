DROP PROCEDURE IF EXISTS SP_INSERTAR_PRESUPUESTO;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_PRESUPUESTO;
DROP PROCEDURE IF EXISTS SP_ELIMINAR_PRESUPUESTO;
DROP FUNCTION IF EXISTS SP_CONSULTAR_PRESUPUESTO;

CREATE PROCEDURE SP_INSERTAR_PRESUPUESTO
(

	IN p_id_usuario bigint,
	IN p_nombre_descriptivo varchar(500),
    IN p_anio_inicio SMALLINT,
    IN p_mes_inicio TINYINT,
    IN p_anio_fin SMALLINT,
    IN p_mes_fin TINYINT,
    IN p_total_ingresos decimal(14,2),
    IN p_total_gastos decimal(14,2),
    IN p_total_ahorro decimal(14,2)

)
MODIFIES SQL DATA
BEGIN ATOMIC
	
	--primero hare la validacion de vigencia
	IF not((p_anio_fin>p_anio_inicio)OR(p_anio_fin=p_anio_inicio AND p_mes_fin>=p_mes_inicio))THEN
		SIGNAL SQLSTATE'45000' SET message_text='Periodo invalido: fecha fin debe ser igual o posterior a fecha inicio';
	END IF;
	
	
	--luego se valida que no exista otro presupuesto activo del mismo usuario que se solape
	 IF exists(SELECT 1 FROM PRESUPUESTO pr
        WHERE pr.Id_usuario=p_id_usuario
          AND pr.Estado_presupuesto='activo'
          AND NOT(
              (pr.Anio_de_fin<p_anio_inicio)
              OR(pr.Anio_de_fin=p_anio_inicio AND pr.Mes_de_fin<p_mes_inicio)
              OR(pr.Anio_de_inicio>p_anio_fin)
              OR(pr.Anio_de_inicio=p_anio_fin AND pr.Mes_de_inicio>p_mes_fin)))THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Ya existe un presupuesto activo para el usuario en un periodo solapado';
    END IF;
	
	--otra validacion que me da pereza poner, seria lo de validar montos, osea El total de ingresos debe ser mayor o igual a la suma de gastos y ahorro'
	--que se hagan mejor en el front foc
	
	INSERT INTO PRESUPUESTO
	(
	
		Id_usuario,
        Nombre_descriptivo,
        Anio_de_inicio,
        Mes_de_inicio,
        Anio_de_fin,
        Mes_de_fin,
        Total_de_ingresos,
        Total_de_gastos,
        Total_de_ahorro,
        Fecha_hora_creacion,
        Estado_presupuesto,
        creado_en,
        creado_por
	
	)values(
	
		p_id_usuario,
        p_nombre_descriptivo,
        p_anio_inicio,
        p_mes_inicio,
        p_anio_fin,
        p_mes_fin,
        p_total_ingresos,
        p_total_gastos,
        p_total_ahorro,
        CURRENT_TIMESTAMP,
        'activo',
        CURRENT_TIMESTAMP,
        'royum'
	
	);
END;

CREATE PROCEDURE SP_ACTUALIZAR_PRESUPUESTO
(
	
	IN p_id_presupuesto bigint,
    IN p_nombre_descriptivo varchar(500),
    IN p_anio_inicio SMALLINT,
    IN p_mes_inicio TINYINT,
    IN p_anio_fin SMALLINT,
    IN p_mes_fin TINYINT,
    IN p_total_ingresos decimal(14,2),
    IN p_total_gastos decimal(14,2),
    IN p_total_ahorro decimal(14,2),
    IN p_modificado_por varchar(100)	

)
MODIFIES SQL DATA
BEGIN ATOMIC
	
	--se verifica si existe el presupuesto
	DECLARE v_count int;
	DECLARE v_id_usuario bigint;
	SELECT count(*) INTO v_count FROM PRESUPUESTO WHERE Id_presupuesto=p_id_presupuesto;
	IF v_count=0 then
		SIGNAL SQLSTATE '45000'SET message_text='El presupuesto indicado no existe';
	END IF;
	
	SELECT Id_usuario INTO v_id_usuario
	FROM PRESUPUESTO
	WHERE Id_presupuesto=p_id_presupuesto;
	
	--se valida la vigencia 
	IF NOT((p_anio_fin>p_anio_inicio)OR(p_anio_fin=p_anio_inicio AND p_mes_fin>=p_mes_inicio))THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Periodo invalido: fecha fin debe ser igual o posterior a fecha inicio';
    END IF;

	--se valida que no exista otro presupuesto activo del mismo usuario q solape 
	 IF EXISTS(SELECT 1 FROM PRESUPUESTO pr
        WHERE pr.Id_usuario=v_id_usuario
          AND pr.Estado_presupuesto='activo'
          AND pr.Id_presupuesto<>p_id_presupuesto
          AND NOT((pr.Anio_de_fin<p_anio_inicio)OR(pr.Anio_de_fin=p_anio_inicio AND pr.Mes_de_fin<p_mes_inicio)OR(pr.Anio_de_inicio>p_anio_fin)
              OR(pr.Anio_de_inicio=p_anio_fin AND pr.Mes_de_inicio>p_mes_fin)))THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Ya existe un presupuesto activo del usuario que solapa el periodo solicitado';
    END IF;
	
	--ahora si actualizar 
	UPDATE PRESUPUESTO
	SET Nombre_descriptivo=p_nombre_descriptivo,
        Anio_de_inicio=p_anio_inicio,
        Mes_de_inicio=p_mes_inicio,
        Anio_de_fin=p_anio_fin,
        Mes_de_fin=p_mes_fin,
        Total_de_ingresos=p_total_ingresos,
        Total_de_gastos=p_total_gastos,
        Total_de_ahorro=p_total_ahorro,
        modificado_en=CURRENT_TIMESTAMP,
        modificado_por='royum'
    WHERE Id_presupuesto=p_id_presupuesto;
	
END;


CREATE PROCEDURE SP_ELIMINAR_PRESUPUESTO
(

	IN p_id_presupuesto bigint

)
MODIFIES SQL DATA
BEGIN ATOMIC
	--si existe
	DECLARE v_countt int;
	SELECT count(*) INTO v_countt FROM presupuesto WHERE Id_presupuesto=p_id_presupuesto;
	IF v_countt=0 THEN
		 SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El presupuesto indicado no existe';
    END IF;

	--se verifica que no tenga transacciones asociadas
	IF exists(SELECT 1 FROM TRANSACCION t WHERE t.Id_presupuesto=p_id_presupuesto)THEN
		SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='No se puede eliminar el presupuesto: tiene transacciones asociadas';
    END IF;
	
	DELETE FROM PRESUPUESTO WHERE Id_presupuesto=p_id_presupuesto;
	
END;


CREATE FUNCTION SP_CONSULTAR_PRESUPUESTO(p_id_presupuesto BIGINT)
RETURNS table
(

	Id_presupuesto bigint,
    Id_usuario bigint,
    Nombre_descriptivo varchar(500),
    Anio_de_inicio SMALLINT,
    Mes_de_inicio TINYINT,
    Anio_de_fin SMALLINT,
    Mes_de_fin TINYINT,
    Total_de_ingresos decimal(14,2),
    Total_de_gastos decimal(14,2),
    Total_de_ahorro decimal(14,2),
    Fecha_hora_creacion TIMESTAMP,
    Estado_presupuesto varchar(20),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por varchar(100),
    modificado_por varchar(100)

)
READS SQL DATA
BEGIN ATOMIC
	--si existe 
	DECLARE v_counttt int;
	SELECT count(*) INTO v_counttt FROM PRESUPUESTO WHERE Id_presupuesto=p_id_presupuesto;
	IF v_counttt=0 THEN
		SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El presupuesto no existe';
    END IF;

	RETURN TABLE
	(
	
		SELECT
			 Id_presupuesto,
            Id_usuario,
            Nombre_descriptivo,
            Anio_de_inicio,
            Mes_de_inicio,
            Anio_de_fin,
            Mes_de_fin,
            Total_de_ingresos,
            Total_de_gastos,
            Total_de_ahorro,
            Fecha_hora_creacion,
            Estado_presupuesto,
            creado_en,
            modificado_en,
            creado_por,
            modificado_por
         FROM PRESUPUESTO
         WHERE Id_presupuesto=p_id_presupuesto
	
	);
END;

CALL SP_INSERTAR_PRESUPUESTO(
  1,                                 -- p_id_usuario
  'Presupuesto Prueba 2025 Q1',      -- p_nombre_descriptivo
  2025,                              -- p_anio_inicio
  1,                                 -- p_mes_inicio
  2025,                              -- p_anio_fin
  3,                                 -- p_mes_fin
  10000.00,                          -- p_total_ingresos
  6000.00,                           -- p_total_gastos
  2000.00,                           -- p_total_ahorro
  'royum'                            -- p_creado_por
);

-- Intento solapado 
CALL SP_INSERTAR_PRESUPUESTO(
  1,
  'Presupuesto Solapado 2025',
  2025,
  2,
  2025,
  4,
  8000.00,
  4000.00,
  1000.00,
  'royum'
);









