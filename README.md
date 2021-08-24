# spring_security_demo

# Learning Path
**For Complete Understanding, Read through docs and code of all branches in below order** \
1. role_based_authentication
2. permission_based_authentication
3. database_authentication
4.  java_json_web_tokens


**Form Based Authentication**
1. Add below code in configure method.
 ```
 @Override
    protected void configure(HttpSecurity http) throws Exception {
        // any request should be authenticated i.e user should provide username and password and mechanism should be basic auth
        // antMatchers --> to allow access to home page like index to all users without any username and password
        // antMatchers("/api/**").hasRole(STUDENT.name()) --> Only allow user who have a role of STUDENT to access this API

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(STUDENT.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll();
    }
 ```
 .formLogin() to enable form based authentication. \
 .loginPage("/login").permitAll() to redirect to custom login page
 
 2. Add below dependancy.
  ```
  <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
			<version>2.5.1</version>
		</dependency>
  ```
  3. Create Controller for custom login
```
@Controller
@RequestMapping("/")
public class TemplateController {

    @GetMapping("login")
    public String getLogin(){
        return "login";
    }
}
```
**Redirect After Login Success to Some Page**
1. Create a method for view in TemplateController.
```
@GetMapping("courses")
    public String getCourses(){
        return "courses";
    }
```
2. Create a courses html page
3. Add below code in configure method after permit all like below.
```
 .loginPage("/login").permitAll()
 .defaultSuccessUrl("/courses", true);
```

  
