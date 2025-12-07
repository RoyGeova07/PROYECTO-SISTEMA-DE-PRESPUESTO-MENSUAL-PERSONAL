DROP PROCEDURE IF EXISTS SP_INSERTAR_PRESUPUESTO_DETALLE;
DROP PROCEDURE IF EXISTS SP_ACTUALIZAR_PRESUPUESTO_DETALLE;
DROP PROCEDURE IF EXISTS SP_ELIMINAR_PRESUPUESTO_DETALLE;
DROP FUNCTION IF EXISTS SP_CONSULTAR_PRESUPUESTO_DETALLE;
DROP FUNCTION IF EXISTS SP_LISTAR_DETALLES_PRESUPUESTO;

CREATE PROCEDURE SP_INSERTAR_PRESUPUESTO_DETALLE
(

	IN p_id_presupuesto bigint,
    IN p_id_subcategoria bigint,
    IN p_monto_mensual decimal(14,2),
    IN p_justificacion varchar(500)

)
MODIFIES SQL DATA
BEGIN ATOMIC
	 DECLARE v_count int;
	--validar primero que el presupuesto exista
	SELECT count(*)into v_count FROM PRESUPUESTO WHERE Id_presupuesto=p_id_presupuesto;
	IF v_count=0 THEN
		  SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El presupuesto indicado no existe';
    END IF;

	--ahora validar la subcategoria
	SELECT count(*)into v_count FROM SUBCATEGORIA WHERE Id_subcategoria=p_id_subcategoria;
	IF v_count=0 THEN
		  SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='La subcategoria indicada no existe';
    END IF;
	
	--y por ultimo validar que no exista ya detalle para la misma subcategoria en ese presupuesto
	SELECT count(*)into v_count FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto=p_id_presupuesto AND Id_subcategoria=p_id_subcategoria;
	IF v_count>0 then
		SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='Ya existe un detalle para esa subcategoria en el presupuesto';
    END IF;
	
	--ahora por las dudas monto no negativo
	IF p_monto_mensual<0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El monto mensual no puede ser negativo';
	END IF;
	
	INSERT INTO PRESUPUESTO_DETALLE
	(
	
		Id_presupuesto,
        Id_subcategoria,
        Monto_mensual,
        Justificacion_del_monto,
        creado_en,
        creado_por
	
	)values(
	
		p_id_presupuesto,
        p_id_subcategoria,
        p_monto_mensual,
        p_justificacion,
        CURRENT_TIMESTAMP,
        'royum'
	
	);
END;  
	

CREATE PROCEDURE SP_ACTUALIZAR_PRESUPUESTO_DETALLE
(

	IN p_id_detalle bigint,
    IN p_monto_mensual decimal(14,2),
    IN p_justificacion varchar(500)

)
MODIFIES SQL DATA
BEGIN ATOMIC
	DECLARE v_count INT;
	SELECT count(*) INTO v_count FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto_detalle=p_id_detalle;
	 IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El detalle de presupuesto no existe';
    END IF;

	IF p_monto_mensual<0 THEN
		SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT='El monto mensual no puede ser negativo';
	END IF;
	
	UPDATE PRESUPUESTO_DETALLE
	SET Monto_mensual=p_monto_mensual,
		Justificacion_del_monto=p_justificacion,
        modificado_en=CURRENT_TIMESTAMP,
        modificado_por='royum'
    WHERE Id_presupuesto_detalle=p_id_detalle;
END;
	
	
CREATE PROCEDURE SP_ELIMINAR_PRESUPUESTO_DETALLE
(

	IN p_id_detalle bigint

)
MODIFIES SQL DATA
BEGIN ATOMIC
	 DECLARE v_count INT;
	SELECT count(*) INTO v_count FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto_detalle=p_id_detalle;
	 IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El detalle de presupuesto no existe';
    END IF;

	--ahora validacion para evitar eliminar si ya hay transacciones en ese presupuesto/subcategoria
	IF exists(SELECT 1 FROM PRESUPUESTO_DETALLE pd
		INNER JOIN TRANSACCION t ON t.Id_presupuesto=pd.Id_presupuesto AND t.Id_subcategoria=pd.Id_subcategoria
		WHERE pd.Id_presupuesto_detalle=p_id_detalle)then
		SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='No se puede eliminar el detalle: existen transacciones asociadas a ese presupuesto/subcategoria';
    END IF;	
	
	--delete crack
	DELETE FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto_detalle=p_id_detalle;
END;

CREATE FUNCTION SP_CONSULTAR_PRESUPUESTO_DETALLE(p_id_detalle bigint)
RETURNS table
(
	
	Id_presupuesto_detalle bigint,
    Id_presupuesto bigint,
    Id_subcategoria bigint,
    Nombre_subcategoria varchar(70),
    Id_categoria bigint,
    Nombre_categoria varchar(100),
    Monto_mensual decimal(14,2),
    Justificacion_del_monto varchar(500),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por varchar(100),
    modificado_por varchar(100)

)	
READS SQL DATA
BEGIN ATOMIC
	DECLARE v_count INT;
	SELECT count(*) INTO v_count FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto_detalle=p_id_detalle;
	 IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El detalle de presupuesto no existe';
    END IF;

	RETURN TABLE 
	(
	
		SELECT 
			pd.Id_presupuesto_detalle,
            pd.Id_presupuesto,
            pd.Id_subcategoria,
            s.Nombre_subcategoria,
            s.Id_categoria,
            c.Nombre AS Nombre_categoria,
            pd.Monto_mensual,
            pd.Justificacion_del_monto,
            pd.creado_en,
            pd.modificado_en,
            pd.creado_por,
            pd.modificado_por
        FROM PRESUPUESTO_DETALLE pd
        LEFT JOIN SUBCATEGORIA s ON s.Id_subcategoria=pd.id_subcategoria
        LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
		WHERE pd.Id_presupuesto_detalle=p_id_detalle
	
	);
END;
	
	
CREATE FUNCTION SP_LISTAR_DETALLES_PRESUPUESTO(p_id_presupuesto bigint)
RETURNs table
(

	Id_presupuesto_detalle bigint,
    Id_presupuesto bigint,
    Id_subcategoria bigint,
    Nombre_subcategoria varchar(70),
    Id_categoria bigint,
    Nombre_categoria varchar(100),
    Monto_mensual decimal(14,2),
    Justificacion_del_monto varchar(500),
    creado_en TIMESTAMP,
    modificado_en TIMESTAMP,
    creado_por varchar(100),
    modificado_por varchar(100)

)
READS SQL DATA
BEGIN ATOMIC
	DECLARE v_count INT;
	SELECT count(*) INTO v_count FROM PRESUPUESTO WHERE Id_presupuesto=p_id_presupuesto;
	 IF v_count=0 THEN
        SIGNAL SQLSTATE '45000'SET MESSAGE_TEXT='El detalle de presupuesto no existe';
    END IF;

	RETURN TABLE
	(
	
		 SELECT
            pd.Id_presupuesto_detalle,
            pd.Id_presupuesto,
            pd.Id_subcategoria,
            s.Nombre_subcategoria,
            s.Id_categoria,
            c.Nombre AS Nombre_categoria,
            pd.Monto_mensual,
            pd.Justificacion_del_monto,
            pd.creado_en,
            pd.modificado_en,
            pd.creado_por,
            pd.modificado_por
        FROM PRESUPUESTO_DETALLE pd
        LEFT JOIN SUBCATEGORIA s ON s.Id_subcategoria=pd.Id_subcategoria
        LEFT JOIN CATEGORIA c ON c.Id_categoria=s.Id_categoria
        WHERE pd.Id_presupuesto=p_id_presupuesto
        ORDER BY pd.Id_presupuesto_detalle
	
	);
	
END;
	
	
	
	
CALL SP_INSERTAR_PRESUPUESTO_DETALLE(1,4,4000.00,'educacion mensual','royum');
	
	
	
	
	
	