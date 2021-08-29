# spring_security_demo

# Learning Path
**For Complete Understanding, Read through docs and code of all branches in below order** \
1. role_based_authentication
2. permission_based_authentication
3. database_authentication
4.  java_json_web_tokens

**What is JWT**
JWT is a way between two applications to communicate. It is mostly used for authorization. \
Client sends credentials, server authenticate the user and sends a token to user/client. Client then sends that token to server to access the resource. It checks the token if it is valid and if it is, it will check the permissions, if the client have the specific permission to access that resource then it will give back the resoucre.

**Authentication**
Verifies you are who you say you are -> e.g username and password \

Common Methods.
1. Login Form
2. HTTP authentication
3. Custom auth. method

**Authorization**
Decides if you have a permission to access a resource.
E.g for ADMIN application looks different and ADMIN have more permissions and he can do more things as compared to other users

**Implement Java JWT** \
**Validating Credentials and returning Token to client in Response Header** \
1. Add below depedencies in pom.xml
```
<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.2</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.2</version>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId> <!-- or jjwt-gson if Gson is preferred -->
			<version>0.11.2</version>
			<scope>runtime</scope>
		</dependency>
```

2. Create UsernameAndPasswordAuthenticationRequest class to model credentials passed in request body by the user.
```
public class UsernameAndPasswordAuthenticationRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```
4. Create a class and extend UsernamePasswordAuthenticationFilter interface.
```

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        Authentication authentication;

        try {
            UsernameAndPasswordAuthenticationRequest authenticationRequest =
                    new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);
            authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // validate username and password
        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // called after attemptAuthentication is successful

        String key = "secretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkey"; // key should be very long
        String token = Jwts.builder()
                .setSubject(authResult.getName()) // add sub to payload
                .claim("authorities", authResult.getAuthorities()) // adding authorities to payload
                .setIssuedAt(new Date()) // token issued at date
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusWeeks(1))) // token expiration date
                .signWith(Keys.hmacShaKeyFor(key.getBytes())) // signature key
                .compact();

        response.addHeader("Authorization", "Bearer " + token); // add token to response header
    }
}


```
4. In security configuration class, add filter in configure method
```
@Override
    protected void configure(HttpSecurity http) throws Exception {
        // any request should be authenticated i.e user should provide username and password and mechanism should be basic auth
        // antMatchers --> to allow access to home page like index to all users without any username and password
        // antMatchers("/api/**").hasRole(STUDENT.name()) --> Only allow user who have a role of STUDENT to access this API

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Because Tokens are stateless
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager())) // Filters authenticate requests before accessing resources
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .anyRequest()
                .authenticated();
    }
```

**Validate Token passed in subsequent requests**
1. Create a second filter class to validate the above token passed in subsequent requests.
```

import com.google.common.base.Strings;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtTokenVerifier extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String key = "secretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkeysecretkey"; // same key as used in token generation
        String token = authorizationHeader.replace("Bearer ", "");

        try {
            Jwt parse = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
                    .build()
                    .parse(token);

            Claims body = (Claims) parse.getBody();
            String username = body.getSubject();
            List<Map<String, String>> authorities = (List<Map<String, String>>) body.get("authorities");

            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                    .map(m -> new SimpleGrantedAuthority(m.get("authority")))
                    .collect(Collectors.toSet());

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    simpleGrantedAuthorities
            );

            // This will now validate the token
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            // Don't trust the token as validation above is failed
            throw new IllegalStateException(String.format("Token %s cannot be trusted", token) + ", " + e.getMessage());
        }

        // Now we need to pass the request to next filter in chain
        // in our case as there is no filter after this so user will get the expected resource i.e endpoint results that the client sent request on
        filterChain.doFilter(request,response);
    }
}


```
2. Add this filter in configure method of the ApplicationSecurityConfig class (Use addFilterAfter like below).
```
@Override
    protected void configure(HttpSecurity http) throws Exception {
        // any request should be authenticated i.e user should provide username and password and mechanism should be basic auth
        // antMatchers --> to allow access to home page like index to all users without any username and password
        // antMatchers("/api/**").hasRole(STUDENT.name()) --> Only allow user who have a role of STUDENT to access this API

        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Because Tokens are stateless
                .and()
                .addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager())) // Filters authenticate requests before accessing resources
                .addFilterAfter(new JwtTokenVerifier(), JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .anyRequest()
                .authenticated();
    }
```
