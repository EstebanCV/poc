package com.example.prueba.security;

import com.example.prueba.dto.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

@Configuration
public class JwtTokenConfig extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JwtTokenConfig.class);

    @Value("${jwt.key}")
    private String SECRET_KEY;
    @Value("${jwt.expiration}")
    private Long JWT_EXPIRATION;

    // se ejecuta para cada solicitud, analiza y valida JWT por el token entrante
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getAccessToken(request);

        if (token != null && validateAccessToken(token)) {
            UserDetails userDetails = getUserDetails(token);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getAccessToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");

        if (StringUtils.hasText(header) && header.startsWith("Bearer "))
            return header.substring(7);
        else
            return null;
    }

    //valida token jwt
    private boolean validateAccessToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
            return true;
        } catch (MalformedJwtException e) {
            LOG.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOG.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOG.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOG.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    //obtiene el users mediante el token
    private UserDetails getUserDetails(String token) {
        Users userDetails = new Users();
        String subject = Jwts.parserBuilder()
                .setSigningKey(key()).build()//se pasa la clave de firma, q es la llave secreta
                .parseClaimsJws(token)//pasa token para ser analizado
                .getBody()
                .getSubject();

        //sacar el asunto q es ID + email
        String[] jwtSubject = subject.split(",");

        userDetails.setId(Long.valueOf(jwtSubject[0]));
        userDetails.setEmail(jwtSubject[1]);

        return userDetails;
    }

    // genera el token
    public String generateAccessToken(Users u) {
        return Jwts.builder()
                .setSubject(String.format("%s,%s", u.getId(), u.getEmail())) //asunto
                .setIssuer("Esteban") //emisor
                .setIssuedAt(new Date()) //fecha actual
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION)) //expiracion sera hora actual en milisegundos + 3 min
                .signWith(key(), SignatureAlgorithm.HS256) //indica algoritmo a utilizar y clave secreta
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    //obtiene el user logeado del contexto de seguridad de spring
    public Users getUserConect(){
        return (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
