package com.mx.zonaD.model.services.response;

import com.mx.zonaD.model.types.FichasType;

import java.io.Serializable;

import java.util.List;

/**
 * La clase <code>FichasResponse</code> Respuesta de fichas
 *
 * @author Manuel Guinto.
 */
public class FichasResponse implements Serializable{

    /**
     * Versi&oacute;n de la clase.
     */
    @SuppressWarnings("compatibility:635350253363932640")
    private static final long serialVersionUID = 1L;

    /**
     * Lista de fichas.
     */
    List<FichasType> responseFichas;
    
    /**
     * Constructor por defecto.
     */
    public FichasResponse() {
        super();
    }

    /**
     * Inicializa la lista de fichas.
     * 
     * @param responseFichas Lista de fichas.
     */
    public void setResponseFichas(List<FichasType> responseFichas) {
        this.responseFichas = responseFichas;
    }

    /**
     * Obtiene la lista de fichas.
     * 
     * @return Lista de fichas.
     */
    public List<FichasType> getResponseFichas() {
        return responseFichas;
    }
}
