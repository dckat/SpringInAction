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
    * Server-Client 구조`
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