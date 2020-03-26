package springsecurityjwt.Controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
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


    @GetMapping("/hello")
    public String getHello(){
       return"hello Word";

    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> getAuthenticated(@RequestBody AuthenticationRequestModel requestBody ) throws Exception {
        System.out.println(" request body ==> " +  requestBody);
     try{
         // checking if the user exist or not via the manager by using the token
         UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(  // creating spring security token
                 requestBody.getUserName(),
                 requestBody.getPassword()
         );
         authenticationManager.authenticate(userToken);

     }catch(BadCredentialsException err){
         throw new Exception(" == INCORRECT USER NAME OR PASSWORD ==  ",  err);
     }

     // we generate the token after the authorization is being processed in the try loop
     final UserDetails  userDetails = myUserDetailService.loadUserByUsername(requestBody.getUserName());
     final String  jwt = jwtUtils.generateToken(userDetails);

     // sending the token
       return new ResponseEntity<>(new AuthenticationResponseModel(jwt).getJwt() , HttpStatus.OK);
    }


}
