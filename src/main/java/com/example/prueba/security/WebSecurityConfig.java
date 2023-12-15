package com.example.prueba.security;

import com.example.prueba.dto.Users;
import com.example.prueba.helper.Utils;
import com.example.prueba.repository.UsersRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

//configurar la seguridad de spring
@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    private final UsersRepository userRepo;
    private final JwtTokenConfig jwtTokenConfig;
    private final Utils utils;

    @Autowired
    public WebSecurityConfig(UsersRepository userRepo, JwtTokenConfig jwtTokenConfig, Utils utils){
        this.userRepo = userRepo;
        this.jwtTokenConfig = jwtTokenConfig;
        this.utils = utils;
    }

    //configuramos si queremos a exigir a todos autenticacion
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) //proteccion para ataques maliciosos
                //maneja excepciones de autenticacion
            .exceptionHandling(exception -> exception.authenticationEntryPoint(
                (request, response, ex) -> {
                    utils.validateTokenExpired(request, userRepo);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{ \"mensaje\" : \"token de acceso invalido o caducado\" }");
                }
            ))
                //nunca creara una session xq no la usara
            .sessionManagement(session -> session.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
            ))
                //valida path q necesitan seguridad
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(antMatcher("/auth/token")).permitAll()
                .requestMatchers(antMatcher("/demo/users/registro")).permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers(antMatcher("/swagger-ui/**")).permitAll()
                .requestMatchers(antMatcher("/v3/**")).permitAll()
                .anyRequest().authenticated()
            );

        //permite cualquier solicitud del mismo origen 'consola h2'
        http.headers(header -> header.frameOptions(frame -> frame.sameOrigin()));
        // proveedor de autenticacion para recuperar al usuario logueado
        http.authenticationProvider(authenticationProvider());
        //agrega un filtro, en este caso el aplicado por el jwt
        http.addFilterBefore(jwtTokenConfig, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // proveedor de autenticacion para recuperar al usuario logueado
    //devuelve la instancia de una clase
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    //creo objeto userDetail y utilizo el modo loadUser para verificacion/autenticacion de el user ingresado en el login
    // bean devuelve la instancia de una clase
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                Users userDB = userRepo.findByEmail(username).orElseThrow(() -> new ValidationException("Usuario "+ username +" no existe"));
                return new Users(userDB.getId(), userDB.getEmail(), utils.encodePassword(userDB.getPassword()));
            }
        };
    }

    //se agrega al proveedor de autenticacion una codificacion de contraseña para verificar credenciales
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //para validar credenciales y retornar al user logeado
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
