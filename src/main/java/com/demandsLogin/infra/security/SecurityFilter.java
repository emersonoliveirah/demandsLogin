package com.demandsLogin.infra.security;

import com.demandsLogin.domain.user.User;
import com.demandsLogin.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;

    private static final List<String> PUBLIC_PATHS = List.of("/auth/register", "/auth/login");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();

        // Skip token validation for public paths
        if (PUBLIC_PATHS.contains(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = this.recoverToken(request);
        var login = tokenService.validateToken(token);

        if (login != null) {
            User user = userRepository.findById(login).orElseThrow(() -> new RuntimeException("User Not Found"));
            String userRole = user.getRole().name(); // Ex: "MANAGER"
            var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole));
            var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // --- THIS LINE IS THE FIX ---
            request.setAttribute("userId", user.getId()); // or user.getUserId()
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}