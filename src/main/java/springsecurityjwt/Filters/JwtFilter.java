package springsecurityjwt.Filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import springsecurityjwt.JwtUtils.JwtUtils;
import springsecurityjwt.Services.MyUserDetailService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


 @Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private MyUserDetailService myUserDetailServices;

     @Autowired
     private JwtUtils jwtUtils;



    @Override // this function do the verification of all the incoming http requests
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


        String header = request.getHeader("Authorization");
        String userName = null;
        String token = null;
        // getting the token and the user name from the header
        if(header != null && header.startsWith("Bearer ")){
            token = header.substring(7);
            userName = jwtUtils.extractUserName(token);
        }
       // validating the token &&  the username  in the spring security
        if(userName != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = myUserDetailServices.loadUserByUsername(userName);
            if(jwtUtils.validateToken(token , userDetails)){
                // creating spring security token
                UsernamePasswordAuthenticationToken  userFromTokenDetail = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                // creating authentication source via request
                WebAuthenticationDetails requestAuthenticationSource = new WebAuthenticationDetailsSource().buildDetails(request);
                // updating the token with the requestSource
                userFromTokenDetail.setDetails(requestAuthenticationSource);
                // informing the spring security about the new user
                SecurityContextHolder.getContext().setAuthentication(userFromTokenDetail);

            }
        }
      // the filters do the filtering
     chain.doFilter(request,response);

    }
}
