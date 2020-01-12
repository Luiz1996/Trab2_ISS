package br.uem.din.bibliotec.config.model;

import java.util.Objects;
import java.util.Observable;

public class Livro extends Observable{
    //declaracao dos atributos do livro
    private Integer codlivro = 0;
    private String codcatalogacao = "";
    private String numchamada = "";
    private String titulo = "";
    private String autor = "";
    private String editora = "";
    private String anolancamento = "";
    private String cidade = "";
    private Integer volume = 0;
    private Integer ativo = -1;
    private String status = "";
    private String msg_retorno = "";
    private String color_msg_retorno = "";
    private String datacad = "";
    private String dataalt = "";
    private String disponibilidade = "";
    private String datares = "";
    private int usuariores = 0;
    private String nomeUsuarioRes = "";
    private String emailUsuarioRes = "";
    private String busca = "";

    //contrutores e gets/sets
    public Livro(String datares, int usuariores) {
        this.datares = datares;
        this.usuariores = usuariores;
    }

    public Livro(){

    }

    public Livro(Integer codlivro, String codcatalogacao, String numchamada, String titulo, String autor, String editora, String anolancamento, String cidade, Integer volume, Integer ativo) {
        this.codlivro = codlivro;
        this.codcatalogacao = codcatalogacao;
        this.numchamada = numchamada;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.cidade = cidade;
        this.volume = volume;
        this.ativo = ativo;
        this.color_msg_retorno = color_msg_retorno;
        this.msg_retorno = msg_retorno;
    }

    public Livro(String titulo, String autor, String editora, String anolancamento, Integer volume, String datares, int codlivro) {
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.volume = volume;
        this.datares = datares;
        this.codlivro = codlivro;
    }

    public Livro(Integer codlivro, String codcatalogacao, String numchamada, String titulo, String autor, String editora, String anolancamento, String cidade, Integer volume, Integer ativo, String status) {
        this.codlivro = codlivro;
        this.codcatalogacao = codcatalogacao;
        this.numchamada = numchamada;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.cidade = cidade;
        this.volume = volume;
        this.ativo = ativo;
        this.status = status;
        this.color_msg_retorno = color_msg_retorno;
        this.msg_retorno = msg_retorno;
    }

    public Livro(Integer codlivro, String codcatalogacao, String numchamada, String titulo, String autor, String editora, String anolancamento, String cidade, Integer volume, Integer ativo, String status, String msg_retorno, String color_msg_retorno, String datacad, String dataalt, String disponibilidade) {
        this.codlivro = codlivro;
        this.codcatalogacao = codcatalogacao;
        this.numchamada = numchamada;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.cidade = cidade;
        this.volume = volume;
        this.ativo = ativo;
        this.status = status;
        this.msg_retorno = msg_retorno;
        this.color_msg_retorno = color_msg_retorno;
        this.datacad = datacad;
        this.dataalt = dataalt;
        this.disponibilidade = disponibilidade;
    }

    public Livro(Integer codlivro, String titulo, String autor, String editora, String anolancamento, String codcatalogacao) {
        this.codlivro = codlivro;
        this.titulo = titulo;
        this.autor = autor;
        this.editora = editora;
        this.anolancamento = anolancamento;
        this.codcatalogacao = codcatalogacao;
    }

    public Livro(String disponibilidade){
        this.disponibilidade = disponibilidade;
    }

    public void setNovaDisp(String disponibilidade) {
        if(disponibilidade.equals("1".trim())){
            this.disponibilidade = disponibilidade;
            setChanged();
            notifyObservers();
        }
    }

    public String getBusca() { return busca; }

    public void setBusca(String busca) { this.busca = busca; }

    public String getEmailUsuarioRes() { return emailUsuarioRes; }

    public void setEmailUsuarioRes(String emailUsuarioRes) { this.emailUsuarioRes = emailUsuarioRes; }

    public String getNomeUsuarioRes() { return nomeUsuarioRes; }

    public void setNomeUsuarioRes(String nomeUsuarioRes) { this.nomeUsuarioRes = nomeUsuarioRes; }

    public String getDatares() { return datares; }

    public void setDatares(String datares) { this.datares = datares; }

    public int getUsuariores() { return usuariores; }

    public void setUsuariores(int usuariores) { this.usuariores = usuariores; }

    public String getDisponibilidade() { return this.disponibilidade; }

    public void setDisponibilidade(String disponibilidade) { this.disponibilidade = disponibilidade; }

    public Livro(Integer codlivro) { this.codlivro = codlivro; }

    public String getDatacad() { return datacad; }

    public void setDatacad(String datacad) { this.datacad = datacad; }

    public String getDataalt() { return dataalt; }

    public void setDataalt(String dataalt) { this.dataalt = dataalt; }

    public String getColor_msg_retorno() { return color_msg_retorno; }

    public void setColor_msg_retorno(String color_msg_retorno) { this.color_msg_retorno = color_msg_retorno; }

    public String getMsg_retorno() { return msg_retorno; }

    public void setMsg_retorno(String msg_retorno) { this.msg_retorno = msg_retorno; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public Integer getCodlivro() { return codlivro; }

    public void setCodlivro(Integer codlivro) { this.codlivro = codlivro; }

    public String getCodcatalogacao() { return codcatalogacao; }

    public void setCodcatalogacao(String codcatalogacao) { this.codcatalogacao = codcatalogacao; }

    public String getNumchamada() { return numchamada; }

    public void setNumchamada(String numchamada) { this.numchamada = numchamada; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }

    public void setAutor(String autor) { this.autor = autor; }

    public String getEditora() { return editora; }

    public void setEditora(String editora) { this.editora = editora; }

    public String getAnolancamento() { return anolancamento; }

    public void setAnolancamento(String anolancamento) { this.anolancamento = anolancamento; }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) { this.cidade = cidade; }

    public Integer getVolume() { return volume; }

    public void setVolume(Integer volume) { this.volume = volume; }

    public Integer getAtivo() { return ativo; }

    public void setAtivo(Integer ativo) { this.ativo = ativo; }

    //declaração da equals e hashcode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livro livro = (Livro) o;
        return usuariores == livro.usuariores &&
                Objects.equals(codlivro, livro.codlivro) &&
                Objects.equals(codcatalogacao, livro.codcatalogacao) &&
                Objects.equals(numchamada, livro.numchamada) &&
                Objects.equals(titulo, livro.titulo) &&
                Objects.equals(autor, livro.autor) &&
                Objects.equals(editora, livro.editora) &&
                Objects.equals(anolancamento, livro.anolancamento) &&
                Objects.equals(cidade, livro.cidade) &&
                Objects.equals(volume, livro.volume) &&
                Objects.equals(ativo, livro.ativo) &&
                Objects.equals(status, livro.status) &&
                Objects.equals(msg_retorno, livro.msg_retorno) &&
                Objects.equals(color_msg_retorno, livro.color_msg_retorno) &&
                Objects.equals(datacad, livro.datacad) &&
                Objects.equals(dataalt, livro.dataalt) &&
                Objects.equals(disponibilidade, livro.disponibilidade) &&
                Objects.equals(datares, livro.datares) &&
                Objects.equals(nomeUsuarioRes, livro.nomeUsuarioRes) &&
                Objects.equals(emailUsuarioRes, livro.emailUsuarioRes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codlivro, codcatalogacao, numchamada, titulo, autor, editora, anolancamento, cidade, volume, ativo, status, msg_retorno, color_msg_retorno, datacad, dataalt, disponibilidade, datares, usuariores, nomeUsuarioRes, emailUsuarioRes);
    }
}
