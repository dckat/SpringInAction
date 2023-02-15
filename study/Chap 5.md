# 5. 구성 속성 사용하기
## 개요
* 자동-구성: 스프링 애플리케이션 개발의 단순화 가능
* (과거) 스프링 XML 구성으로 속성값을 설정 → 빈 구성 없이 속성 설정 불가
* (최근) 스프링 부트: 구성 속성을 사용하는 법을 제공
    * 빈의 속성, JVM 시스템 속성, 명령행 인자, 환경변수 등의 속성 설정
***
## 5.1. 자동-구성 세부 조정하기
* 스프링에서의 두 가지 형태의 구성
    * 빈 연결: 스프링 애플리케이션 컨텍스트에서 빈으로 생성되는 애플리케이션 컴포넌트 및 상호 간 주입되는 방법을 선언하는 구성
    * 속성 주입: 스프링 애플리케이션 컨텍스트에서 빈의 속성 값을 설정
* 스프링 환경 추상화
    * 개념: 구성 가능한 모든 속성을 한 곳에서 관리
    * 스프링 환경에서 가져오는 원천 속성
        * JVM 시스템 속성
        * 운영체제의 환경변수
        * 명령행 인자
        * 애플리케이션의 속성 구성 파일 (application.properties, application.yml)
    * 애플리케이션의 서블릿 컨테이너의 포트 설정 지정 가능
* 데이터 소스 구성
    * DataSource 빈을 명시적으로 구성 가능 → 스프링 부트 사용 시에는 필요 X
    * 구성 속성을 통해 데이터베이스의 URL.인증을 구성 → DataSource 빈 구성시 해당 속성 설정을 연결 데이터로 사용
    * 명시적인 데이터 소스 구성 대신 JNDI에 구성하는 방법도 가능
        * JNDI: 디렉터리 서비스에서 제공하는 데이터 및 객체를 발견하고 참고하기 위한 자바 API
            * 용도: 자바 애플리케이션의 데이터베이스 or LDAP 서버 연결
            * 지정된 데이터베이스 정보에 JNDI 설정 → 애플리케이션에서 JNDI 호출하여 사용
* 내장 서버 구성
    * HTTPS 요청 처리를 위한 컨테이너 설정
        * 선행: JDK keytool 명령행 유틸리티로 키스토어를 생성
      ```
      $ keytool -keystore mykeys.jks -genkey -alias tomcat -keyalg RSA
      ```
        * 키스토어 생성 후 HTTPS 활성화를 위해 속성을 설정
* 로깅 구성
    * 기본적으로 INFO 수준으로 콘솔에 메시지를 쓰기 위해 Logback을 통한 로깅 구성
    * 로깅 구성을 변경하기 위해 logback.xml 파일을 생성
        * 스프링 부트의 자동-구성 속성 활용 시 생성하지 않고도 로깅 구성 변경 가능
* 다른 속성의 값 불러오기
    * 하드코딩 된 String 또는 숫자로 속성 값 설정 필요 X
***
## 5.2. 구성 속성 생성하기
 * 전제조건: 구성 속성은 빈의 속성을 나타내는 것
 * ConfigurationProperties: 구성 속성의 주입을 위한 어노테이션
 * 구성 속성 활용 예시 (클래스)
 ```
 // 컨트롤러에 구성-속성(페이지) 구현 (기본값: 20)
 ...
 @ConfigurationProperties(prefix="taco.orders")
 public class OrderController {
    private int pageSize = 20;
    
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    ...
    
    @GetMapping
    public String ordersForUser(...) {
        Pageable pageable = PageRequest.of(0, pageSize);
        ...
    }
    ...
 }
 ```
 * application.yml 파일을 활용하여 속성 설정 가능
 ```
 taco:
   orders:
     pageSize: 10
 ```
 * 구성 속성 홀더 정의
   * ConfigurationProperties 어노테이션을 특정 컨트롤러(또는 빈)에만 사용되는 것 X
   * 클래스 외부에서 구성 관련 정보 유지 가능 → 여러 빈에 공통적인 속성 공유 가능
   * 홀더 클래스 구현 예시
   ```
   @Component
   @ConfigurationProperties(prefix="taco.orders")
   @Data
   public class OrderProps {
        private int pageSize = 20;
   }
   ```
   * 홀더 클래스 구현으로 변경된 컨트롤러 클래스
   ```
   ...
   public class OrderController {
     private OrderProps props;
   
     private OrderRepository orderRepo;
   
     public OrderController(OrderRepository orderRepo,
      OrderProps props) {
       this.orderRepo = orderRepo;
       this.props = props;
     }

     @GetMapping
     public String ordersForUser(...) {
       Pageable pageable = PageRequest.of(0, props.getPageSize());
       ...
     }
     ...
   }
   ```
 * 구성속성 메타데이터 선언
   * application.yml에서 unknown이 뜨는 경우 → 구성속성에 관한 메타데이터 존재 X
   * 메타데이터를 선언하는 과정
     1) 의존성으로 spring-boot-configuration-processor(스프링 부트 구성 처리기) 추가
     2) metadata.json 파일 생성 후 메타데이터 작성
***
## 5.3. 프로파일 사용해서 구성하기
 * 애플리케이션이 다른 런타임에서 배포될 때 구성 명세가 달라지는 경우 발생 (DB 연결 명세 등)
 * 운영체제의 환경변수를 사용해서 구성
   * 여러 구성속성을 지정할 시 번거로운 문제
   * 환경 변수을 추적 관리하거나 오류 발생 시 변경 전으로 되돌리는 데 어려움
 * 스프링 프로파일
   * 런타임 시 활성화 되는 프로파일에 따라 서로 다른 빈.클래스.구성 속성들이 적용 또는 무시 가능
 * 프로파일 특정 속성 정의
   * 프로덕션 환경의 속성밀단 포함하는 다른 yml(또는 properties) 파일 생성
     * 파일 이름 규칙: application-{프로파일 이름}.yml (또는 application-{프로파일 이름}.properties)
   * 공통되는 속성 외에 특정 속성을 ---을 추가하고 해당 프로파일 이름 지정
     * --- 이후의 특정 프로파일에만 적용
 * 프로파일 활성화
   * spring.profiles.active 속성에 지정 → 프로덕션 특정 속성을 개발 속성과 분리시키는 장점 활용 X
   * 환경변수를 활용하여 활성화 프로파일을 설정
   * JAR 파일로 실행할 시 명령행 인자로 활성화 프로파일을 설정
 * 프로파일을 사용해서 조건별로 빈 생성
   * Profile 어노테이션을 활용하여 특정 프로파일이 활성화될때만 빈이 생성
   * 사용 예시
   ```
   // dev 프로파일이 활성화 되었을 때 CommandLineRunner 빈이 생성
   @Profile({"dev"})
   public CommandLineRunner dataLoader(...) {
    ...
   }
   
   // dev 프로파일이 활성화 되지 않았을 때 CommandLineRunner 빈이 생성
   @Profile({"!dev"})
   public CommandLineRunner dataLoader(...) {
    ...
   }
   ```
   * 메소드 단위가 아닌 클래스 단위에서도 어노테이션 지정 가능 → 클래스에 정의된 모든 빈에 적용
***