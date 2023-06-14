package tgb.btc.rce.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@ComponentScan("tgb.btc.rce")
@EnableWebMvc
public class DispatcherServletConfiguration extends WebMvcConfigurerAdapter {

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("*/js/**", "*/js/*/**", "/js/**", "/js/*/**").addResourceLocations("/main/js", "/js/", "/", "/resources/")
//                .setCachePeriod(86400);
//    }
//
//    @Bean
//    public ViewResolver getViewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setViewClass(JstlView.class);
//        viewResolver.setSuffix(".jsp");
//        return viewResolver;
//    }
}
