package com.mx.zonaD.services;

import com.google.gson.Gson;

import com.mx.zonaD.model.services.ZonaDModelService;
import com.mx.zonaD.services.utils.ZonaDServicesUtils;
import com.mx.zonaD.types.FichasResponseType;
import com.mx.zonaD.types.FichasType;
import com.mx.zonaD.types.UserType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase <code>ZonaDServicesCreateFichas</code> se utiliza para la creaci&oacute;n de
 * fichas.
 *
 * @author Manuel Guinto.
 */
public class ZonaDServicesCreateFichas {

    /**
     * Contructor por defecto.
     */
    public ZonaDServicesCreateFichas() {
        super();
    }

    /**
     * Lee el archivo json para obtener las plantillas a generar.
     * @return Plantillas.
     */
    @SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public static FichasResponseType getFichasToCreate(){
        
        File doc = new File("createFichas.json");
        FichasResponseType response = null;
        BufferedReader obj;
        
        //Verfica que exista el archivo json
        if (doc != null && doc.exists()){
            
            try {
                obj = new BufferedReader(new FileReader(doc));
                
                String json = "";
                StringBuffer buffer = new StringBuffer();
                while ((json = obj.readLine()) != null){
                    buffer.append(json);                
                }                       
                
                Gson gson = new Gson();                        
                response = gson.fromJson(buffer.toString(), FichasResponseType.class);
                
                if(response != null && response.getFichasProfile().size() >0){
                    for (FichasType current :  response.getFichasProfile()){
                        System.out.println("Package Name: "+current.getPackageName());
                    }
                }
                
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }else{
            System.out.println("No se encontró el archivo json con las plantillas a generar");
        }
        return response;
         
    }

    /**
     * Clase main para el alta de fichas.
     * @param args Argumentos de la clase.
     */
    @SuppressWarnings("oracle.jdeveloper.java.nested-assignment")
    public static void main(String[] args) {
        
        FichasResponseType fichas = null;
        fichas = getFichasToCreate();
        
        //Verifica si 
        if (fichas != null && 
            fichas.getFichasProfile() != null && 
            fichas.getFichasProfile().size()>0){
            
            
            //Inicializa la lista de usuarios
            ZonaDServices services = new ZonaDServices();        
            
            //Inicializa el grupo de usuarios
            List<UserType> userGroups = new ArrayList<UserType>();
            
            //Itera sobre las plantillas de fichas a crear
            for(FichasType currentFicha : fichas.getFichasProfile()){
                
                UserType userGroup = null;
                userGroup = services.createUsers9Template(currentFicha.getPrefijo(),
                                                        currentFicha.getPackageName(),
                                                        currentFicha.getVendor());
                userGroup.setTitle(currentFicha.getTitle());
                userGroups.add(userGroup);  
                
            }
            
            //Verifica que haya fichas por crear.
            if (userGroups != null && userGroups.size()>0 ){
                
                Gson gson = new Gson();
                String jsonTemplate = gson.toJson(userGroups);
                
                try {
                    //Genera las fichas en la BD
                    ZonaDModelService.registerFichas(jsonTemplate);
                    
                    //Guarda el archivo json
                    ZonaDServicesUtils.generateJsonFile(jsonTemplate);
                } catch (Exception e) {
                    e.printStackTrace();
                }    
            }
            
        }
    }
}
