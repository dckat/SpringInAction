# 8. 비동기 메시지 전송하기
* 개요
  * 동기와 비동기
    * 동기
      * 직렬형 작업 처리 모델. 순차적으로 작업이 실행
      * 하나의 작업이 끝날때 까지 다음 작업이 대기
      * (예시) DB 쿼리 수행, REST 활용 동기화 통신(7장)
      * (장점) 간단한 설계. 직관적인 코드. Breakpoint를 활용한 디버깅 용이
      * (단점) 특정 작업이 오래 걸릴 경우 다른 작업은 대기로 인한 시간 낭비
    * 비동기
      * 병렬형 작업 처리 모델
      * 작업이 종료되지 않아도 대기없이 다음작업 수행 가능
      * (예시) Ajax 요청, 비동기 메시
      * (장점) 자원의 효율적 사용 가능
      * (단점) 동기보다 다소 복잡한 설계
  * 스프링에서의 비동기 메시징
    * 애플리케이션 간 응답 대기 없이 간접적으로 메시지 전송
    * 애플리케이션 간 결합도 ↓. 확장성 ↑
    * 종류: JMS, RabbitMQ, AMQP, 아파치 카프카 등
***
## 8.1. JMS
* JMS (Java Message Service)
  * 두 개 이상의 클라이언트 간 메시지 통신을 위한 공통 API를 정의하는 자바 표준
  * JAVA에서 비동기 메시징을 처리하기 위한 방법으로 오랫동안 사용 (2001년 소개)
  * (JMS 이전) 클라이언트 간 통신 중재를 위한 메시지 브로커들이 별도의 API 존재 → 메시징 코드가 브로커간 호환 X
* JMS 메시지 구조
  * 헤더
    * 메시지 경로 지정 및 식별에 사용되는 값 포함
    * JMS 메시지에서의 필수 요소
  * 등록정보
    * 프로세스에 대한 정보, 작성된 시간, 각 부분의 구조가 명시
    * 등록정보 이름-등록정보 값의 쌍으로 이루어짐
  * 메시지 본문
    * StreamMessage: JAVA 프리미티브 값의 스트림이 포함
    * MapMessage: 이름-값 쌍을 포함
    * TextMessage: JAVA 문자열 포함
    * ObjectMessage: 일련화된 객체 포함
    * BytesMessage: 해석되지 않은 바이트의 스트림 포함
* JmsTemplate
  * 스프링에서 JMS를 지원하는 템플릿 기반 클래스
  * Producer-Consumer 간 메시지 통신이 가능
  * 메시지 기반 POJO 지원
    * POJO: 큐나 토픽에 도착하는 메시지에 반응하여 비동기 방식으로 메시지를 처리하는 JAVA 객체
* JMS 설정
  * 프로젝트 의존성 추가
  ```
  <!-- ActiveMQ 의존성 -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-activemq</artifactId>
  </dependency>
  
  <!-- ActiveMQ Artemis 의존성 -->
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-artemis</artifactId>
  </dependency>
  ```
  * ActiveMQ
    * 자바로 작성된 오픈소스 메시지 브로커
    * 클라이언트 간 메시지를 송수신이 가능
    * 브로커 위치와 인증정보 구성 속성
      * ```spring.activemq.broker-url:```: 브로커 URL
      * ```spring.activemq.user```: 브로커를 사용하기 위한 사용자
      * ```spring.activemq.password```: 브로커를 사용하기 위한 패스워드
      * ```spring.activemq.in-memory```: 인메모리 브로커로 시작할지 여부 (기본값: true)
  * ActiveMQ Artemis
    * ActiveMQ를 새롭게 구현한 차세대 브로커
    * 브로커의 위치와 인증 정보를 구성 속성
      * ```spring.artemis.host:```: 브로커 호스트
      * ```spring.artemis.port```: 브로커 포트
      * ```spring.artemis.user```: 브로커를 사용하기 위한 사용자
      * ```spring.artemis.password```: 브로커를 사용하기 위한 패스워드
* JmsTemplate 활용 메시지 전송
  * 전송 주요 메소드
    * send
      * 원시 메시지를 전송
      * Message 객체 생성을 위해 MessageCreator 필요
    * convertAndSend
      * Object 객체를 인자로 받아 Message 타입으로 변환
      * 메시지 전송 전 후처리를 위한 MessagePostProcessor도 인자로 받음
    * 오버로딩 형태
      * 도착지 매개변수 존재 X (기본 도착지로 전송) → 기본 도착지는 별도로 속성지정 필요
      * 도착지를 나타내는 객체를 인자로 받음
      * 도착지를 나타내는 문자열을 인자로 받음
  * send 활용 메시지 전송
    * MessageCreator
      ```
      // MessageCreator 활용 send 메소드로 메시지 전송
      jmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(order);
            }
        });

      // 람다함수 형태로 메시지 전송 (위의 코드와 작동방식 동일)
      jmsTemplate.send(session -> session.createObjectMessage(order));
      ```
    * 도착지 객체 활용
      ```
      // 도착지 객체 설정
      @Bean
      public Destination orderQueue() {
        return new ActiveMQQueue("tacocloud.order.queue");
      }
      
      @Autowired
      public JmsOrderMessagingService(..., Destination orderQueue) {
        ...
        this.orderQueue = orderQueue;
      }
      ...
      
      @Override
      public void sendOrder(Order order) {
        jms.send(
            orderQueue,
            session -> session.createObjectMessage(order));
      ```
    * 도착지 문자열 활용
      ```
      @Override
      public void sendOrder(Order order) {
        jms.send(
            "tacocloud.order.queue",
            session -> session.createObjectMessage(order));
      }
      ```
  * convertAndSend 활용 메시지 전송
    ```
    @Override
    public void sendOrder(Order order) {
      jms.convertAndSend("tacocloud.order.queue", order);
    }
    ```
  * 메시지 변환기 구현
    * convertAndSend: 객체를 Message로 변환 → 번거로운 변환작업 발생
    * MessageConverter 활용 메시지 변환기 구현 가능
  * 메시지 변환기 (MessageConvertor)
    * 메시지를 변환해주는 인터페이스 (스프링에 정의)
    * 구현된 스프링 메시지 변환기
      * MappingJackson2MessageConvertor: 메시지를 JSON으로 상호변환 (Jackson2 JSON 라이브러리 활용)
      * MarshallingMessageConvertor: 메시지를 XML로 상호 변환 (JAXB 활용)
      * MessagingMessageConverter: 메시지를 Message 객체로 상호변환. JmsHeaderMapper를 표준 메시지 헤더로 상호변환
      * SimpleMessageConverter: 문자열을 TextMessage로. byte 배열을 BytesMessage로. Map을 MapMessage로. 
        Serializable 객체를 Object로 변환 (Serializable 객체 별도 구현 필요)
* JMS 메시지 수신하기
  * 메시지 수신 방식
    * 풀 모델: 메시지를 요청하고 도착할 때까지 대기. JmsTemplate의 모든 메소드가 제공하는 모델
    * 푸시 모델: 메시지가 수신가능할 때 코드로 자동 전달
  * 수신 주요 메소드
    * receive
      * 변환되지 않은 원시 메시지 수신
    * receiveAndConvert
      * 도메인 타입으로 변환 후 메시지 수신
      * 변환을 위해 메시지 변환기가 사용
    * 메시지 수신 구현 예시
      * receive 활용
      ```
      public class JmsOrderReceiver {
          private JmsTemplate jms;
          private MessageConverter converter; // 변환기

          @Autowired
          public JmsOrderReceiver(JmsTemplate jms, MessageConverter converter) {
             this.jms = jms;
             this.converter = converter;
          }

          public Order receiveOrder() {
             Message message = jms.receive("tacocloud.queue");
             return (Order) converter.fromMessage(message);
          }
      }
      ```
      * receiveAndConvert 활용
      ```
      public class JmsOrderReceiver {
        private JmsTemplate jmsTemplate;

        @Autowired
        public JmsOrderReceiver(JmsTemplate jmsTemplate) {
          this.jmsTemplate = jmsTemplate;
        }

        public Order receiveOrder() {
          return (Order) jmsTemplate.receiveAndConvert("tacocloud.order.queue");
        }
      }
        ```
  * 메시지 리스너
    * 푸시 모델: 메시지가 도착할때 까지 기다려야 하는 모델 → 이를 위한 리스너가 필요
    * 메시지가 도착할 때까지 대기하는 수동적 컴포넌트
    * JmsListener: 메시지 리스너를 지정하기 위한 어노테이션
***
## 8.2. RabbitMQ와 AMQP
* AMQP (Advanced Message Queueing Protocol)
  * 메시지 지향 미들웨어를 위한 개방형 표준 응용 계층 프로토콜
  * 기존 MQ → 플랫폼에 종속적인 요소로 메시지 교환을 위한 브릿지 이용 또는 시스템 통일 필요
    * 문제점: 브릿지 이용으로 인한 속도 저하 발생. 시스템 통일로 인한 불편함 발생
  * 서로 다른 시스템 간 효율적인 메시지 교환을 위하여 설계
  * AMQP가 충족해야할 조건
    1) 모든 브로커들은 같은 방식으로 동작
    2) 모든 클라이언트들은 같은 방식으로 동작
    3) 네트워크 상으로 전송되는 명령어들의 표준화
  * AMQP를 위한 주요 컴포넌트
    ![다운로드](https://user-images.githubusercontent.com/19167273/225232119-409b48b3-a5e8-486e-be9c-42f88e595451.png)
    * Exchange
      * Publisher 부터 수신한 메시지를 바인딩된 Queue 또는 다른 Exchange로 분배
      * 각 Exchange는 바인딩을 사용해서 상위 Exchange에 바인드
    * Queue
      * 메모리나 디스크에 메시지 저장. Consumer에게 메시지를 전달
  * Exchange 타입
    * Direct Exchange
      * 메시지의 라우팅 키를 queue에 1:N으로 매칭
      * 일반적으로 queue의 이름을 바인딩하고자 하는 라우팅키와 동일하게 작성
      ![img (1)](https://user-images.githubusercontent.com/19167273/225232158-c2945d49-5cda-4532-9163-4f2008935674.png)
    * Topic Exchange
      * 와일드카드 활용 메시지를 큐에 매칭
      * 와일드카드
        * *(start): 하나의 단어
        * #(hash): 0개 이상의 단어
        * .: 단어 구분
      ![img](https://user-images.githubusercontent.com/19167273/225232192-3d484a54-be05-4ce4-b8e8-66aa53051206.png)
    * Fanout Exchange
      * 모든 메시지를 모든 큐로 라우팅
      ![img (2)](https://user-images.githubusercontent.com/19167273/225232228-bbc59393-72bf-467b-b40e-4971a34c46f7.png)
    * Headers Exchange
      * Key-Value로 정의된 헤더에 의해 라우팅 결정
      * x-match 값에 의해 바인딩 조건이 모두 충족인지 일부 충족인지를 결정
        * x-match="all": 바인딩 조건 모두 충족
        * x-match="any": 바인딩 조건 중 하나만 충족
      ![img (3)](https://user-images.githubusercontent.com/19167273/225232249-02eddfd3-a266-4ad5-afc5-577ceb77d21b.png)
* RabbitMQ
  * AMQP를 구현한 오픈소스 메시지 브로커
  * JMS vs RabbitMQ
    * JMS: 메시지의 수신자가 가져갈 도착지의 이름을 주소로 사용
    * RabbitMQ: 리스닝 하는 큐와 exchange 이름과 라우팅 키를 주소로 사용
  * RabbitMQ의 exchange 종류 (교재 기반)
    * 기본(default): 브로커가 자동으로 생성한 exchange. 라우팅 키와 이름이 같은 큐로 전달. 모든 큐와 자동으로 연결
    * 디렉트(direct): 바인딩 키가 메시지의 라우팅 키와 같은 큐에 메시지 전달
    * 토픽(topic): 와일드 카드를 포함하는 바인딩 키가 메시지의 라우팅 키와 일치하는 하나 이상 큐에 메시지 전달
    * 팬아웃(Fanout): 바인딩 키.라우팅 키에 상관없이 모두 연결된 큐에 메시지 전달
    * 헤더(Header): 토픽과 유사. 라우팅 키 대신 헤더 값을 기반
    * 데드 레터(Dead Letter): 어떤 exchange-queue 바인딩과도 일치하지 않는 모든 메시지를 보관
* RabbitMQ의 스프링 구현
  * 의존성 추가
   ```
   <!-- ActiveMQ 의존성 -->
   <dependency>
     <groupId>org.springframework.boot</groupId>
     <artifactId>spring-boot-starter-ampq</artifactId>
   </dependency>
  ```
  * RabbitMQ 브로커 위치와 인증정보 구성 속성
    * ```spring.rabbitmq.addresses```: 쉼표로 구성된 리스트 형태의 RabbitMQ 브로커 주소
    * ```spring.rabbitmq.host```: 브로커 호스트 (default: localhost)
    * ```spring.rabbitmq.port```: 브로커 포트 (default: 5672)
    * ```spring.rabbitmq.username```: 브로커를 사용하기 위한 사용자 이름
    * ```spring.rabbitmq.password```: 브로커를 사용하기 위한 사용자 패스워드
* RabbitTemplate 활용 메시지 전송
  * RabbitTemplate: JmsTemplate과 유사하게 RabbitMQ 메시징을 위한 스프링 지원
  * 주요 메소드
    * send: 원시 메시지 전송
    * convertAndSend: 변환된 메시지 전송 / 변환 후 후처리 되는 메시지 전송
    * RabbitTemplate 활용 시 exchange와 라우팅 키의 형태로 메시지 전송 → 이들을 메소드 인자로 받음
  * RabbitMQ에서 활용되는 메시지 변환기
    * Jackson2JsonMessageConverter: Jackson2JSONProcessor 사용 객체를 JSON으로 상호 변환
    * MarshallingMessageConverter: 스프링 Marshaller/Unmarshaller 활용 변환
    * SerializerMessageConverter: 스프링 Serializer/Deserializer 활용 String과 객체 변환
    * SimpleMessageConverter: String, byte 배열, Serializable 타입 변환
    * ContentTypeDelegatingMessageConverter: contentType 헤더를 기반으로 다른 메시지 변환기에 변환 위임
* RabbitTemplate 활용 메시지 수신
  * 수신 모델 (JmsTemplate과 동일)
    * 풀 모델: RabbitTemplate.receive 메소드를 활용하여 메시지 수신
    * 푸시 모델: 리스너(RabbitListener)활용 지정된 메소드로 메시지를 푸싱
***
