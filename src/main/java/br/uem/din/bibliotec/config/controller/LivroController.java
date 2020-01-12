package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.dao.LivroDao;
import br.uem.din.bibliotec.config.dao.UsuarioDao;
import br.uem.din.bibliotec.config.model.Livro;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Named
@SessionScoped
public class LivroController implements Serializable {
    private Livro livro = new Livro(0, "", "", "", "", "", "", "", 0, -1);
    private LivroDao livroDao = new LivroDao();
    private UsuarioDao userDao = new UsuarioDao();
    private int retorno = 0;
    private final String SUCESSO = "green";
    private final String FALHA = "red";

    //contrutores e gets/sets
    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public LivroDao getLivroDAO() { return livroDao; }

    public void setLivroDAO(LivroDao livroDAO) {
        this.livroDao = livroDAO;
    }

    public UsuarioDao getUserDAO() { return userDao; }

    public void setUserDAO(UsuarioDao userDAO) { this.userDao = userDAO; }

    public String getSUCESSO() { return SUCESSO; }

    public String getFALHA() { return FALHA; }

    public LivroDao getLivroDao() { return livroDao; }

    public void setLivroDao(LivroDao livroDao) { this.livroDao = livroDao; }

    public UsuarioDao getUserDao() { return userDao; }

    public void setUserDao(UsuarioDao userDao) { this.userDao = userDao; }

    public int getRetorno() { return retorno; }

    public void setRetorno(int retorno) { this.retorno = retorno; }

    //chama método pra cadastramento de livro(s)
    public String realizarCadastroLivro() throws SQLException {
        retorno = livroDao.cadastrarLivro(livro);

        if(retorno == 1){
            livro.setMsg_retorno("Retorno: O livro foi cadastrado com sucesso.");
            livro.setColor_msg_retorno(SUCESSO);
        }else{
            livro.setMsg_retorno("Retorno: A operação de cadastramento do livro falhou.");
            livro.setColor_msg_retorno(FALHA);
        }
        return userDao.homePage();
    }

    public List<Livro> livrosDisponiveis() throws SQLException {
        return livroDao.consultaLivrosDisponiveis(livro);
    }

    //chama método para consultar livro(s)
    public List<Livro> realizarConsultaLivro() throws SQLException {
        return livroDao.consultarLivro(livro, 0);
    }

    public List<Livro> realizarConsultaLivroBibliotecario() throws SQLException {
        return livroDao.consultarLivroBibliotecario(livro, 0);
    }

    public List<Livro> realizarConsultaLivroBibliotecarioSoAtivos() throws SQLException {
        return livroDao.consultarLivroBibliotecario(livro, 1);
    }

    //chama método para exclusão de livro (exclusão lógica, ativo = 0)
    public String realizarDelecaoLivro(){
        retorno =  livroDao.deletarLivro(livro);

        if(retorno == 0){
            livro.setMsg_retorno("Retorno: A deleção do livro falhou.");
            livro.setColor_msg_retorno(FALHA);
        }else{
            if(retorno == 1){
                livro.setMsg_retorno("Retorno: O livro foi deletado com sucesso.");
                livro.setColor_msg_retorno(SUCESSO);
            }else{
                if(retorno == -1){
                    livro.setMsg_retorno("Retorno: O livro possui reserva, deleção falhou.");
                    livro.setColor_msg_retorno(FALHA);
                }else{
                    if(retorno == -2){
                        livro.setMsg_retorno("Retorno: O livro possui empréstimos, deleção falhou.");
                        livro.setColor_msg_retorno(FALHA);
                    }
                }
            }
        }
        return userDao.homePage();
    }

    //chama método para editar as informações de um livro
    public String realizarEdicaoLivro(){
        retorno = livroDao.editarLivro(livro);

        if(retorno == 1){
            livro.setMsg_retorno("Retorno: As informações do livro foram atualizadas com sucesso.");
            livro.setColor_msg_retorno(SUCESSO);
        }else{
            if(retorno == 0){
                livro.setMsg_retorno("Retorno: A operação de alteração do livro selecionado falhou.");
                livro.setColor_msg_retorno(FALHA);
            }else{
                livro.setMsg_retorno("Retorno: O livro informado não existe.");
                livro.setColor_msg_retorno(FALHA);
            }
        }
        return userDao.homePage();
    }

    public void carregarDadosLivros(){
        livroDao.carregarDadosLivro(livro);
    }
}
