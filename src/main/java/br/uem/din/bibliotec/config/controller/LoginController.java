package br.uem.din.bibliotec.config.controller;

import br.uem.din.bibliotec.config.dao.UsuarioDao;
import br.uem.din.bibliotec.config.model.Usuario;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

@Named
@SessionScoped
public class LoginController implements Serializable {
    private final long tempoMilisegundos = 60000;  //1min
    private final int numeroTentativas = 4;        //5 tentativas -> 0,1,2,3,4
    private UsuarioDao userDao = new UsuarioDao();
    private Usuario login;
    private String usuario;
    private String senha;

    private HttpServletRequest request;

    //contrutores e gets/sets
    public LoginController(){ login = new Usuario(); }

    public UsuarioDao getUserDAO() { return userDao; }

    public void setUserDAO(UsuarioDao userDAO) { this.userDao = userDAO; }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Usuario getLogin() {
        return login;
    }

    public void setLogin(Usuario login) {
        this.login = login;
    }

    public UsuarioDao getUserDao() { return userDao; }

    public void setUserDao(UsuarioDao userDao) { this.userDao = userDao; }

    //realizando autenticação
    public String loginController() throws SQLException, NoSuchAlgorithmException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        request = (HttpServletRequest) externalContext.getRequest();

        ForcaBrutaController control;

        if (externalContext.getSessionMap().get("ForcaBrutaController") == null) {
            control = new ForcaBrutaController(tempoMilisegundos, numeroTentativas);
            externalContext.getSessionMap().put("ForcaBrutaController", control);
        } else {
            control = (ForcaBrutaController) externalContext.getSessionMap().get("ForcaBrutaController");
        }

        if (!control.podeLoggar(request)) {
            userDao.bloquearUsuario(usuario);
            login.setMsg_autenticacao("IP BLOQUEADO! Aguarde 5min.");
            return "/gestaoBibliotecas.xhtml";
        }

        //Simula uma busca do usuário no banco de dados
        int colaborador = userDao.buscaPermissao(login, usuario, senha);

        if(colaborador == 1){
            return userDao.homePage();
        }else{
            control.registraFalha(request);
            return userDao.homePage();
        }
    }

    //encerrando sessão do usuário
    public String logoutSession(){
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/gestaoBibliotecas?faces-redirect=true";
    }
}
