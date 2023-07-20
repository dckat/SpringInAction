# 11. 리액티브 API 개발하기
## 11.1. 스프링 WebFlux 사용하기
* 스프링 WebFlux 개요
  * 스프링 MVC
    * 동기적 + 블로킹 방식
    * 하나의 요청에 대해 하나의 스레드 사용
      * (문제점) 믾은 사용자가 동시에 요청을 보낼 시 처리하지 못하는 현상 발생
      * (해결) 애플리케이션 실행 시 미리 스레드 풀을 만들어 둠 → 풀의 사이즈를 조정하는 것이 중요
  * 스프링 WebFlux
    * 비동기적 + 논블로킹 방식
    * 이벤트 루프 → 요청 발생 시 핸들러가 처리 후 콜백함수를 통해 응답 반환
***
## 11.2. 함수형 요쳥 핸들러 정의하기
* 애노테이션 기반 스프링 MVC 프로그래밍의 단점
  * 무엇(What)을 하는지-어떻게(How) 해야하는지의 괴리 발생 (애노테이션은 무엇을 정의)
  * 프로그래밍 모델의 커스터마이징/확장이 복잡 (변경을 위해 애노테이션 외부 코드로 작업해야하기 때문)
  * 디버깅의 까다로움 발생 (애노테이션에 Breakpoint 설정 불가)
* 함수형 프로그래밍을 위한 타입
  * RequestPredicate: 처리될 요청의 종류 선언
  * RouterFunction: 일치하는 요청이 어떻게 핸들러에 전달되어야 하는지 선언
  * ServerRequest: HTTP 요청. Header.Body 정보 사용 가능
  * ServerResponse: HTTP 응답. Header.Body 정보 포함
  * 사용 예시
    ```
    ...
    
    @configuration
    public class RouterFunctionConfig {
    
      @Bean
      public RouterFunction<?> helloRouterFunction() {
        return route(GET("/hello"),
            request -> ok.body(just("Hello World!"), String.class));
      }
    }
    ```
    * route 메소드 인자
      * RequestPredicate 객체 (HTTP GET 메소드 요청)
      * 일치하는 요청을 처리하는 함수 (GET 요청에 대한 응답으로 OK 응답 + "Hello World!" 생성)
  * 
  