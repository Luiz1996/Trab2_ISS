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

@WebFilter(filterName = "BibliotecarioFilter", urlPatterns = {
                                                                "/cadastrarLivro.xhtml",
                                                                "/consultarLivro.xhtml",
                                                                "/alterarLivro.xhtml",
                                                                "/deletarLivro.xhtml",
                                                                "/acessoBibliotecario.xhtml"
                                                        }
           )
public class BibliotecarioFilter implements Filter {
    public BibliotecarioFilter(){}

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;
        HttpSession session = (HttpSession) req.getSession();
        String login = (String)session.getAttribute("usuario");

        try{
            if(login == null){
                res.sendRedirect(req.getContextPath() + "/gestaoBibliotecas.xhtml");
            }else {
                //realiza conexão com banco de dados
                int permissaoAcesso = 0;
                Conexao con = new Conexao();
                con.conexao.setAutoCommit(true);
                Statement st = con.conexao.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = null;

                st.execute("SELECT u.permissao FROM `bibliotec`.`usuarios` u WHERE u.usuario = '" + login.trim() + "';");
                rs = st.getResultSet();

                while (rs.next()) {
                    permissaoAcesso = rs.getInt("permissao");
                }

                if (permissaoAcesso != 1) {
                    session.invalidate();
                    res.sendRedirect(req.getContextPath() + "/acessoRestrito.xhtml");
                }
                //Se a permissão for de Bibliotecario, então deixa carregar nova página
                chain.doFilter(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
