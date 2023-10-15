package br.com.sertech.todolist.filter;


import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.sertech.todolist.repository.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

import static at.favre.lib.crypto.bcrypt.BCrypt.*;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository  userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var servletPath = request.getServletPath();
        System.out.println(servletPath);
        if(servletPath.startsWith("/tasks/")){
            //pegar a autenticação
            var authorization = request.getHeader("Authorization");
            var authEncoded = authorization.substring("Basic".length()).trim();
            byte[] authDecode = Base64.getDecoder().decode(authEncoded);

            var authString = new String(authDecode);
            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];
            //validar Usuário
            var user = this.userRepository.findByUsername(username);
            if(user == null){
                response.sendError(401);
            }else{
                //validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(),user.getPassword());
                if ( passwordVerify.verified){
                    //segue viagem
                    request.setAttribute("idUser",user.getId());
                    filterChain.doFilter(request,response);
                }
                response.sendError(401);
            }
        }else {
            filterChain.doFilter(request,response);
        }

    }
}
