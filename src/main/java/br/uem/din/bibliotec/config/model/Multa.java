package br.uem.din.bibliotec.config.model;

import java.util.Objects;

public class Multa {
    //atributos persistentes
    private int codMulta;
    private int codLivro;
    private int codUsuario;
    private int codEmprestimo;
    private int codCotacao;
    private String dataCad;
    private String dataAlt;
    private int diasAtraso;
    private double valor;
    private int ativo;

    //atributo(s) auxiliar(es)
    private String cpfUsuario;
    private String idMultasSeparadosVirgula;
    private String nomeUsuario;
    private String tituloLivro;
    private String autorLivro;
    private String editoraLivro;
    private double valorCotacao;
    private String msgRetorno;
    private String colorMsgRetorno;
    private String totMulta;

    public Multa(){}

    public Multa(int codMulta, int diasAtraso, double valor, String nomeUsuario, String tituloLivro, String autorLivro, String editoraLivro, double valorCotacao, String totMulta) {
        this.codMulta = codMulta;
        this.diasAtraso = diasAtraso;
        this.valor = valor;
        this.nomeUsuario = nomeUsuario;
        this.tituloLivro = tituloLivro;
        this.autorLivro = autorLivro;
        this.editoraLivro = editoraLivro;
        this.valorCotacao = valorCotacao;
        this.totMulta = totMulta;
    }

    public int getCodMulta() {
        return codMulta;
    }

    public void setCodMulta(int codMulta) {
        this.codMulta = codMulta;
    }

    public int getCodLivro() {
        return codLivro;
    }

    public void setCodLivro(int codLivro) {
        this.codLivro = codLivro;
    }

    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getDataCad() {
        return dataCad;
    }

    public void setDataCad(String dataCad) {
        this.dataCad = dataCad;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public String getCpfUsuario() { return cpfUsuario; }

    public String getIdMultasSeparadosVirgula() { return idMultasSeparadosVirgula; }

    public void setIdMultasSeparadosVirgula(String idMultasSeparadosVirgula) { this.idMultasSeparadosVirgula = idMultasSeparadosVirgula; }

    public void setCpfUsuario(String cpfUsuario) { this.cpfUsuario = cpfUsuario; }

    public String getMsgRetorno() { return msgRetorno; }

    public void setMsgRetorno(String msgRetorno) { this.msgRetorno = msgRetorno; }

    public String getColorMsgRetorno() { return colorMsgRetorno; }

    public void setColorMsgRetorno(String colorMsgRetorno) { this.colorMsgRetorno = colorMsgRetorno; }

    public int getCodEmprestimo() { return codEmprestimo; }

    public void setCodEmprestimo(int codEmprestimo) { this.codEmprestimo = codEmprestimo; }

    public int getCodCotacao() { return codCotacao; }

    public void setCodCotacao(int codCotacao) { this.codCotacao = codCotacao; }

    public int getDiasAtraso() { return diasAtraso; }

    public void setDiasAtraso(int diasAtraso) { this.diasAtraso = diasAtraso; }

    public String getNomeUsuario() { return nomeUsuario; }

    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }

    public String getTituloLivro() { return tituloLivro; }

    public void setTituloLivro(String tituloLivro) { this.tituloLivro = tituloLivro; }

    public String getAutorLivro() { return autorLivro; }

    public void setAutorLivro(String autorLivro) { this.autorLivro = autorLivro; }

    public String getEditoraLivro() { return editoraLivro; }

    public void setEditoraLivro(String editoraLivro) { this.editoraLivro = editoraLivro; }

    public double getValorCotacao() { return valorCotacao; }

    public void setValorCotacao(double valorCotacao) { this.valorCotacao = valorCotacao; }

    public String getTotMulta() { return totMulta; }

    public void setTotMulta(String totMulta) { this.totMulta = totMulta; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Multa multa = (Multa) o;
        return codMulta == multa.codMulta &&
                codLivro == multa.codLivro &&
                codUsuario == multa.codUsuario &&
                Double.compare(multa.valor, valor) == 0 &&
                ativo == multa.ativo &&
                Objects.equals(dataCad, multa.dataCad) &&
                Objects.equals(dataAlt, multa.dataAlt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codMulta, codLivro, codUsuario, dataCad, dataAlt, valor, ativo);
    }

    @Override
    public String toString() {
        return "Multa{" +
                "codMulta=" + codMulta +
                ", codLivro=" + codLivro +
                ", codUsuario=" + codUsuario +
                ", codEmprestimo=" + codEmprestimo +
                ", codCotacao=" + codCotacao +
                ", dataCad='" + dataCad + '\'' +
                ", dataAlt='" + dataAlt + '\'' +
                ", diasAtraso=" + diasAtraso +
                ", valor=" + valor +
                ", ativo=" + ativo +
                ", cpfUsuario='" + cpfUsuario + '\'' +
                ", idMultasSeparadosVirgula='" + idMultasSeparadosVirgula + '\'' +
                ", nomeUsuario='" + nomeUsuario + '\'' +
                ", tituloLivro='" + tituloLivro + '\'' +
                ", autorLivro='" + autorLivro + '\'' +
                ", editoraLivro='" + editoraLivro + '\'' +
                ", valorCotacao=" + valorCotacao +
                ", msgRetorno='" + msgRetorno + '\'' +
                ", colorMsgRetorno='" + colorMsgRetorno + '\'' +
                ", totMulta=" + totMulta +
                '}';
    }
}
