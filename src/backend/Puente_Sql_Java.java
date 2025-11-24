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
import java.sql.*;
import java.time.LocalDate;

/*

Usare preparedstatement porque me sirve para ejecutar consultas SQL de forma segura y eficiente, 
previniendo los ataques de inyeccion SQL y mejorando el rendimiento al precompilar las sentencias

tambien usare CallableStatement porque tambien me sirve para ejecutar procedimientos 
almacenados de SQL que ya están guardados en una base de datos

al igual que ResultSet lo utilizo para manejar los resultados de una consulta de base de datos 


*/

public class Puente_Sql_Java 
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
    
    public LinkedHashMap<String,Object>ConsultarUsuario(long idUsuario)throws SQLException
    {
        
        String SQL="SELECT ID_USUARIO, NOMBRE_USUARIO, APELLIDO_USUARIO, "+"       CORREO_ELECTRONICO, FECHA_REGISTRO, SALARIO_MENSUAL_BASE, "+"       ESTADO_USUARIO, CREADO_EN, MODIFICADO_EN, "+"       CREADO_POR, MODIFICADO_POR "+"FROM PUBLIC.USUARIOS "+"WHERE ID_USUARIO = ?";
        
        try(Connection con=ConexionBD.getConnection(); PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setLong(1, idUsuario);
            
            try(ResultSet rs=ps.executeQuery())
            {
                
                if(rs.next())
                {
                    
                    LinkedHashMap<String,Object>fila=new LinkedHashMap<>();
                    fila.put("id_usuario",rs.getLong("ID_USUARIO"));
                    fila.put("nombre_usuario",rs.getString("NOMBRE_USUARIO"));
                    fila.put("apellido_usuario",rs.getString("APELLIDO_USUARIO"));
                    fila.put("correo_electronico",rs.getString("CORREO_ELECTRONICO"));
                    fila.put("fecha_registro",rs.getTimestamp("FECHA_REGISTRO"));
                    fila.put("salario_mensual_base",rs.getBigDecimal("SALARIO_MENSUAL_BASE"));
                    fila.put("estado_usuario",rs.getBoolean("ESTADO_USUARIO"));
                    fila.put("creado_en",rs.getTimestamp("CREADO_EN"));
                    fila.put("modificado_en",rs.getTimestamp("MODIFICADO_EN"));
                    fila.put("creado_por",rs.getString("CREADO_POR"));
                    fila.put("modificado_por",rs.getString("MODIFICADO_POR"));
                    return fila;
                    
                }else{
                    
                    return null;//no existe crack
                    
                }
                
            }
            
        }
        
    }
    public long insertarUsuarioYObtenerId(String nombre,
            String apellido,
            String email,
            BigDecimal salarioMensual,
            String creadoPor) throws SQLException {
        // 1) Insertar el usuario usando tu SP
        insertarUsuario(nombre, apellido, email, salarioMensual, creadoPor);

        // 2) Consultar el ID (Correo_electronico es UNIQUE)
        String sqlId = "SELECT Id_usuario FROM PUBLIC.USUARIOS WHERE Correo_electronico = ?";

        try (Connection cn = ConexionBD.getConnection(); PreparedStatement ps = cn.prepareStatement(sqlId)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("Id_usuario");
                } else {
                    throw new SQLException("No se pudo obtener el ID del usuario recien insertado.");
                }
            }
        }
    }
    
    public void InsertarCategoria(String nombre,String descripcion,String tipo,long idUsuario,String creadoPor)throws SQLException
    {
        
        String SQL="CALL PUBLIC.SP_INSERTAR_CATEGORIA(?,?,?,?,?)";
        
        try(Connection cn=ConexionBD.getConnection(); CallableStatement cs=cn.prepareCall(SQL)) 
        {
            
            cs.setString(1, nombre);
            cs.setString(2, descripcion);
            cs.setString(3, tipo);
            cs.setLong(4, idUsuario);
            cs.setString(5, creadoPor);

            cs.execute(); //ejecutarrrrrr 
            
        }
        
    }
    
    public void EliminarCategoria(long Id_categoria)throws SQLException
    {
        
        String SQL="SELECT COUNT(*) "+"FROM SUBCATEGORIA "+"WHERE Id_categoria = ? "+"  AND Estado = TRUE "+"  AND Por_defecto = FALSE";
        
        String SQL_BORRAR_SUBCA="DELETE FROM SUBCATEGORIA WHERE Id_categoria=?";
        
        String SQL_SP_BORRAR_CATE="CALL PUBLIC.SP_ELIMINAR_CATEGORIA(?)";
        
        Connection con=null;
        
        try
        {
            
            //aqui se validan las subcategorias activas no por defecto
            try(PreparedStatement ps=con.prepareStatement(SQL))
            {
                
                ps.setLong(1, Id_categoria);
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    rs.next();
                    int cantidad=rs.getInt(1);
                    if(cantidad>0)
                    {
                        
                        throw new SQLException("No se puede eliminar la categoria: tiene subcategorias activas adicionales.");
                        
                    }
                    
                }
                
            }
            //aqui se borran todas las subcategorias, por defecto y las que queden
            //try(PreparedStatement psDel=con.pre) por ahora no lo agrego porque falta agregar eliminar subcategoria
            
            //aqui se borra la categoria usando el sp
            try(CallableStatement cs=con.prepareCall(SQL_SP_BORRAR_CATE))
            {
                
                cs.setLong(1, Id_categoria);
                cs.executeUpdate();
                
            }
            //aqui se confirma todo
            con.commit();//fin de transaccion
            System.out.println("Categoria"+Id_categoria+" eliminada correctamente");
            
        }catch(SQLException eror_porque){
            
            if(con!=null)
            {
                
                try
                {
                    
                    con.rollback();
                    
                }catch(SQLException ignorar){}
                
            }
            throw eror_porque;
            
        }finally{
            
            if(con!=null)
            {
                
                try
                {
                    
                    con.setAutoCommit(true);
                    con.close();
                    
                }catch(SQLException sapo){}
                
            }
            
        }
        
    }
    
    public LinkedHashMap<String,Object>consultarCategoria(long idCategoria)throws SQLException 
    {
        
        String SQL="SELECT Id_categoria, Nombre, Descripcion_detallada, Tipo_de_categoria "+"FROM CATEGORIA "+"WHERE Id_categoria = ?";
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idCategoria);

            try(ResultSet rs=ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    
                    LinkedHashMap<String,Object> fila=new LinkedHashMap<>();
                    fila.put("id_categoria", rs.getLong("Id_categoria"));
                    fila.put("nombre", rs.getString("Nombre"));
                    fila.put("descripcion_detallada", rs.getString("Descripcion_detallada"));
                    fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                    return fila;
                    
                }
                return null;//no existe foc
            }
        }
        
    }
    
    public List<LinkedHashMap<String,Object>>listarCategorias(long idUsuario, String tipo) throws SQLException 
    {
        
        String SQL= "SELECT c.Id_categoria, c.Nombre, c.Descripcion_detallada, c.Tipo_de_categoria "+"FROM CATEGORIA c ";
        
        //filtroo
        if(tipo!=null&&!tipo.isBlank())
        {
            
            SQL+= "WHERE c.Tipo_de_categoria = ? ";
            
        }
        List<LinkedHashMap<String,Object>>lista=new ArrayList<>();
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            if(tipo!=null&&!tipo.isBlank())
            {
                
                ps.setString(1, tipo);
                
            }
            try(ResultSet rs=ps.executeQuery())
            {
                
                while(rs.next())
                {
                    
                LinkedHashMap<String,Object>fila=new LinkedHashMap<>();
                fila.put("id_categoria", rs.getLong("Id_categoria"));
                fila.put("nombre", rs.getString("Nombre"));
                fila.put("descripcion_detallada", rs.getString("Descripcion_detallada"));
                fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                lista.add(fila);
                    
                }
                
            }
            
        }
        return lista;
        
    }
    public void InsertarSubcategoria(long idCategoria,String nombre,String descripcion,boolean esDefecto,String creadoPor)throws SQLException 
    {
        
        String SQL="CALL PUBLIC.SP_INSERTAR_SUBCATEGORIA(?,?,?,?,?)";
        
        try(Connection cn=ConexionBD.getConnection(); CallableStatement cs=cn.prepareCall(SQL)) 
        {
            cs.setLong(1, idCategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setBoolean(4, esDefecto);
            cs.setString(5, creadoPor);

            cs.execute();
        }
        
    }
    public void ActualizarSubcategoria(long idSubcategoria,String nombre,String descripcion,String modificadoPor)throws SQLException 
    {
        
        String SQL="CALL PUBLIC.SP_ACTUALIZAR_SUBCATEGORIA(?,?,?,?)";
       
        try(Connection cn=ConexionBD.getConnection();CallableStatement cs=cn.prepareCall(SQL)) 
        {
            
            cs.setLong(1, idSubcategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setString(4, modificadoPor);

            cs.executeUpdate();
            
        }

    }
    public void EliminarSubcategoria(long idSubcategoria)throws SQLException
    {
        
        String sqlUsoPresuDet="SELECT COUNT(*) FROM PRESUPUESTO_DETALLE WHERE Id_subcategoria =?";
        String sqlUsoTrans="SELECT COUNT(*) FROM TRANSACCION WHERE Id_subcategoria = ?";
        String sqlUsoMeta="SELECT COUNT(*) FROM META_AHORRO WHERE Id_subcategoria = ?";
        String sqlUsoObligacion="SELECT COUNT(*) FROM OBLIGACION_FIJA WHERE Id_subcategoria = ?";
        
        String sqlSpEliminar="CALL PUBLIC.SP_ELIMINAR_SUBCATEGORIA(?)";
        
        Connection con=null;
        
        try
        {
            
            con=ConexionBD.getConnection();
            con.setAutoCommit(false);//se empieza con transaccion
            
            int totalUsos=0;
            
            //presupuesto_detalla
            try(PreparedStatement ps=con.prepareStatement(sqlUsoPresuDet))
            {
                
                ps.setLong(1,idSubcategoria);
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(rs.next())
                    {
                        
                        totalUsos+=rs.getInt(1);
                        
                    }
                    
                }
                
            }
            //transaccion
            try(PreparedStatement ps=con.prepareStatement(sqlUsoTrans))
            {
                
                ps.setLong(1, idSubcategoria);
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(rs.next())
                    {
                        
                        totalUsos+=rs.getInt(1);
                        
                    }
                    
                }
                
            }
            //meta_ahorro
            try(PreparedStatement ps=con.prepareStatement(sqlUsoMeta))
            {
                
                ps.setLong(1, idSubcategoria);
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(rs.next())
                    {
                        
                        totalUsos+=rs.getInt(1);
                        
                    }
                    
                }
                
            }
            //obligacion_fija
            try(PreparedStatement ps=con.prepareStatement(sqlUsoObligacion))
            {
                
                ps.setLong(1, idSubcategoria);
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(rs.next())
                    {
                        
                        totalUsos+=rs.getInt(1);
                        
                    }
                    
                }
                
            }
            if(totalUsos>0)
            {
                
                throw new SQLException("No se puede eliminar la subcategoria "+idSubcategoria+": esta en uso en presupuesto, transacciones, metas o obligaciones");
                
            }
            //si no esta en uso, llamo al sp
            try(CallableStatement cs=con.prepareCall(sqlSpEliminar))
            {
                
                cs.setLong(1, idSubcategoria);
                cs.executeUpdate();
                
            }
            con.commit();
            System.out.println("subcategoria "+idSubcategoria+" eliminada correctamente");
                
            
        }catch(SQLException tamarindo){
            
            if(con!=null)
            {
                
                try{con.rollback();}catch(SQLException ignorar){}
                
            }
            throw tamarindo;
            
        }finally{
            
            if(con!=null)
            {
                
                try
                {
                    
                    con.setAutoCommit(true);
                    con.close();
                    
                }catch(SQLException ignore){}
                
            }
            
        }

    }
    public LinkedHashMap<String,Object>consultarSubCategoria(long idSubcategoria)throws SQLException
    {
        
        String SQL="SELECT s.Id_subcategoria, "+
        "s.Id_categoria, "+
        "s.Nombre_subcategoria, "+
        "s.Descripcion_detallada, "+
        "s.Estado, "+
        "s.Por_defecto, "+
        "s.creado_en, s.modificado_en, s.creado_por, s.modificado_por, " +
        "c.Nombre AS Nombre_categoria, "+
        "c.Tipo_de_categoria "+
        "FROM SUBCATEGORIA s "+
        "JOIN CATEGORIA c ON c.Id_categoria =s.Id_categoria "+"WHERE s.Id_subcategoria=?";
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps =con.prepareStatement(SQL)) 
        {
            
            ps.setLong(1, idSubcategoria);
            
            try(ResultSet rs=ps.executeQuery())
            {
                
                if(rs.next())
                {
                    
                    LinkedHashMap<String,Object> fila=new LinkedHashMap<>();

                    fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                    fila.put("id_categoria", rs.getLong("Id_categoria"));
                    fila.put("nombre_subcategoria", rs.getString("Nombre_subcategoria"));
                    fila.put("descripcion_detallada", rs.getString("Descripcion_detallada"));
                    fila.put("estado", rs.getBoolean("Estado"));
                    fila.put("por_defecto", rs.getBoolean("Por_defecto"));
                    fila.put("creado_en", rs.getTimestamp("creado_en"));
                    fila.put("modificado_en", rs.getTimestamp("modificado_en"));
                    fila.put("creado_por", rs.getString("creado_por"));
                    fila.put("modificado_por", rs.getString("modificado_por"));
                    fila.put("nombre_categoria", rs.getString("Nombre_categoria"));
                    fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                    
                    return fila;

                }else{
                    
                    return null;
                    
                }
                
            }
            
        }
        
    }
    public List<LinkedHashMap<String,Object>>listarSubcategoriasPorCategoria(long idCategoria)throws SQLException
    {
        
        String SQL= "SELECT s.Id_subcategoria, "+
        "s.Id_categoria, "+
        "s.Nombre_subcategoria, "+
        "s.Descripcion_detallada, "+
        "s.Estado, "+
        "s.Por_defecto, "+
        "s.creado_en, s.modificado_en, s.creado_por, s.modificado_por, " +
        "c.Nombre AS Nombre_categoria, "+
        "c.Tipo_de_categoria "+
        "FROM SUBCATEGORIA s "+
        "JOIN CATEGORIA c ON c.Id_categoria = s.Id_categoria "+
        "WHERE s.Id_categoria = ? "+
        "ORDER BY s.Id_subcategoria";
        
        List<LinkedHashMap<String,Object>>lista=new ArrayList<>();
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setLong(1, idCategoria);
            try(ResultSet rs=ps.executeQuery())
            {
                
                while(rs.next()) 
                {

                    LinkedHashMap<String,Object>fila= new LinkedHashMap<>();
                    
                    fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                    fila.put("id_categoria", rs.getLong("Id_categoria"));
                    fila.put("nombre_subcategoria", rs.getString("Nombre_subcategoria"));
                    fila.put("descripcion_detallada", rs.getString("Descripcion_detallada"));
                    fila.put("estado", rs.getBoolean("Estado"));
                    fila.put("por_defecto", rs.getBoolean("Por_defecto"));
                    fila.put("creado_en", rs.getTimestamp("creado_en"));
                    fila.put("modificado_en", rs.getTimestamp("modificado_en"));
                    fila.put("creado_por", rs.getString("creado_por"));
                    fila.put("modificado_por", rs.getString("modificado_por"));
                    fila.put("nombre_categoria", rs.getString("Nombre_categoria"));
                    fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                    
                    lista.add(fila);

                }

            }
            
        }
        return lista;
        
    }
    public void insertarPresupuesto(long idUsuario,String nombre,String descripcion,int anioInicio,int mesInicio,int anioFin,int mesFin,String creadoPor)throws SQLException 
    {
        
        //validaciones 
        if(mesInicio<1||mesFin>12||mesFin<1||mesInicio>12)
        {
            
            throw new SQLException("Mes de inicio/fin fuera de rango (1...12)");
            
        }
        int inicioNuevo=anioInicio*100+mesInicio;
        int finNuevo=anioFin*100+mesFin;
        
        if(finNuevo<inicioNuevo)
        {
            
            throw new SQLException("El periodo de fin debe ser posterior o igual al de inicio");
            
        }
        
        String SQL_VALIDAR="SELECT COUNT(*) "+"FROM PRESUPUESTO " +"WHERE Id_usuario =? "+"AND Estado_presupuesto='activo' " +"AND(Anio_de_inicio*100+Mes_de_inicio)<=? "+"AND (Anio_de_fin*100+Mes_de_fin)>=?";
        
        String SQL="CALL PUBLIC.SP_INSERTAR_PRESUPUESTO(?,?,?,?,?,?,?,?)";
        
        try(Connection con =ConexionBD.getConnection())
        {
            
            //1) validar solapamiento de periodos con otros presupuestos activo del mismo usuario
            try(PreparedStatement ps=con.prepareStatement(SQL_VALIDAR))
            {
                
                ps.setLong(1, idUsuario);
                ps.setInt(2, finNuevo);    // <= nuevo fin
                ps.setInt(3, inicioNuevo); // >= nuevo inicio
                
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(rs.next()&&rs.getInt(1)>0)
                    {
                        
                        throw new SQLException("Ya existe un presupuesto ACTIVO para este usuario"+" en un periodo que se traslapa con el inddicado");
                        
                    }
                    
                }
                
            }
            //insertar presupuesto
            try(CallableStatement cs=con.prepareCall(SQL))
            {
                
                cs.setLong(1, idUsuario);
                cs.setString(2, nombre);
                cs.setString(3, descripcion); // por ahora la tabla no tiene campo específico, pero es parte de la firma
                cs.setInt(4, anioInicio);
                cs.setInt(5, mesInicio);
                cs.setInt(6, anioFin);
                cs.setInt(7, mesFin);
                cs.setString(8, creadoPor);
                
                cs.execute();
                
            }
            
        }
        
    }
    public void ActualizarPresupuesto(long idPresupuesto,String nombre,String descripcion,int anioInicio,int mesInicio,int anioFin,int mesFin,String modificadoPor)throws SQLException 
    {
        
        //validaciones 
        if(mesInicio<1||mesFin>12||mesFin<1||mesInicio>12)
        {
            
            throw new SQLException("Mes de inicio/fin fuera de rango (1...12)");
            
        }
        int inicioNuevo=anioInicio*100+mesInicio;
        int finNuevo=anioFin*100+mesFin;
        
        if(finNuevo<inicioNuevo)
        {
            
            throw new SQLException("El periodo de fin debe ser posterior o igual al de inicio");
            
        }
        
        String SQL_OBTENER_USUARIO="SELECT Id_usuario, Estado_presupuesto "+"FROM PRESUPUESTO "+"WHERE Id_presupuesto =?";
        
        String SQL_VALIDAR="SELECT COUNT(*) "+
        "FROM PRESUPUESTO "+
        "WHERE Id_usuario =? "+
        "AND Estado_presupuesto = 'activo' "+
        "AND Id_presupuesto <> ? "+    // excluir el mismo
        "AND (Anio_de_inicio*100+Mes_de_inicio)<=? "+
        "AND (Anio_de_fin *100+Mes_de_fin)>=?";

        String SQL="CALL PUBLIC.SP_ACTUALIZAR_PRESUPUESTO(?,?,?,?,?,?,?,?)";
        
        try(Connection con=ConexionBD.getConnection())
        {
            
            long idUsuario;
            String estado;
            
            //aqui obtenemos el usuario y el estado del presupuesto
            try(PreparedStatement ps=con.prepareStatement(SQL_OBTENER_USUARIO))
            {
                
                ps.setLong(1, idPresupuesto);
                
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(!rs.next())
                    {
                        
                        throw new SQLException("no existe el presupuesto con ID = "+idPresupuesto);
                        
                    }
                    idUsuario=rs.getLong("Id_usuario");
                    estado=rs.getString("Estado_presupuesto");
                    
                }
                
            }
            //aqui se valida el solapamiento si este presupuesto esta activo
            if("activo".equalsIgnoreCase(estado))
            {
                
                try(PreparedStatement ps=con.prepareStatement(SQL_VALIDAR))
                {
                    
                    ps.setLong(1, idUsuario);
                    ps.setLong(2, idPresupuesto);
                    ps.setInt(3, finNuevo);
                    ps.setInt(4, inicioNuevo);

                    try(ResultSet rs=ps.executeQuery())
                    {
                        
                        if(rs.next()&&rs.getInt(1)>0)
                        {
                            
                            throw new SQLException("No se puede actualizar: el nuevo periodo se traslapa "+" con otro presupuesto ACTIVO del mismo usuario");
                            
                        }
                        
                    }
                    
                }
                
            }
            //aqui se ejecuta la actualizacion
            try(CallableStatement cs=con.prepareCall(SQL))
            {
                
                
                cs.setLong(1, idPresupuesto);
                cs.setString(2, nombre);
                cs.setString(3, descripcion);
                cs.setInt(4, anioInicio);
                cs.setInt(5, mesInicio);
                cs.setInt(6, anioFin);
                cs.setInt(7, mesFin);
                cs.setString(8, modificadoPor);
                
                cs.executeUpdate();

            }
            
        }

        
    }
    public void EliminarPresupuesto(long idPresupuesto)throws SQLException
    {
        
        String SQL_VALIDAR_USO="SELECT COUNT(*) "+"FROM TRANSACCION "+"WHERE Id_presupuesto =?";
        
        String SQL="CALL PUBLIC.SP_ELIMINAR_PRESUPUESTO(?)";
        
        try(Connection con=ConexionBD.getConnection())
        {
            
            //aqui se verifica si hay transacciones asociadas
            try(PreparedStatement ps=con.prepareStatement(SQL_VALIDAR_USO))
            {
                
                ps.setLong(1, idPresupuesto);
                
                try(ResultSet rs=ps.executeQuery())
                {
                    
                    if(rs.next()&&rs.getInt(1)>0)
                    {
                        
                        throw new SQLException("No se puede eliminar el presupuesto "+idPresupuesto+ ": no tiene transacciones asociadas");
                        
                    }
                    
                }
                
            }
            //si no tiene transacciones,llamar a la funcion de eliminar
            try(CallableStatement cs=con.prepareCall(SQL))
            {
                
                cs.setLong(1, idPresupuesto);
                cs.executeUpdate();
                
            }
            
        }

        
    }
    public LinkedHashMap<String,Object>consultarPresupuesto(long idPresupuesto)throws SQLException
    {
        
        String SQL="SELECT Id_presupuesto, Id_usuario, Nombre_descriptivo, Anio_de_inicio, Mes_de_inicio, "+
        "Anio_de_fin, Mes_de_fin, Total_de_ingresos, Total_de_gastos, Total_de_ahorro, "+
        "Fecha_hora_creacion, Estado_presupuesto, creado_en, modificado_en, creado_por, modificado_por "+
        "FROM PRESUPUESTO "+
        "WHERE Id_presupuesto = ?";
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setLong(1, idPresupuesto);
            
            try(ResultSet rs=ps.executeQuery())
            {
                
                if(rs.next())
                {
                    
                    LinkedHashMap<String,Object>fila=new LinkedHashMap<>();
                    fila.put("id_presupuesto", rs.getLong("Id_presupuesto"));
                    fila.put("id_usuario", rs.getLong("Id_usuario"));
                    fila.put("nombre_descriptivo", rs.getString("Nombre_descriptivo"));
                    fila.put("anio_de_inicio", rs.getInt("Anio_de_inicio"));
                    fila.put("mes_de_inicio", rs.getInt("Mes_de_inicio"));
                    fila.put("anio_de_fin", rs.getInt("Anio_de_fin"));
                    fila.put("mes_de_fin", rs.getInt("Mes_de_fin"));
                    fila.put("total_de_ingresos", rs.getBigDecimal("Total_de_ingresos"));
                    fila.put("total_de_gastos", rs.getBigDecimal("Total_de_gastos"));
                    fila.put("total_de_ahorro", rs.getBigDecimal("Total_de_ahorro"));
                    fila.put("fecha_hora_creacion", rs.getTimestamp("Fecha_hora_creacion"));
                    fila.put("estado_presupuesto", rs.getString("Estado_presupuesto"));
                    fila.put("creado_en", rs.getTimestamp("creado_en"));
                    fila.put("modificado_en", rs.getTimestamp("modificado_en"));
                    fila.put("creado_por", rs.getString("creado_por"));
                    fila.put("modificado_por", rs.getString("modificado_por"));
                    return fila;
                    
                }else{
                    
                    return null;
                    
                }
                
            }
            
        }
        
    }
    //listar presupuesto por usuario
    public List<LinkedHashMap<String,Object>>listarPresupuestoPorUsuario(long idUsuario,String estado)throws SQLException
    {
        
        StringBuilder sb=new StringBuilder();
        sb.append("SELECT Id_presupuesto, Id_usuario, Nombre_descriptivo, Anio_de_inicio, Mes_de_inicio, ");
        sb.append("Anio_de_fin, Mes_de_fin, Total_de_ingresos, Total_de_gastos, Total_de_ahorro, ");
        sb.append("Fecha_hora_creacion, Estado_presupuesto, creado_en, modificado_en, creado_por, modificado_por ");
        sb.append("FROM PRESUPUESTO ");
        sb.append("WHERE Id_usuario =? ");
        
        if(estado!=null&&!estado.isBlank())
        {
            
            sb.append("AND Estado_presupuesto=?");
            
        }
        sb.append("ORDER BY Id_presupuesto");
        
        String SQL=sb.toString();
        
        List<LinkedHashMap<String,Object>>lista=new ArrayList<>();
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setLong(1, idUsuario);
            if(estado!=null&&!estado.isBlank())
            {
                
                ps.setString(2,estado);
                
            }
            try(ResultSet rs=ps.executeQuery())
            {
                
                while(rs.next())
                {
               
                    LinkedHashMap<String, Object>fila=new LinkedHashMap<>();
                    fila.put("id_presupuesto", rs.getLong("Id_presupuesto"));
                    fila.put("id_usuario", rs.getLong("Id_usuario"));
                    fila.put("nombre_descriptivo", rs.getString("Nombre_descriptivo"));
                    fila.put("anio_de_inicio", rs.getInt("Anio_de_inicio"));
                    fila.put("mes_de_inicio", rs.getInt("Mes_de_inicio"));
                    fila.put("anio_de_fin", rs.getInt("Anio_de_fin"));
                    fila.put("mes_de_fin", rs.getInt("Mes_de_fin"));
                    fila.put("total_de_ingresos", rs.getBigDecimal("Total_de_ingresos"));
                    fila.put("total_de_gastos", rs.getBigDecimal("Total_de_gastos"));
                    fila.put("total_de_ahorro", rs.getBigDecimal("Total_de_ahorro"));
                    fila.put("fecha_hora_creacion", rs.getTimestamp("Fecha_hora_creacion"));
                    fila.put("estado_presupuesto", rs.getString("Estado_presupuesto"));
                    fila.put("creado_en", rs.getTimestamp("creado_en"));
                    fila.put("modificado_en", rs.getTimestamp("modificado_en"));
                    fila.put("creado_por", rs.getString("creado_por"));
                    fila.put("modificado_por", rs.getString("modificado_por"));
                    
                    lista.add(fila);
                    
                }
                
            }
            
        }
        return lista;
        
    }
    public void recalcularTotalesPresupuesto(long idPresupuesto)throws SQLException
    {
    
        String SQL = "{ CALL SP_RECALCULA_TOTALES_PARA_PRESUPUESTO(?) }";

        try (Connection con = ConexionBD.getConnection(); CallableStatement cstmt = con.prepareCall(SQL)) {

            cstmt.setLong(1, idPresupuesto);
            cstmt.execute();
            System.out.println("SP_RECALCULA_TOTALES_PARA_PRESUPUESTO: OK");
        }

    }
    public  void insertarDetalleYRecalcular(long idPresupuesto, long idSubcategoria,BigDecimal montoMensual, String justificacion,String creadoPor) throws SQLException
    {

        String SQL_insertar = "{ CALL SP_INSERTAR_PRESUPUESTO_DETALLE(?,?,?,?,?) }";
        String SQL_recalcular = "{ CALL SP_RECALCULA_TOTALES_PARA_PRESUPUESTO(?) }";

        try (Connection con = ConexionBD.getConnection(); CallableStatement cstmtIns = con.prepareCall(SQL_insertar); CallableStatement cstmtRecal = con.prepareCall(SQL_recalcular)) {

            con.setAutoCommit(false);
            try {
                cstmtIns.setLong(1, idPresupuesto);
                cstmtIns.setLong(2, idSubcategoria);
                cstmtIns.setBigDecimal(3, montoMensual);
                cstmtIns.setString(4, justificacion);
                cstmtIns.setString(5, creadoPor);
                cstmtIns.execute();

                // CORRECCIÓN: usar cstmtRecal para preparar el recálculo
                cstmtRecal.setLong(1, idPresupuesto);
                cstmtRecal.execute();

                con.commit();
                System.out.println("Insertar detalle + recalcular: OK");

            } catch (SQLException e) {
                con.rollback();
                String msg = interpretarSQLException(e);
                throw new SQLException(msg, e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
    /*
    
        Actualiza un detalle y recalcula totales (transaccional).
    
    */
    public  void actualizarDetalleYRecalcular(long idDetalle,BigDecimal nuevoMonto,String nuevaJustificacion,String modificadoPor)throws SQLException
    {
        
        String callUpdate="{ CALL SP_ACTUALIZAR_PRESUPUESTO_DETALLE(?,?,?,?) }";
        String selectPresu="SELECT Id_presupuesto FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto_detalle = ?";
        String callRecalc="{ CALL SP_RECALCULA_TOTALES_PARA_PRESUPUESTO(?) }";
        
        try(Connection conn=ConexionBD.getConnection();PreparedStatement psGet = conn.prepareStatement(selectPresu);CallableStatement cstmtUpd=conn.prepareCall(callUpdate);CallableStatement cstmtRecalc=conn.prepareCall(callRecalc)) 
        {
            
            conn.setAutoCommit(false);
            try
            {
                
                //obtener el idpresupuesto
                psGet.setLong(1, idDetalle);
                long idPresupuesto;
                
                try(ResultSet rs=psGet.executeQuery())
                {
                    
                    if(!rs.next())throw new SQLException("Detalle no encontrado: "+idDetalle);
                    idPresupuesto=rs.getLong(1);
                    
                }
                  cstmtUpd.setLong(1, idDetalle);
                cstmtUpd.setBigDecimal(2, nuevoMonto);
                cstmtUpd.setString(3, nuevaJustificacion);
                cstmtUpd.setString(4, modificadoPor);
                cstmtUpd.execute();

                cstmtRecalc.setLong(1, idPresupuesto);
                cstmtRecalc.execute();
                
                conn.commit();
                System.out.println("Update detalle + recalc: OK");

                
            }catch(SQLException e){
                
                conn.rollback();
                throw e;
                
            }finally{
                
                conn.setAutoCommit(true);
                
            }
            
        }

        
    }
    /*
    
        Elimina un detalle y recalcula totales (transaccional).
    
    */
    public  void eliminarDetalleYRecalcular(long idDetalle) throws SQLException 
    {
        
        String selectPresu="SELECT Id_presupuesto FROM PRESUPUESTO_DETALLE WHERE Id_presupuesto_detalle = ?";
        String callDelete="{ CALL SP_ELIMINAR_PRESUPUESTO_DETALLE(?) }";
        String callRecalc="{ CALL SP_RECALCULA_TOTALES_PARA_PRESUPUESTO(?) }";

        try(Connection conn=ConexionBD.getConnection(); PreparedStatement psGet=conn.prepareStatement(selectPresu); CallableStatement cstmtDel=conn.prepareCall(callDelete); CallableStatement cstmtRecalc=conn.prepareCall(callRecalc)) 
        {

            conn.setAutoCommit(false);
            try 
            {
                psGet.setLong(1, idDetalle);
                long idPresupuesto;
                try(ResultSet rs=psGet.executeQuery()) 
                {
                    if(!rs.next()) 
                    {
                        
                        throw new SQLException("Detalle no encontrado: "+idDetalle);
                        
                    }
                    idPresupuesto =rs.getLong(1);
                }

                cstmtDel.setLong(1, idDetalle);
                cstmtDel.execute();

                cstmtRecalc.setLong(1, idPresupuesto);
                cstmtRecalc.execute();

                conn.commit();
                System.out.println("Delete detalle + recalc: OK");
            }catch(SQLException ex){
                
                conn.rollback();
                throw ex;
                
            }finally{
                
                conn.setAutoCommit(true);
                
            }
        }
    }
    /*
    
        Recalcula totales a partir de un id_detalle (llama al SP que obtiene el presupuesto desde la fila detalle).
    
    */
    public void recalcularTotalesPorDetalle(long idDetalle) throws SQLException {
        String SQL = "{ CALL SP_RECALCULAR_TOTALES_PARA_PRESUPUESTO_IDDETALLE(?) }";
        try (Connection conn = ConexionBD.getConnection(); CallableStatement cstmt = conn.prepareCall(SQL)) {

            cstmt.setLong(1, idDetalle);
            cstmt.execute();
            System.out.println("SP_RECALCULAR_TOTALES_PARA_PRESUPUESTO_IDDETALLE: OK");

        }
    }

    public List<LinkedHashMap<String, Object>> listarDetallesPresupuesto(long p_id_presupuesto) throws SQLException {
        String SQL=""
                +"SELECT pd.Id_presupuesto_detalle, pd.Id_presupuesto, pd.Id_subcategoria, pd.Monto_mensual, "
                +"pd.Justificacion_del_monto, pd.creado_en, pd.modificado_en, "
                +"s.Nombre_subcategoria, s.Descripcion_detallada AS Descripcion_subcategoria, "
                +"c.Id_categoria, c.Nombre AS Nombre_categoria, c.Tipo_de_categoria "
                +"FROM PUBLIC.PRESUPUESTO_DETALLE pd "
                +"JOIN PUBLIC.SUBCATEGORIA s ON pd.Id_subcategoria = s.Id_subcategoria "
                +"JOIN PUBLIC.CATEGORIA c ON s.Id_categoria = c.Id_categoria "
                +"WHERE pd.Id_presupuesto = ? "
                +"ORDER BY pd.Id_presupuesto_detalle";

        List<LinkedHashMap<String, Object>> lista = new ArrayList<>();

        try(Connection con =ConexionBD.getConnection(); PreparedStatement ps= con.prepareStatement(SQL)) 
        {

            ps.setLong(1, p_id_presupuesto);

            try(ResultSet rs = ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    LinkedHashMap<String, Object> fila=new LinkedHashMap<>();
                    fila.put("id_presupuesto_detalle", rs.getLong("Id_presupuesto_detalle"));
                    fila.put("id_presupuesto", rs.getLong("Id_presupuesto"));
                    fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                    fila.put("monto_mensual", rs.getBigDecimal("Monto_mensual"));
                    fila.put("justificacion", rs.getString("Justificacion_del_monto"));
                    fila.put("creado_en", rs.getTimestamp("creado_en"));
                    fila.put("modificado_en", rs.getTimestamp("modificado_en"));
                    fila.put("nombre_subcategoria", rs.getString("Nombre_subcategoria"));
                    fila.put("descripcion_subcategoria", rs.getString("Descripcion_subcategoria"));
                    fila.put("id_categoria", rs.getLong("Id_categoria"));
                    fila.put("nombre_categoria", rs.getString("Nombre_categoria"));
                    fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                    lista.add(fila);
                }
            }
        }

        return lista;
    }
    
    public LinkedHashMap<String, Object> consultarPresupuestoDetalle(long p_id_detalle) throws SQLException 
    {
        String SQL=""
                +"SELECT pd.Id_presupuesto_detalle, pd.Id_presupuesto, pd.Id_subcategoria, pd.Monto_mensual, "
                +"pd.Justificacion_del_monto, pd.creado_en, pd.modificado_en, "
                +"p.Nombre_descriptivo AS Nombre_presupuesto, "
                +"s.Nombre_subcategoria, s.Descripcion_detallada AS Descripcion_subcategoria, "
                +"c.Id_categoria, c.Nombre AS Nombre_categoria, c.Tipo_de_categoria "
                +"FROM PUBLIC.PRESUPUESTO_DETALLE pd "
                +"JOIN PUBLIC.PRESUPUESTO p ON pd.Id_presupuesto = p.Id_presupuesto "
                +"JOIN PUBLIC.SUBCATEGORIA s ON pd.Id_subcategoria = s.Id_subcategoria "
                +"JOIN PUBLIC.CATEGORIA c ON s.Id_categoria = c.Id_categoria "
                +"WHERE pd.Id_presupuesto_detalle = ?";

        try(Connection con =ConexionBD.getConnection(); PreparedStatement ps =con.prepareStatement(SQL)) 
        {

            ps.setLong(1, p_id_detalle);

            try(ResultSet rs =ps.executeQuery()) 
            {
                if(!rs.next()) 
                {
                    return null;
                }

                LinkedHashMap<String, Object> fila=new LinkedHashMap<>();

                fila.put("id_presupuesto_detalle", rs.getLong("Id_presupuesto_detalle"));
                fila.put("id_presupuesto", rs.getLong("Id_presupuesto"));
                fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                fila.put("monto_mensual", rs.getBigDecimal("Monto_mensual"));
                fila.put("justificacion", rs.getString("Justificacion_del_monto"));
                fila.put("creado_en", rs.getTimestamp("creado_en"));
                fila.put("modificado_en", rs.getTimestamp("modificado_en"));

                // Info del presupuesto
                fila.put("nombre_presupuesto", rs.getString("Nombre_presupuesto"));

                // Info de subcategoria
                fila.put("nombre_subcategoria", rs.getString("Nombre_subcategoria"));
                fila.put("descripcion_subcategoria", rs.getString("Descripcion_subcategoria"));

                // Info de categoria
                fila.put("id_categoria", rs.getLong("Id_categoria"));
                fila.put("nombre_categoria", rs.getString("Nombre_categoria"));
                fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));

                return fila;
            }
        }
    }
    // Insertar obligación fija

    public void insertarObligacion(long idUsuario,long idSubcategoria,String nombre,String descripcion,BigDecimal monto,int diaVencimiento,java.sql.Date fechaInicio,java.sql.Date fechaFin,String creadoPor) throws SQLException 
    {

        String SQL="{ CALL PUBLIC.SP_INSERTAR_OBLIGACION(?,?,?,?,?,?,?,?,?) }";
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idUsuario);
            cs.setLong(2, idSubcategoria);
            cs.setString(3, nombre);
            cs.setString(4, descripcion);
            cs.setBigDecimal(5, monto);
            cs.setInt(6, diaVencimiento);
            cs.setDate(7, fechaInicio);
            if(fechaFin!=null) 
            {
                cs.setDate(8, fechaFin);
                
            }else{
                
                cs.setNull(8, java.sql.Types.DATE);
                
            }
            
            cs.setString(9, creadoPor);

            cs.execute();
        }
    }

    public void actualizarObligacion(long idObligacion,String nombre,String descripcion,BigDecimal monto,int diaVencimiento,java.sql.Date fechaFin,boolean activo,String modificadoPor) throws SQLException
    {

        String SQL="{ CALL PUBLIC.SP_ACTUALIZAR_OBLIGACION(?,?,?,?,?,?,?,?) }";
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idObligacion);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setBigDecimal(4, monto);
            cs.setInt(5, diaVencimiento);
            if(fechaFin!=null) 
            {
                
                cs.setDate(6, fechaFin);
                
            }else{
                
                cs.setNull(6, java.sql.Types.DATE);
                
            }
            cs.setBoolean(7, activo);
            cs.setString(8, modificadoPor);

            cs.executeUpdate();
        }
    }

    public void eliminarObligacion(long idObligacion) throws SQLException 
    {
        String SQL="{ CALL PUBLIC.SP_ELIMINAR_OBLIGACION(?) }";
        try(Connection con =ConexionBD.getConnection(); CallableStatement cs =con.prepareCall(SQL)) 
        {

            cs.setLong(1, idObligacion);
            cs.executeUpdate();

        }
    }

    public List<LinkedHashMap<String, Object>> listarObligacionesPorUsuario(long idUsuario) throws SQLException {

        String SQL="SELECT o.Id_obligacion_fija, o.Id_usuario, o.Id_subcategoria, o.Nombre, o.Descripcion_detallada, "
                +"o.Monto_fijo_mensual, o.Dia_del_mes_de_vencimiento, o.Esta_vigente, o.Fecha_inicio_de_la_obligacion, o.Fecha_de_finalizacion, "
                +"s.Nombre_subcategoria, c.Nombre AS Nombre_categoria, c.Tipo_de_categoria "
                +"FROM PUBLIC.OBLIGACION_FIJA o "
                +"JOIN PUBLIC.SUBCATEGORIA s ON o.Id_subcategoria = s.Id_subcategoria "
                +"JOIN PUBLIC.CATEGORIA c ON s.Id_categoria = c.Id_categoria "
                +"WHERE o.Id_usuario = ? "
                +"ORDER BY o.Id_obligacion_fija";

        List<LinkedHashMap<String, Object>> lista = new ArrayList<>();
        try(Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(SQL)) 
        {
            ps.setLong(1, idUsuario);
            try(ResultSet rs =ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    LinkedHashMap<String, Object> fila =new LinkedHashMap<>();
                    fila.put("id_obligacion", rs.getLong("Id_obligacion_fija"));
                    fila.put("id_usuario", rs.getLong("Id_usuario"));
                    fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                    fila.put("nombre", rs.getString("Nombre"));
                    fila.put("descripcion", rs.getString("Descripcion_detallada"));
                    fila.put("monto", rs.getBigDecimal("Monto_fijo_mensual"));
                    fila.put("dia_vencimiento", rs.getInt("Dia_del_mes_de_vencimiento"));
                    fila.put("esta_vigente", rs.getBoolean("Esta_vigente"));
                    fila.put("fecha_inicio", rs.getDate("Fecha_inicio_de_la_obligacion"));
                    fila.put("fecha_fin", rs.getDate("Fecha_de_finalizacion"));
                    fila.put("nombre_subcategoria", rs.getString("Nombre_subcategoria"));
                    fila.put("nombre_categoria", rs.getString("Nombre_categoria"));
                    fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }

    public LinkedHashMap<String, Object> consultarObligacion(long idObligacion) throws SQLException 
    {
        String SQL="SELECT o.*, s.Nombre_subcategoria, c.Nombre AS Nombre_categoria, c.Tipo_de_categoria "
                +"FROM PUBLIC.OBLIGACION_FIJA o "
                +"JOIN PUBLIC.SUBCATEGORIA s ON o.Id_subcategoria=s.Id_subcategoria "
                +"JOIN PUBLIC.CATEGORIA c ON s.Id_categoria =c.Id_categoria "
                +"WHERE o.Id_obligacion_fija =?";
        try(Connection con=ConexionBD.getConnection(); PreparedStatement ps=con.prepareStatement(SQL)) 
        {
            ps.setLong(1, idObligacion);
            try(ResultSet rs=ps.executeQuery()) 
            {
                if(!rs.next()) 
                {
                    
                    return null;
                    
                }
                LinkedHashMap<String, Object> fila=new LinkedHashMap<>();
                fila.put("id_obligacion", rs.getLong("Id_obligacion_fija"));
                fila.put("id_usuario", rs.getLong("Id_usuario"));
                fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                fila.put("nombre", rs.getString("Nombre"));
                fila.put("descripcion", rs.getString("Descripcion_detallada"));
                fila.put("monto", rs.getBigDecimal("Monto_fijo_mensual"));
                fila.put("dia_vencimiento", rs.getInt("Dia_del_mes_de_vencimiento"));
                fila.put("esta_vigente", rs.getBoolean("Esta_vigente"));
                fila.put("fecha_inicio", rs.getDate("Fecha_inicio_de_la_obligacion"));
                fila.put("fecha_fin", rs.getDate("Fecha_de_finalizacion"));
                fila.put("nombre_subcategoria", rs.getString("Nombre_subcategoria"));
                fila.put("nombre_categoria", rs.getString("Nombre_categoria"));
                fila.put("tipo_de_categoria", rs.getString("Tipo_de_categoria"));
                return fila;
            }
        }
    }
    // Inserta una transaccion usando SP_INSERTAR_TRANSACCION
    //                                                                                                      nullable

    public void insertarTransaccion(long idUsuario,long idPresupuesto,int anio,int mes,long idSubcategoria,Long idObligacion,String tipo,String descripcion, BigDecimal monto,java.sql.Date fecha,String metodoPago,String creadoPor) throws SQLException 
    {

        String SQL = "{ CALL PUBLIC.SP_INSERTAR_TRANSACCION(?,?,?,?,?,?,?,?,?,?,?,?) }"; // 12 placeholders
        try (Connection con = ConexionBD.getConnection(); CallableStatement cs = con.prepareCall(SQL)) {

            cs.setLong(1, idUsuario);
            cs.setLong(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            cs.setLong(5, idSubcategoria);

            if(idObligacion!=null) 
            {
                
                cs.setLong(6, idObligacion);
                
            }else{
                
                cs.setNull(6, java.sql.Types.BIGINT);
                
            }

            cs.setString(7, tipo);
            cs.setString(8, descripcion);
            cs.setBigDecimal(9, monto);
            cs.setDate(10, fecha);
            cs.setString(11, metodoPago);
            cs.setString(12, creadoPor); 

            cs.execute();
            System.out.println("SP_INSERTAR_TRANSACCION: executed (verifica si se inserto).");
        }
    }


    // Actualiza una transaccion (SP_ACTUALIZAR_TRANSACCION)
    public void actualizarTransaccion(long idTransaccion,int anio,int mes,String descripcion,BigDecimal monto,java.sql.Date fecha,String metodoPago,String modificadoPor) throws SQLException 
    {
        String SQL="{ CALL PUBLIC.SP_ACTUALIZAR_TRANSACCION(?,?,?,?,?,?,?,?) }";
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idTransaccion);
            cs.setInt(2, anio);
            cs.setInt(3, mes);
            cs.setString(4, descripcion);
            cs.setBigDecimal(5, monto);
            cs.setDate(6, fecha);
            cs.setString(7, metodoPago);
            cs.setString(8, modificadoPor);

            int updated=cs.executeUpdate();
            System.out.println("SP_ACTUALIZAR_TRANSACCION -> filas afectadas: " + updated);
        }
    }

    // Elimina (borra) una transacción (SP_ELIMINAR_TRANSACCION)
    public void eliminarTransaccion(long idTransaccion) throws SQLException 
    {
        String SQL="{ CALL PUBLIC.SP_ELIMINAR_TRANSACCION(?) }";
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idTransaccion);
            int deleted=cs.executeUpdate();
            System.out.println("SP_ELIMINAR_TRANSACCION -> filas afectadas: "+deleted);
        }
    }

    // Lista transacciones por presupuesto (informacion básica)
    public List<LinkedHashMap<String, Object>> listarTransaccionesPorPresupuesto(long idPresupuesto) throws SQLException 
    {
        String SQL="SELECT t.Id_transaccion, t.Id_usuario, t.Id_presupuesto, t.Anio, t.Mes, t.Id_subcategoria, "
                +"t.Id_obligacion_fija, t.Tipo_de_transaccion, t.Descripcion, t.Monto, t.Fecha, t.Metodo_de_pago, t.Fecha_hora_de_registro "
                +"FROM PUBLIC.TRANSACCION t WHERE t.Id_presupuesto = ? ORDER BY t.Id_transaccion";

        List<LinkedHashMap<String, Object>> lista = new ArrayList<>();
        try(Connection con=ConexionBD.getConnection(); PreparedStatement ps=con.prepareStatement(SQL)) 
        {
            ps.setLong(1, idPresupuesto);
            try(ResultSet rs=ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    LinkedHashMap<String, Object> fila=new LinkedHashMap<>();
                    fila.put("id_transaccion", rs.getLong("Id_transaccion"));
                    fila.put("id_usuario", rs.getLong("Id_usuario"));
                    fila.put("id_presupuesto", rs.getLong("Id_presupuesto"));
                    fila.put("anio", rs.getInt("Anio"));
                    fila.put("mes", rs.getInt("Mes"));
                    fila.put("id_subcategoria", rs.getLong("Id_subcategoria"));
                    fila.put("id_obligacion", rs.getObject("Id_obligacion_fija") != null ? rs.getLong("Id_obligacion_fija") : null);
                    fila.put("tipo", rs.getString("Tipo_de_transaccion"));
                    fila.put("descripcion", rs.getString("Descripcion"));
                    fila.put("monto", rs.getBigDecimal("Monto"));
                    fila.put("fecha", rs.getDate("Fecha"));
                    fila.put("metodo_pago", rs.getString("Metodo_de_pago"));
                    fila.put("fecha_hora_registro", rs.getTimestamp("Fecha_hora_de_registro"));
                    lista.add(fila);
                }
            }
        }
        return lista;
    }



    private static String interpretarSQLException(SQLException ex) {
        String sqlState = ex.getSQLState();
        int code = ex.getErrorCode();
        String mensaje = ex.getMessage();

        
        if (mensaje != null && mensaje.toLowerCase().contains("unique") || mensaje.toLowerCase().contains("duplicate")) {
            return "Violación de restricción: ya existe un registro duplicado (clave única).";
        }
        if (mensaje != null && mensaje.toLowerCase().contains("referential") || mensaje.toLowerCase().contains("foreign key")) {
            return "Violación de llave foránea: recurso relacionado no existe (FK). Verificar Id_presupuesto o Id_subcategoria.";
        }
        // Mensaje por defecto
        return "Error SQL (código " + code + ", sqlstate " + sqlState + "): " + mensaje;
    }


    
//por si acaso
    public void cerrarBaseDeDatos() throws SQLException
    {
        try(Connection cn=ConexionBD.getConnection(); java.sql.Statement st=cn.createStatement()) 
        {
            st.execute("SHUTDOWN");
            System.out.println("BD cerrada con SHUTDOWN.");
        }
    }


    public void hacerCheckpoint()throws SQLException 
    {
        try(Connection cn=ConexionBD.getConnection(); java.sql.Statement st=cn.createStatement()) 
        {
            
            st.execute("CHECKPOINT");
            System.out.println("\nCHECKPOINT ejecutado (cambios guardados en disco).");
            
        }
    }
    
    
//PRUEBAZZZZZ AQUIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII=========================================================
  public static void main(String[] args) 
    {
        Puente_Sql_Java dao=new Puente_Sql_Java();

        try 
        {
            dao.cerrarBaseDeDatos();
            long idDetalle = 1L;
            long idUsuario = 1L;
            long idSubcategoria = 1L;
            long idPresupuesto = 1L;
            int anio = 2025;
            int mes = 11;
            Long idObligacion = null; // sin obligación vinculada
            String tipo = "gasto"; // debe coincidir con el tipo de la categoria padre
            String descripcion = "Compra supermercado - prueba Java";
            BigDecimal monto = new BigDecimal("1200.00");
            LocalDate fechaLocal = LocalDate.of(2025, 11, 15);
            java.sql.Date fecha = java.sql.Date.valueOf(fechaLocal);
            String metodoPago = "efectivo";
            String creadoPor = "ROYY";

            System.out.println("\n==========USUARIOS ACTUALES==========");
            for(var fila:dao.listarUsuarios())
            {
                
                System.out.println(fila);
                
            }
            
            System.out.println("\n\n=====MOSTRANDO CATEGORIAS============");
            for(var fila:dao.listarCategorias(1L,"gasto"))
            {
                
                System.out.println(fila);
                
            }
            System.out.println("\n\n=====MOSTRANDO SUBCATEGORIAS============");
            for(var fila:dao.listarSubcategoriasPorCategoria(1L))
            {
                
                System.out.println(fila);
                
            }
            for(var fila:dao.listarPresupuestoPorUsuario(1L,"activo"))
            {
                
                System.out.println(fila);
                
            }
            System.out.println("\n\n=====LISTA DE PRESUPUESTOS_DETALLE");
            List<LinkedHashMap<String, Object>> detalles=dao.listarDetallesPresupuesto(1L); // usa el id_presupuesto correcto
            if(detalles.isEmpty()) 
            {
                System.out.println("No se encontraron detalles para el presupuesto "+1L);
            }else{
                
                for(var d : detalles) 
                {
                    
                    System.out.println(d);
                    
                }
                
            }
            
            List<LinkedHashMap<String, Object>> lista=dao.listarObligacionesPorUsuario(idUsuario);
            System.out.println("\n\n===== OBLIGACIONES DEL USUARIO "+ idUsuario+" =====");
            for (var fila : lista) 
            {
                
                System.out.println(fila);
            }
            
            dao.insertarTransaccion(idUsuario, idPresupuesto, anio, mes, idSubcategoria, idObligacion,
                    tipo, descripcion, monto, fecha, metodoPago, creadoPor);
            
            // --- 2) Listado de transacciones para el presupuesto ---
            System.out.println("\n=== LISTANDO TRANSACCIONES PARA PRESUPUESTO " + idPresupuesto + " ===");
            List<LinkedHashMap<String, Object>> list = dao.listarTransaccionesPorPresupuesto(idPresupuesto);
            if(list.isEmpty()) 
            {
                
                System.out.println("No hay transacciones. (Si no se insertó, revisa las validaciones del SP)");
                
            }else{
                for(var fila:list) 
                {
                    
                    System.out.println(fila);
                    
                }
            }



            //para guardar en disco
            dao.hacerCheckpoint();
            

        } catch (SQLException e) {

            e.printStackTrace();

        }
    }

}