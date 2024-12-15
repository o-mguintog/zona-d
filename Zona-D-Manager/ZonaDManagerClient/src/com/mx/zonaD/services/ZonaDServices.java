package com.mx.zonaD.services;

import com.google.gson.Gson;

import com.mx.zonaD.model.services.ZonaDModelService;
import com.mx.zonaD.model.services.response.FichasResponse;
import com.mx.zonaD.model.types.FichasType;
import static com.mx.zonaD.services.comands.ZonaDMikrotikCommands.DISABLED_USER;
import static com.mx.zonaD.services.comands.ZonaDMikrotikCommands.GET_ACTIVE_USERS;
import static com.mx.zonaD.services.comands.ZonaDMikrotikCommands.GET_ENABLED_USERS;
import static com.mx.zonaD.services.comands.ZonaDMikrotikCommands.GET_USERS_MKT;
import static com.mx.zonaD.services.comands.ZonaDMikrotikCommands.ID_MKT;
import com.mx.zonaD.types.UserType;

import examples.Config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.legrange.mikrotik.ApiConnection;
import me.legrange.mikrotik.MikrotikApiException;

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
        if (mkConnection != null){
            mkConnection.close();    
        }
        
    }


    /**
     * Conexi&oacute;n a mikrotik.
     * 
     * @throws Exception Excepci&oacute;n generada.
     */
    public void connect() throws Exception {        
                
        mkConnection = ApiConnection.connect(Config.HOST);
        //Se loguea a mikrotik
        mkConnection.login(Config.USERNAME, Config.PASSWORD);
    }

    /**
     * Crea un conjunto de usuarios aleatorios para el paquete ingresado
     *      
     * @param paquete Nombre del paquete.
     */
    public UserType createUsers9Template(String prefijo, String paquete, String vendor, String promotion){
        
        UserType userGroup = new UserType();        
        
        try {
            //Genera la conexión a mikrotik
            connect();
            
            userGroup = createUsers(prefijo,12,paquete,vendor, promotion);
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
    public UserType createUsers(String prefijo, Integer numUsuarios, String paquete, String vendor, String promotion){
        
        UserType usersPackages = new UserType();
        //Verifica la conexión a mikrotik
        if (mkConnection != null){                        
                    
            //Construye los usuarios
            try {              
                //Itera sobre los usuarios a crear
                for(int numUser =1; numUser<= numUsuarios; numUser++){
                    //Obtiene la sentencia de creación de usuarios
                    String statement = getStatementCreateUser(prefijo,paquete,usersPackages,numUser,vendor,promotion);                    
                    
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
    private String getStatementCreateUser(String prefijo, String paquete,UserType usersPackages,Integer numUser, String vendor, String promotion){
        
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
        packages.put("Zona-D-Flash-Corrido", "00:30:00");
        packages.put("Zona-D-Mensual-Corrido", "30d");
        packages.put("Zona-D-Quincenal-Corrido", "15d");
        packages.put("Zona-D-Semanal-Corrido", "7d");
        
        
        limit= packages.get(paquete);
        user= prefijo.toLowerCase()+"-"+password;  
        password = "";
        
        //Usuario
        statement.append("name=");
        statement.append(user);
        
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
        initUserPackage(usersPackages,numUser,user,password, prefijo.toUpperCase(), vendor, promotion);
        
        
        return statement.toString();
    }

    /**
     * Inicializa el usuario en el paquete.
     * 
     * @param userPackage grupo de usuarios de paquete.
     * @param numUser N&uacute;mero de usuario.
     * @return Grupo de usuarios inicializados.
     */
    private void initUserPackage(UserType userPackage, Integer numUser, String username, String password,String profileCode, String vendor, String promotion){
        
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
            case 10:
                userPackage.setUsername10(username);
                userPackage.setPassword10(password);
                break;        
            case 11:
                userPackage.setUsername11(username);
                userPackage.setPassword11(password);
                break;        
            case 12:
                userPackage.setUsername12(username);
                userPackage.setPassword12(password);
                break;        
        }
        
        userPackage.setProfileCode(profileCode);
        userPackage.setVendor(vendor);  
        userPackage.setPromotion(promotion);
    }

    /**
     * Activa las nuevas fichas.
     */
    public void activeFichas(){
        
        try {
            //Genera la conexión a mikrotik
            connect();
            
            //Verifica la conexión a mikrotik
            if (mkConnection != null){                        
                
                try {              
                    //Obtiene los usuarios                                         
                    List<Map<String, String>>mktUsers = mkConnection.execute(GET_ENABLED_USERS);
                    
                    //Obtiene las fichas activas
                    List<Map<String, String>>mktUsersActives = mkConnection.execute(GET_ACTIVE_USERS);
                    
                    FichasResponse fichasBD=null;
                    fichasBD = ZonaDModelService.getFichasNew();                    
                    String newFichas = getNewFichasActived(mktUsers,fichasBD, mktUsersActives);
                    
                    //Verifica que existan fichas a procesar
                    if(newFichas != null){
                        ZonaDModelService.processNewFichas(newFichas);                        
                    }
                    
                } catch (MikrotikApiException e) {
                    //Imprime el error
                    e.printStackTrace();
                }
            }
            
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
    }

    /**
     * Verifica si las fichas est&aacute;n activas.
     * 
     * @param mktUsers Usuarios mikrotik
     * @param fichasBD Fichas nuevas de la BD.
     */
    public String getNewFichasActived(List<Map<String, String>>mktUsers, FichasResponse fichasBD, List<Map<String, String>>mktUsersActives ){
        
        final String NAME="name";
        final String ACTIVE ="ACTIVE";
        
        boolean isChanged = false;
        boolean isFichaActiva = false;
        List<FichasType> fichaBDList =null;
        String jsonUsers =null;
        
        //Verifica si hay fichas a evaluar
        if (fichasBD == null){
            System.out.println("No hay fichas nuevas");
            return null;
        }
        
        if(mktUsers != null){
                        
            fichaBDList = fichasBD.getResponseFichas();
            
            //Itera sobre los usuarios de BD.
            for (FichasType fichaBD: fichaBDList){
                
                
                //Itera sobre los usuarios mikrotik
                for( Map<String, String> currentUser : mktUsersActives){
                    
                    //Busca la ficha nueva de BD en la lista de fichas iniciadas Mikrotik
                    if(currentUser.get("user") != null && currentUser.get("user").equals(fichaBD.getFicha())){
                        
                        fichaBD.setStatus(ACTIVE);
                        isChanged = true;
                        isFichaActiva = true;
                        break;
                    }                    
                }
                
                //Si no encontró activa la ficha la busca en los usuarios
                if (!isFichaActiva){
                    //Itera sobre los usuarios mikrotik
                    for( Map<String, String> currentUser : mktUsers){
                        
                        //Busca la ficha nueva de BD en la lista de fichas iniciadas Mikrotik
                        if(currentUser.get(NAME) != null && currentUser.get(NAME).equals(fichaBD.getFicha())){
                            
                            fichaBD.setStatus(ACTIVE);
                            isChanged = true;                            
                            break;
                        }                    
                    }
                }
                
                isFichaActiva = false;                    
            }                        
        }
        
        //Verifica si hubo cambios
        if(isChanged){
            //Convert List to JSON
            Gson gson = new Gson();
            jsonUsers = gson.toJson(fichasBD);    
        }
        
        return jsonUsers;        
    }
    
    /**
     * Desactiva fichas.
     */
    public void disabledFichas(){                
        
        boolean isChanged = false;
        String jsonUsers = null;
        final String FINISHED="FINISHED";
        try {
            //Genera la conexión a mikrotik
            connect();
            
            //Verifica la conexión a mikrotik
            if (mkConnection != null){                        
                
                try {              
                    //Obtiene los usuarios activos                                        
                    FichasResponse fichasBD=null;
                    fichasBD = ZonaDModelService.getFichasActive();                    
                   
                   //Verifica si hay fichas para desactivar
                    if(fichasBD != null && fichasBD.getResponseFichas() != null && !fichasBD.getResponseFichas().isEmpty()){
                        
                        
                        List<FichasType> fichasToDisabled = fichasBD.getResponseFichas();
                        //Itera las fichas de BD a deshabilitar
                        for(FichasType currentFicha : fichasToDisabled){
                            
                            //Busca el ID del usuario Mikrotik                            
                            List<Map<String, String>> res = mkConnection.execute(GET_USERS_MKT+currentFicha.getFicha());
                            //Itera sobre los atributos para deshabilitarlo
                            for (Map<String, String> attr : res) {
                                String id = attr.get(ID_MKT);
                                mkConnection.execute(DISABLED_USER +ID_MKT + " =" + id);
                                isChanged = true;
                                currentFicha.setStatus(FINISHED);
                                break;
                            }
                            
                        }
                    }
                    
                    //Verifica si hubo cambios
                    if(isChanged){
                        //Convert List to JSON
                        Gson gson = new Gson();
                        jsonUsers = gson.toJson(fichasBD);   
                        ZonaDModelService.disabledFichas(jsonUsers); 
                    }
                    
                } catch (MikrotikApiException e) {
                    //Imprime el error
                    e.printStackTrace();
                }
            }
            
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
    }
}
