package br.uem.din.bibliotec.config.model;

import java.util.Objects;

public class Cotacao {
    //atributos persistentes
    private int codcotacao;
    private double valor;
    private String dataini;
    private String datafim;
    private int ativo;

    //atributo auxiliares
    private String msgRetorno;
    private String colorMsgRetorno;

    public Cotacao(){}

    public Cotacao(int codcotacao, double valor, String dataini, String datafim, int ativo) {
        this.codcotacao = codcotacao;
        this.valor = valor;
        this.dataini = dataini;
        this.datafim = datafim;
        this.ativo = ativo;
    }

    public int getCodcotacao() {
        return codcotacao;
    }

    public void setCodcotacao(int codcotacao) {
        this.codcotacao = codcotacao;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getDataini() {
        return dataini;
    }

    public void setDataini(String dataini) {
        this.dataini = dataini;
    }

    public String getDatafim() {
        return datafim;
    }

    public void setDatafim(String datafim) {
        this.datafim = datafim;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public String getMsgRetorno() {
        return msgRetorno;
    }

    public void setMsgRetorno(String msgRetorno) {
        this.msgRetorno = msgRetorno;
    }

    public String getColorMsgRetorno() {
        return colorMsgRetorno;
    }

    public void setColorMsgRetorno(String colorMsgRetorno) {
        this.colorMsgRetorno = colorMsgRetorno;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cotacao cotacao = (Cotacao) o;
        return codcotacao == cotacao.codcotacao &&
                Double.compare(cotacao.valor, valor) == 0 &&
                ativo == cotacao.ativo &&
                Objects.equals(dataini, cotacao.dataini) &&
                Objects.equals(datafim, cotacao.datafim) &&
                Objects.equals(msgRetorno, cotacao.msgRetorno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codcotacao, valor, dataini, datafim, ativo, msgRetorno);
    }
}
