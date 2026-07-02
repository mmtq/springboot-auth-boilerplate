package com.mmtq.boilerplate.auth.filters;

import com.mmtq.boilerplate.auth.models.Session;
import com.mmtq.boilerplate.auth.models.User;
import com.mmtq.boilerplate.auth.repositories.UserRepository;
import com.mmtq.boilerplate.auth.services.SessionService;
import com.mmtq.boilerplate.auth.utils.AuthPrincipal;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionFilter extends OncePerRequestFilter {

    private final SessionService sessionService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = extractCookie(request, "sid");

        if (token != null) {
            try {
                Session session = sessionService.validate(token);

                request.setAttribute("currentSession", session);

                User user = userRepository.findById(session.getUserId())
                        .orElseThrow();

                AuthPrincipal principal = new AuthPrincipal(user, session);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                principal,
                                null,
                                List.of()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;

        for (Cookie c : req.getCookies()) {
            if (c.getName().equals(name)) {
                return c.getValue();
            }
        }
        return null;
    }
}