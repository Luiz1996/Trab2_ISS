package br.uem.din.bibliotec.config.model;

import javax.servlet.ServletRequest;

public class RegistroFalha {

    private String login;
    private int tentativa;
    private long ultimaTentativa;

    public static RegistroFalha criarRegistroFalha(ServletRequest request) {
        RegistroFalha falha = new RegistroFalha();
        falha.setLogin(request.getRemoteHost());
        System.out.println(request.getRemoteHost());
        falha.setTentativa(1);
        falha.setUltimaTentativa(System.currentTimeMillis());
        return falha;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getTentativa() {
        return tentativa;
    }

    public void setTentativa(int tentativa) {
        this.tentativa = tentativa;
    }

    public long getUltimaTentativa() {
        return ultimaTentativa;
    }

    public void setUltimaTentativa(long ultimaTentativa) {
        this.ultimaTentativa = ultimaTentativa;
    }

    public long getTempo() {
        return System.currentTimeMillis() - ultimaTentativa;
    }
}

