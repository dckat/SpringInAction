# 6. REST 서비스 생성하기
## 6.1. REST 컨트롤러 작성하기
* REST
  * REST(Representational State Transfer) 정의
    * HTTP URI(Uniform Resource Identifier)를 통해 자원을 명시
    * HTTP Method(POST, GET, PUT, DELETE)를 활용
    * 해당 자원에 대한 CRUD 연산을 적용
  * REST 구성 요소
    * 자원(Resource): HTTP URI
    * 행위(Verb): HTTP Method
    * 내용(Representations): HTTP Message PayLoad
  * 주요 HTTP Method
    * GET: 서버에 Resource 요청
    * POST: 서버에 데이터 추가
    * PUT: 서버의 데이터 갱신
    * DELETE: 서버의 데이터 삭제
    * PATCH: 서버의 데이터 일부분 수정
    * HEAD: 서버 리소스의 메타데이터 요청
  * REST 특징
    * Server-Client 구조
    * Stateless → 상태정보를 별도로 저장 X
    * Cacheable → Last-Modified.E-Tag를 활용하여 캐싱 가능
    * Layered System → 다중계층 구성 가능 (보안.로드밸런싱.암호화 등 추가)
    * Uniform Interface → 특정 언어나 기술에 독립적으로 사용 가능
  * REST 장.단점
    * 장점
      * HTTP 프로토콜의 인프라 활용으로 별도의 인프라 구축 필요 X
      * HTTP 프로토콜의 표준을 최대한 활용 가능
      * 메시지가 의도하는 바를 명확하게 표현하여 쉽게 이해 가능
      * 서버.클라이언트의 역할을 명확하게 분리
    * 단점
      * 표준 자체가 존재하지 않아 정의 필요
      * HTTP Method 형태가 제한적
      * 구형 브라우저에서는 호환 X
* REST API
  * 정의
    * REST 원리를 따르는 API
  * REST API 설계 예시
    * URI는 명사와 소문자를 사용하여야 함
    * 마지막에 슬래시를 포함 X
    * 언더바 대신 하이픈을 사용
    * 파일 확장자는 URI에 포함 X
    * Method는 URI에 포함 X
* 스프링 MVC HTTP 요청-처리 어노테이션

|어노테이션|HTTP Method|용도|
|------|-----------|------|
|GetMapping|HTTP GET 요청|리소스 데이터 읽기|
|PostMapping|HTTP POST 요청|리소스 생성하기|
|PutMapping|HTTP PUT 요청|리소스 변경하기|
|PatchMapping|HTTP PATCH 요청|리소스 변경하기|
|DeleteMapping|HTTP DELETE 요청|리소스 삭제하기|
|RequestMapping|다목적 요청 처리||

* RestController 컨트롤러 구현
  * RestController 어노테이션 지정
    * 컨트롤러에서의 모든 요청 처리 메소드에서 HTTP 응답으로 브라우저에 전달
    * 반환값이 뷰를 통해 HTML로 변환되지 않음
  * Controller 어노테이션 지정
    * 모든 요청 처리 메소드에 ResponseBody 지정 필요
  * ResponseEntity 객체 반환
***
## 6.2. 하이퍼미디어 사용하기
* 기존 API의 문제점
  * 클라이언트가 하드코딩된 API의 스킴을 알아야 할 필요 있음
  * URL 스킴 변경 시 잘못된 인식으로 정상적인 실행 X → 클라이언트 코드의 불안정 발생
  * 엔드포인트 URL이 정해지면 이를 변경하것이 어려움 (이유: 변경 시 모든 클라이언트에서 수정 필요)
* HATEOAS (Hyper As The Engine Of Application State)
  * API로부터 반환되는 리소스와 이에 대한 하이퍼링크가 포함
  * 최소한의 API URL로만 반환되는 리소스와 연관된 URL 발생 → 엔드포인트의 하드코딩 필요 X
* HAL
  * HATEOAS를 활용하여 API의 리소스들을 하이퍼링크를 제공하는 언어
  * HAL 예시
  ```
  {
  "_embedded" : {
    "tacos" : [ {
      "createdAt" : "2023-02-21T11:39:34.633+00:00",
      "name" : "Carnivore",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/tacos/2"
        },
        "taco" : {
          "href" : "http://localhost:8080/api/tacos/2"
        },
        "ingredients" : {
          "href" : "http://localhost:8080/api/tacos/2/ingredients"
        }
      }
    },
   ...
     ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/api/tacos"
    }
  }
  ```
  * _links 속성 → 클라이언트가 수행할 수 있는 관련 API의 하이퍼링크 포함
  * 클라이언트가 특정 리소스 접근 시 해당 링크를 참조하는 self 링크를 요청 (특정 URL 요청 필요 X)
* HATEOAS 활용 하이퍼미디어 생성방법
  1) HATEOAS 의존성 추가 (build.gradle 또는 pom.xml)
  2) 리스트 내 각 리소스의 하이퍼미디어 생성을 위한 리소스 어셈블러 생성
  3) 리소스를 반환하도록 컨트롤러 클래스의 메소드 수정
* HATEOAS에성 제공하는 클래스 및 속성

| 분류             | 스프링 2.2 이전               | 스프링 2.2 이후        |
|----------------|--------------------------|-------------------|
| 단일 리소스         | Resource                 | EntityModel       |
| 다중 리소스         | Resources                | CollectionModel   |
| 리소스 Link 관리    | ResourceSupport          | RepresentationalModel |
| 객체 →리소스로 변환    | ResourceAssemblerSupport | RepresentationModelAssemblerSupport |
***
## 6.3. 데이터 기반 서비스 활성화하기
* 스프링 데이터
  * (3장) 정의한 인터페이스를 기반으로 리포지토리 클래스를 자동으로 생성하고 이에 대한 기능 수행
  * 애플리케이션의 API를 정의하는 기능도 수행
* 스프링 데이터 REST
  * 스프링 데이터가 생성하는 리포지토리의 REST API를 자동으로 생성
  * 스프링 데이터 REST 활용 리소스 획득 예시
  ```
  curl localhost:8080/ingredients ← 실행 후 터미널 입력
  {
  "_embedded" : {
    "ingredients" : [ {
      "name" : "Flour Tortilla",
      "type" : "WRAP",
      "_links" : {
        "self" : {
          "href" : "http://localhost:8080/api/ingredients/FLTO"
        },
        "ingredient" : {
          "href" : "http://localhost:8080/api/ingredients/FLTO"
        }
      }
    },
    ...
    ]
  },
  "_links" : {
    "self" : {
      "href" : "http://localhost:8080/api/ingredients"
    },
    "profile" : {
      "href" : "http://localhost:8080/api/profile/ingredients"
    }
  }
  }
  ```
  * 확인할 수 있는 내용: 해당 리소스의 엔드포인트, 하이퍼링크를 포함한 리소스
* API 기본 경로 설정
  * API의 엔드포인트가 다른 컨트롤러와의 충돌을 방지하기 위함
  * spring.data.rest.base-path 속성에 기본 경로 설정 가능
* 리소소의 경로와 관계 이름 변경
  * RestResource: 관계 이름과 경로를 사용자 정의에 따라 지정 가능
  ```
  @RestResource(rel="tacos", path="tacos") ← 경로와 관계이름을 tacos로 설정
  public class Taco {
  ...
  }
  ```
* 스프링 데이터 REST 활용 커스텀 엔드포인트 추가
  * 구현 전 고려사항
    * 사용자가 정의한 엔드포인트 컨트롤러는 기본경로로 매핑 X → 기본경로가 앞에 붙도록 매핑 필요
    * 기본경로 변경 시 해당 컨트롤러의 매핑이 일치되도록 수정
    * 정의한 엔드포인트는 스프링 데이터 REST 엔드포인트에서 반환되는 리소스 하이퍼링크에 포함 X 
      → 이를 포함시키도록 구현
  * 기본경로 매핑
    * RepositoryRestController
      * 스프링 데이터 REST 엔드포인트에 구성되는 것과 동일한 기본경로로 매핑이 가능
      * 해당 어노테이션이 지정된 컨트롤러는 spring.data.rest.base-path 속성 값이 붙은 경로를 가짐
  * 커스텀 하이퍼링크를 스프링 데이터 엔드포인트에 추가
    * RepresentationModelProcessor
      * API를 통해 리소스가 반환되기 전에 리소스를 조작하는 인터페이스
      * 리소스에 하이퍼링크를 추가하여 하이퍼링크가 스프링 데이터 엔드포인트에 추가되도록 구현
***