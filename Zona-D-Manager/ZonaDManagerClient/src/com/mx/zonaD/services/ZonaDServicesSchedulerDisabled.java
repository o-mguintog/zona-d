package com.mx.zonaD.services;

/**
 * La clase <code>ZonaDServicesSchedulerDisabled</code> Se utiliza para deshabilitar fichas
 * caducadas.
 * 
 * @author Manuel Guinto.
 */
public class ZonaDServicesSchedulerDisabled {

    /**
     * Constructor por defecto.
     */
    public ZonaDServicesSchedulerDisabled() {
        super();
    }

    /**
     * Proceso para deshabilitar las fichas en la BD y en Mikrotik.
     * @param args Argumentos.
     */
    public static void main(String[] args) {
       
       ZonaDServices services = new ZonaDServices();
       services.disabledFichas();
   }
}
