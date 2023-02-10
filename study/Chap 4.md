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