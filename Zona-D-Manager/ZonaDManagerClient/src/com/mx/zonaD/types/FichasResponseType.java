package com.mx.zonaD.types;

import java.util.List;

/**
 * Almacena la respuesta del json obtenido para generar fichas.
 */
public class FichasResponseType {

    /**
     * Arreglo de perfiles de fichas a crear.
     */
    private List<FichasType> fichasProfile;

    /**
     * Constructor por defecto.
     */
    public FichasResponseType() {
        super();
    }

    public void setFichasProfile(List<FichasType> fichasProfile) {
        this.fichasProfile = fichasProfile;
    }

    public List<FichasType> getFichasProfile() {
        return fichasProfile;
    }
}
