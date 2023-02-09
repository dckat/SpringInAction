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