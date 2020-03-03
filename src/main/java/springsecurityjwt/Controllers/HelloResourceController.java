package springsecurityjwt.Controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springsecurityjwt.JwtUtils.JwtUtils;
import springsecurityjwt.Models.AuthenticationRequestModel;
import springsecurityjwt.Models.AuthenticationResponseModel;
import springsecurityjwt.Services.MyUserDetailService;


@RestController
public class HelloResourceController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;


    @Autowired
    private MyUserDetailService  myUserDetailService;


    @RequestMapping("/hello")
    public String getHello(){
       return"hello Word";

    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> getAuthenticated(@RequestBody AuthenticationRequestModel requestBody ) throws Exception {
        System.out.println(" request body ==> " +  requestBody);
     try{

         authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestBody.getUserName(), requestBody.getPassword()));

     }catch(BadCredentialsException err){
         throw new Exception(" == INCORRECT USER NAME OR PASSWORD ==  ",  err);
     }


     final UserDetails  userDetails = myUserDetailService.loadUserByUsername(requestBody.getUserName());
     final String  jwt = jwtUtils.generateToken(userDetails);

       return new ResponseEntity<>(new AuthenticationResponseModel(jwt).getJwt() , HttpStatus.OK);
    }


}
