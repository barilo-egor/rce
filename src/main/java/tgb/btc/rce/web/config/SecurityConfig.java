package tgb.btc.rce.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                //Доступ только для не зарегистрированных пользователей
                .antMatchers("/web/registration/**", "/static/**", "/extJS/**", "/fontawesome/**",
                        "/js/login/**", "/js/util/**", "/js/registration/**", "/login/**", "/api/**",
                        "/loginSuccess", "/loginError", "/").permitAll()
                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/web/settings/**", "/js/settings/**", "/js/main/**",
                        "/web/main/**").hasRole("ADMIN")
                //Все остальные страницы требуют аутентификации
                .anyRequest().hasRole("ADMIN")
                .and()
                //Настройка для входа в систему
                .formLogin()
                .loginPage("/login")
                //Перенарпавление на главную страницу после успешного входа
                .defaultSuccessUrl("/loginSuccess", true)
                .failureUrl("/loginError")
                .permitAll()
                .and()
                .logout()
                .logoutUrl("/logout")
                .permitAll()
                .logoutSuccessUrl("/login");
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers();
    }
}
