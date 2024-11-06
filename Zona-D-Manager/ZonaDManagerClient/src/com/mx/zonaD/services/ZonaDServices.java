package com.mx.zonaD.services;

import com.mx.zonaD.services.utils.ZonaDServicesUtils;
import com.mx.zonaD.types.UserType;

import examples.AnonymousSocketFactory;
import examples.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.UUID;
import com.google.gson.Gson;

import me.legrange.mikrotik.*;

/**
 * La clase <code>ZonaDServices</code> es utilizada para gestionar usuarios de mikrotik.
 * 
 * @author Mikrotik
 * @author Manuel Guinto.
 */
public class ZonaDServices {

    /**
     * Conexi&aacute;n a mikrotik
     */
    private ApiConnection mkConnection;


    /**
     * Constructor por defecto.
     */
    public ZonaDServices() {
        super();
    }


    /**
     * Desconecta la conexi&oacute;n a Mikrotik.
     * 
     * @throws Exception Excepci&oacute;n generada.
     */
    public void disconnect() throws Exception {
        mkConnection.close();
    }


    /**
     * Conexi&oacute;n a mikrotik.
     * 
     * @throws Exception Excepci&oacute;n generada.
     */
    public void connect() throws Exception {        
        
        //Obtiene la conexión a mikrotik
        mkConnection = ApiConnection.connect(AnonymousSocketFactory.getDefault(), 
                                    Config.HOST, 
                                    ApiConnection.DEFAULT_TLS_PORT, 
                                    ApiConnection.DEFAULT_CONNECTION_TIMEOUT);
        //Se loguea a mikrotik
        mkConnection.login(Config.USERNAME, Config.PASSWORD);
    }

    /**
     * Crea un conjunto de usuarios aleatorios para el paquete ingresado
     *      
     * @param paquete Nombre del paquete.
     */
    public UserType createUsers9Template(String prefijo, String paquete){
        
        UserType userGroup = new UserType();        
        
        try {
            //Genera la conexión a mikrotik
            connect();
            
            userGroup = createUsers(prefijo,9,paquete);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            
            try {
                //Se desconecta del mikrotik
                disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userGroup;
    }
    
    /**
     * Genera un c&oacute;digo &uacute;nico.
     * @return C&oacute;digo generado.
     */
    public String generateUnicCode(){
        
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0,6);
    }

    /**
     * Construye los usuarios.
     * 
     * @param numUsuarios N&uacute;mero de usuarios a generar
     * @param paquete Nombre del paquete.     
     */
    public UserType createUsers(String prefijo, Integer numUsuarios, String paquete){
        
        UserType usersPackages = new UserType();
        //Verifica la conexión a mikrotik
        if (mkConnection != null){                        
                    
            //Construye los usuarios
            try {              
                //Itera sobre los usuarios a crear
                for(int numUser =1; numUser<= numUsuarios; numUser++){
                    //Obtiene la sentencia de creación de usuarios
                    String statement = getStatementCreateUser(prefijo,paquete,usersPackages,numUser);                    
                    
                    //Ejecuta la sentencia para la creación de usuarios
                    mkConnection.execute(statement);
                }
                
            } catch (MikrotikApiException e) {
                //Imprime el error
                e.printStackTrace();
            }
        }
        return usersPackages;
    }

    /**
     * Crea la sentencia de ejecuci&oacute;n de usuarios.
     *      
     * @param paquete Nombre del paquete.   
     * 
     * @return Sentencia a ejecutar.
     */
    private String getStatementCreateUser(String prefijo, String paquete,UserType usersPackages,Integer numUser){
        
        final String CREATE_USER="/ip/hotspot/user/add ";
        
        StringBuilder statement = new StringBuilder(CREATE_USER);
        final String SPACE =" ";
                
        //Separa la cadena por guiones medios                
        String user= null;
        String limit =null;
        
        //Obtiene el valor único
        String password = generateUnicCode();
        
        Map<String,String> packages = new HashMap<String,String>();
        packages.put("Zona-D-1Hr-Corrido", "01:00:00");
        packages.put("Zona-D-2Hr-Corrido", "02:00:00");
        packages.put("Zona-D-24Hr-Corrido", "24:00:00");
        packages.put("Zona-D-1Hr-Pausado", "01:00:00");
        packages.put("Zona-D-2Hr-Pausado", "02:00:00");
        packages.put("Zona-D-24Hr-Pausado", "24:00:00");
        packages.put("Zona-D-1Hr2-Corrido", "02:00:00");
        packages.put("Zona-D-2Hr2-Corrido", "02:00:00");
        packages.put("Zona-D-24Hr2-Corrido", "24:00:00");
        
        limit= packages.get(paquete);
        user= prefijo+"-"+password;        
        
        //Usuario
        statement.append("name=");
        statement.append(user);
        
        //Password:
        statement.append(SPACE);
        statement.append("password=");
        statement.append(password);
        
        //Password:
        statement.append(SPACE);
        statement.append("profile=");
        statement.append(paquete);
        
        //Server Note: Pasar a constantes el nombre del servidor
        statement.append(SPACE);
        statement.append("server=");
        statement.append("hotspot1");  
        
        statement.append(SPACE);
        statement.append("limit-uptime=");
        statement.append(limit);  
        
        //Inicializa el usuario en el objeto usuarios        
        initUserPackage(usersPackages,numUser,user,password);
        
        
        return statement.toString();
    }

    /**
     * Inicializa el usuario en el paquete.
     * 
     * @param userPackage grupo de usuarios de paquete.
     * @param numUser N&uacute;mero de usuario.
     * @return Grupo de usuarios inicializados.
     */
    private void initUserPackage(UserType userPackage, Integer numUser, String username, String password){
        
        switch(numUser){
            
            case 1:
                userPackage.setUsername1(username);
                userPackage.setPassword1(password);
                break;
        
            case 2:
                userPackage.setUsername2(username);
                userPackage.setPassword2(password);
                break;
        
            case 3:
                userPackage.setUsername3(username);
                userPackage.setPassword3(password);
                break;
        
            case 4:
                userPackage.setUsername4(username);
                userPackage.setPassword4(password);
                break;
        
            case 5:
                userPackage.setUsername5(username);
                userPackage.setPassword5(password);
                break;
        
            case 6:
                userPackage.setUsername6(username);
                userPackage.setPassword6(password);
                break;
            
            case 7:
                userPackage.setUsername7(username);
                userPackage.setPassword7(password);
                break;
        
            case 8:
                userPackage.setUsername8(username);
                userPackage.setPassword8(password);
                break;
            case 9:
                userPackage.setUsername9(username);
                userPackage.setPassword9(password);
                break;            
        }
        
    }

    /**
     * Cliente para ejecutar la funcionalidad de administraci&oacute;n de vouchers.
     * @param args Argumentos.
     */
    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        
        
        //Inicializa la lista de usuarios
        ZonaDServices services = new ZonaDServices();        
        
        //Inicializa el grupo de usuarios
        List<UserType> userGroups = new ArrayList<UserType>();
        
        //Genera grupo de fichas de 1 hr, equivalente a 24 fichas
        for(int ij=0;ij<2;ij++){

            //Crea el primer grupo de 12 usuarios por paquete
            UserType userGroup = null;
            userGroup = services.createUsers9Template("u24s","Zona-D-24Hr2-Corrido");
            userGroup.setTitle("1 Hr Corrido");
            userGroups.add(userGroup);        
            
        }
        //Note: falta transformar el objeto de grupo de usuarios a json
        Gson gson = new Gson();
        String jsonTemplate = gson.toJson(userGroups);
 
        
        try {
            //Guarda el archivo json
            ZonaDServicesUtils.generateJsonFile(jsonTemplate);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    
}
