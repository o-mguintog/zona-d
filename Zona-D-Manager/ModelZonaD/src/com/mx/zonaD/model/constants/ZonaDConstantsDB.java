package com.mx.zonaD.model.constants;

/**
 * La clase <code>ZonaDConstantsDB</code> almacena constantes de la BD.
 *
 * @author Manuel Guinto.
 */
public class ZonaDConstantsDB {

    /**
     * URL de conexi&oacute;n a la BD.
     */
    public static final String JDBC_URL="jdbc:oracle:thin:@127.0.0.1:1521:xe";

    /**
     * Usuario de BD.
     */
    public static final String USERNAME="\"Zona-D\"";

    /**
     * Password.
     */
    public static final String PASSWORD="1234";

    /**
     * Constructor por defecto.
     */
    public ZonaDConstantsDB() {
        super();
    }
}
