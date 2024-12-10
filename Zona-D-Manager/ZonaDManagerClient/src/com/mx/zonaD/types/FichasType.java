package com.mx.zonaD.types;

import java.io.Serializable;

/**
 * La clase <code>FichasType</code> se utiliza para almacenar los datos de las fichas.
 * 
 * @author Manuel Guinto.
 */
public class FichasType implements Serializable{

    /**
     * Versi&oacute;n de la clase.
     */
    @SuppressWarnings("compatibility:-1494121101766681693")
    private static final long serialVersionUID = 1L;

    /**
     * Nombre del paquete.
     */
    private String packageName;

    /**
     * Nombre del vendedor.
     */
    private String vendor;

    /**
     * Titulo de la ficha.
     */
    private String title;

    /**
     * Prefijo del nombre de la ficha.
     */
    private String prefijo;
    
    /**
     * Constructor por defecto.
     */
    public FichasType() {
        super();
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return vendor;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public String getPrefijo() {
        return prefijo;
    }
}
