/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  royum
 * Created: 4 nov 2025
 */

/*

Estas son auditorias me ayudan a saber cuando se creo, modifico un registro (creado_en,modificado_en)
quien lo creo/modifico (Creado_por,modificado_por)

con este se gana:Trazabilidad seria ver el historial basico de cada fila.
Depuracion: si algo se 'rompe', se sabe quien/cuando cambio algo.
Buenas practicas: casi todos los sistemas productivos tienen auditoria minima.

esto se usa al momento de insertar, se pone creado_por(por ejemplo, el usuario autenticado de la app).
al actualizar se setea modificado_por

*/

alter table USUARIOS add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table PRESUPUESTO add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table CATEGORIA add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table SUBCATEGORIA add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table PRESUPUESTO_DETALLE add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table OBLIGACION_FIJA add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table META_AHORRO add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table TRANSACCION add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);

alter table ALERTA add column if not exists creado_en timestamp default current_timestamp,
add column if not exists modificado_en timestamp,
add column if not exists creado_por varchar(100) default 'system',
add column if not exists modificado_por varchar(100);