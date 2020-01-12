package br.uem.din.bibliotec.config.model;

import br.uem.din.bibliotec.config.services.Email;

import java.util.Objects;
import java.util.Observable;
import java.util.Observer;

public class Usuario implements Observer {
    //atributos dos usuarios
    private static Email sendEmail = new Email();
    private String email = "";
    private String usuario = "";
    private String senha = "";
    private String nome = "";
    private String rg = "";
    private String cpf = "";
    private String endereco = "";
    private String cep = "";
    private String cidade = "";
    private String estado = "";
    private String msg_autenticacao = "";
    private String color_msg = "";
    private int    permissao  = 0;
    private int    ativo = -1;
    private String status = "";
    private String perfil = "";
    private int    codusuario = 0;
    private String datacad = "";
    private String dataalt = "";
    private String datanasc = "";
    private int    jaativado = 0;
    private String busca = "";
    private int bloq;
    private int minutos;

    //contrutores e gets/sets
    public Usuario(String email, String usuario, String senha, String nome, String rg, String cpf, String endereco, String cep, String cidade, String estado, int permissao, int ativo, String msg_autenticacao, String color_msg) {
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.permissao = permissao;
        this.ativo = ativo;
        this.msg_autenticacao = msg_autenticacao;
        this.color_msg = color_msg;
    }

    public Usuario(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public Usuario(String email, String usuario, String senha, String nome, String rg, String cpf, String endereco, String cep, String cidade, String estado, String msg_autenticacao, String color_msg, int permissao, int ativo, String status, String perfil, int codusuario) {
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.msg_autenticacao = msg_autenticacao;
        this.color_msg = color_msg;
        this.permissao = permissao;
        this.ativo = ativo;
        this.status = status;
        this.perfil = perfil;
        this.codusuario = codusuario;
    }

    public Usuario(String email, String usuario, String senha, String nome, String rg, String cpf, String endereco, String cep, String cidade, String estado, String msg_autenticacao, String color_msg, int permissao, int ativo, String status, String perfil, int codusuario, String datacad, String dataalt, String datanasc) {
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.msg_autenticacao = msg_autenticacao;
        this.color_msg = color_msg;
        this.permissao = permissao;
        this.ativo = ativo;
        this.status = status;
        this.perfil = perfil;
        this.codusuario = codusuario;
        this.datacad = datacad;
        this.dataalt = dataalt;
        this.datanasc = datanasc;
    }

    public Usuario(String email, String usuario, String senha, String nome, String rg, String cpf, String endereco, String cep, String cidade, String estado, String msg_autenticacao, String color_msg, int permissao, int ativo, String status, String perfil, int codusuario, String datacad, String dataalt, String datanasc, int jaativado) {
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.nome = nome;
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.cidade = cidade;
        this.estado = estado;
        this.msg_autenticacao = msg_autenticacao;
        this.color_msg = color_msg;
        this.permissao = permissao;
        this.ativo = ativo;
        this.status = status;
        this.perfil = perfil;
        this.codusuario = codusuario;
        this.datacad = datacad;
        this.dataalt = dataalt;
        this.datanasc = datanasc;
        this.jaativado = jaativado;
    }

    public Usuario(String nome, int codusuario, String cpf, String email, String rg, String datanasc) {
        this.nome = nome;
        this.codusuario = codusuario;
        this.cpf = cpf;
        this.email = email;
        this.rg = rg;
        this.datanasc = datanasc;
    }

    Observable book;
    public Usuario(Observable livro){
        this.book = livro;
        book.addObserver(this);
    }

    public Usuario(){}

    public Usuario(int codusuario) { this.codusuario = codusuario; }

    public String getBusca() { return busca; }

    public void setBusca(String busca) { this.busca = busca; }

    public String getDatanasc() { return datanasc; }

    public void setDatanasc(String datanasc) { this.datanasc = datanasc; }

    public int getJaativado() { return jaativado; }

    public void setJaativado(int jaativado) { this.jaativado = jaativado; }

    public String getDatacad() { return datacad; }

    public void setDatacad(String datacad) { this.datacad = datacad; }

    public String getDataalt() { return dataalt; }

    public void setDataalt(String dataalt) { this.dataalt = dataalt; }

    public int getCodusuario() { return codusuario; }

    public void setCodusuario(int codusuario) { this.codusuario = codusuario; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String getPerfil() { return perfil; }

    public void setPerfil(String perfil) { this.perfil = perfil; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getRg() { return rg; }

    public void setRg(String rg) { this.rg = rg; }

    public String getCpf() { return cpf; }

    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEndereco() { return endereco; }

    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getCep() { return cep; }

    public void setCep(String cep) { this.cep = cep; }

    public String getCidade() { return cidade; }

    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    public int getPermissao() { return permissao; }

    public void setPermissao(int permissao) { this.permissao = permissao; }

    public int getAtivo() { return ativo; }

    public void setAtivo(int ativo) { this.ativo = ativo; }

    public String getMsg_autenticacao() {
        return msg_autenticacao;
    }

    public void setMsg_autenticacao(String msg_autenticacao) {
        this.msg_autenticacao = msg_autenticacao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getColor_msg() { return color_msg; }

    public void setColor_msg(String color_msg) { this.color_msg = color_msg; }

    public Email getSendEmail() { return sendEmail; }

    public void setSendEmail(Email sendEmail) { this.sendEmail = sendEmail; }

    public int getBloq() { return bloq; }

    public void setBloq(int bloq) { this.bloq = bloq; }

    public Observable getBook() { return book; }

    public void setBook(Observable book) { this.book = book; }

    public int getMinutos() { return minutos; }

    public void setMinutos(int minutos) { this.minutos = minutos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario1 = (Usuario) o;
        return permissao == usuario1.permissao &&
                ativo == usuario1.ativo &&
                codusuario == usuario1.codusuario &&
                jaativado == usuario1.jaativado &&
                Objects.equals(email, usuario1.email) &&
                Objects.equals(usuario, usuario1.usuario) &&
                Objects.equals(senha, usuario1.senha) &&
                Objects.equals(nome, usuario1.nome) &&
                Objects.equals(rg, usuario1.rg) &&
                Objects.equals(cpf, usuario1.cpf) &&
                Objects.equals(endereco, usuario1.endereco) &&
                Objects.equals(cep, usuario1.cep) &&
                Objects.equals(cidade, usuario1.cidade) &&
                Objects.equals(estado, usuario1.estado) &&
                Objects.equals(msg_autenticacao, usuario1.msg_autenticacao) &&
                Objects.equals(color_msg, usuario1.color_msg) &&
                Objects.equals(status, usuario1.status) &&
                Objects.equals(perfil, usuario1.perfil) &&
                Objects.equals(datacad, usuario1.datacad) &&
                Objects.equals(dataalt, usuario1.dataalt) &&
                Objects.equals(datanasc, usuario1.datanasc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, usuario, senha, nome, rg, cpf, endereco, cep, cidade, estado, msg_autenticacao, color_msg, permissao, ativo, status, perfil, codusuario, datacad, dataalt, datanasc, jaativado);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Livro) {
            sendEmail.setAssunto("Reserva de Livro(" + ((Livro) o).getTitulo().trim() + ") - Biblioteca X");
            sendEmail.setEmailDestinatario(((Livro) o).getEmailUsuarioRes().trim());
            sendEmail.setMsg("Olá " + ((Livro) o).getNomeUsuarioRes().trim() + ", <br><br> A sua reserva do livro <b>'" + ((Livro) o).getTitulo().trim() + "'</b> está disponível!<br>Retirar livro até <b>" + ((Livro) o).getDatares().trim() + "</b>.<br><br>Se o livro não for retirado até a data informada, automaticamente sua reserva será <b>cancelada!</b>");

            new Thread(enviarEmail).start();
        }
    }

    private static final Runnable enviarEmail = new Runnable() {
        @Override
        public void run() {
            sendEmail.enviarGmail();
        }
    };
}
