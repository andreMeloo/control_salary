package config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class SessionFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Object atributo = httpRequest.getSession().getAttribute("usuarioAutenticado");
        // Verifica se o usuário possui uma sessão ativa
        if (atributo == null && httpRequest.getRequestURI().endsWith("/login.xhtml")) {
            chain.doFilter(request, response);
            return;
        } else if (atributo == null && !httpRequest.getRequestURI().endsWith("/login.xhtml")) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
            return;
        } else if (atributo != null && httpRequest.getRequestURI().endsWith("/login.xhtml")) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/pessoas_salario.xhtml");
            return;
        }

        // O usuário possui uma sessão ativa, continua o processamento normal da requisição
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
