# Chap 2. 웹 애플리케이션 개발하기
## 2.1. 정보 보여주기
* 스프링 MVC 처리 흐름
  * 요청 처리 흐름
  ![처리흐름](https://user-images.githubusercontent.com/19167273/183591669-52699b1f-d4da-4974-a120-e73e115de980.jpg)
  * 주요 컴포넌트
    1) 속성 정의 도메인 클래스
    2) 정보를 가져와서 뷰에 전달하는 스프링 MVC 컨틀롤러 클래스
    3) 사용자의 브라우저에 보여주는 뷰-템플릿
* 도메인 클래스 설정
  * LomBok: getter, setter 등의 속성정보와 관련된 메소드를을 정의한 라이브러리 (의존성 추가 별도로 필요)

    ※ 라이브러리 추가 방법
    1) pom.xml 등 의존성이 정의된 파일에 직접 추가
    2) 실행가능한 jar 파일을 직접 찾아 실행파일 디렉터리에 복사
  * Data: final 속성 초기화 생성자, Getter.Setter 등의 생성을 Lombok에 알려주는 어노테이션
* 컨트롤러 클래스 설정
  * 스프링 MVC 요청-대응 주요 어노테이션
    * RequestMapping: 다목적 요청 처리
    * GetMapping: HTTP GET 요청 처리
    * PostMapping: HTTP POST 요청 처리
    * PutMapping: HTTP PUT 요청 처리
    * DeleteMapping: HTTP DELETE 요청 처리
    * PatchMapping: HTTP PATCH 요청 처리
  * Slf4j: 해당 클래스에 SLF4J(Simple Logging Facade) Logger 생성 어노테이션
  * Controller: 해당 클래스를 컨틀롤러로 식별, 컴포넌트 검색을 수행해야 하는 것을 나타내는 어노테이션
* 뷰 디자인
  * 뷰 디자인과 관련한 템플릿: JSP(JavaServer Pages), Thymeleaf, FreeMarker 등
  * 뷰 라이브러리: 추상화 모델을 알지 못함, 컨트롤러가 Model 대신 서블릿 요청 속성 사용
  * Thymeleaf 주요 속성
    * th:text: 교체를 수행하는 네임스페이스 속성
    * th:each: 컬렉션의 반복처리, 각 요소를 하나씩 HTML로 표현

## 2.2. 폼 제출 처리하기
