package com.mx.zonaD.services;

/**
 * La clase <code>ZonaDServicesScheduler</code> se utiliza para 
 * ejecuciones autom&aacute;ticas del control de fichas.
 * 
 * @author Manuel Guinto.
 */
public class ZonaDServicesScheduler {

    /**
     * Constructor default.
     */
    public ZonaDServicesScheduler() {
        super();
    }

    /**
     * Proceso de activaci&oacute;n de fichas en la BD.
     * @param args Argumentos.
     */
    public static void main(String[] args) {
        ZonaDServices services = new ZonaDServices();
        services.activeFichas();        
   }
}
