# 7. REST 서비스 사용하기
* 개요
  * (6장) 외부 클라이언트가 사용할 수 있는 엔드포인트 정의에 초점
  * 스프링 애플리케이션에서 API 제공에 다른 애플리케이션의 API를 요청할 수 있음
  * 스프링을 활용하여 다른 REST API와 상호작용하는 방법에 이해
  * REST API를 사용할 수 있는 방법
    * RestTemplate: 스프링 프레임워크에서 제공하는 간단하고 동기화된 REST 클라이언트
    * Traverson: 스프링 HATEOAS에서 제공하는 하이퍼링크를 인식하는 REST 클라이언트
    * WebClient: 반응형 비동기 REST 클라이언트 (11장)
***
## 7.1. RestTemplate로 REST 엔드포인트 사용하기
* RestTemplate
  * REST 리소스를 사용하는 데에 발생하는 장황한 코드 발생 X
  * 상호작용을 위해 41개의 메소드 제공. 그 중 12개가 고유한 작업 수행 (나머지는 오버로딩된 버전)
* RestTemplate이 정의하는 고유한 작업을 수행하는 메소드

| 메소드             | 기능                                                                |
|-----------------|-------------------------------------------------------------------|
| delete          | 지정된 URL 리소스에 HTTP DELETE 수행                                       |
| exchange        | 지정된 HTTP 메소드를 URL에 대해 실행. 응답 body와 연결된 객체를 포함하는 ResponseEntity 반환 |
| execute         | 지정된 HTTP 메소드를 URL에 대해 실행. 응답 body와 연결되는 객체 반환                     |
| getForEntity    | HTTP GET 요청 전송. 응답 body와 연결된 객체를 포함한 ResponseEntity 반환            |
 | getForObject    | HTTP GET 요청 전송. 응답 body와 연결된 객체 반환                                |
 | headForHeaders  | HTTP HEAD 요청 전송. 지정된 리소스 URL의 HTTP 헤더 반환                          |
 | optionsForAllow | HTTP OPTIONS 요청 전송. 지정된 URL의 Allow 헤더 반환                          |
 | patchForObject  | HTTP PATCH 요청 전송. 응답 body와 연결된 결과 객체 반환                           |
 | postForEntity   | URL에 데이터 POST. 응답 body와 연결된 객체를 포함하는 ResponseEntity 반환            |
 | postForLocation | URL에 데이터 POST. 새로 생성된 리소스의 URL 반환                                 |
 | postForObject   | URL에 데이터 POST. 응답 body에 연결된 객체 반환                                 |
 | put             | 리소스 데이터를 지정된 URL에 PUT                                             |
 * 오버로딩 된 형태 (3가지)
   * 가변인자 리스트에 지정된 URL 매개변수에 URL 문자열을 인자로 받음
   * Map<String,String>에 지정된 URL 매개변수에 URL 문자열을 인자로 받음
   * java.net.URI를 URL에 대한 인자로 받으며, 매개변수화된 URL은 지원하지 않음
***
## 7.2. Traverson으로 REST API 사용하기
* Traverson
  * 스프링 애플리케이션에서 하이퍼미디어 API를 사용할 수 있는 자바 기반 라이브러리
  * Traverson 생성 예시
    ```
    // Traverson을 기본 URL로 지정
    Traverson traverson = new Traverson(
        URI.create("http://localhost:8080/api"), MediaTypes.HAL_JSON);
    ```
  * 주요 메소드
    * follow: 문자열을 인자로 받아 관계 이름이 문자열인 리소스로 이동
    * toObject: 데이터를 읽어들이는 객체의 타입 지정
  * Traverson 활용 예시
  ```
  // 반환 리소스 타입 지정
  ParameterizedTypeReference<CollectionModel<Ingredient>> ingredientType = 
    new ParameterizedTypeReference<CollectionModel<Ingredient>>() {};
  
  
  CollectionModel<Ingredient> ingredients = 
     traverson
       .follow("ingredients")  ← 관계이름이 ingredients인 리소스로 이동
       .object(ingredientType); ← 해당 리소스의 콘텐츠를 get (타입은 ingredientType)
  ```
* RestTemplate vs Traverson?
  * RestTemplate: 리소스의 쓰거나 삭제를 메소드를 통해 가능. API 이동 X
  * Traverson: API를 이동하면서 리소스를 쉽게 가져올 수 있음. 리소스를 쓰거나 삭제 메소드 제공 X
  * 리소스의 이동 시에는 Traverson 활용. 리소스의 POST.PUT.DELETE 등은 RestTemplate 활용
  * 활용 예시
  ```
  private Ingredient addIngredient(Ingredient ingredient) {
    // Traverson 활용 API 이동
    String ingredientsUrl = traverson
        .follow("ingredients")
        .asLink()  ← 링크 자체를 요청
        .getHref(); ← 링크의 URL을 반환
  
    // RestTemplate 활용 ingredient POST
    return rest.postForObject(ingredientsUrl, ingredients, Ingredient.class);
  ```
***