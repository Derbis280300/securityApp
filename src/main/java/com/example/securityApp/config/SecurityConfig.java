package com.example.securityApp.config;


import com.example.securityApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true,prePostEnabled = true,proxyTargetClass = true)//аннотация которая защищает методы контроллера
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {//which page can access which not
       http.exceptionHandling().accessDeniedPage("/accessDenied");//403

        http.
               authorizeRequests().antMatchers("/css/**","/js/**").permitAll();
//
        http.formLogin()
                .loginProcessingUrl("/toLogin").permitAll()
                .loginPage("/login").permitAll()
                .usernameParameter("u_email")
                .passwordParameter("u_password")
                .defaultSuccessUrl("/profile")
                .failureUrl("/login?error");
///login?error
        http.logout().logoutSuccessUrl("/login")
                .logoutUrl("/sign_out").permitAll();
        http.csrf().disable();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();//шифровать пароли
    }



}


