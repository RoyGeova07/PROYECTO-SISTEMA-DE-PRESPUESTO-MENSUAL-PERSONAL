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