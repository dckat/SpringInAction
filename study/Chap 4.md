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
   * 구현
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
     public void configure(AuthenticationManagerBuilder auth) throws Exception {
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
 
 * JDBC 기반 사용자 스토어
   * 사용자 정보는 관계형 DB로 유지.관리가 대다수 → JDBC를 사용하여 시큐리티 구성
   * 데이터 엑세스를 위한 dataSource() 메소드 호출 (Autowired 어노테이션 지정 시 DataSource 자동 지정)
   * 사용자 정보 탐색 시 시큐리티 내부에서 아래의 쿼리 수행
   ```
   public static final String DEF_USERS_BY_USERNAME_QUERY = 
        "select username, password, enabled " +
        "from users " +
        "where username = ?";
   public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
        "select username, authority " +
        "from authorities " +
        "where username = ?";
   public static final DEF_GROUP_AUTHORITIES_BY_USERNAME_QUERY =
        "select g.id, g.group_name, ga.authority " +
        "from authorities g, group_members gm, group_authorites ga " +
        "where gm.username = ? " +
        "and g.id = ga.group_id " +
        "and g.id = gm.group_id";
   ```
   * 스프링 시큐리티에서 내부적으로 생성되는 테이블
     * user: 사용자 정보
     * authorities: 권한 정보
     * group_member: 그룹 사용자
     * group_authorities: 그룹의 권한
   * 스프링 시큐리티의 기본 SQL을 대체 시 매개변수는 username 하나여야 함
     ```
     ...usersByUsernameQuery("select ... from ... where username=?");
     ```
   ※ 패스워드의 암호화
     * 스프링 시큐리티 5 버전부터 의무적으로 PasswordEncoder로 암호화 의무
     * PasswordEncoder 인터페이스를 구현한 클래스
       1) BCryptPasswordEncoder: bcrypt를 해싱 암호화
       2) NoOpPasswordEncoder: 암호화를 하지 않음
       3) Pbkdf2PasswordEncoder: PBKDF2를 암호화
 * LDAP 기반 사용자 스토어
   * LDAP(Lightweight Directory Access Protocol)
     * 네트워크 상에서 조직이나 개인, 파일, 디바이스 등을 찾아볼 수 있게 해주는 프로토콜
     * TCP/IP 위에서 운용되는 프로토콜
       
       ※ DAP: OSI계층 전체의 프로토콜을 지원 → 네트워크의 자원 소비가 많음
     * LDAP 모델
       1) information 모델: 데이터의 형태와 데이터를 디렉토리 구조로 정보를 저장하는 방식
       2) Naming 모델: 디렉토리 구조에서 각 Entry의 식별 및 구성에 대한 설명
       3) Functional 모델: 디렉토리에서 작업하는 명령
       4) Security 모델: 접근하는 사용자 인증과 데이터 접근 권한을 통한 서비스 보호
   * 스프링 시큐리티에서의 LDAP 구성
     * 원격 서버: contextSource() 메소드로 LDAP 서버의 위치 지정
     * 내장된 서버: spring에서 제공되는 ldap 의존성을 추가
   * 내장된 서버에서의 LDAP 접근
     * LDIF(LDAP Data Interchange Format): LDAP 데이터를 나타내는 표준화된 방법을 정의한 텍스트 파일
     * 스프링에서 ldif() 메소드를 통해 구현한 LDIF 파일위치 접근 (메소드 미사용시 classpath에서 탐색)
   * LDAP 기반 사용자 스토어를 위한 주요 메소드
     * userSearchFilter, groupSearchFilter: 사용자와 그룹 검색
     * userSearchBase, groupSearchBase: 사용자/그룹을 찾기 위한 기준점 쿼리 지정 (default: root)
 * 사용자 인증 커스터마이징
   * 스프링 시큐리티 내장스토어(인메모리, JDBC, LDAP)
     * 장점: 사용에 편리하여 일반적인 용도로 사용에 좋음
     * 단점: 사용자를 인증하는데 필요한 정보(이름.비밀번호.활성화 여부)만 보유 → 더 자세한 사용자 정보가 필요한 경우가 발생
   * JPA 기반 사용자 커스터마이징을 위한 단계
     1) 사용자 도메인 객체와 리퍼지터리 정의
     2) 사용자 명세 서비스 생성
     3) 사용자 등록 컨트롤러.뷰 구현
   * 사용자 도메인 객체 정의
     * UserDetails: 스프링 시큐리티에서 제공하는 사용자 정보 인터페이스
       * getAuthorities: 사용자에게 부여된 권한을 저장한 컬렉션 반환 메소드
       * is..Expired: 사용자 계정의 활성화 여부 반환 메소드
   * 사용자 명세 서비스 생성
     * UserDetailsService: 사용자 명세 서비스 구현을 위한 인터페이스
       * 정의
         ```
         public interface UserDetailsService {
          UserDetails loadUserByUsername(String username)
              throws UsernameNotFoundException;
         ```
***
## 4.3. 웹 요청 보안 처리하기
 * configure(HttpSecurity): 스프링 보안규칙을 구성하기 위한 메소드
 * HttpSecurity를 사용해서 구성할 수 있는 것
   1) HTTP 요청처리를 허용하기 위한 보안 조건 구성
   2) 커스텀 로그인 페이지 구성
   3) 로그아웃이 가능
   4) CSRF 공격으로 부터 보호
 * 웹 요청 보안 처리
   * configure 메소드 구성
   ```
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http
           .authorizeRequests()
           .antMatchers("/design", "/orders")
           .access("hasRole('ROLE_USER')")
           .antMatchers("/", "/**").access("permitAll");
       return http.build();
   }
   ```
    * authorizeRequests: URL 경로와 패턴 및 해당 경로의 보안 요구사항 구성
    * antMatchers: 지정된 경로와 패턴 일치를 검사 → 우선적으로 처리되어야 함
      
      (순서를 바꿀 시 모든 요청의 사용자에게 permit 적용)
    * 요청 경로가 보안처리 되는 방법을 정의하는 메소드
      * hasRole(String): 지정된 역할을 사용자가 가지고 있으면 접근 허용
      * permitAll(): 무조건 접근 허용 / denyAll(): 무조건 접근 거부
      * access(String): 인자로 전달된 SpEL 표현식이 true면 접근 허용
      * anonymous(): 익명의 사용자에게 접근 허용
      * authenticated(): 익명이 아닌 사용자로 인증인 경우 접근 허용
      * rememberMe(): 이전 로그인정보를 쿠키에 저장 후 일정 기간 내에 접근이 저장된 정보로 재로그인시 접근 허용
    * 스프링 시큐리티에서 확장된 SpEL(스프링 표현식 언어)
      * authentication: 해당 사용자의 인증 객체
      * hasRole(역할): 지정된 역할을 사용자가 가지고 있으면 true
      * permitAll: 항상 true / denyAll: 항상 false
      * isAnonymous(): 익명 사용자이면 true
      * isAuthenticated(): 익명이 아닌 사용자로 인증되면 true
 * CSRF 공격 방어
   * CSRF(Cross-Site Request Forgey)
     * 웹사이트에 악의적인 코드를 삽입하고 이를 폼으로 제출하여 공격에 노출되도록 하는 보안 공격
       ![](../../../Downloads/Csrf1.jpg)
       ex) SNS 계정에서 발생되는 피싱사이트 광고: 피싱사이트에 글쓰기 폼 삽입 후 특정사용자의 작성글에 등록
   * 스프링 시큐리티에서 CSRF 구현 및 처리단계
     1) 공격방어를 위해 폼의 숨김 필드에 CSRF 토큰 생성
     2) 폼 제출 시 데이터와 함께 토큰도 서버에 전송
     3) 서버에서 전송된 토큰을 원래 생성된 토큰과 비교
     4) 토큰이 일치할 시 해당 요청 처리 허용 (일치하지 않을 시 악의적인 웹사이트에서 제출된 것으로 간주)
***
## 4.4. 사용자 인지하기
 * 사용자 인지를 위해 주로 사용되는 방법
   * Principal 객체를 컨트롤러 메소드에 주입
   * Authentication 객체를 컨트롤러 메소드에 주입
   * SecurityContextHolder 활용 보안 컨텍스트 획득
   * AuthenticationPrincipal 어노테이션을 메소드에 적용
 * Principal 객체를 활용한 사용자 인지
   * Principal 객체: 로그인한 사용자가 누구인지를 알기 위해 사용하는 객체 
   ```
   public String processOrder(@Valid Order order, Errors errors, 
      SessionStatus sessionStatus, Principal principal) {
    
      ...
      User user = userRepository.findByUsername(principal.getName());
      order.setUser(user);
      ...
   }
   ```
   * 보안과 관련없는 코드가(findByUsername 메소드) 혼재한다는 문제점 존재
 * Authentication 객체를 활용한 사용자 인지
   ```
   public String processOrder(@Valid Order order, Errors errors, 
      SessionStatus sessionStatus, Authentication authentication) {
    
      ...
      User user = (User) authentication.getPrincipal();
      order.setUser(user);
      ...
   }
   ```
   * getPrincipal 메소드를 활용하여 Principal 객체 반환 (반환 타입: Object)
   * 사용자 클래스로 별도의 캐스팅 작업이 필요
 * SecurityContextHolder 활용 보안 컨텍스트 획득 후 사용자 인지
   ```
   Authentication authentication = 
      SecurityContextHolder.getContext().getAuthentication();
   User user = (User) authentication.getPrincipal();
   ```
   * 보안 특정 코드가 다른 코드에 비해 많음
   * 해당 보안 컨텍스트를 애플리케이션 어느곳에서든 사용가능
 * AuthenticationPrincipal 어노테이션을 메소드에 주입
   * AuthenticationPrincipal: 인증된 사용자 정보를 가져오기 위한 어노테이션
   * 구현 코드
   ```
   public String processOrder(@Valid Order order, Errors errors, 
      SessionStatus sessionStatus,
      @AuthenticationPrincipal User user) {
    
      ...
      order.setUser(user);
      ...
   }   
   ```
   * 별도의 타입변환 없이 사용자 정보를 가져올 수 있음
   * 특정한 보안 코드만 가질 수 있음
***