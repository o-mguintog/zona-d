package com.mx.zonaD.services.comands;

/**
 * La clase <code>ZonaDMikrotikCommands</code> es utilizada para almacenar
 * comandos utilizados de mikrotik utilizados para operaciones.
 * 
 * @author Manuel Guinto.
 */
public class ZonaDMikrotikCommands {

    /**
     * Obtiene las fichas iniciadas y activas en mikrotik
     */
    public static final String GET_ENABLED_USERS ="/ip/hotspot/user/print where uptime >0 and disabled =no";
    
    /**
     * Constructor por defecto.
     */
    public ZonaDMikrotikCommands() {
        super();
    }
}
