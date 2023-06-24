package tgb.btc.rce.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                .antMatchers("/api/**").not().fullyAuthenticated()
                .antMatchers("/extJS/**").not().fullyAuthenticated()
                .antMatchers("/fontawesome/**").not().fullyAuthenticated()
                .antMatchers("/web/login/**").not().fullyAuthenticated()
                .antMatchers("/js/login/**").not().fullyAuthenticated()
                .antMatchers("/js/util/**").not().fullyAuthenticated()
                .antMatchers("/web/registration/**").not().fullyAuthenticated()
                .antMatchers("/js/registration/**").not().fullyAuthenticated()
                //Доступ только для пользователей с ролью Администратор
                .antMatchers("/web/settings/**").hasRole("ADMIN")
                .antMatchers("/js/settings/**").hasRole("ADMIN")
                //Доступ разрешен всем пользователей
//                .antMatchers("/", "/resources/**").permitAll()
                //Все остальные страницы требуют аутентификации
                .anyRequest().hasRole("ADMIN")
                .and()
                //Настройка для входа в систему
                .formLogin()
                .loginPage("/web/login/init")
                //Перенарпавление на главную страницу после успешного входа
                .defaultSuccessUrl("/")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/");
    }
}
