package com.example.securityApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    @Bean
   public ReloadableResourceBundleMessageSource messageSource(){
       ReloadableResourceBundleMessageSource source=new ReloadableResourceBundleMessageSource();
       source.setDefaultEncoding("UTF-8");
       source.setBasename("classpath:messages");
       return source;
    }

    @Bean
    public LocaleResolver localeResolver()
    {
        CookieLocaleResolver localeResolver=new CookieLocaleResolver();
        localeResolver.setCookieMaxAge(3600*24);
        localeResolver.setDefaultLocale(new Locale("eng"));
        localeResolver.setCookieName("yazyk");
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeInterceptor()
    {
        LocaleChangeInterceptor localeChangeInterceptor=new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lng");
        return localeChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(localeInterceptor());
    }

}

