package com.mx.zonaD.model.queries;

/**
 * La clase <code></code> es utilizada para almacenar las consultas utilizadas
 * para Zona D.
 */
public class ZonaDQueries {

    /**
     * Obtiene las fichas nuevas.
     */
    public static final String GET_FICHAS_NEW =" SELECT '{\"responseFichas\":'||Info2.jsonValue||'}' FROM ( SELECT JSON_ARRAYAGG (JSON_OBJECT( 'ficha' IS Info.NAME, 'status' IS Info.STATUS) )jsonValue FROM ( SELECT NAME, STATUS FROM zd_fichas WHERE 1 = 1 AND STATUS = 'NEW' )Info )Info2 ";
    
    /**
     * Constructor por defecto.
     */
    public ZonaDQueries() {
        super();
    }
}
