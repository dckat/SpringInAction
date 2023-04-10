# 10. 리액터 개요
## 10.1. 리액티브 프로그래밍
* 명령형 프로그래밍과 선언형 프로그래밍
  * 명령형 프로그래밍
    * 어떻게(How) 나타낼 것인지를 설명하는 방식
    * 한 번에 하나씩 만나는 순서대로 실행되는 명령어들로 구성
      * 절차지향 프로그래밍: 수행되어야 할 순차적인 처리과정을 포함하는 방식 (C)
      * 객체지향 프로그래밍: 객체들의 집합으로 프로그램의 상호작용 표현 (C++. JAVA)
  * 선언형 프로그래밍
    * 무엇(What)을 할 것인지를 설명하는 방식
    * 데이터가 흘러가는 파이프라인. 스트림을 포함
    * 데이터를 사용할 수 있을 때 까지 리액티브 스트림 처리 → 무한한 데이터 처리 가능
      * 함수형 프로그래밍: 함수를 조합하고 소프트웨어를 만드는 방식
* 함수형 프로그래밍
  * 등장 배경
    * 명령형 프로그래밍: 소프트웨어의 확대에 따라 스파게티 코드 등으로 인한 유지보수에 어려움 발생
    * 거의 모든것을 순수함수로 나누어 문제를 해결 → 유지보수가 용이하도록 함
  * 특징
    * 순수함수
      * 동일한 입력에는 항상 같은 값을 반환
      * 함수의 실행이 프로그램의 실행에 영향 X
      * 인자의 값 변경이나 프로그램 상태를 변경하는 Side Effect X
    * Stateless. Immutable
      * 데이터는 변하지 않도록 유지하여야 함
      * 데이터 변경 필요시 원본 데이터를 변경하지 않고 복사본을 만들어 변경 작업 진행
    * 1급 객체
      * 변수나 데이터 구조에 저장 가능
      * 파라미터로 전달 가능. 반환값으로 사용 가능
    * 고차함수
      * 함수를 인자로써 전달
      * 함수의 반환값으로 또 다른 함수 사용 가능
  * 장점과 단점
    * 장점
      * 높은 수준의 추상화 제공
      * 함수 단위의 코드 재사용 용이
      * 불변성 지향으로 프로그램의 동작 예측 용이
    * 단점
      * 코드의 가독성 저하
      * 재귀적 코드 스타일로 인한 무한 루프 가능성 존재
      * 순수함수의 조합의 어려움
* 리액티브 프로그래밍
  * 정의: 변화의 전파와 데이터 흐름과 관련된 선언적 프로그래밍의 패러다임
    * 변화의 전파와 데이터 흐름: 데이터가 변경될 때마다 이벤트를 발생시켜 데이터를 지속적으로 전달
    * 선언적 프로그래밍: 어떻게가 아닌 무엇을 할지를 설명
  * 기본 동작 흐름
    * 데이터 발행 → 데이터 가공 → 데이터 구독 → 결과 처리
  * 주요 요소
    * Observable: 데이터 소스 (변경되는 데이터 관찰)
    * Operators: 데이터 소스 처리 함수
    * Scheduler: 스레드 관리자
    * Subscriber: Observable이 주는 데이터의 구독자
  * 리액티브 개념이 적용된 예
    * Push 방식: 데이터 변화 발생 시 변경이 발생한 곳에서 데이터를 전송
      * 예시: DB 트리거. 스프링의 ApplicationEvent. 스마트폰 Push 메시지 등
    * Pull 방식: 변경된 데이터가 있는지 요청을 보내고 변경데이터를 가져오는 방식
      * 예시: 클라이언트 요청-서버 응답 방식 애플리케이션. 절차형 프로그래밍 언어
* 리액티브 스트림
  * 정의: Back Pressure로 비동기 요소들 사이의 상호작용을 정의한 스펙
  * 주요 개념
    * Streaming
    ![img](https://user-images.githubusercontent.com/19167273/230029803-a9cda839-155f-4bd2-a409-e7e6d66cb692.png)
      * 전통적인 데이터 처리 방식: 요청에 대한 데이터를 모두 저장하여 응답 전송
        * 한 요청의 데이터가 많을 경우: out of memory 이슈 발생 가능성
        * 동시에 수많은 요청 발생하는 경우: 다량의 Garbage Collection 발생 가능성
      * 당장 처리할 데이터만 저장 → 적은 메모리로 많은 데이터 처리 가능
    * Back Pressure
      * Subscriber가 수용 가능한 만큼 데이터를 요청
        * 재요청으로 인한 CPU.네트워크의 사용량 ↓
        * 대기 Queue의 out of memory 이슈 발생 X
  * 자바 스트림 vs 리액티브 스트림
    * 공통점
      * 데이터를 작업하기 위한 API 제공
      * 다수의 같은 오퍼레이션 공유
    * 차이점
      * 자바 스트림: 대개 동기화. 한정된 데이터로 작업 수행
      * 리액티브 스트림: 비동기 처리 지원. 무한 데이터셋의 처리
  * 주요 인터페이스
  ![img_1](https://user-images.githubusercontent.com/19167273/230029852-6659eb94-89f1-4a04-b2a5-5d64e084c119.png)
    * Publisher: 데이터 생성. Subscriber에게 데이터 전달
    * Subscriber: 데이터 구독. 전달받은 데이터를 처리
    * Subscription: Publisher-Subscriber간 데이터 교환하도록 연결
    * Processor: Publisher.Subscriber 모두 상속받은 인터페이스
***
## 10.2. 리액터
* 스프링 리액터
  * 스프링에서 리액티브 프로그래밍을 위한 모듈
  * 프로그래밍 예시
    * 명령형 프로그래밍
    ```
    String name = "Kim";
    String captialName = name.toUpperCase();
    String greeting = "Hello, " + capitalName + "!";
    System.out.println(greeting);
    ```
    * 리액티브 프로그래밍
    ```
    Mono.just("Kim")
        .map(n -> n.toUpperCase())
        .map(cn -> "Hello, " + cn + "!")
        .subscribe(System.out::println);
    ```
  * 주요 타입
    * Flux: 0.1 또는 다수의 데이터를 갖는 타입
    * Mono: 하나의 데이터 항목만 갖는 타입
* 리액티브 플로우 다이어그램
  * 마블 다이어그램으로 리액티브 플로우를 표현
    * 상단: Flux나 Mono를 통해 전달되는 데이터의 타임라인
    * 중앙: 오퍼레이션
    * 하단: 결과로 생성되는 Flux나 Mono의 타임라인
  * 다이어그램 예시
  ![그림](https://user-images.githubusercontent.com/19167273/230038913-ad97c197-7157-448e-bbd3-f69cf47654de.png)
* 리액터 의존성 추가
  ```
  <dependency>
    <groupId>io.projectreactor</groupId>
    <artifactId>reactor-core</artifactId>
  </dependency>
  ```
***
## 10.3. 리액티브 오퍼레이션 적용하기
* 리액티브 프로그래밍에서의 오퍼레이션 분류
  * 생성(Creation)
  * 조합(Combination)
  * 변환(Transformation)
  * 로직(Logic)
* 생성 오퍼레이션
  * just: 리액티브 타입 생성
  * fromArray: 배열로부터 리액티브 생성
  * fromIterable: List.Set.Iterable 등의 컬렉션으로부터 생성
  * fromStream: 스트림 객체로부터 생성
  * range: 1씩 증가하는 리액티브 생성
  * interval: 시간간격.주기에 따라 증가하는 리액티브 생성
* 조합 오퍼레이션
  * mergeWith: 2개의 Flux 스트림을 하나의 Flux로 병합하여 생성
  * zip: 각 Flux로 부터 한 항목씩 번갈아가면서 새로운 Flux 생성
  * first: 2개의 Flux 중 먼저 값을 방출하는 소스 Flux를 가져와서 Flux 생성
* 변환 오퍼레이션
  * skip: 앞에서부터 일부 갯수나 시간만큼 건너뛰어 새로운 Flux를 갖도록 생성
  * take: 앞에서부터 일부 갯수만큼 새로운 Flux를 갖도록 방출
  * filter: 조건에 기반하여 일부만큼 새로운 Flux 방출
  * distinct: 중복을 제거하여 새로운 Flux 방출
  * map / flatMap: 발행된 항목을 다른 형태나 타입으로 매핑
    * map: 동기적으로(순차적으로) 매핑이 수행
    * flatMap: 비동기적으로(병행 처리) 매핑이 수행 → 동시성을 위해 Schedulers 활용
      * Schedulers 동시성 모델
        * immediate: 현재 스레드에서 구독 실행
        * single: 단일 재사용 가능한 스레드에서 구독 실행
        * newSingle: 매 호출마다 전용 스레드에서 구독 실행
        * elastic: 무한하고 신축성 있는 풀에서 가져온 작업 스레드에서 구독 실행
        * parallel: 고정된 크기의 풀에서 가져온 스레드에서 구독 실행 (크기: CPU 코어의 갯수)
  * buffer: 데이터 스트림을 일정 갯수만큼 분할
  * collectMap / collectList: 모든 항목을 List(또는 Map)으로 수집
* 로직 오퍼레이션
  * all: 모든 요소가 조건을 충족하는지에 대한 로직 연산 수행
  * any: 최소 하나의 요소가 조건을 충족하는지에 대한 로직 연산 수행
* [리액터 연산 예제 코드 참조](https://github.com/dckat/refactorTest)
***