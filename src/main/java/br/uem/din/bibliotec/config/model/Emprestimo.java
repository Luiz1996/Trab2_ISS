package br.uem.din.bibliotec.config.model;

import java.util.Objects;

public class Emprestimo {
    //atributos persistentes no banco de dados
    private int codEmprestimo;
    private int codUsuario;
    private int codLivro;
    private String dataEmp;
    private String dataDev;
    private String dataEntrega;
    private String dataAlt;
    private int ativo;

    //atributos auxiliares
    private String msgRetorno;
    private String colorMsgRetorno;
    private String nomeUsuario;
    private String emailUsuario;
    private String tituloLivro;
    private String autorLivro;
    private String editoraLivro;
    private String anoLancamentoLivro;
    private String statusEmprestimo;
    private String rgUsuario;
    private String cpfUsuario;
    private int empAtrasado;

    public Emprestimo (){}

    public Emprestimo(String dataEmp, String dataDev, String tituloLivro, String autorLivro, String editoraLivro, int empAtrasado) {
        this.dataEmp = dataEmp;
        this.dataDev = dataDev;
        this.tituloLivro = tituloLivro;
        this.autorLivro = autorLivro;
        this.editoraLivro = editoraLivro;
        this.empAtrasado = empAtrasado;
    }

    public Emprestimo(int codUsuario, int codLivro, String dataEmp, String dataDev, String dataAlt, String nomeUsuario, String emailUsuario, String tituloLivro, String autorLivro, String editoraLivro, String anoLancamentoLivro, String statusEmprestimo) {
        this.codUsuario = codUsuario;
        this.codLivro = codLivro;
        this.dataEmp = dataEmp;
        this.dataDev = dataDev;
        this.dataAlt = dataAlt;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.tituloLivro = tituloLivro;
        this.autorLivro = autorLivro;
        this.editoraLivro = editoraLivro;
        this.anoLancamentoLivro = anoLancamentoLivro;
        this.statusEmprestimo = statusEmprestimo;
    }

    public Emprestimo(int codEmprestimo, int codUsuario, int codLivro, String dataEmp, String dataDev, String dataAlt, String nomeUsuario, String emailUsuario, String tituloLivro, String autorLivro, String editoraLivro, String anoLancamentoLivro, String statusEmprestimo, String rgUsuario, String cpfUsuario, String dataEntrega) {
        this.codEmprestimo = codEmprestimo;
        this.codUsuario = codUsuario;
        this.codLivro = codLivro;
        this.dataEmp = dataEmp;
        this.dataDev = dataDev;
        this.dataAlt = dataAlt;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.tituloLivro = tituloLivro;
        this.autorLivro = autorLivro;
        this.editoraLivro = editoraLivro;
        this.anoLancamentoLivro = anoLancamentoLivro;
        this.statusEmprestimo = statusEmprestimo;
        this.rgUsuario = rgUsuario;
        this.cpfUsuario = cpfUsuario;
        this.dataEntrega = dataEntrega;
    }

    public int getCodEmprestimo() {
        return codEmprestimo;
    }

    public void setCodEmprestimo(int codeEmprestimo) {
        this.codEmprestimo = codeEmprestimo;
    }

    public int getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(int codUsuario) {
        this.codUsuario = codUsuario;
    }

    public int getCodLivro() {
        return codLivro;
    }

    public void setCodLivro(int codLivro) {
        this.codLivro = codLivro;
    }

    public String getDataEmp() {
        return dataEmp;
    }

    public void setDataEmp(String dataEmp) {
        this.dataEmp = dataEmp;
    }

    public String getDataDev() {
        return dataDev;
    }

    public void setDataDev(String dataDev) {
        this.dataDev = dataDev;
    }

    public String getDataAlt() {
        return dataAlt;
    }

    public void setDataAlt(String dataAlt) {
        this.dataAlt = dataAlt;
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

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getTituloLivro() {
        return tituloLivro;
    }

    public void setTituloLivro(String tituloLivro) {
        this.tituloLivro = tituloLivro;
    }

    public String getAutorLivro() {
        return autorLivro;
    }

    public void setAutorLivro(String autorLivro) {
        this.autorLivro = autorLivro;
    }

    public String getEditoraLivro() {
        return editoraLivro;
    }

    public void setEditoraLivro(String editoraLivro) {
        this.editoraLivro = editoraLivro;
    }

    public String getAnolancamentoLivro() {
        return anoLancamentoLivro;
    }

    public void setAnolancamentoLivro(String anolancamentoLivro) {
        this.anoLancamentoLivro = anolancamentoLivro;
    }

    public String getStatusEmprestimo() {
        return statusEmprestimo;
    }

    public void setStatusEmprestimo(String statusEmprestimo) {
        this.statusEmprestimo = statusEmprestimo;
    }

    public String getRgUsuario() {
        return rgUsuario;
    }

    public void setRgUsuario(String rgUsuario) {
        this.rgUsuario = rgUsuario;
    }

    public String getCpfUsuario() {
        return cpfUsuario;
    }

    public void setCpfUsuario(String cpfUsuario) {
        this.cpfUsuario = cpfUsuario;
    }

    public int getEmpAtrasado() { return empAtrasado; }

    public void setEmpAtrasado(int empAtrasado) { this.empAtrasado = empAtrasado; }

    public String getDataEntrega() { return dataEntrega; }

    public void setDataEntrega(String dataEntrega) { this.dataEntrega = dataEntrega; }

    public String getAnoLancamentoLivro() { return anoLancamentoLivro; }

    public void setAnoLancamentoLivro(String anoLancamentoLivro) { this.anoLancamentoLivro = anoLancamentoLivro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emprestimo that = (Emprestimo) o;
        return codEmprestimo == that.codEmprestimo &&
                codUsuario == that.codUsuario &&
                codLivro == that.codLivro &&
                ativo == that.ativo &&
                Objects.equals(dataEmp, that.dataEmp) &&
                Objects.equals(dataDev, that.dataDev) &&
                Objects.equals(dataAlt, that.dataAlt) &&
                Objects.equals(msgRetorno, that.msgRetorno) &&
                Objects.equals(colorMsgRetorno, that.colorMsgRetorno) &&
                Objects.equals(nomeUsuario, that.nomeUsuario) &&
                Objects.equals(emailUsuario, that.emailUsuario) &&
                Objects.equals(tituloLivro, that.tituloLivro) &&
                Objects.equals(autorLivro, that.autorLivro) &&
                Objects.equals(editoraLivro, that.editoraLivro) &&
                Objects.equals(anoLancamentoLivro, that.anoLancamentoLivro) &&
                Objects.equals(statusEmprestimo, that.statusEmprestimo) &&
                Objects.equals(rgUsuario, that.rgUsuario) &&
                Objects.equals(cpfUsuario, that.cpfUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codEmprestimo, codUsuario, codLivro, dataEmp, dataDev, dataAlt, ativo, msgRetorno, colorMsgRetorno, nomeUsuario, emailUsuario, tituloLivro, autorLivro, editoraLivro, anoLancamentoLivro, statusEmprestimo, rgUsuario, cpfUsuario);
    }
}

