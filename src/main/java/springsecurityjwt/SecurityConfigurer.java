package springsecurityjwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import springsecurityjwt.Filters.JwtFilter;
import springsecurityjwt.JwtUtils.JwtUtils;
import springsecurityjwt.Services.MyUserDetailService;


@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {



    @Override
    @Bean  // this is doing nothing just adapting with the version of java
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Autowired // fetching the user from the db
    private MyUserDetailService myUserDetailService;

    @Autowired
    JwtFilter jwtFilter;

    @Override // authenticating the user
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
             auth.userDetailsService(myUserDetailService);
    }

    @Override // authorizing the end point
    protected void configure(HttpSecurity auto) throws Exception {
         auto.csrf().disable()
                 .authorizeRequests().antMatchers("/authenticate")
                 .permitAll().anyRequest().authenticated()
                ///  telling spring do not create sessions
                .and()
                 .sessionManagement()
                 .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
         // applying the jwtFilter and
           auto.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return NoOpPasswordEncoder.getInstance();
    }
}
