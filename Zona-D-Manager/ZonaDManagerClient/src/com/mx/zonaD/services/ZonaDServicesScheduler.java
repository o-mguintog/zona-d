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
    
    public static void main(String[] args) {
        ZonaDServices services = new ZonaDServices();
        services.activeFichas();        
   }
}
