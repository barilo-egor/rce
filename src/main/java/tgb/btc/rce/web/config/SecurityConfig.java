package tgb.btc.rce.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tgb.btc.rce.repository.RoleRepository;
import tgb.btc.rce.repository.WebUserRepository;

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
                        "/js/main/**", "/web/main/**", "/web/roles"
                )
                .hasRole("ADMIN");

        // Доступ для операторов
        httpSecurity
                .authorizeRequests()
                .antMatchers(
                        "/js/main/**", "/web/main/**", "/web/roles"
                )
                .hasRole("OPERATOR");

        // Доступ всех оставшихся юрлов
        httpSecurity
                .authorizeRequests()
                //Все остальные страницы требуют аутентификации
                .anyRequest()
                .hasAnyRole("ADMIN", "OPERATOR");

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
