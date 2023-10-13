package tgb.btc.rce.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tgb.btc.rce.bean.Role;
import tgb.btc.rce.bean.WebUser;
import tgb.btc.rce.repository.RoleRepository;
import tgb.btc.rce.repository.WebUserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableAsync
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected WebUserRepository webUserRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authentication -> {
            String username = authentication.getPrincipal() + "";

            WebUser user = webUserRepository.getByUsername(username);
            if (user == null) {
                throw new BadCredentialsException("1000");
            }
            if (!user.isEnabled()) {
                throw new DisabledException("1001");
            }
            Set<Role> userRights = roleRepository.getByName(username);
            return new UsernamePasswordAuthenticationToken(username, authentication.getCredentials(), userRights.stream()
                    .map(x -> new SimpleGrantedAuthority(x.getName()))
                    .collect(Collectors.toList()));
        };
    };

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable();
        //  Доступ для всех
        httpSecurity
                .authorizeRequests()
                .antMatchers(
                        "/web/registration/**", "/static/**", "/extJS/**", "/fontawesome/**",
                        "/js/login/**", "/login/**",
                        "/js/util/**", "/js/registration/**",
                        "/js/api/**",
                        "/api/**",
                        "/loginSuccess", "/loginError",
                        "/css/**", "/web/main",
                        "/api/**", "/documentation/**"
                )
                .permitAll();
        // Доступ для юзеров
        httpSecurity
                .authorizeRequests()
                .antMatchers(
                        "/", "/js/mainUser/**"
                )
                .hasRole("USER");

        // Доступ для админов
        httpSecurity
                .authorizeRequests()
                .antMatchers(
                        "/js/main/**", "/web/main/**"
                )
                .hasRole("ADMIN");

        // Доступ всех оставшихся юрлов
        httpSecurity
                .authorizeRequests()
                //Все остальные страницы требуют аутентификации
                .anyRequest()
                .hasRole("ADMIN");

        // Конфигурация логина
        httpSecurity
                .formLogin()
                .loginPage("/web/main")
                //Перенарпавление на главную страницу после успешного входа
                .defaultSuccessUrl("/loginSuccess", true)
                .failureUrl("/loginError")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessUrl("/web/main");
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers();
    }
}
