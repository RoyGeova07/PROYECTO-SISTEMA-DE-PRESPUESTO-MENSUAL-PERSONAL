/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.hsqldb.jdbc.JDBCDataSource;


/**
 *
 * @author royum
 */

/*

esta clase gestionara la conexion de la base de datos del sistema de presupuesto mensual personal

ademas la clase concentra la configuracion de conexion en un solo lugar, por lo que si cambia 
la ruta del archivo , el usuario o la contrasenia, solo se modifica aqui

*/

public class ConexionBD
{
    
    
    private static final String URL="jdbc:hsqldb:hsql://localhost:9001/presupuesto_db";
    private static final String USUARIO="SA";     
    private static final String PASSWORD="";  

    private static JDBCDataSource dataSource;

    static
    {
        try 
        {
            //Registrar el driver (por si acaso)
            Class.forName("org.hsqldb.jdbc.JDBCDriver");

            dataSource=new JDBCDataSource();
            dataSource.setUrl(URL);
            dataSource.setUser(USUARIO);
            dataSource.setPassword(PASSWORD);

        }catch(ClassNotFoundException ex){
            
            throw new RuntimeException("Cagada chele, no se encontro el driver", ex);
            
        }
    }

    public static Connection getConnection() throws SQLException 
    {
        
        return dataSource.getConnection();
        
    }

    
}