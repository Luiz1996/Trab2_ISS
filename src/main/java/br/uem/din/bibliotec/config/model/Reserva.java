package br.uem.din.bibliotec.config.model;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class Reserva {
    //atributos persistentes em banco de dados
    private int codReserva;
    private int codLivro;
    private int codUsuario;
    private String dataCad;
    private String dataAlt;
    private String dataRes;
    private int ativo;

    //atributos auxiliares
    private String titulo;
    private String autor;
    private String editora;
    private String msgRetorno;
    private String colorMsgRetorno;

    public Reserva(){}

    @Contract(pure = true)
    public Reserva(int codReserva, int codLivro, int codUsuario, String dataCad, String dataAlt, int ativo) {
        this.codReserva = codReserva;
        this.codLivro = codLivro;
        this.codUsuario = codUsuario;
        this.dataCad = dataCad;
        this.dataAlt = dataAlt;
        this.ativo = ativo;
    }

    public Reserva(int codReserva, String dataCad, String dataRes, String titulo, String autor, String editora) {
        this.codReserva = codReserva;
        this.dataCad = dataCad;
        this.dataRes = dataRes;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
    }

    public int getCodReserva() {
        return codReserva;
    }

    public void setCodReserva(int codReserva) {
        this.codReserva = codReserva;
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

    public String getDataRes() {
        return dataRes;
    }

    public void setDataRes(String dataRes) {
        this.dataRes = dataRes;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
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
        Reserva reserva = (Reserva) o;
        return codReserva == reserva.codReserva &&
                codLivro == reserva.codLivro &&
                codUsuario == reserva.codUsuario &&
                ativo == reserva.ativo &&
                Objects.equals(dataCad, reserva.dataCad) &&
                Objects.equals(dataAlt, reserva.dataAlt) &&
                Objects.equals(dataRes, reserva.dataRes) &&
                Objects.equals(titulo, reserva.titulo) &&
                Objects.equals(autor, reserva.autor) &&
                Objects.equals(editora, reserva.editora) &&
                Objects.equals(msgRetorno, reserva.msgRetorno) &&
                Objects.equals(colorMsgRetorno, reserva.colorMsgRetorno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codReserva, codLivro, codUsuario, dataCad, dataAlt, dataRes, ativo, titulo, autor, editora, msgRetorno, colorMsgRetorno);
    }
}
