package doanjava1com.example.demo1.Security;

import doanjava1com.example.demo1.Services.UserDetailsServicelmpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    String arrPermitAll[]= new String[] {"/webjars/**","/","/verify"};

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return new UserDetailsServicelmpl();
    }

    @Bean
    protected DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("filter");
        http.authorizeHttpRequests(requests -> requests
                        .antMatchers(arrPermitAll).permitAll()
                        .antMatchers("/").permitAll()
                        .antMatchers("/register").permitAll()
                        .antMatchers("/process_register").permitAll()
                        .antMatchers("/users/profile").authenticated()
                        .antMatchers("/users/**").permitAll()
                        .antMatchers("/forgot_password").permitAll()
                        .antMatchers("/reset_password").permitAll()
                        .antMatchers("/API/**").permitAll()
                        .antMatchers("/clothes/").hasAnyAuthority("USER", "ADMIN")
                        .antMatchers("/clothes/new").hasAnyAuthority("ADMIN")
                        .antMatchers("/clothes/edit/**").hasAnyAuthority("ADMIN")
                        .antMatchers("/clothes/delete/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login").permitAll())
                .logout(logout -> logout.permitAll())
                .exceptionHandling(handling -> handling.accessDeniedPage("/403"))
                .csrf().disable();
        return http.build();
    }
}
