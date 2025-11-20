package backend;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashMap;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.util.List;


/*
 * Operaciones directas sobre la tabla USUARIOS
 */
public class Usuario_Data 
{

        
    public void insertarUsuario(String nombre,String apellido,String email,BigDecimal salarioMensual,String creadoPor)throws SQLException 
    {

        //Sentencia para llamar al procedimiento en HSQLDB
        String sql="CALL PUBLIC.SP_INSERTAR_USUARIO(?, ?, ?, ?, ?)";


        //try-with-resources cierra conexion y statement automáticamente
        try(Connection cn =ConexionBD.getConnection();CallableStatement cs =cn.prepareCall(sql)) 
        {

            cs.setString(1, nombre);
            cs.setString(2, apellido);
            cs.setString(3, email);
            cs.setBigDecimal(4, salarioMensual);
            cs.setString(5, creadoPor);

            cs.execute();  // ejecuta el procedimiento
        }
    }
    
    public void ActualizarUsuario(long idUsuario,String nombre,String apellido,BigDecimal salarioMensual,String modificadoPor)throws SQLException
    {
        
        String SQL="CALL PUBLIC.SP_ACTUALIZAR_USUARIO(?,?,?,?,?)";
        
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1,idUsuario);
            cs.setString(2,nombre);
            cs.setString(3,apellido);
            cs.setBigDecimal(4,salarioMensual);
            cs.setString(5,modificadoPor);
            
            cs.executeUpdate();
            
        }
        
    }
    public void EliminarUsuario(long idUsuario)throws SQLException
    {
        
        String SQL="CALL PUBLIC.SP_ELIMINAR_USUARIO(?)";
        
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1,idUsuario);
            cs.executeUpdate();
            
        }
        
    }
    public List<LinkedHashMap<String,Object>>listarUsuarios()throws SQLException
    {
        
        String SQL="SELECT ID_USUARIO, NOMBRE_USUARIO, APELLIDO_USUARIO, "+"       CORREO_ELECTRONICO, FECHA_REGISTRO, ESTADO_USUARIO "+"FROM PUBLIC.USUARIOS";
        
        List<LinkedHashMap<String,Object>>lista=new ArrayList<>();
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL); ResultSet rs=ps.executeQuery())
        {
            
            while(rs.next())
            {
                
                LinkedHashMap<String,Object>fila=new LinkedHashMap<>();
                fila.put("id_usuario", rs.getLong("ID_USUARIO"));
                fila.put("nombre_usuario", rs.getString("NOMBRE_USUARIO"));
                fila.put("apellido_usuario", rs.getString("APELLIDO_USUARIO"));
                fila.put("correo_electronico", rs.getString("CORREO_ELECTRONICO"));
                fila.put("fecha_registro", rs.getTimestamp("FECHA_REGISTRO"));
                fila.put("estado_usuario", rs.getBoolean("ESTADO_USUARIO"));
                lista.add(fila);
                
            }
            
        }
        
        return lista;
    }
    public void hacerCheckpoint()throws SQLException 
    {
        try(Connection cn=ConexionBD.getConnection(); java.sql.Statement st=cn.createStatement()) 
        {
            
            st.execute("CHECKPOINT");
            System.out.println("CHECKPOINT ejecutado (cambios guardados en disco).");
            
        }
    }


    public static void main(String[] args) {
        Usuario_Data dao=new Usuario_Data();

        try 
        {
            
            //listar
            System.out.println("===USUARIOS====");
            for(var fila : dao.listarUsuarios()) 
            {

                System.out.println(fila);

            }

            dao.ActualizarUsuario(
                    2L,
                    "Leonel",
                    "Messi",
                    new BigDecimal("21304.32"),
                    "admin_Crick"
            );

            System.out.println("\n===Lista deespues de actualizar\n======");
            for(var fila:dao.listarUsuarios()) 
            {

                System.out.println(fila);

            }
            //para guardar en disco
            dao.hacerCheckpoint();
            

        }catch (SQLException e){
            
            e.printStackTrace();
            
        }
    }
}