package br.com.dihnauer.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.dihnauer.todolist.users.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    var servletPath = request.getServletPath();

    if (servletPath.startsWith("/tasks/")) {
      // GET AUTHORIZATION (username and password)
      var authorization = request.getHeader("Authorization");
      var authCode = authorization.substring("Basic".length()).trim();
      byte[] authDecode = Base64.getDecoder().decode(authCode);

      // username:password
      var authString = new String(authDecode);

      // ["username", "password"]
      String[] credentials = authString.split(":");

      String username = credentials[0];
      String password = credentials[1];

      // VALIDATE USERNAME
      var user = this.userRepository.findByUsername(username);

      if (user == null) {
        response.sendError(401);
      } else {
        // VALIDATE PASSWORD
        var verifiedPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

        if (verifiedPassword.verified) {
          request.setAttribute("userId", user.getId());
          filterChain.doFilter(request, response);
        } else {
          response.sendError(401);
        }
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

}