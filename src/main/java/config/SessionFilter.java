package config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletRequest;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;

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

        // Verifica se o usuário está tentando acessar a página de login
        if (httpRequest.getRequestURI().endsWith("/login.xhtml")) {
            // O acesso à página de login é permitido, continua o processamento normal da requisição
            chain.doFilter(request, response);
            return;
        }

        Object atributo = httpRequest.getSession().getAttribute("usuarioAutenticado");
        // Verifica se o usuário possui uma sessão ativa
        if (atributo == null) {
            // Redireciona para a página de login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
            return;
        }

        // O usuário possui uma sessão ativa, continua o processamento normal da requisição
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
