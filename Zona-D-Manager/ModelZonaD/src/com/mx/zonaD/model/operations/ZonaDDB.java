package com.mx.zonaD.model.operations;

import java.sql.Connection;
import static com.mx.zonaD.model.constants.ZonaDConstantsDB.JDBC_URL;
import static com.mx.zonaD.model.constants.ZonaDConstantsDB.USERNAME;
import static com.mx.zonaD.model.constants.ZonaDConstantsDB.PASSWORD;

import static com.mx.zonaD.model.queries.ZonaDQueries.GET_FICHAS_NEW;
import static com.mx.zonaD.model.queries.ZonaDQueries.GET_FICHAS_ACTIVE;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.google.gson.Gson;

import com.mx.zonaD.model.services.response.FichasResponse;

import java.io.BufferedReader;
import java.io.IOException;

import java.sql.CallableStatement;
import java.sql.Clob;

/**
 * La clase <code>ZonaDDB</code> hace la conexi&oacute;n a la BD de Zona D.
 *
 * @author Manuel Guinto.
 */
public class ZonaDDB {
    
    private static final String NO_DATA_FOUND="{\"responseFichas\":}";

    /**
     * Constructor por defecto.
     */
    public ZonaDDB() {
        super();
    }

    /**
     * Obtiene la conexi&oacute;n a Zona D.
     * @return Conection.
     */
    public static Connection getConnection(){
        
        //Conexión a la BD.
        Connection connection = null;        
                
        try {
            // Establecer la conexión
            Class.forName("oracle.jdbc.driver.OracleDriver"); 
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos");
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } 
        System.out.println("Conexión exitosa a la base de datos");
        return connection;
    }

    /**
     * Obtiene las fichas nuevas de Zona D.
     * 
     * @return Fichas nuevas en formato JSON.
     */
    public static FichasResponse getFichasNew(){
        
        Connection connection =null;
        Clob jsonFichas=null;
        String jsonString=null;
        FichasResponse response =null;
        
        //Obtiene la conexión a la BD-
        connection = getConnection();                
        
        //Verifica la conexión a BD
        if (connection != null){
            
            //Declara los recursos
            ResultSet resultSet = null;
            Statement statement = null;
            try{
                
                //Ejecuta la sentencia 
                statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery(GET_FICHAS_NEW);
                
                //Verifica si se obtuvieron resultados
                if(resultSet != null && resultSet.next()){
                    jsonFichas = resultSet.getClob(1);
                    
                    if (jsonFichas != null && !jsonFichas.equals(NO_DATA_FOUND)){
                        
                        jsonString = getClobString(jsonFichas);
                        
                        Gson gson = new Gson();                        
                        response = gson.fromJson(jsonString, FichasResponse.class);
                    }
                }                
                
            }catch(SQLException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try {
                    //Cierra el resultado
                    if (resultSet != null && !resultSet.isClosed()){
                        resultSet.close();    
                    }
                    
                    //Cierra la sentencia
                    if (statement != null && !statement.isClosed()){
                        statement.close();    
                    }
                                        
                    //Cierra la conexión
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    
                    //Nulifica los recursos
                    resultSet = null;
                    statement = null;
                    connection = null;
                    
                    e.printStackTrace();
                }
            }
        }
        
        return response;
    }

    /**
     * Convierte el tipo de dato clob a String.
     *
     * @param clob Tipo de dato Clob.
     * @return Cadena de tipo json.
     *
     * @throws SQLException Propaga excepci&oacute; de tipo sql.
     * @throws IOException Propaga la excepci&oacute;n de tipo io.
     */
    @SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public static String getClobString(Clob clob) throws SQLException, IOException {
            
            //Verifica que el clob tenga valor.
            if(clob == null){
                return null;
            }
            
            BufferedReader stringReader = new BufferedReader(clob.getCharacterStream());
            String singleLine = null;
            StringBuffer strBuff = new StringBuffer();
            
            //Itera sobre el clob.
            while ((singleLine = stringReader.readLine()) != null) {
                strBuff.append(singleLine);
            }
            
            //Regresa la cadena de tipo string.
            return strBuff.toString();
        }


    /**
     * Procesa las nuevas fichas.
     * @param fichasNew Fichas nuevas.
     */
    public static void processNewFichas(String fichasNew){
        
        //Obtiene la conexión a la BD-
        Connection connection =null;
        CallableStatement cs =null;
        connection = getConnection();                
        
        //Verifica la conexión a BD
        if (connection != null){
            
            try {
                cs = connection.prepareCall("{call ZONA_D_PROCESS_PKG.ACTIVE_NEW_FICHAS_PS(?)}");
                cs.setString(1, fichasNew);                
                cs.executeQuery();
                //connection.commit();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                try {
                    //Cierra el resultado
                    if (cs != null && !cs.isClosed()){
                        cs.close();    
                    }
                                  
                    //Cierra la conexión
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    
                    //Nulifica los recursos                    
                    connection = null;
                    
                    e.printStackTrace();
                }
            }
        
        }
    }
    
    /**
     * Procesa las nuevas fichas.
     * @param fichasNew Fichas nuevas.
     */
    public static void disabledFichas(String fichasNew){
        
        //Obtiene la conexión a la BD-
        Connection connection =null;
        CallableStatement cs =null;
        connection = getConnection();                
        
        //Verifica la conexión a BD
        if (connection != null){
            
            try {
                cs = connection.prepareCall("{call ZONA_D_PROCESS_PKG.DISABLED_FICHAS_PS(?)}");
                cs.setString(1, fichasNew);                
                cs.executeQuery();
                //connection.commit();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                try {
                    //Cierra el resultado
                    if (cs != null && !cs.isClosed()){
                        cs.close();    
                    }
                                  
                    //Cierra la conexión
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    
                    //Nulifica los recursos                    
                    connection = null;
                    
                    e.printStackTrace();
                }
            }
        
        }
    }
    
    /**
     * Obtiene las fichas nuevas de Zona D.
     * 
     * @return Fichas activas en formato JSON.
     */
    public static FichasResponse getFichasActive(){
        
        Connection connection =null;
        String jsonFichas=null;
        FichasResponse response =null;
        
        //Obtiene la conexión a la BD-
        connection = getConnection();                
        
        //Verifica la conexión a BD
        if (connection != null){
            
            //Declara los recursos
            ResultSet resultSet = null;
            Statement statement = null;
            try{
                
                //Ejecuta la sentencia 
                statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                resultSet = statement.executeQuery(GET_FICHAS_ACTIVE);
                
                //Verifica si se obtuvieron resultados
                if(resultSet != null && resultSet.next()){
                    jsonFichas = resultSet.getString(1);
                    
                    if (jsonFichas != null && !jsonFichas.equals(NO_DATA_FOUND)){
                        
                        Gson gson = new Gson();                        
                        response = gson.fromJson(jsonFichas, FichasResponse.class);
                    }
                }                
                
            }catch(SQLException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                try {
                    //Cierra el resultado
                    if (resultSet != null && !resultSet.isClosed()){
                        resultSet.close();    
                    }
                    
                    //Cierra la sentencia
                    if (statement != null && !statement.isClosed()){
                        statement.close();    
                    }
                                        
                    //Cierra la conexión
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    
                    //Nulifica los recursos
                    resultSet = null;
                    statement = null;
                    connection = null;
                    
                    e.printStackTrace();
                }
            }
        }
        
        return response;
    }
    
    /**
     * Registra las nuevas fichas.
     * @param fichasNew Fichas nuevas.
     */
    public static void registerFichas(String fichasNew){
        
        //Obtiene la conexión a la BD-
        Connection connection =null;
        CallableStatement cs =null;
        connection = getConnection();                
        
        //Verifica la conexión a BD
        if (connection != null){
            
            try {
                cs = connection.prepareCall("{call ZONA_D_PROCESS_PKG.REGISTER_FICHAS_PS(?)}");
                cs.setString(1, fichasNew);                
                cs.executeQuery();
                //connection.commit();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }finally{
                try {
                    //Cierra el resultado
                    if (cs != null && !cs.isClosed()){
                        cs.close();    
                    }
                                  
                    //Cierra la conexión
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    
                    //Nulifica los recursos                    
                    connection = null;
                    
                    e.printStackTrace();
                }
            }        
        }
    }
}
