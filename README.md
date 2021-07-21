# spring_security_demo
**spring-boot-starter-security**
Just by adding spring security dependency, we get a default login page with username and password.
To access any resource we need to provide username and password.\
**username**: user \
**password**: generated by spring security

**Implement Basic Auth Security**
1. Create a class, annotate it with @Configuration and @EnableWebSecurity and extend WebSecurityConfigurerAdapter.
2. Overrid configure(HttpSecurity http) and add below code.
   ```
    protected void configure(HttpSecurity http) throws Exception {
        // any request should be authenticated i.e user should provide username and password and mechanism should be basic auth
        // antMatchers --> to allow access to home page like index to all users without any username and password
        // antMatchers("/api/**").hasRole(STUDENT.name()) --> Only allow user who have a role of STUDENT to access this API

        http.authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }```

**Code Explaination** \
http.authorizeRequests("authorize the request").anyRequest("any request").authenticated("should be authenticated").and("and also").httpBasic("mechanism should be basic auth");
