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
    public static final String GET_ENABLED_USERS = "/ip/hotspot/user/print where uptime >0 and disabled =no";

    /**
     * Obtiene los usuarios activos.
     */
    public static final String GET_ACTIVE_USERS = "/ip/hotspot/active/print";

    /**
     * Desabilita el usuario
     */
    public static final String DISABLED_USER = "/ip/hotspot/user/disable ";

    /**
     * Obtiene la informaci&oacute;n de un usuario.
     */
    public static final String GET_USERS_MKT ="/ip/hotspot/user/print where name=";

    /**
     * Atributo ID.
     */
    public static final String ID_MKT =".id";
    
    /**
     * Constructor por defecto.
     */
    public ZonaDMikrotikCommands() {
        super();
    }
}
