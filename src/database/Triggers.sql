--aqui trigger de subcategoria
/*

en este caso , quiero que cada vez que se cree una categoria , automaticamente se cree una subcategoria por defecto(general)
en resumen: cuando se crea una categoria, crear subcategoria por defecto.

el after insert on categoria: esto define cuando y sobre que tabla se dispara:
ON CATEGORIA --> el trigger esta asociado a la tabla CATEGORIA
AFTER INSERT --> se ejecuta despues de que la fila fue insertada correctamente

REFERENCING NEW ROW AS N: aqui le damos un alias a la fila que se acaba de insertar 
NEW ROW --> la fila nueva, la que se acaba de insertar 
as N --> la vamos a llamar N (de New).

Dentro del cuerpo del trigger, cuando escribo 'N.Id_categoria', esta leyendo el valor de la columna Id_categoria de la nueva categoria que se inserto.
EJEMPLO:
INSERT INTO CATEGORIA (Nombre, Descripcion_detallada, Tipo_de_categoria)
VALUES ('Alimentación', 'Gastos de comida', 'gasto');

FOR EACH ROW
este significa: Dispara este trigger por cada fila que se inserte, no una sola vez por sentencia.
EJEMPLO: 
INSERT INTO CATEGORIA (Nombre, Descripcion_detallada, Tipo_de_categoria)
VALUES 
  ('Alimentación', 'Gastos de comida', 'gasto'),
  ('Transporte',   'Gastos de transporte', 'gasto');
Este INSERT mete 2 filas.
Con FOR EACH ROW el trigger se ejecuta dos veces
1. Una vez para la categoría 'Alimentacion'.
2. Otra vez para la categoría 'Transporte'.

Cada ejecucion usara un N.Id_categoria distinto.

------------------------------------------------------------------------------------------------
COMANDO PARA VERIFICAR QUE EXISTEN LOS TRIGGERS, YA QUE NETBEANS NO TIENE CARPETA PARA TRIGERS
SELECT TRIGGER_NAME, EVENT_MANIPULATION, EVENT_OBJECT_TABLE
FROM INFORMATION_SCHEMA.TRIGGERS
WHERE TRIGGER_NAME = 'nombre_trigger';
------------------------------------------------------------------------------------------------

*/
drop trigger if exists TG_CATEGORIA_CREAR_SUBCATEGORIA_DEFECTO;
DROP TRIGGER IF EXISTS TG_TRANS_INSERTAR_META;
DROP TRIGGER IF EXISTS TG_TRANS_ACTUALIZAR_META;
DROP TRIGGER IF EXISTS TG_TRANS_BORRAR_META;

--PENDIENTE MODIFCAR ESATA FUNCION CON EL VALUES, SE HARA DE UN SOLO EN EL FRONTEND
create trigger TG_CATEGORIA_CREAR_SUBCATEGORIA_DEFECTO
after insert on CATEGORIA
referencing new row as N
for each row                                                            -- si en lugar de general quiero que la subcategoria se llame igual que la categoria, agrego N.Nombre
    insert into SUBCATEGORIA(Id_categoria,Nombre_subcategoria,Descripcion_detallada,Estado,Por_defecto)
    values(N.Id_categoria,'Subcategoría por defecto de la categoria',N.Nombre,true,true);
							--que el uusario puede elegir el nombre de la categoria





-- INSERT INTO CATEGORIA (Nombre, Descripcion_detallada, Tipo_de_categoria)
-- VALUES ('Prueba trigger', 'Gastos de prueba', 'gasto');
-- 
-- SELECT * FROM SUBCATEGORIA;


-- ---------- TRIGGERS: mantener monto_ahorrado sincronizado ----------

/*
   al insertar una TRANSACCION tipo 'ahorro' sumamos el monto a la meta asociada (si existe).
   al actualizar una TRANSACCION ajustamos diferencia (restamos old, sumamos new) si cambio monto/subcategoria/tipo.
   al borrar una TRANSACCION restamos su monto de la meta asociada (si existe).
  Nota: estas acciones asumen que una transaccion de 'ahorro' esta vinculada a la subcategoria que tiene meta.
*/

CREATE TRIGGER TG_TRANS_INSERTAR_META
AFTER INSERT ON TRANSACCION
REFERENCING NEW ROW AS N
FOR EACH ROW 
BEGIN ATOMIC 
	IF lower(N.Tipo_de_transaccion)='ahorro'THEN
		--sumar monto a meta si existe meta para esa subcategoria y estado en_progreso o pausada (sea criterio)
		UPDATE META_AHORRO
        SET Monto_ahorrado=Monto_ahorrado+N.Monto
        WHERE Id_subcategoria=N.Id_subcategoria;
	
		--marcar completada si alcanza el objetivo
		UPDATE META_AHORRO
		SET Estado='Completada'
		WHERE Id_subcategoria=N.Id_subcategoria
          AND Monto_ahorrado>=Monto_total_alcanzar;
    END IF;
END;



CREATE TRIGGER TG_TRANS_ACTUALIZAR_META
AFTER UPDATE ON TRANSACCION
REFERENCING OLD ROW AS O NEW ROW AS N
FOR EACH ROW
BEGIN ATOMIC
    --si tipo o subcategoria o monto cambio, ajustar
    IF Lower(O.Tipo_de_transaccion)='ahorro'THEN
        --restar el monto viejo de su meta (si existe)
        UPDATE META_AHORRO
        SET Monto_ahorrado=Monto_ahorrado-O.Monto
        WHERE Id_subcategoria=O.Id_subcategoria;
    END IF;

    IF lower(N.Tipo_de_transaccion)='ahorro'THEN
        -- sumar el monto nuevo a la meta (si existe)
        UPDATE META_AHORRO
        SET Monto_ahorrado=Monto_ahorrado+N.Monto
        WHERE Id_subcategoria=N.Id_subcategoria;
    END IF;

    --actualizar estados: completar donde corresponda
    UPDATE META_AHORRO
    SET Estado='completada'
    WHERE Monto_ahorrado>=Monto_total_alcanzar;

    --opcional: si una meta quedo con monto_ahorrado < monto_total y estaba completada, devolver a en_progreso
    UPDATE META_AHORRO
    SET Estado='en_progreso'
    WHERE Estado='completada' AND Monto_ahorrado<Monto_total_alcanzar;
END;


--AFTER DELETE ON TRANSACCION
CREATE TRIGGER TG_TRANS_BORRAR_META
AFTER DELETE ON TRANSACCION
REFERENCING OLD ROW AS O
FOR EACH ROW
BEGIN ATOMIC
    IF lower(O.Tipo_de_transaccion)='ahorro'THEN
        --restar monto antiguo de la meta si existe
        UPDATE META_AHORRO
        SET Monto_ahorrado=Monto_ahorrado-O.Monto
        WHERE Id_subcategoria=O.Id_subcategoria;

        --si se resto por debajo del objetivo, ajustar estado
        UPDATE META_AHORRO
        SET Estado='en_progreso'
        WHERE Monto_ahorrado<Monto_total_alcanzar;
    END IF;
END;



























































