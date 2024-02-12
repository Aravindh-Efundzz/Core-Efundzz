package com.efundzz.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class FirebaseAuthenticationFilter implements Filter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String authorizationHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);

            String requestURI = httpRequest.getRequestURI();
            if (isPermitAllEndpoint(requestURI)) {
                chain.doFilter(request, response);
                return;
            }

            if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
                String idToken = authorizationHeader.substring(BEARER_PREFIX.length());

                try {
                    FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(decodedToken.getUid(), null, new ArrayList<>());

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (FirebaseAuthException e) {
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
            }

            chain.doFilter(request, response);
        }

    private boolean isPermitAllEndpoint(String requestURI) {
        // Use this method to check if the request URI matches any of your permit-all endpoints.
        return pathMatcher.match("/reference/**", requestURI) ||
                pathMatcher.match("/lead/save", requestURI); // Add your new pattern here
    }
}
