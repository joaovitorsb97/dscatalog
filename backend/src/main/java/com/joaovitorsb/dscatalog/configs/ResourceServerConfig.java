package com.joaovitorsb.dscatalog.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private JwtTokenStore tokenStore;

    private static final String[] PUBLIC = { "/oauth/token" };

    private static final String[] OPERATOR_OR_ADMIN = { "/products/**", "/categories/**" };

    private static final String[] ADMIN = { "/users/**" };

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenStore(tokenStore);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
        .antMatchers(PUBLIC).permitAll() //Everybody can access vector PUBLIC routes
        .antMatchers(HttpMethod.GET, OPERATOR_OR_ADMIN).permitAll() //Permit only method GET for all at vector OPERATOR_OR_ADMIN routes
        .antMatchers(OPERATOR_OR_ADMIN).hasAnyRole("OPERATOR", "ADMIN") //Only entity with roles "OPERATOR or "ADMIN" can access vector OPERATOR_OR_ADMIN routes
        .antMatchers(ADMIN).hasRole("ADMIN") //Only entity with role "ADMIN" can access vector ADMIN routes
        .anyRequest().authenticated(); //Other routes needs to be authenticated
    }
}
