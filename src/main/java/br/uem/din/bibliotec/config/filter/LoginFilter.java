package br.uem.din.bibliotec.config.filter;

import br.uem.din.bibliotec.config.conexao.Conexao;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebFilter(filterName = "loginAuth", urlPatterns = {"/gestaoBibliotecas.xhtml"})
class LoginFilter implements Filter {
    public LoginFilter() {
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = (HttpSession) req.getSession();
        String login = (String) session.getAttribute("usuario");
        try {
            //realiza conexão com banco de dados
            int permissaoAcesso = 0;
            Conexao con = new Conexao();
            con.conexao.setAutoCommit(true);
            Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs;

            st.execute("SELECT u.permissao FROM `bibliotec`.`usuarios` u WHERE u.usuario = '" + login + "';");
            rs = st.getResultSet();

            while (rs.next()) {
                permissaoAcesso = rs.getInt("permissao");
            }

            if (permissaoAcesso == 1) {
                res.sendRedirect(req.getContextPath() + "/acessoBibliotecario.xhtml");
            }

            if (permissaoAcesso == 2) {
                res.sendRedirect(req.getContextPath() + "/acessoBalconista.xhtml");
            }

            if (permissaoAcesso == 3) {
                res.sendRedirect(req.getContextPath() + "/acessoAluno.xhtml");
            }

            chain.doFilter(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
