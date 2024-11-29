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
    
    
    
}
