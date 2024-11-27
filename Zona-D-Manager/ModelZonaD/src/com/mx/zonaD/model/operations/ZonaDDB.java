package com.mx.zonaD.model.operations;

import java.sql.Connection;
import static com.mx.zonaD.model.constants.ZonaDConstantsDB.JDBC_URL;
import static com.mx.zonaD.model.constants.ZonaDConstantsDB.USERNAME;
import static com.mx.zonaD.model.constants.ZonaDConstantsDB.PASSWORD;

import static com.mx.zonaD.model.queries.ZonaDQueries.GET_FICHAS_NEW;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.google.gson.Gson;

import com.mx.zonaD.model.services.response.FichasResponse;

/**
 * La clase <code>ZonaDDB</code> hace la conexi&oacute;n a la BD de Zona D.
 *
 * @author Manuel Guinto.
 */
public class ZonaDDB {

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
                resultSet = statement.executeQuery(GET_FICHAS_NEW);
                
                //Verifica si se obtuvieron resultados
                if(resultSet != null && resultSet.next()){
                    jsonFichas = resultSet.getString(1);
                    
                    if (jsonFichas != null){
                        
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
    
    public static void main(String[] args) {
        
       ZonaDDB.getFichasNew();
   }
}
