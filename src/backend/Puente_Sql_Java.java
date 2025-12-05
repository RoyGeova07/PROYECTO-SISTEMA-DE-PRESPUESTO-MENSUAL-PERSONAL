package backend;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/*

Usare preparedstatement porque me sirve para ejecutar consultas SQL de forma segura y eficiente, 
previniendo los ataques de inyeccion SQL y mejorando el rendimiento al precompilar las sentencias

tambien usare CallableStatement porque tambien me sirve para ejecutar procedimientos 
almacenados de SQL que ya están guardados en una base de datos

al igual que ResultSet lo utilizo para manejar los resultados de una consulta de base de datos 


*/

public class Puente_Sql_Java 
{

    public void Insertar_usuario(String nombre,String apellido,String correo,BigDecimal salario,String creadoPor)throws SQLException
    {
        
        String SQL="{ CALL sp_insertar_usuario(?,?,?,?,?)}";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setString(1, nombre);
            cs.setString(2, apellido);
            cs.setString(3, correo);
            cs.setBigDecimal(4, salario);
            cs.setString(5, creadoPor);
            
            cs.execute();
            
        }
        
    }
    public void Actualizar_usuario(long idUsuario,String nombre,String apellido,BigDecimal salario,String modificadoPor)throws SQLException
    {
        
        String SQL="{ CALL sp_actualizar_usuario(?,?,?,?,?)}";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1, idUsuario);
            cs.setString(2, nombre);
            cs.setString(3, apellido);
            cs.setBigDecimal(4, salario);
            cs.setString(5, modificadoPor);
            
            cs.executeUpdate();
            
        }
        
    }
    public boolean Eliminar_usuario(long idUsuario)throws SQLException
    {
        
        String SQL="{ CALL sp_eliminar_usuario(?) }";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1, idUsuario);
           
            cs.execute();//el procedure hace el update internamente bro
            
            return true;//si no lanzo excepcion pijudo
            
        }catch(SQLException valio){
            
            throw valio;//valio
            
        }
        
    }
    public List<Usuario>ListarUsuarios()throws SQLException    
    {
        
        List<Usuario>lista=new ArrayList<>();
        String SQL="SELECT*FROM TABLE(PUBLIC.SP_LISTAR_USUARIOS())";//por a funcion de returns table
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL);ResultSet rs=ps.executeQuery())
        {
            
            while(rs.next())
            {
                
                Usuario u=MapearUsuario(rs);
                lista.add(u);
                
            }
            
        }
        return lista;
        
    }
    private Usuario MapearUsuario(ResultSet rs)throws SQLException
    {
        
        Usuario u=new Usuario();
        u.setId(rs.getLong("ID_USUARIO"));
        u.setNombre(rs.getString("NOMBRE_USUARIO"));
        u.setApellido(rs.getString("APELLIDO_USUARIO"));
        u.setCorreo(rs.getString("CORREO_ELECTRONICO"));
        u.setFechaRegistro(rs.getTimestamp("FECHA_REGISTRO"));
        u.setSalario(rs.getBigDecimal("SALARIO_MENSUAL_BASE"));
        
        boolean estado=true;
        try
        {
            
            estado=rs.getBoolean("ESTADO_USUARIO");
            if(rs.wasNull())u.setEstado_usuario(null);
            else u.setEstado_usuario(estado);
            
        }catch(SQLException valio){
            
            //la columna no existe, se deha en null
            u.setEstado_usuario(null);
            
        }
        
        try{u.setCreadoEn(rs.getTimestamp("CREADO_EN"));}catch(SQLException e){}
        try{u.setCreadoPor(rs.getString("CREADO_POR"));}catch(SQLException e){}
        try{u.setModficadoEn(rs.getTimestamp("MODIFICADO_EN"));}catch(SQLException e){}
        try{u.setModificadoPor(rs.getString("MODIFICADO_POR"));}catch(SQLException hitler){}

        return u;
        
    }
    public Usuario ConsultarUsuario(long idUsuario)throws SQLException
    {
        
        String SQL="SELECT * FROM sp_consultar_usuario(?)";
        
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setLong(1, idUsuario);
            try(ResultSet rs=ps.executeQuery())
            {
                
                if(rs.next())
                {
                    
                    return MapearUsuario(rs);
                    
                }else{
                    
                    return null;//no existe crack
                    
                }
                
            }
            
        }
        
    }
    public boolean ReactivarUsuario(long idUsuario,String modificadoPor)throws SQLException
    {
        
        String SQL="{ CALL sp_reactivar_usuario(?, ?) }";
        
        System.out.println("DEBUG: Reactivando usuario con id = " + idUsuario + ", modificadoPor = " + modificadoPor);
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1, idUsuario);
            cs.setString(2, modificadoPor);
            
            cs.execute();
            
            return true;
            
        }catch(SQLException ex){
            
            if("45000".equals(ex.getSQLState())) 
            {

                System.err.println("ERROR BD controlado: SQLState=" + ex.getSQLState() + " Mensaje=" + ex.getMessage());
                throw ex;
            }
            ex.printStackTrace();
            throw ex;
            
        }
        
 
    }
    public void InsertarCategoria(String nombre,String descripcion,String tipo,Long idUsuario,String creadoPor)throws SQLException
    {
        
        String SQL="{ CALL SP_INSERTAR_CATEGORIA(?, ?, ?, ?, ?) }";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setString(1, nombre);
            cs.setString(2, descripcion);
            cs.setString(3, tipo==null?null:tipo);//por el lower
            if(idUsuario==null)cs.setNull(4, Types.BIGINT);else cs.setLong(4, idUsuario);
            cs.setString(5, creadoPor);
            
            cs.execute();
            
        }
        
    }
    public void ActualizarCategoria(long idCategoria,String nombre,String descripcion,String modificadoPor)throws SQLException
    {
        
        String SQL="{ CALL SP_ACTUALIZAR_CATEGORIA(?, ?, ?, ?) }";
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1, idCategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            cs.setString(4, modificadoPor);
            cs.execute();
            
        }
        
    }
    public boolean EliminarCategoria(long idSubcategoria)throws SQLException
    {
        
        String SQL="{ CALL SP_ELIMINAR_CATEGORIA(?) }";
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1, idSubcategoria);
            cs.execute();
            return true;
            
        }catch(SQLException e){
            
            String sqlState=e.getSQLState();
            
            if("23503".equals(sqlState) || "23513".equals(sqlState) || "23000".equals(sqlState)) 
            {
                
                String userMessage = "No se puede eliminar la categoría porque está relacionada con subcategorías u otros registros.";
                
                throw new SQLException(userMessage, sqlState);

            }
            
            if("45000".equals(e.getSQLState()))
            {
                
                throw e;
                
            }
            throw e;
            
        }
        
    }
    public Categoria ConsultarCategoria(long idCategoria)throws SQLException
    {
        
        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_CATEGORIA(?))";
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL))
        {
            
            ps.setLong(1, idCategoria);
            try(ResultSet rs = ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    return mapearCategoria(rs);
                    
                }else{
                    
                    return null; 
                    
                }
            }
            
            
        }catch(SQLException e){
            
            if("45000".equals(e.getSQLState())) 
            {
                
                System.err.println("ERROR BD controlado (consultar categoria): "+e.getMessage());
                throw e;
            }
            // Otro error inesperado
            throw e;
            
        }
       
        
        
    }
    private Categoria mapearCategoria(ResultSet rs) throws SQLException 
    {
        Categoria c=new Categoria();

        c.setId(rs.getLong("ID_CATEGORIA"));
        try{c.setNombre(rs.getString("NOMBRE"));}catch(SQLException e){c.setNombre(null);}
        try{c.setDescripcion(rs.getString("DESCRIPCION_DETALLADA"));}catch(SQLException e){c.setDescripcion(null);}
        try{c.setTipo(rs.getString("TIPO_DE_CATEGORIA"));}catch(SQLException e){c.setTipo(null);}

        // timestamps y created/modified
        try{c.setCreadoEn(rs.getTimestamp("CREADO_EN"));}catch(SQLException e){c.setCreadoEn(null);}
        try{c.setModificadoEn(rs.getTimestamp("MODIFICADO_EN"));}catch(SQLException e){c.setModificadoEn(null);}
        try{c.setCreadoPor(rs.getString("CREADO_POR"));}catch(SQLException e){c.setCreadoPor(null);}
        try{c.setModificadoPor(rs.getString("MODIFICADO_POR"));}catch(SQLException e){c.setModificadoPor(null);}

        // idUsuario no existe en tu tabla actual; si lo agregas podrías mapearlo igual aquí.
        try 
        {
            long idUsr=rs.getLong("ID_USUARIO");
            if(rs.wasNull())c.setIdUsuario(null);
            else c.setIdUsuario(idUsr);
        }catch(SQLException e){
            
            c.setIdUsuario(null);
        }

        return c;
    }
    public void InsertarSubcategoria(long idCategoria,String nombre,String descripcion,boolean esDefecto,String creadoPor)throws SQLException 
    {
        
        String SQL="{ CALL SP_INSERTAR_SUBCATEGORIA(?,?,?,?,?) }";
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
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
        String SQL="{ CALL SP_ACTUALIZAR_SUBCATEGORIA(?,?,?,?) }";
        try (Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idSubcategoria);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);

            if(modificadoPor==null) 
            {
                cs.setNull(4, java.sql.Types.VARCHAR);
            }else{
                cs.setString(4, modificadoPor);
            }

            cs.execute();
        }
    }
    public boolean EliminarSubcategoria(long idSubcategoria)throws SQLException 
    {
        String SQL="{ CALL SP_ELIMINAR_SUBCATEGORIA(?) }";
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idSubcategoria);
            cs.execute();
            return true;

        }catch(SQLException ex){
            String sqlState=ex.getSQLState();
            
            if("45000".equals(sqlState)) 
            {
                
                System.err.println("BD controlada: " + ex.getMessage());
                throw ex;
            }
            
            if("23503".equals(sqlState) || "23513".equals(sqlState) || "23000".equals(sqlState)) 
            {
                throw new SQLException("No se puede eliminar la subcategoría: está en uso o viola restricciones de integridad.", sqlState);
            }
            throw ex;
        }
    }
    public Subcategoria ConsultarSubcategoria(long idSubcategoria) throws SQLException 
    {
        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_SUBCATEGORIA(?))";
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idSubcategoria);
            try(ResultSet rs = ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    return mapearSubcategoria(rs);
                }else{
                    
                    return null;
                    
                }
            }
        }catch(SQLException ex){
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (consultar subcategoria): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public List<Subcategoria>ListarSubcategoriasPorCategoria(long idCategoria)throws SQLException 
    {
        List<Subcategoria>lista=new ArrayList<>();
        String SQL="SELECT * FROM TABLE(SP_LISTAR_SUBCATEGORIAS_POR_CATEGORIA(?))";
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idCategoria);
            try(ResultSet rs=ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    
                    lista.add(mapearSubcategoria(rs));
                    
                }
            }
        }
        return lista;
    }
    private Subcategoria mapearSubcategoria(ResultSet rs)throws SQLException 
    {
        
        Subcategoria s=new Subcategoria();
        s.setId(rs.getLong("ID_SUBCATEGORIA"));
        try{s.setIdCategoria(rs.getLong("ID_CATEGORIA"));}catch(SQLException e){}
        try{s.setNombre(rs.getString("NOMBRE_SUBCATEGORIA"));}catch(SQLException e){}
        try{s.setDescripcion(rs.getString("DESCRIPCION_DETALLADA"));}catch(SQLException e){}

        try 
        {
            boolean est=rs.getBoolean("ESTADO");
            if(rs.wasNull()) s.setEstado(null);else s.setEstado(est);
        }catch(SQLException e) { s.setEstado(null);}

        try 
        {
            boolean pd=rs.getBoolean("POR_DEFECTO");
            if(rs.wasNull()) s.setPorDefecto(null);else s.setPorDefecto(pd);
        }catch(SQLException e){s.setPorDefecto(null);}

        try{s.setTipoCategoria(rs.getString("TIPO_DE_CATEGORIA"));}catch(SQLException e){s.setTipoCategoria(null);}
        try{s.setCategoriaNombre(rs.getString("CATEGORIA_NOMBRE"));}catch(SQLException e){s.setCategoriaNombre(null);}

        try{s.setCreadoEn(rs.getTimestamp("CREADO_EN"));}catch(SQLException e){s.setCreadoEn(null);}
        try{s.setModificadoEn(rs.getTimestamp("MODIFICADO_EN"));}catch(SQLException e){s.setModificadoEn(null); }
        try{s.setCreadoPor(rs.getString("CREADO_POR"));}catch(SQLException e){s.setCreadoPor(null);}
        try{s.setModificadoPor(rs.getString("MODIFICADO_POR"));}catch(SQLException e){s.setModificadoPor(null); }

        return s;
    }
    public void InsertarPresupuesto(long idUsuario,String nombreDescriptivo,int anioInicio, int mesInicio,int anioFin, int mesFin,java.math.BigDecimal totalIngresos,java.math.BigDecimal totalGastos,java.math.BigDecimal totalAhorro,String creadoPor)throws SQLException
    {
        
        String SQL="{ CALL SP_INSERTAR_PRESUPUESTO(?,?,?,?,?,?,?,?,?,?) }";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL))
        {
            
            cs.setLong(1, idUsuario);
            cs.setString(2, nombreDescriptivo);
            cs.setInt(3, anioInicio);
            cs.setInt(4, mesInicio);
            cs.setInt(5, anioFin);
            cs.setInt(6, mesFin);
            
            if(totalIngresos==null)cs.setNull(7, java.sql.Types.DECIMAL);else cs.setBigDecimal(7, totalIngresos);
            if(totalGastos==null)cs.setNull(8, java.sql.Types.DECIMAL);else cs.setBigDecimal(8, totalGastos);
            if(totalAhorro==null)cs.setNull(9, java.sql.Types.DECIMAL);else cs.setBigDecimal(9, totalAhorro);
            if(creadoPor==null)cs.setNull(10, java.sql.Types.VARCHAR);else cs.setString(10, creadoPor);
            
            cs.execute();


        }catch(SQLException ex){
            
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (insertar presupuesto): "+ex.getMessage());
                throw ex;
            }
            throw ex;
        }
        
    }
    public void ActualizarPresupuesto(long idPresupuesto,String nombreDescriptivo,int anioInicio, int mesInicio,int anioFin, int mesFin,java.math.BigDecimal totalIngresos,java.math.BigDecimal totalGastos,java.math.BigDecimal totalAhorro,String modificadoPor)throws SQLException
    {
        
        String SQL="{ CALL SP_ACTUALIZAR_PRESUPUESTO(?,?,?,?,?,?,?,?,?,?) }";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {
             
            cs.setLong(1, idPresupuesto);
            cs.setString(2, nombreDescriptivo);
            cs.setInt(3, anioInicio);
            cs.setInt(4, mesInicio);
            cs.setInt(5, anioFin);
            cs.setInt(6, mesFin);

            if(totalIngresos==null)cs.setNull(7, java.sql.Types.DECIMAL);else cs.setBigDecimal(7, totalIngresos);
            if(totalGastos==null)cs.setNull(8, java.sql.Types.DECIMAL);else cs.setBigDecimal(8, totalGastos);
            if(totalAhorro==null)cs.setNull(9, java.sql.Types.DECIMAL);else cs.setBigDecimal(9, totalAhorro);

            if(modificadoPor==null)cs.setNull(10, java.sql.Types.VARCHAR);else cs.setString(10, modificadoPor);
            
            cs.execute();
             
        }catch(SQLException ex){
            
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (actualizar presupuesto): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
        
    }
    public boolean EliminarPresupuesto(long idPresupuesto)throws SQLException 
    {
        String SQL="{ CALL SP_ELIMINAR_PRESUPUESTO(?) }";
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idPresupuesto);
            cs.execute();
            return true;
        }catch(SQLException ex){
            String sqlState=ex.getSQLState();
            if("45000".equals(sqlState)) 
            {
                System.err.println("BD controlada (eliminar presupuesto): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public Presupuesto ConsultarPresupuesto(long idPresupuesto)throws SQLException 
    {
        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_PRESUPUESTO(?))";
        try(Connection con=ConexionBD.getConnection();PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idPresupuesto);
            try(ResultSet rs = ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    return mapearPresupuesto(rs);
                    
                }else{
                    
                    return null;
                    
                }
            }
        }catch (SQLException ex){
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (consultar presupuesto): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    private Presupuesto mapearPresupuesto(ResultSet rs)throws SQLException 
    {
        Presupuesto p=new Presupuesto();

        // id
        long id=rs.getLong("ID_PRESUPUESTO");
        if(rs.wasNull()) 
        {
            p.setId(null);
        }else{
            
            p.setId(id);
            
        }

        // idUsuario
        try
        {
            long idUsr=rs.getLong("ID_USUARIO");
            if(rs.wasNull()) 
            {
                p.setIdUsuario(null);
            }else{
                p.setIdUsuario(idUsr);
            }
        }catch(SQLException e) {
            p.setIdUsuario(null);
        }

        try 
        {
            
            p.setNombreDescriptivo(rs.getString("NOMBRE_DESCRIPTIVO"));
            
        }catch(SQLException e){
            
            p.setNombreDescriptivo(null);
            
        }

        try 
        {
            int ai=rs.getInt("ANIO_DE_INICIO");
            if(rs.wasNull()) 
            {
                
                p.setAnioInicio(null);
                
            }else{
                
                p.setAnioInicio(ai);
                
            }
        }catch(SQLException e){
            
            p.setAnioInicio(null);
            
        }
        try 
        {
            int mi=rs.getInt("MES_DE_INICIO");
            if(rs.wasNull()) 
            {
                p.setMesInicio(null);
            }else{
                p.setMesInicio(mi);
            }
        }catch (SQLException e){
            p.setMesInicio(null);
        }
        try 
        {
            int af=rs.getInt("ANIO_DE_FIN");
            if(rs.wasNull())
            {
                p.setAnioFin(null);
            }else{
                p.setAnioFin(af);
            }
        }catch (SQLException e){
            p.setAnioFin(null);
        }
        try 
        {
            int mf=rs.getInt("MES_DE_FIN");
            if(rs.wasNull()) 
            {
                p.setMesFin(null);
            }else{
                p.setMesFin(mf);
            }
        }catch (SQLException e){
            p.setMesFin(null);
        }

        try 
        {
            p.setTotalIngresos(rs.getBigDecimal("TOTAL_DE_INGRESOS"));
        }catch (SQLException e){
            p.setTotalIngresos(null);
        }
        try 
        {
            p.setTotalGastos(rs.getBigDecimal("TOTAL_DE_GASTOS"));
        }catch(SQLException e){
            p.setTotalGastos(null);
        }
        try 
        {
            p.setTotalAhorro(rs.getBigDecimal("TOTAL_DE_AHORRO"));
        }catch (SQLException e){
            p.setTotalAhorro(null);
        }

        try 
        {
            p.setFechaHoraCreacion(rs.getTimestamp("FECHA_HORA_CREACION"));
        }catch (SQLException e){
            p.setFechaHoraCreacion(null);
        }
        try 
        {
            p.setEstado(rs.getString("ESTADO_PRESUPUESTO"));
        }catch (SQLException e){
            p.setEstado(null);
        }
        try 
        {
            p.setCreadoEn(rs.getTimestamp("CREADO_EN"));
        }catch (SQLException e){
            p.setCreadoEn(null);
        }
        try 
        {
            p.setModificadoEn(rs.getTimestamp("MODIFICADO_EN"));
        }catch (SQLException e){
            p.setModificadoEn(null);
        }
        try 
        {
            p.setCreadoPor(rs.getString("CREADO_POR"));
        }catch (SQLException e){
            p.setCreadoPor(null);
        }
        try 
        {
            p.setModificadoPor(rs.getString("MODIFICADO_POR"));
        }catch (SQLException e){
            p.setModificadoPor(null);
        }

        return p;
    }
    public void InsertarPresupuestoDetalle(long idPresupuesto,long idSubcategoria,java.math.BigDecimal montoMensual,String justificacion,String creadoPor)throws SQLException
    {
        
        String SQL="{ CALL SP_INSERTAR_PRESUPUESTO_DETALLE(?,?,?,?,?) }";
        
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {
            
            cs.setLong(1, idPresupuesto);
            cs.setLong(2, idSubcategoria);
            if(montoMensual==null) cs.setNull(3, java.sql.Types.DECIMAL);else cs.setBigDecimal(3, montoMensual);
            if(justificacion== null) cs.setNull(4, java.sql.Types.VARCHAR);else cs.setString(4, justificacion);
            if(creadoPor ==null) cs.setNull(5, java.sql.Types.VARCHAR);else cs.setString(5, creadoPor);
            
            cs.execute();
            
        }catch (SQLException ex){
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (insertar detalle): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
        
    }
    public void ActualizarPresupuestoDetalle(long idDetalle,java.math.BigDecimal montoMensual,String justificacion,String modificadoPor)throws SQLException 
    {
        String SQL = "{ CALL SP_ACTUALIZAR_PRESUPUESTO_DETALLE(?,?,?,?) }";
        try(Connection con=ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idDetalle);
            if(montoMensual ==null)
            {
                cs.setNull(2, java.sql.Types.DECIMAL);
            }else{
                cs.setBigDecimal(2, montoMensual);
            }
            if(justificacion ==null) 
            {
                cs.setNull(3, java.sql.Types.VARCHAR);
            }else{
                cs.setString(3, justificacion);
            }
            if(modificadoPor==null) 
            {
                cs.setNull(4, java.sql.Types.VARCHAR);
            }else{
                cs.setString(4, modificadoPor);
            }

            cs.execute();
        }catch (SQLException ex){
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (actualizar detalle): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public boolean EliminarPresupuestoDetalle(long idDetalle)throws SQLException 
    {
        String SQL="{ CALL SP_ELIMINAR_PRESUPUESTO_DETALLE(?) }";
        try(Connection con=ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idDetalle);
            cs.execute();
            
            return true;
        }catch (SQLException ex){
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (eliminar detalle): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public Presupuesto_detalle ConsultarPresupuestoDetalle(long idDetalle)throws SQLException 
    {
        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_PRESUPUESTO_DETALLE(?))";
        try(Connection con=ConexionBD.getConnection(); PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idDetalle);
            try(ResultSet rs =ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    return mapearPresupuestoDetalle(rs);
                }else{
                    return null;
                }
            }
        }catch (SQLException ex){
            if("45000".equals(ex.getSQLState())) 
            {
                System.err.println("BD controlada (consultar detalle): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public List<Presupuesto_detalle>ListarDetallesPresupuesto(long idPresupuesto)throws SQLException 
    {
        List<Presupuesto_detalle>lista =new ArrayList<>();
        String SQL="SELECT * FROM TABLE(SP_LISTAR_DETALLES_PRESUPUESTO(?))";
        try(Connection con=ConexionBD.getConnection(); PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idPresupuesto);
            try(ResultSet rs=ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    lista.add(mapearPresupuestoDetalle(rs));
                }
            }
        }
        return lista;
    }
    private Presupuesto_detalle mapearPresupuestoDetalle(ResultSet rs)throws SQLException 
    {
        Presupuesto_detalle pd=new Presupuesto_detalle();

        try{long id=rs.getLong("ID_PRESUPUESTO_DETALLE");if(rs.wasNull()) pd.setId(null);else pd.setId(id);}catch(SQLException e){pd.setId(null);}
        try{long ip=rs.getLong("ID_PRESUPUESTO");if(rs.wasNull()) pd.setIdPresupuesto(null);else pd.setIdPresupuesto(ip); }catch(SQLException e){pd.setIdPresupuesto(null);}
        try{long is=rs.getLong("ID_SUBCATEGORIA");if(rs.wasNull()) pd.setIdSubcategoria(null);else pd.setIdSubcategoria(is);}catch(SQLException e){pd.setIdSubcategoria(null);}

        try{pd.setNombreSubcategoria(rs.getString("NOMBRE_SUBCATEGORIA"));}catch(SQLException e){pd.setNombreSubcategoria(null);}
        try{long ic=rs.getLong("ID_CATEGORIA");if(rs.wasNull()) pd.setIdCategoria(null);else pd.setIdCategoria(ic);}catch(SQLException e){pd.setIdCategoria(null);}
        try{pd.setNombreCategoria(rs.getString("NOMBRE_CATEGORIA"));}catch(SQLException e){pd.setNombreCategoria(null); }

        try{pd.setMontoMensual(rs.getBigDecimal("MONTO_MENSUAL"));}catch(SQLException e) { pd.setMontoMensual(null);}
        try{pd.setJustificacion(rs.getString("JUSTIFICACION_DEL_MONTO"));}catch(SQLException e) { pd.setJustificacion(null);}

        try{pd.setCreadoEn(rs.getTimestamp("CREADO_EN")); }catch(SQLException e){pd.setCreadoEn(null);}
        try{pd.setModificadoEn(rs.getTimestamp("MODIFICADO_EN"));} catch(SQLException e){pd.setModificadoEn(null);}
        try{pd.setCreadoPor(rs.getString("CREADO_POR")); }catch(SQLException e){pd.setCreadoPor(null);}
        try{pd.setModificadoPor(rs.getString("MODIFICADO_POR"));}catch(SQLException e){pd.setModificadoPor(null);}

        return pd;
    }
    public void InsertarObligacion(long idUsuario,long idSubcategoria,String nombre,String descripcion,java.math.BigDecimal monto,int diaVencimiento,java.sql.Date fechaInicio,java.sql.Date fechaFin,String creadoPor) throws SQLException 
    {
        
       String SQL = "{ CALL SP_INSERTAR_OBLIGACION(?, ?, ?, ?, ?, ?, ?, ?, ?) }";
    try(Connection con = ConexionBD.getConnection();CallableStatement cs = con.prepareCall(SQL)) 
    {

        cs.setLong(1, idUsuario);
        cs.setLong(2, idSubcategoria);
        cs.setString(3, nombre);
        cs.setString(4, descripcion);
        if (monto == null) cs.setNull(5, java.sql.Types.DECIMAL); else cs.setBigDecimal(5, monto);
        cs.setInt(6, diaVencimiento);
        if (fechaInicio == null) cs.setNull(7, java.sql.Types.DATE); else cs.setDate(7, fechaInicio);
        if (fechaFin == null) cs.setNull(8, java.sql.Types.DATE); else cs.setDate(8, fechaFin);
        if (creadoPor == null) cs.setNull(9, java.sql.Types.VARCHAR); else cs.setString(9, creadoPor);

        cs.execute();
    } catch (SQLException ex) {
        if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (insertar obligacion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public void ActualizarObligacion(long idObligacion,String nombre,String descripcion,java.math.BigDecimal monto,int diaVencimiento,java.sql.Date fechaFin,Boolean activo,String modificadoPor) throws SQLException
    {
        
        String SQL="{ CALL SP_ACTUALIZAR_OBLIGACION(?, ?, ?, ?, ?, ?, ?, ?) }";
        try(Connection con = ConexionBD.getConnection(); CallableStatement cs = con.prepareCall(SQL)) 
        {

            cs.setLong(1, idObligacion);
            cs.setString(2, nombre);
            cs.setString(3, descripcion);
            if(monto == null) cs.setNull(4, java.sql.Types.DECIMAL); else cs.setBigDecimal(4, monto);
            cs.setInt(5, diaVencimiento);
            if(fechaFin == null) cs.setNull(6, java.sql.Types.DATE); else cs.setDate(6, fechaFin);
            if(activo == null) cs.setNull(7, java.sql.Types.BOOLEAN); else cs.setBoolean(7, activo);
            if(modificadoPor == null) cs.setNull(8, java.sql.Types.VARCHAR); else cs.setString(8, modificadoPor);

            cs.execute();
        } catch (SQLException ex) {
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (actualizar obligacion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public boolean EliminarObligacion(long idObligacion) throws SQLException 
    {
        
        String SQL="{ CALL SP_ELIMINAR_OBLIGACION(?) }";
        try(Connection con=ConexionBD.getConnection();CallableStatement cs = con.prepareCall(SQL)) 
        {

            cs.setLong(1, idObligacion);
            cs.execute();
            return true;
        } catch (SQLException ex) {
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (eliminar obligacion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public ObligacionFija ConsultarObligacion(long idObligacion) throws SQLException 
    {
        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_OBLIGACION(?))";
        try(Connection con = ConexionBD.getConnection();PreparedStatement ps = con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idObligacion);
            try(ResultSet rs=ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    return mapearObligacionFija(rs);
                }else return null;
            }
        } catch (SQLException ex) {
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (consultar obligacion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public List<ObligacionFija>ListarObligacionesUsuario(long idUsuario, Boolean activo) throws SQLException 
    {
        List<ObligacionFija>lista=new ArrayList<>();
        String SQL="SELECT * FROM TABLE(SP_LISTAR_OBLIGACIONES_USUARIO(?, ?))";
        try(Connection con = ConexionBD.getConnection();PreparedStatement ps = con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idUsuario);
            if(activo==null) ps.setNull(2, java.sql.Types.BOOLEAN); else ps.setBoolean(2, activo);

            try(ResultSet rs=ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    lista.add(mapearObligacionFija(rs));
                }
            }
        }
        return lista;
    }
    private ObligacionFija mapearObligacionFija(ResultSet rs) throws SQLException
    {
        ObligacionFija o=new ObligacionFija();
        try{long id=rs.getLong("ID_OBLIGACION_FIJA"); if (rs.wasNull()) o.setId(null); else o.setId(id); } catch (SQLException e) { o.setId(null); }
        try{long uid= rs.getLong("ID_USUARIO"); if (rs.wasNull()) o.setIdUsuario(null); else o.setIdUsuario(uid); } catch (SQLException e) { o.setIdUsuario(null); }
        try{long sid= rs.getLong("ID_SUBCATEGORIA"); if (rs.wasNull()) o.setIdSubcategoria(null); else o.setIdSubcategoria(sid); } catch (SQLException e) { o.setIdSubcategoria(null); }

        try{o.setNombre(rs.getString("NOMBRE")); } catch (SQLException e) { o.setNombre(null); }
        try{o.setDescripcion(rs.getString("DESCRIPCION_DETALLADA")); } catch (SQLException e) { o.setDescripcion(null); }
        try{o.setMontoMensual(rs.getBigDecimal("MONTO_FIJO_MENSUAL")); } catch (SQLException e) { o.setMontoMensual(null); }
        try{int dv=rs.getInt("DIA_DEL_MES_DE_VENCIMIENTO"); if (rs.wasNull()) o.setDiaVencimiento(null); else o.setDiaVencimiento(dv); } catch (SQLException e) { o.setDiaVencimiento(null); }
        try{boolean ev=rs.getBoolean("ESTA_VIGENTE"); if (rs.wasNull()) o.setEstaVigente(null); else o.setEstaVigente(ev); } catch (SQLException e) { o.setEstaVigente(null); }
        try{o.setFechaInicio(rs.getDate("FECHA_INICIO_DE_LA_OBLIGACION")); } catch (SQLException e) { o.setFechaInicio(null); }
        try{o.setFechaFin(rs.getDate("FECHA_DE_FINALIZACION")); } catch (SQLException e) { o.setFechaFin(null); }

        try{o.setNombreSubcategoria(rs.getString("NOMBRE_SUBCATEGORIA")); } catch (SQLException e) { o.setNombreSubcategoria(null); }
        try{long cid=rs.getLong("ID_CATEGORIA"); if (rs.wasNull()) o.setIdCategoria(null); else o.setIdCategoria(cid); } catch (SQLException e) { o.setIdCategoria(null); }
        try{o.setNombreCategoria(rs.getString("NOMBRE_CATEGORIA")); } catch (SQLException e) { o.setNombreCategoria(null); }

        try{o.setCreadoEn(rs.getTimestamp("CREADO_EN")); } catch (SQLException e) { o.setCreadoEn(null); }
        try{o.setModificadoEn(rs.getTimestamp("MODIFICADO_EN")); } catch (SQLException e) { o.setModificadoEn(null); }
        try{o.setCreadoPor(rs.getString("CREADO_POR")); } catch (SQLException e) { o.setCreadoPor(null); }
        try{o.setModificadoPor(rs.getString("MODIFICADO_POR")); } catch (SQLException e) { o.setModificadoPor(null); }

        return o;
    }
    //                                                                                  puede ser null
    public void InsertarTransaccion(long idUsuario,long idPresupuesto,int anio,int mes,long idSubcategoria,Long idObligacion,String tipo,String descripcion,BigDecimal monto,java.sql.Date fecha,String metodoPago,String creadoPor)throws SQLException 
    {
        
        String SQL="{ CALL SP_INSERTAR_TRANSACCION(?,?,?,?,?,?,?,?,?,?,?,?) }";
        
        try(Connection con = ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {
            
            cs.setLong(1, idUsuario);
            cs.setLong(2, idPresupuesto);
            cs.setInt(3, anio);
            cs.setInt(4, mes);
            cs.setLong(5, idSubcategoria);

            if(idObligacion == null) cs.setNull(6, Types.BIGINT);
            else cs.setLong(6, idObligacion);

            cs.setString(7, tipo);
            if(descripcion == null) cs.setNull(8, Types.VARCHAR);
            else cs.setString(8, descripcion);

            cs.setBigDecimal(9, monto);
            cs.setDate(10, fecha);
            cs.setString(11, metodoPago);
            if(creadoPor == null) cs.setNull(12, Types.VARCHAR);
            else cs.setString(12, creadoPor);

            cs.execute();
            
        }catch (SQLException ex){
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (insert transaccion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
        
    }
    public void ActualizarTransaccion(long idTransaccion,int anio,int mes,String descripcion,BigDecimal monto,java.sql.Date fecha,String metodoPago,String modificadoPor) throws SQLException 
    {
        
        String SQL="{ CALL SP_ACTUALIZAR_TRANSACCION(?,?,?,?,?,?,?,?) }";
        
        try(Connection con = ConexionBD.getConnection();CallableStatement cs=con.prepareCall(SQL)) 
        {
            
            cs.setLong(1, idTransaccion);
            cs.setInt(2, anio);
            cs.setInt(3, mes);
            if(descripcion==null) cs.setNull(4, Types.VARCHAR);
            else cs.setString(4, descripcion);

            cs.setBigDecimal(5, monto);
            cs.setDate(6, fecha);
            cs.setString(7, metodoPago);

            if(modificadoPor==null) cs.setNull(8, Types.VARCHAR);
            else cs.setString(8, modificadoPor);

            cs.execute();
            
        }catch (SQLException ex){
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (actualizar transaccion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
        
    }
    public boolean EliminarTransaccion(long idTransaccion) throws SQLException 
    {
        
        String SQL="{ CALL SP_ELIMINAR_TRANSACCION(?) }";
        try(Connection con = ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {
            
            cs.setLong(1, idTransaccion);
            cs.execute();
            return true;
            
        }catch (SQLException ex){
            
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (eliminar transaccion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public Transaccion ConsultarTransaccion(long idTransaccion) throws SQLException 
    {
        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_TRANSACCION(?))";
        try(Connection con=ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(SQL)) 
        {
            ps.setLong(1, idTransaccion);
            try(ResultSet rs=ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    
                    return mapearTransaccion(rs);
                    
                }else{
                    
                    return null;
                }
            }
        }catch (SQLException ex){
            
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (consult transaccion): " + ex.getMessage());
                throw ex;
            }
            throw ex;
            
        }
    }
    public List<Transaccion> ListarTransaccionesPresupuesto(long idPresupuesto, Integer anio, Integer mes, String tipo) throws SQLException 
    {
        List<Transaccion>lista=new ArrayList<>();
        String SQL="SELECT * FROM TABLE(SP_LISTAR_TRANSACCIONES_PRESUPUESTO(?, ?, ?, ?))";
        try(Connection con =ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(SQL)) 
        {
            ps.setLong(1,idPresupuesto);
            if(anio ==null) 
            {
                ps.setNull(2, java.sql.Types.SMALLINT);
            }else{
                ps.setInt(2, anio);
            }
            if(mes ==null) 
            {
                ps.setNull(3, java.sql.Types.SMALLINT);
            }else{
                ps.setInt(3, mes);
            }
            if(tipo ==null) 
            {
                ps.setNull(4, java.sql.Types.VARCHAR);
            }else{
                ps.setString(4, tipo);
            }

            try(ResultSet rs =ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    lista.add(mapearTransaccion(rs));
                }
            }
        }
        return lista;
    }
    private Transaccion mapearTransaccion(ResultSet rs) throws SQLException
    {
        Transaccion t =new Transaccion();
        try 
        {
            t.setId(rs.getLong("ID_TRANSACCION"));
        }catch (SQLException e) {
            
            t.setId(0L);
        }
        try 
        {
            t.setIdUsuario(rs.getLong("ID_USUARIO"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setIdPresupuesto(rs.getLong("ID_PRESUPUESTO"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setAnio(rs.getShort("ANIO"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setMes(rs.getShort("MES"));
        } catch (SQLException e) {
        }
        try
        {
            t.setIdSubcategoria(rs.getLong("ID_SUBCATEGORIA"));
        } catch (SQLException e) {
        }
        
        try 
        {
            long tmpOb =rs.getLong("ID_OBLIGACION_FIJA");
            if(rs.wasNull()) 
            {
                t.setIdObligacionFija(null);
            }else {
                t.setIdObligacionFija(tmpOb);
            }
        } catch (SQLException e) {
            t.setIdObligacionFija(null);
        }
        try
        {
            t.setTipo(rs.getString("TIPO_DE_TRANSACCION"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setDescripcion(rs.getString("DESCRIPCION"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setMonto(rs.getBigDecimal("MONTO"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setFecha(rs.getDate("FECHA"));
        } catch (SQLException e) {
        }
        try
        {
            t.setMetodoPago(rs.getString("METODO_DE_PAGO"));
        } catch (SQLException e) {
        }
        try {
            t.setFechaHoraRegistro(rs.getTimestamp("FECHA_HORA_DE_REGISTRO"));
        } catch (SQLException e) {
        }
        try
        {
            t.setCreadoEn(rs.getTimestamp("CREADO_EN"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setModificadoEn(rs.getTimestamp("MODIFICADO_EN"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setCreadoPor(rs.getString("CREADO_POR"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setModificadoPor(rs.getString("MODIFICADO_POR"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setNombreSubcategoria(rs.getString("NOMBRE_SUBCATEGORIA"));
        } catch (SQLException e) {
        }
        try 
        {
            t.setIdCategoria(rs.getLong("ID_CATEGORIA"));
            if (rs.wasNull()) {
                t.setIdCategoria(null);
            }
        } catch (SQLException e) {
        }
        try 
        {
            t.setNombreCategoria(rs.getString("NOMBRE_CATEGORIA"));
        } catch (SQLException e) {
        }
        return t;
    }
    public void InsertarMetaAhorro(long idUsuario,long idSubcategoria,String nombre,String descripcion,BigDecimal montoObjetivo,java.sql.Date fechaInicio,java.sql.Date fechaObjetivo,String prioridad,String creadoPor) throws SQLException 
    {

    String SQL = "{ CALL SP_INSERTAR_META(?,?,?,?,?,?,?,?,?) }";

        try(Connection con = ConexionBD.getConnection(); CallableStatement cs = con.prepareCall(SQL)) 
        {

            cs.setLong(1, idUsuario);
            cs.setLong(2, idSubcategoria);
            cs.setString(3, nombre);

            if(descripcion ==null) 
            {
                cs.setNull(4, Types.VARCHAR);
            }else{
                cs.setString(4, descripcion);
            }

            cs.setBigDecimal(5, montoObjetivo);
            cs.setDate(6, fechaInicio);
            cs.setDate(7, fechaObjetivo);

            if(prioridad ==null)
            {
                cs.setNull(8, Types.VARCHAR);
            }else{
                cs.setString(8, prioridad);
            }

            if(creadoPor ==null) 
            {
                cs.setNull(9, Types.VARCHAR);
            }else{
                cs.setString(9, creadoPor);
            }

            cs.execute();
        }catch (SQLException ex)
        {
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (insert meta): " + ex.getMessage());
            }
            throw ex;
        }
    }
    public void ActualizarMetaAhorro(long idMeta,String nombre,String descripcion,BigDecimal montoObjetivo,java.sql.Date fechaObjetivo,String prioridad,String estado,String modificadoPor) throws SQLException 
    {

        String SQL="{ CALL SP_ACTUALIZAR_META(?,?,?,?,?,?,?,?) }";

        try(Connection con = ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idMeta);

            if(nombre ==null) 
            {
                cs.setNull(2, Types.VARCHAR);
            }else{
                cs.setString(2, nombre);
            }

            if(descripcion== null) 
            {
                cs.setNull(3, Types.VARCHAR);
            }else{
                cs.setString(3, descripcion);
            }

            if(montoObjetivo ==null) 
            {
                cs.setNull(4, Types.DECIMAL);
            }else{
                cs.setBigDecimal(4, montoObjetivo);
            }

            if(fechaObjetivo==null)
            {
                cs.setNull(5, Types.DATE);
            }else{
                cs.setDate(5, fechaObjetivo);
            }

            if(prioridad ==null) 
            {
                cs.setNull(6, Types.VARCHAR);
            }else{
                cs.setString(6, prioridad);
            }

            if(estado ==null)
            {
                cs.setNull(7, Types.VARCHAR);
            }else{
                cs.setString(7, estado);
            }

            if(modificadoPor== null) 
            {
                cs.setNull(8, Types.VARCHAR);
            }else{
                cs.setString(8, modificadoPor);
            }

            cs.execute();
        }catch (SQLException ex){
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (actualizar meta): " + ex.getMessage());
            }
            throw ex;
        }
    }
    public boolean EliminarMetaAhorro(long idMeta) throws SQLException 
    {

        String SQL="{ CALL SP_ELIMINAR_META(?) }";

        try(Connection con = ConexionBD.getConnection(); CallableStatement cs=con.prepareCall(SQL)) 
        {

            cs.setLong(1, idMeta);
            cs.execute();
            return true;

        }catch (SQLException ex){
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (eliminar meta): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public MetaAhorro ConsultarMetaAhorro(long idMeta) throws SQLException
    {

        String SQL="SELECT * FROM TABLE(SP_CONSULTAR_META(?))";

        try(Connection con= ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idMeta);

            try(ResultSet rs=ps.executeQuery()) 
            {
                if(rs.next()) 
                {
                    return mapearMetaAhorro(rs);
                }else{
                    return null;
                }
            }

        }catch (SQLException ex){
            if ("45000".equals(ex.getSQLState())) {
                System.err.println("BD controlada (consultar meta): " + ex.getMessage());
                throw ex;
            }
            throw ex;
        }
    }
    public List<MetaAhorro> ListarMetasAhorroUsuario(long idUsuario, String estado) throws SQLException 
    {

        List<MetaAhorro>lista=new ArrayList<>();
        String SQL="SELECT * FROM TABLE(SP_LISTAR_METAS_USUARIO(?,?))";

        try(Connection con= ConexionBD.getConnection(); PreparedStatement ps=con.prepareStatement(SQL)) 
        {

            ps.setLong(1, idUsuario);
            if(estado==null) 
            {
                ps.setNull(2, Types.VARCHAR);
            }else{
                ps.setString(2, estado);
            }

            try(ResultSet rs=ps.executeQuery()) 
            {
                while(rs.next()) 
                {
                    lista.add(mapearMetaAhorro(rs));
                }
            }
        }
        return lista;
    }
    private MetaAhorro mapearMetaAhorro(ResultSet rs) throws SQLException 
    {
        MetaAhorro m = new MetaAhorro();

        try 
        {
            long id = rs.getLong("ID_AHORRO");
            if(rs.wasNull()) 
            {
                m.setId(null);
            }else {
                m.setId(id);
            }
        }catch (SQLException e) 
        {
            m.setId(null);
        }

        try 
        {
            m.setIdUsuario(rs.getLong("ID_USUARIO"));
        }catch (SQLException e){
        }
        try 
        {
            m.setIdSubcategoria(rs.getLong("ID_SUBCATEGORIA"));
        }catch (SQLException e) 
        {
        }
        try 
        {
            m.setNombre(rs.getString("NOMBRE"));
        } catch (SQLException e) 
        {
        }
        try 
        {
            m.setDescripcion(rs.getString("DESCRIPCION_DETALLADA"));
        } catch (SQLException e) {
        }

        try 
        {
            m.setMontoObjetivo(rs.getBigDecimal("MONTO_TOTAL_ALCANZAR"));
        } catch (SQLException e) {
        }
        try 
        {
            m.setMontoAhorrado(rs.getBigDecimal("MONTO_AHORRADO"));
        } catch (SQLException e) {
        }

        try 
        {
            m.setPorcentajeAvance(rs.getBigDecimal("PORCENTAJE_AVANCE"));
        } catch (SQLException e) 
        {
            m.setPorcentajeAvance(null);
        }

        try 
        {
            m.setFechaInicio(rs.getDate("FECHA_INICIO"));
        } catch (SQLException e) {
        }
        try
        {
            m.setFechaObjetivo(rs.getDate("FECHA_OBJETIVO"));
        } catch (SQLException e) {
        }
        try 
        {
            m.setPrioridad(rs.getString("PRIORIDAD"));
        } catch (SQLException e) {
        }
        try 
        {
            m.setEstado(rs.getString("ESTADO"));
        } catch (SQLException e) {
        }

        try 
        {
            m.setCreadoEn(rs.getTimestamp("CREADO_EN"));
        } catch (SQLException e) {
        }
        try
        {
            m.setModificadoEn(rs.getTimestamp("MODIFICADO_EN"));
        } catch (SQLException e) {
        }
        try
        {
            m.setCreadoPor(rs.getString("CREADO_POR"));
        } catch (SQLException e) {
        }
        try 
        {
            m.setModificadoPor(rs.getString("MODIFICADO_POR"));
        } catch (SQLException e) {
        }

        return m;
    }

    
    public BigDecimal fnCalcularMontoEjecutado(long idSubcategoria, int anio, int mes) throws SQLException {

    // IMPORTANTE: usamos VALUES en lugar de SELECT
    String SQL = "VALUES ( fn_calcular_monto_ejecutado(?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idSubcategoria);
        ps.setInt(2, anio);
        ps.setInt(3, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                // Primera columna (C1)
                BigDecimal valor = rs.getBigDecimal(1);
                return (valor != null) ? valor : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        }

    } catch (SQLException ex) {
        if ("45000".equals(ex.getSQLState())) {
            System.err.println("BD controlada (fn_calcular_monto_ejecutado): " + ex.getMessage());
        }
        throw ex;
    }
}


    
   public BigDecimal fnCalcularPorcentajeEjecutado(long idSubcategoria, long idPresupuesto,
                                                int anio, int mes) throws SQLException {

    String SQL = "VALUES ( fn_calcular_porcentaje_ejecutado(?, ?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idSubcategoria);
        ps.setLong(2, idPresupuesto);
        ps.setInt(3, anio);
        ps.setInt(4, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}


    public BigDecimal fnObtenerBalanceSubcategoria(long idPresupuesto, long idSubcategoria,
                                               int anio, int mes) throws SQLException {

    String SQL = "VALUES ( fn_obtener_balance_subcategoria(?, ?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idPresupuesto);
        ps.setLong(2, idSubcategoria);
        ps.setInt(3, anio);
        ps.setInt(4, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}

   public BigDecimal fnObtenerTotalCategoriaMes(long idCategoria, long idPresupuesto,
                                             int anio, int mes) throws SQLException {

    String SQL = "VALUES ( fn_obtener_total_categoria_mes(?, ?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idCategoria);
        ps.setLong(2, idPresupuesto);
        ps.setInt(3, anio);
        ps.setInt(4, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}

  public BigDecimal fnObtenerTotalEjecutadoCategoriaMes(long idCategoria,
                                                      int anio, int mes) throws SQLException {

    String SQL = "VALUES ( fn_obtener_total_ejecutado_categoria_mes(?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idCategoria);
        ps.setInt(2, anio);
        ps.setInt(3, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}

    
    public Integer fnDiasHastaVencimiento(long idObligacion) throws SQLException {

    String SQL = "VALUES ( fn_dias_hasta_vencimiento(?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idObligacion);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return null;
        }
    }
}

  public boolean fnValidarVigenciaPresupuesto(java.sql.Date fecha, long idPresupuesto) throws SQLException {

    String SQL = "VALUES ( fn_validar_vigencia_presupuesto(?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setDate(1, fecha);
        ps.setLong(2, idPresupuesto);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;
        }
    }
}


   public Long fnObtenerCategoriaPorSubcategoria(long idSubcategoria) throws SQLException {

    String SQL = "VALUES ( fn_obtener_categoria_por_subcategoria(?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idSubcategoria);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return null;
        }
    }
}

   public BigDecimal fnCalcularProyeccionGastoMensual(long idSubcategoria,
                                                   int anio, int mes) throws SQLException {

    String SQL = "VALUES ( fn_calcular_proyeccion_gasto_mensual(?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idSubcategoria);
        ps.setInt(2, anio);
        ps.setInt(3, mes);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}

    public BigDecimal fnObtenerPromedioGastoSubcategoria(long idUsuario, long idSubcategoria,
                                                     int cantidadMeses) throws SQLException {

    String SQL = "VALUES ( fn_obtener_promedio_gasto_subcategoria(?, ?, ?) )";

    try (Connection con = ConexionBD.getConnection();
         PreparedStatement ps = con.prepareStatement(SQL)) {

        ps.setLong(1, idUsuario);
        ps.setLong(2, idSubcategoria);
        ps.setInt(3, cantidadMeses);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }
}

    
    
    

}