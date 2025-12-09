package com.dataware.recibo.util;

import com.dataware.recibo.model.Empresa;

/**
 * Gerenciador de sessão para manter a empresa logada no sistema
 */
public class SessionManager {
    private static SessionManager instance;
    private Empresa empresaLogada;

    private SessionManager() {
        // Construtor privado para Singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public Empresa getEmpresaLogada() {
        return empresaLogada;
    }

    public void setEmpresaLogada(Empresa empresa) {
        this.empresaLogada = empresa;
    }

    public Integer getEmpresaSistemaId() {
        return empresaLogada != null ? empresaLogada.getId() : null;
    }

    public boolean isLogado() {
        return empresaLogada != null;
    }

    public void logout() {
        this.empresaLogada = null;
    }
}



