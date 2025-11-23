/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 4 nov 2025
 */

/*

    triggers - auditoria de actualizacion (modifcado en)
    
    cada vez que se haga un 'update' a una fila,marcar automaticamente la fecha/hora de ultima modificacion
    en "modificado_en" (la columna que se agrego en auditoria.sql)
    Esto evita que el app tenga que acordarse de poner ese timestamp

    sintaxis base de un trigger en HyperSQL
    CREATE TRIGGER <nombre>
    { BEFORE | AFTER } { INSERT | UPDATE | DELETE }
    ON <tabla>
    [REFERENCING OLD ROW AS O NEW ROW AS N]
    FOR EACH ROW
    BEGIN ATOMIC
      -- cuerpo (una o varias sentencias SQL)
    END;

    BEFORE vs AFTER: usamos BEFORE UPDATE para poder asignar valores a la fila nueva (NEW)
    antes de que se escriba en la tabla.

    REFERENCING: nos da alias para la fila vieja (OLD) y la nueva (NEW). En HSQLDB es común usar N para NEW.

    FOR EACH ROW: que dispare por cada fila afectada, no una sola vez por sentencia.

*/
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



----COMANDO PARA VERIFICAR QUE EXISTEN LOS TRIGGERS, YA QUE NETBEANS NO TIENE CARPETA PARA TRIGERS
SELECT TRIGGER_NAME, EVENT_MANIPULATION, EVENT_OBJECT_TABLE
FROM INFORMATION_SCHEMA.TRIGGERS
WHERE TRIGGER_NAME = 'nombre_trigger';

*/
drop trigger if exists TG_CATEGORIA_CREAR_SUBCATEGORIA_DEFECTO;

create trigger TG_CATEGORIA_CREAR_SUBCATEGORIA_DEFECTO
after insert on CATEGORIA
referencing new row as N
for each row                                                            -- si en lugar de general quiero que la subcategoria se llame igual que la categoria, agrego N.Nombre
    insert into SUBCATEGORIA(Id_categoria,Nombre_subcategoria,Descripcion_detallada,Estado,Por_defecto)
    values(N.Id_categoria,'Subcategoría por defecto de la categoria','General',true,true);


-- INSERT INTO CATEGORIA (Nombre, Descripcion_detallada, Tipo_de_categoria)
-- VALUES ('Prueba trigger', 'Gastos de prueba', 'gasto');
-- 
-- SELECT * FROM SUBCATEGORIA;


