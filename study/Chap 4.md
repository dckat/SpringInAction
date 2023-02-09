# Chap 4. 스프링 시큐리티
## 4.1. 스프링 시큐리티 활성화하기
 * 의존성 활성화 (pom.xml 기준)
 ```
 <dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
 </dependency>
 <dependency>
	<groupId>org.springframework.security</groupId>
	<artifactId>spring-security-test</artifactId>
	<scope>test</scope>
 </dependency>
 ```
 * 의존성
   * spring-boot-starter-security: 스프링 부트 시큐리티 의존성
   * spring-security-test: 시큐리티 테스트 의존성
 
 * 의존성 추가 후 실행 시 http 인증 대화상자 발생
   * 초기 설정 아이디: user
   * 초기 설정 비밀번호: 무작위 36자리 비밀번호 (로그에 생성)
     ```
      Using genereated security password: (36자리 비밀번호)
     ```
 * 스프링 시큐리티가 자동으로 제공하는 보안구성
   * 모든 HTTP 요청 경로는 인증되어야 함
   * 어떤 특정 역할이나 권한이 없음
   * 로그인 페이지가 별도로 없음
   * 스프링 시큐리티의 기본인증(대화상자)을 사용해서 인증
   * 사용자는 하나만 존재하며 이름은 user (비밀번호는 암호화)
 
 * 사용자 기반 애플리케이션을 위한 보안 구성 요소
   * 인증 대화상자가 아닌 로그인 페이지를 별도 구성
   * 다수의 사용자 제공, 사용자를 등록할 수 있는 페이지 구성
     * 다른 HTTP 요청경로에 따라 다른 보안 규칙 적용
        
       ex) 홈페이지 / 사용자 등록 페이지는 인증 필요 X
***
## 4.2. 스프링 시큐리티 구성하기
 * 스프링 시큐리티 기본 구성 클래스하기
   * 스프링 시큐리티 5.7 주요 변경 사항
     * WebSecurityConfigureAdapter를 지원하지 않음 -> configure를 오버라이딩하여 사용 불가
       
       (이유) 컴포넌트 기반 시큐리티(component-based security)를 위함
       
     * 5.7 이후부터는 SecurityChain을 Bean 등록하여 시큐리티 구성
     * 5.7 이전
     ```
      public class SecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
           http
           .authorizeRequests()
           .antMatchers("/design", "/orders")
           .access("hasRole('ROLE_USER')")
           .antMatchers("/", "/**").access("permitAll")
           .and()
           .httpBasic();
        }
     }
     ```
     * 5.7 이후
     ```
     public class SecurityConfig {
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
           http
           .authorizeRequests()
           .antMatchers("/design", "/orders")
           .access("hasRole('ROLE_USER')")
           .antMatchers("/", "/**").access("permitAll")
           .and()
           .httpBasic();
           return http.build();
        }
     }
     ```
   * 스프링 시큐티티 구성 클래스: 사용자의 HTTP 요청 경로에 대한 접근 제한 등의 보안 처리 설정
 * 한 명 이상의 사용자 처리를 위한 사용자 스토어 구성 방법
    1) 인메모리 사용자 스토어
    2) JDBC 기반 사용자 스토어
    3) LDAP 기반 사용자 스토어
    4) 커스텀 사용자 명세 서비스

 * 인메모리 사용자 스토어
   * 사용자 정보를 코드 내부에 정의하여 유지.관리하는 기법
   * 5.7 이전
   ```
   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user1")
            .password("{noop}password1")
            .authorities("ROLE_USER");
   }
   ```
   * 5.7 이후
   ```
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user1")
                .password("{noop}password1")
                .authorities("ROLE_USER")
                .and()
                .withUser("user2")
                .password("{noop}password2")
                .authorities("ROLE_USER");
    }
   ```
   * 주요 메소드 및 클래스
     * AuthenticationManagerBuilder: 인증 명세를 구성하기 위한 빌더클래스
     * inMemoryAuthentication: 인메모리 사용자 스토어를 위한 메소드
     * withUser: 사용자 구성 시작 / authorities: 사용자의 권한 설정 / and: 여러 사용자 지정가능
   * 장.단점
     * 장점: 테스트 목적이나 간단한 애플리케이션에 사용하기 편리
     * 단점: 사용자의 추가 및 변경이 어려움 (코드를 수시로 변경하여 빌드하고 배포 필요)