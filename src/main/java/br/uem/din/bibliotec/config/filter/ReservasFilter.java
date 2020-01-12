package br.uem.din.bibliotec.config.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "Reservas", urlPatterns = {"/cadastrarReserva.xhtml",
                                                   "/cancelarReserva.xhtml",
                                                   "/minhasReservas.xhtml"})
public class ReservasFilter implements Filter {

    public ReservasFilter(){}

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException{

        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        HttpSession session = (HttpSession) req.getSession();

        String login = (String)session.getAttribute("usuario");

        if(login == null){
            res.sendRedirect(req.getContextPath() + "/gestaoBibliotecas.xhtml");
        }else{
            chain.doFilter(request, response);
        }
    }
}
