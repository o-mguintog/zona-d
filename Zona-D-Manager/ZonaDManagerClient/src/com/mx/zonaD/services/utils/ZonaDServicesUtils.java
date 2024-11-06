package com.mx.zonaD.services.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import examples.Config;

/**
 * La clase <code>ZonaDServicesUtils</code> es utilizada como
 * utiler&iacute;a para generar el archivo json.
 *
 * @author Manuel Guinto.
 */
public class ZonaDServicesUtils {
    
    /**
     * Constructor default
     */
    public ZonaDServicesUtils() {
        super();
    }

    /**
     * Genera el archivo json.
     * 
     * @param json Json a guardar.
     * @throws Exception Exception generada.
     */
    public static void  generateJsonFile(String json) throws Exception {
        
        //Verifica que el json tenga valor.
        if(json == null){
            
            throw new Exception ("No se ha ingresado información del archivo");
        }
        
        File file = new File(Config.JSON_REPORT_PATH);
        
        //Verifica si ya existe el archivo
        if (file != null && file.exists()){
            file.delete();         
        }
        
        //Declara los recursos
        FileWriter fw=null;;
        BufferedWriter bw=null;
        
        try{
            //Crea el archivo
            fw=new FileWriter(file);
            bw=new BufferedWriter(fw);
            bw.write(json);
            bw.flush();    
            
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            bw.close();
            fw.close();
        }
    }
}
