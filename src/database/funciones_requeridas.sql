------------------------------------------------------------
-- FUNCIONES REQUERIDAS – PROYECTO PRESUPUESTO PERSONAL
-- Archivo: funciones_requeridas.sql
------------------------------------------------------------


/*==========================================================
  1) Monto ejecutado de una subcategoría en un mes
     fn_calcular_monto_ejecutado(id_subcategoria, anio, mes)
==========================================================*/
DROP FUNCTION IF EXISTS fn_calcular_monto_ejecutado;

CREATE FUNCTION fn_calcular_monto_ejecutado(
    p_id_subcategoria BIGINT,
    p_anio            SMALLINT,
    p_mes             SMALLINT
)
RETURNS DECIMAL(14,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count   INT;
    DECLARE v_result  DECIMAL(14,2);

    -- Validar subcategoría
    SELECT COUNT(*) INTO v_count
    FROM SUBCATEGORIA
    WHERE Id_subcategoria = p_id_subcategoria;

    IF v_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La subcategoria indicada no existe';
    END IF;

    -- Sumamos solo transacciones de tipo GASTO
    SELECT COALESCE(SUM(Monto), 0)
      INTO v_result
      FROM TRANSACCION
     WHERE Id_subcategoria       = p_id_subcategoria
       AND Anio                  = p_anio
       AND Mes                   = p_mes
       AND LOWER(Tipo_de_transaccion) = 'gasto';

    RETURN COALESCE(v_result, 0);
END;



/*==========================================================
  2) Porcentaje ejecutado comparando transacciones vs presupuesto
     fn_calcular_porcentaje_ejecutado(
        id_subcategoria, id_presupuesto, anio, mes )
==========================================================*/
DROP FUNCTION IF EXISTS fn_calcular_porcentaje_ejecutado;

CREATE FUNCTION fn_calcular_porcentaje_ejecutado(
    p_id_subcategoria BIGINT,
    p_id_presupuesto  BIGINT,
    p_anio            SMALLINT,
    p_mes             SMALLINT
)
RETURNS DECIMAL(9,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count_presu INT;
    DECLARE v_count_sub   INT;
    DECLARE v_ejecutado   DECIMAL(14,2);
    DECLARE v_presupuestado DECIMAL(14,2);
    DECLARE v_result      DECIMAL(9,2);

    -- Validar presupuesto
    SELECT COUNT(*) INTO v_count_presu
      FROM PRESUPUESTO
     WHERE Id_presupuesto = p_id_presupuesto;

    IF v_count_presu = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El presupuesto indicado no existe';
    END IF;

    -- Validar subcategoría
    SELECT COUNT(*) INTO v_count_sub
      FROM SUBCATEGORIA
     WHERE Id_subcategoria = p_id_subcategoria;

    IF v_count_sub = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La subcategoria indicada no existe';
    END IF;

    -- Monto ejecutado (gasto) en el mes
    SELECT COALESCE(SUM(Monto), 0)
      INTO v_ejecutado
      FROM TRANSACCION
     WHERE Id_subcategoria       = p_id_subcategoria
       AND Id_presupuesto        = p_id_presupuesto
       AND Anio                  = p_anio
       AND Mes                   = p_mes
       AND LOWER(Tipo_de_transaccion) = 'gasto';

    -- Monto presupuestado mensual de esa subcategoría
    SELECT COALESCE(Monto_mensual, 0)
      INTO v_presupuestado
      FROM PRESUPUESTO_DETALLE
     WHERE Id_presupuesto  = p_id_presupuesto
       AND Id_subcategoria = p_id_subcategoria;

    IF v_presupuestado IS NULL OR v_presupuestado = 0 THEN
        RETURN 0;  -- sin presupuesto definido -> 0%
    END IF;

    SET v_result = (v_ejecutado / v_presupuestado) * 100;

    RETURN v_result;
END;



/*==========================================================
  3) Balance disponible de una subcategoría en un mes
     fn_obtener_balance_subcategoria(id_presupuesto, id_subcategoria, anio, mes)
     Balance = presupuestado - ejecutado
==========================================================*/
DROP FUNCTION IF EXISTS fn_obtener_balance_subcategoria;

CREATE FUNCTION fn_obtener_balance_subcategoria(
    p_id_presupuesto  BIGINT,
    p_id_subcategoria BIGINT,
    p_anio            SMALLINT,
    p_mes             SMALLINT
)
RETURNS DECIMAL(14,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_presu_count INT;
    DECLARE v_sub_count   INT;
    DECLARE v_ejecutado   DECIMAL(14,2);
    DECLARE v_presupuestado DECIMAL(14,2);

    -- Validar presupuesto
    SELECT COUNT(*) INTO v_presu_count
      FROM PRESUPUESTO
     WHERE Id_presupuesto = p_id_presupuesto;

    IF v_presu_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El presupuesto indicado no existe';
    END IF;

    -- Validar subcategoría
    SELECT COUNT(*) INTO v_sub_count
      FROM SUBCATEGORIA
     WHERE Id_subcategoria = p_id_subcategoria;

    IF v_sub_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La subcategoria indicada no existe';
    END IF;

    -- Ejecutado
    SET v_ejecutado = fn_calcular_monto_ejecutado(
                          p_id_subcategoria, p_anio, p_mes);

    -- Presupuestado
    SELECT COALESCE(Monto_mensual, 0)
      INTO v_presupuestado
      FROM PRESUPUESTO_DETALLE
     WHERE Id_presupuesto  = p_id_presupuesto
       AND Id_subcategoria = p_id_subcategoria;

    RETURN COALESCE(v_presupuestado,0) - COALESCE(v_ejecutado,0);
END;



/*==========================================================
  4) Total presupuestado de una categoría en un mes
     fn_obtener_total_categoria_mes(
         id_categoria, id_presupuesto, anio, mes )
     (suma de todas las subcategorías de esa categoría)
==========================================================*/
DROP FUNCTION IF EXISTS fn_obtener_total_categoria_mes;

CREATE FUNCTION fn_obtener_total_categoria_mes(
    p_id_categoria   BIGINT,
    p_id_presupuesto BIGINT,
    p_anio           SMALLINT,
    p_mes            SMALLINT
)
RETURNS DECIMAL(14,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_cat_count   INT;
    DECLARE v_presu_count INT;
    DECLARE v_result      DECIMAL(14,2);

    -- Validar categoría
    SELECT COUNT(*) INTO v_cat_count
      FROM CATEGORIA
     WHERE Id_categoria = p_id_categoria;

    IF v_cat_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La categoria indicada no existe';
    END IF;

    -- Validar presupuesto
    SELECT COUNT(*) INTO v_presu_count
      FROM PRESUPUESTO
     WHERE Id_presupuesto = p_id_presupuesto;

    IF v_presu_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El presupuesto indicado no existe';
    END IF;

    -- Suma del monto mensual de todas las subcategorías de esa categoría
    SELECT COALESCE(SUM(pd.Monto_mensual),0)
      INTO v_result
      FROM PRESUPUESTO_DETALLE pd
      JOIN SUBCATEGORIA s
        ON s.Id_subcategoria = pd.Id_subcategoria
     WHERE pd.Id_presupuesto = p_id_presupuesto
       AND s.Id_categoria    = p_id_categoria;

    RETURN COALESCE(v_result,0);
END;



/*==========================================================
  5) Total ejecutado de una categoría en un mes
     fn_obtener_total_ejecutado_categoria_mes(
         id_categoria, anio, mes )
==========================================================*/
DROP FUNCTION IF EXISTS fn_obtener_total_ejecutado_categoria_mes;

CREATE FUNCTION fn_obtener_total_ejecutado_categoria_mes(
    p_id_categoria BIGINT,
    p_anio         SMALLINT,
    p_mes          SMALLINT
)
RETURNS DECIMAL(14,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_cat_count INT;
    DECLARE v_result    DECIMAL(14,2);

    -- Validar categoría
    SELECT COUNT(*) INTO v_cat_count
      FROM CATEGORIA
     WHERE Id_categoria = p_id_categoria;

    IF v_cat_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La categoria indicada no existe';
    END IF;

    -- Suma de los gastos de todas sus subcategorías
    SELECT COALESCE(SUM(t.Monto),0)
      INTO v_result
      FROM TRANSACCION t
      JOIN SUBCATEGORIA s
        ON s.Id_subcategoria = t.Id_subcategoria
     WHERE s.Id_categoria            = p_id_categoria
       AND t.Anio                    = p_anio
       AND t.Mes                     = p_mes
       AND LOWER(t.Tipo_de_transaccion) = 'gasto';

    RETURN COALESCE(v_result,0);
END;



/*==========================================================
  6) Días hasta vencimiento de una obligación fija
     fn_dias_hasta_vencimiento(id_obligacion)
==========================================================*/
DROP FUNCTION IF EXISTS fn_dias_hasta_vencimiento;

CREATE FUNCTION fn_dias_hasta_vencimiento(
    p_id_obligacion BIGINT
)
RETURNS INTEGER
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;

    DECLARE v_dia_venc        SMALLINT;
    DECLARE v_vigente         BOOLEAN;
    DECLARE v_fecha_inicio    DATE;
    DECLARE v_fecha_fin       DATE;

    DECLARE v_hoy             DATE;
    DECLARE v_ano             INT;
    DECLARE v_mes             INT;
    DECLARE v_dia_objetivo    INT;
    DECLARE v_fecha_venc      DATE;

    -- Validar obligación
    SELECT COUNT(*) INTO v_count
      FROM OBLIGACION_FIJA
     WHERE Id_obligacion_fija = p_id_obligacion;

    IF v_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La obligacion indicada no existe';
    END IF;

    SELECT Dia_del_mes_de_vencimiento,
           Esta_vigente,
           Fecha_inicio_de_la_obligacion,
           Fecha_de_finalizacion
      INTO v_dia_venc,
           v_vigente,
           v_fecha_inicio,
           v_fecha_fin
      FROM OBLIGACION_FIJA
     WHERE Id_obligacion_fija = p_id_obligacion;

    IF v_vigente IS FALSE THEN
        RETURN 0;
    END IF;

    SET v_hoy = CURRENT_DATE;
    SET v_ano = YEAR(v_hoy);
    SET v_mes = MONTH(v_hoy);

    -- Evitar problemas con febrero (si pusieron 30 o 31)
    SET v_dia_objetivo =
        CASE
            WHEN v_dia_venc > 28 THEN 28
            ELSE v_dia_venc
        END;

    -- Día de vencimiento de este mes
    SET v_fecha_venc = CAST(
        LPAD(CAST(v_ano AS VARCHAR(4)),4,'0')
        || '-' ||
        LPAD(CAST(v_mes AS VARCHAR(2)),2,'0')
        || '-' ||
        LPAD(CAST(v_dia_objetivo AS VARCHAR(2)),2,'0')
        AS DATE
    );

    -- Si ya pasó, usamos el próximo mes
    IF v_fecha_venc < v_hoy THEN
        SET v_fecha_venc = DATEADD('month', 1, v_fecha_venc);
    END IF;

    -- Respetar fecha de inicio
    WHILE v_fecha_venc < v_fecha_inicio DO
        SET v_fecha_venc = DATEADD('month', 1, v_fecha_venc);
    END WHILE;

    -- Si hay fecha de fin y ya la sobrepasamos, no hay vencimientos futuros
    IF v_fecha_fin IS NOT NULL AND v_fecha_venc > v_fecha_fin THEN
        RETURN 0;
    END IF;

    RETURN CAST(v_fecha_venc - v_hoy AS INTEGER);
END;



/*==========================================================
  7) Validar vigencia de un presupuesto para una fecha
     fn_validar_vigencia_presupuesto(fecha, id_presupuesto)
==========================================================*/
DROP FUNCTION IF EXISTS fn_validar_vigencia_presupuesto;

CREATE FUNCTION fn_validar_vigencia_presupuesto(
    p_fecha          DATE,
    p_id_presupuesto BIGINT
)
RETURNS BOOLEAN
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count INT;

    DECLARE v_anio_inicio SMALLINT;
    DECLARE v_mes_inicio  TINYINT;
    DECLARE v_anio_fin    SMALLINT;
    DECLARE v_mes_fin     TINYINT;

    DECLARE v_fecha_inicio DATE;
    DECLARE v_fecha_fin    DATE;

    -- Validar presupuesto
    SELECT COUNT(*) INTO v_count
      FROM PRESUPUESTO
     WHERE Id_presupuesto = p_id_presupuesto;

    IF v_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El presupuesto indicado no existe';
    END IF;

    SELECT Anio_de_inicio,
           Mes_de_inicio,
           Anio_de_fin,
           Mes_de_fin
      INTO v_anio_inicio,
           v_mes_inicio,
           v_anio_fin,
           v_mes_fin
      FROM PRESUPUESTO
     WHERE Id_presupuesto = p_id_presupuesto;

    -- Primer día de vigencia
    SET v_fecha_inicio = CAST(
        LPAD(CAST(v_anio_inicio AS VARCHAR(4)),4,'0')
        || '-' ||
        LPAD(CAST(v_mes_inicio AS VARCHAR(2)),2,'0')
        || '-01'
        AS DATE
    );

    -- Primer día del mes de fin
    SET v_fecha_fin = CAST(
        LPAD(CAST(v_anio_fin AS VARCHAR(4)),4,'0')
        || '-' ||
        LPAD(CAST(v_mes_fin AS VARCHAR(2)),2,'0')
        || '-01'
        AS DATE
    );

    -- Último día del mes de fin
    SET v_fecha_fin = DATEADD('day', -1, DATEADD('month', 1, v_fecha_fin));

    RETURN (p_fecha >= v_fecha_inicio AND p_fecha <= v_fecha_fin);
END;



/*==========================================================
  8) Obtener id_categoria a partir de una subcategoría
     fn_obtener_categoria_por_subcategoria(id_subcategoria)
==========================================================*/
DROP FUNCTION IF EXISTS fn_obtener_categoria_por_subcategoria;

CREATE FUNCTION fn_obtener_categoria_por_subcategoria(
    p_id_subcategoria BIGINT
)
RETURNS BIGINT
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_count       INT;
    DECLARE v_id_categoria BIGINT;

    SELECT COUNT(*) INTO v_count
      FROM SUBCATEGORIA
     WHERE Id_subcategoria = p_id_subcategoria;

    IF v_count = 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La subcategoria indicada no existe';
    END IF;

    SELECT Id_categoria
      INTO v_id_categoria
      FROM SUBCATEGORIA
     WHERE Id_subcategoria = p_id_subcategoria;

    RETURN v_id_categoria;
END;



/*==========================================================
  9) Proyección de gasto mensual de una subcategoría
     fn_calcular_proyeccion_gasto_mensual(id_subcategoria, anio, mes)
==========================================================*/
DROP FUNCTION IF EXISTS fn_calcular_proyeccion_gasto_mensual;

CREATE FUNCTION fn_calcular_proyeccion_gasto_mensual(
    p_id_subcategoria BIGINT,
    p_anio            SMALLINT,
    p_mes             SMALLINT
)
RETURNS DECIMAL(14,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_monto_actual     DECIMAL(14,2);
    DECLARE v_dias_transcurridos INT;
    DECLARE v_dias_mes         INT;
    DECLARE v_fecha_base       DATE;
    DECLARE v_result           DECIMAL(14,2);

    -- Monto ejecutado en ese mes/año
    SELECT COALESCE(SUM(Monto),0)
      INTO v_monto_actual
      FROM TRANSACCION
     WHERE Id_subcategoria       = p_id_subcategoria
       AND Anio                  = p_anio
       AND Mes                   = p_mes
       AND LOWER(Tipo_de_transaccion) = 'gasto';

    -- Primer día del mes solicitado
    SET v_fecha_base = CAST(
        LPAD(CAST(p_anio AS VARCHAR(4)),4,'0')
        || '-' ||
        LPAD(CAST(p_mes AS VARCHAR(2)),2,'0')
        || '-01'
        AS DATE
    );

    -- Días del mes (último día = primer día del mes siguiente - 1)
    SET v_dias_mes = DAY(
        DATEADD('day', -1, DATEADD('month', 1, v_fecha_base))
    );

    -- Si es el mes actual, usamos día actual; si no, asumimos mes completo
    IF YEAR(CURRENT_DATE) = p_anio AND MONTH(CURRENT_DATE) = p_mes THEN
        SET v_dias_transcurridos = DAY(CURRENT_DATE);
    ELSE
        SET v_dias_transcurridos = v_dias_mes;
    END IF;

    IF v_dias_transcurridos <= 0 THEN
        RETURN v_monto_actual;
    END IF;

    SET v_result = (v_monto_actual / v_dias_transcurridos) * v_dias_mes;
    RETURN v_result;
END;



/*==========================================================
  10) Promedio de gasto en una subcategoría en los últimos N meses
      fn_obtener_promedio_gasto_subcategoria(
          id_usuario, id_subcategoria, cantidad_meses )
==========================================================*/
DROP FUNCTION IF EXISTS fn_obtener_promedio_gasto_subcategoria;

CREATE FUNCTION fn_obtener_promedio_gasto_subcategoria(
    p_id_usuario      BIGINT,
    p_id_subcategoria BIGINT,
    p_cantidad_meses  INT
)
RETURNS DECIMAL(14,2)
READS SQL DATA
BEGIN ATOMIC
    DECLARE v_fecha_inicio DATE;
    DECLARE v_monto_total  DECIMAL(14,2);
    DECLARE v_promedio     DECIMAL(14,2);

    IF p_cantidad_meses IS NULL OR p_cantidad_meses <= 0 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La cantidad de meses debe ser mayor que cero';
    END IF;

    -- Primer día del mes más antiguo a considerar
    SET v_fecha_inicio = DATEADD(
        'month',
        -p_cantidad_meses + 1,
        DATEADD('day', 1 - DAY(CURRENT_DATE), CURRENT_DATE)
    );

    SELECT COALESCE(SUM(Monto),0)
      INTO v_monto_total
      FROM TRANSACCION
     WHERE Id_usuario           = p_id_usuario
       AND Id_subcategoria      = p_id_subcategoria
       AND LOWER(Tipo_de_transaccion) = 'gasto'
       AND Fecha >= v_fecha_inicio
       AND Fecha <= CURRENT_DATE;

    SET v_promedio = v_monto_total / p_cantidad_meses;

    RETURN v_promedio;
END;
