@echo off
REM ---------------------------
REM Start HSQLDB Server - ready to use
REM ---------------------------

REM Ruta al JAR que usa DBeaver (no tocarrrrrrrrrrrrrrr)
set HSQLDB_JAR="C:\Users\royum\AppData\Roaming\DBeaverData\drivers\maven\maven-central\org.hsqldb\hsqldb-2.7.4.jar"

REM Ruta base de la BD (sin extension)
set DB_PATH=file:C:/Users/royum/OneDrive/Documentos/NetBeansProjects/Proyecto_Teo1/database/data/presupuesto_db.script

set DB_NAME=presupuesto_db
set PORT=9001

echo Verificando que no haya conexiones abiertas a la base...
echo - Asegurar de desconectar la conexion Embedded en DBeaver primero.
pause

echo Iniciando HSQLDB server...
java -cp %HSQLDB_JAR% org.hsqldb.Server ^
  --database.0 %DB_PATH% ^
  --dbname.0 %DB_NAME% ^
  --port %PORT%

echo Servidor HSQLDB detenido.
pause
