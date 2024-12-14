package com.mx.zonaD.model.services;

import com.mx.zonaD.model.operations.ZonaDDB;
import com.mx.zonaD.model.services.response.FichasResponse;

/**
 * La clase <code>ZonaDModelService</code> es utilizada para interactuar
 * con la BD de Zona D.
 *
 * @author Manuel Guinto.
 */
public class ZonaDModelService {
    
    /**
     * Constructor por defecto.
     */
    public ZonaDModelService() {
        super();
    }

    /**
     * Obtiene las fichas nuevas que no han sido activadas en la BD.
     * 
     * @return Fichas nuevas.
     */
    public static FichasResponse getFichasNew(){
        
        FichasResponse fichasBD=null;
        try{
            fichasBD = ZonaDDB.getFichasNew();
        }catch(Exception e){
            e.printStackTrace();
        }
        return fichasBD;        
    }
    
    /**
     * Obtiene las fichas activas que no han sido activadas en la BD.
     * 
     * @return Fichas nuevas.
     */
    public static FichasResponse getFichasActive(){
        
        FichasResponse fichasBD=null;
        try{
            fichasBD = ZonaDDB.getFichasActive();
        }catch(Exception e){
            e.printStackTrace();
        }
        return fichasBD;        
    }

    /**
     * Deshabilita las fichas.
     * @param fichasNew Fichas a deshabilitar
     */
    public static void disabledFichas(String fichasNew){
                
        try{
            ZonaDDB.disabledFichas(fichasNew);
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    
    /**
     * Activa las fichas.
     * @param fichasNew Fichas a deshabilitar
     */
    public static void processNewFichas(String fichasNew){
                
        try{
            ZonaDDB.processNewFichas(fichasNew);
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
    
    /**
     * Registra las fichas.
     * @param fichasNew Fichas a deshabilitar
     */
    public static void registerFichas(String fichasNew){
                
        try{
            ZonaDDB.registerFichas(fichasNew);
        }catch(Exception e){
            e.printStackTrace();
        }        
    }
}
