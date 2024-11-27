package com.mx.zonaD.model.types;

import java.io.Serializable;

/**
 * La clase <code>FichasType</code> es utilizada como clase pojo de la
 * entidad fichas.
 *
 * @author Manuel Guinto.
 */
public class FichasType implements Serializable{

    /**
     * Versi&oacute;n de la clase.
     */
    @SuppressWarnings("compatibility:-9064093805175202784")
    private static final long serialVersionUID = 1L;


    /**
     * Clave de la ficha.
     */
    private String ficha;

    /**
     * Estatus de la ficha.
     */
    private String status;
    
    /**
     * Constructor por defecto.
     */
    public FichasType() {
        super();
    }

    /**
     * Inicializa el valor de la ficha.
     * @param ficha Clave de la ficha.
     */
    public void setFicha(String ficha) {
        this.ficha = ficha;
    }

    /**
     * Obtiene la clave de la ficha.
     * @return Clave de la ficha.
     */
    public String getFicha() {
        return ficha;
    }

    /**
     * Inicializa el estado.
     * @param status Estado de la ficha.
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Obtiene el estado de la ficha.
     * @return Estado de la ficha.
     */
    public String getStatus() {
        return status;
    }
}
