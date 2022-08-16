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
 * 폼 제출 시 → 브라우저가 모든 데이터 수집 → 서버에 HTTP POST 요청
 * DesignTacoController
   * 디자인 폼 제출 시 → 인자로 전달되는 Taco 객체의 속성과 바인딩
   * 체크박스 요소: ingredients / 텍스트 입력 요소: name
   * redirect: 리디렉션(변경된 경로로 재접속) 뷰를 반환

## 2.3. 폼 입력 유효성 검사하기
 * 유효성 검사 필요한 상황?
   * 필수선택 요소를 선택하지 않은 경우
   * 유효하지 않은 값 입력 (ex: 숫자입력에 문자가 들어가는 경우)
 * 유효성 검사 방법
   * Controller 메소드에 if/then 구현
     * 코드파악. 디버깅에 어려움, 코드작성에 매우 번거롭다는 단점 존재
   * 스프링: 자바 빈 유효성 검사(Bean Validation) API 지원
     * 추가 코드 작성 없이 유효성 검사 규칙 선언 가능
     * 유효성 검사 라이브러리를 스프링에 쉽게 추가 가능
 * 유효성 검사를 위한 절차
   1) 유효성 검사 규칙 선언
   2) 컨트롤러 메소드에 유효성 검사 수행 지정
   3) 유효성 검사 에러 뷰에 표현
 * 유효성 검사 주요 어노테이션 및 속성
   * NotNull: 속성 값이 null 값을 가지지 않음
   * Size: 문자열 또는 리스트의 길이 min, max 등을 지정
   * NotBlank: 속성 값이 반드시 입력되어야 함
   * CreditCardNumber: 속성 값이 룬 알고리즘에 의해 적합한 카드번호여야 함
 
     ※ 룬 알고리즘: 신용카드번호, 주민등록번호 등이 유효한 값인지를 검사하는 알고리즘 ([참고](https://m.blog.naver.com/jihye2340/220688812796))
   * message: 유효성 규칙을 충족하지 못할 때 보여줄 메시지 (모든 유효성 검사 어노테이션이 가지고 있는 속성)
   * Valid: 제출된 폼이 유효성 검사를 수행하는 객체
 * 유효성 검사 에러 표현 (예시 코드)
   ```
   <label for="deliveryName">Name: </label>
   <input type="text" th:field="*{deliveryName}"/>
   <span class="validationError"
          th:if="${#fields.hasErrors('deliveryName')}"
          th:errors="*{deliveryName}">Name Error</span>
   ```
   * class 속성: 에러의 명칭을 지정
   * th:if 속성: 해당 span을 보여줄지 결정 (hasErrors 메소드를 활용하여 에러 검사하여 있으면 span을 보여줌)
   * th:errors 속성: 사전에 지정된 메시지를 검사에러 메시지로 치환