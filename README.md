# spring_security_demo
**Database Authentication** \
1. Create a class and extend UserDetails class.
```
public class ApplicationUserDetails implements UserDetails {
    private final Set<? extends GrantedAuthority> grantedAuthorities;
    private final String password;
    private final String username;
    private final boolean isAccountNonExpired;
    private final boolean isAccountNonLocked;
    private final boolean isCredentialsNonExpired;
    private final boolean isEnabled;

    public ApplicationUserDetails(Set<GrantedAuthority> grantedAuthorities, String password, String username, boolean isAccountNonExpired, boolean isAccountNonLocked, boolean isCredentialsNonExpired, boolean isEnabled) {
        this.grantedAuthorities = grantedAuthorities;
        this.password = password;
        this.username = username;
        this.isAccountNonExpired = isAccountNonExpired;
        this.isAccountNonLocked = isAccountNonLocked;
        this.isCredentialsNonExpired = isCredentialsNonExpired;
        this.isEnabled = isEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
```
2. Create a DAO interface.
```
public interface ApplicationUserDao {
    Optional<ApplicationUserDetails> selectApplicationUserByUsername(String username);
}
```
3. Create an implementation class of DAO interface above to connect to database and fetch users. \
Below is just an example containing in memory users. You can replace it with real database and fetch users from database.
```

@Repository("fake")
public class FakeApplicationUserDaoImpl implements ApplicationUserDao {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUserDetails> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUserDetails -> username.equalsIgnoreCase(applicationUserDetails.getUsername()))
                .findFirst();
    }

    private List<ApplicationUserDetails> getApplicationUsers(){
        List<ApplicationUserDetails> applicationUsers = Lists.newArrayList(
                new ApplicationUserDetails(
                        STUDENT.getGrantedAuthorities(),
                        passwordEncoder.encode("student"),
                        "student",
                        true,
                        true,
                        true,
                        true
                        ),

                new ApplicationUserDetails(
                        ADMIN.getGrantedAuthorities(),
                        passwordEncoder.encode("admin"),
                        "admin",
                        true,
                        true,
                        true,
                        true
                ),

                new ApplicationUserDetails(
                        ADMINTRAINEE.getGrantedAuthorities(),
                        passwordEncoder.encode("admintrainee"),
                        "admintrainee",
                        true,
                        true,
                        true,
                        true
                )
        );

        return applicationUsers;
    }
}

```
4. Create a class and implement UserDetailsService interface and call the dao here.
```

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public ApplicationUserDetailsService(@Qualifier("fake") ApplicationUserDao applicationUserDao) {
        this.applicationUserDao = applicationUserDao;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserDao
                .selectApplicationUserByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException(String.format("username %s not found", username))
                );
    }
}

```
5. In ApplicationSecurityConfig class, create a bean method to return DaoAuthenticationProvider and then ovveride the configure method to configure the DaoAuthenticationProvider.
```
 @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserDetailsService);

        return provider;
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }
```
  
