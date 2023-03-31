# 9. 스프링 통합하기
## 9.1. 간단한 통합 플로우 선언
* 스프링 통합
  * 통합 플로우를 생성 → 통합 플로우를 통해 리소스 or 애플리케이션에 데이터 전송.수신
  * ex) 채널 어댑터: 파일 시스템에서 파일을 읽거나 쓰기 위해 사용되는 통합 컴포넌트
* 통합 플로우의 정의 방법
  * XML 구성
    * 가장 오랫동안 사용되고 있지만 자바 중심 구성 방향에서는 적합하지 않음
  * JAVA 구성
    * 컴포넌트별로 빈으로 선언하여 플로우를 정의
  * DSL 활용 JAVA 구성
    * 통합 플로우의 각 컴포넌트를 빈으로 선언하지 않고 전체 플로우를 하나의 빈으로 설정
    * 코드의 가독성을 고려하여 플로우를 구성하여야 함
***
## 9.2. 스프링 통합 컴포넌트
* 채널
  * 통합 플로우에서의 서로 다른 컴포넌트 간 데이터 전달
  * 제공하는 구현체
    * PublishSubscribeChannel: 하나 이상의 Consumer로 메시지 전달
    * QueueChannel: FIFO 방식으로 Consumer의 수신까지 큐에 저장
    * PriorityChannel: priority 헤더를 기반으로 Consumer가 메시지 수신
    * RendezvousChannel: Consumer가 수신할 때까지 전송자가 채널을 차단 (전송자와 수신자 동기화)
    * DirectChannel: 전송자와 동일한 스레드로 실행되는 단일 Consumer를 호출하여 메시지 전송. 트랜잭션 지원
    * ExecutorChannel: TaskExecutor를 통해 메시지 전송. 트랜잭션 지원 X
    * FluxMessageChannel: 리액터의 플럭스를 기반으로 하는 Reactive Streams Publisher 채널
  * 구현 예시
    * 선언
    ```
    // PublishSubscribeChannel 선언
    @Bean
    public MessageChannel orderChannel() {
        return new PublishSubscribeChannel();
    }
    
    // QueueChannel 선언
    @Bean
    public MessageChannel orderChannel() {
        return new QueueChannel();
    }
    ```
    * 참조 (선언된 채널을 서비스 액티베이터에서 사용)
    ```
    // PublishSubscribeChannel
    @ServiceActivator(inputChannel="orderChannel")
    
    // QueueChannel (메시지 도착 여부 폴링: 1초에 1번씩 폴링)
    @ServiceActivator(inputChannel="orderChannel", 
                      poller=@Poller(fixedRage="1000"))
    ```
* 필터
  * 조건을 기반으로 메시지의 다음 플로우 통과 여부를 check (파이프라인 중간에 위치)
  * 구현 예시 (홀짝 구분)
    * JAVA 구성
    ```
    @Bean
    @Filter(inputChannel="numberChannel", outputChannel="evenNumberChannel")
    public boolean evenNumberFilter(Integer number) {
        return number % 2 == 0;
    }
    ```
    * JAVA DSL 구성
    ```
    @Bean
    public IntegrationFlow evenNumberFlow(AtomicInteger integerSource) {
        return IntegrationFlows
        ...
        .<Integer>filter((p) -> p % 2 == 0)
        ...
        .get();
    }
    ```
* 변환기
  * 메시지 값 변경. 메시지 페이로드의 타입의 변환
  * 변환작업: 숫자값의 연산. 문자열 값 조작
  * 구현 예시 (숫자값 → 로마숫자 문자열)
    * JAVA
    ```
    @Transformer(inputChannel="numberChannel", 
                 outputChannel="romanNumberChannel")
    public GenericTransformer<Integer, String> romanNumberTransformer() {
        return RomanNumbers::toRoman;
    }
    ```
    * JAVA DSL 구성
    ```
    @Bean
    public IntegrationFlow transformerFlow() {
        return IntegrationFlows
        ...
        .transform(RomanNumbers::toRoman)
        ...
        .get();
    }
    ```
    * 메소드 참조가 아닌 람다로도 지정이 가능
* 라우터
  * 메시지 헤더를 기반으로 여러 채널 중 하나로 메시지 전달
  * 구현 예시 (홀짝 구분하여 각각 다른 채널로 전달)
    * JAVA 구성
    ```
    @Bean
    @Router(inputChannel="numberChannel")
    public AbstractMessageRouter evenOddNumber() {
      return new AbstractMessageRouter() {
        @Override
        protected Collection<MessageChannel>
                  determineTargetChannels(Message<?> message) {
          Integer number = (Integer) message.getPayload();
          if (number % 2 == 0) {
            return Collections.singleton(evenChannel());
          }
          return Collections.singleton(oddChannel());
        }
      };
    }
    
    // 짝수 채널
    @Bean
    public MessageChannel evenChannel() {
      return new DirectChannel();
    }
    
    // 홀수 채널
    @Bean
    public MessageChannel oddChannel() {
      return new DirectChannel();
    }
    ```  
    * JAVA DSL 구성
    ```
    @Bean
    public IntegrationFlow numberRoutingFlow(AtomicInteger source) {
        return IntegrationFlows
        ...
          .<Integer, String>route(n -> n%2 == 0 ? "EVEN":"ODD", mapping -> mapping
            .subFlowMapping("EVEN",
              sf -> sf.<Integer, Integer>transform(n -> n * 10)
                        .handle((i,h) -> { ... })
              )
            .subFlowMapping("ODD", sf -> sf
              .transform(RomanNumbers::toRoman)
                .handle((i,h) -> { ... })
                )
            )
        .get();
    }
    ```
* 분배기
  * 들어오는 메시지를 두 개 이상의 메시지로 분할하여 각기 다른 채널로 전송
  * 분배기를 사용하는 경우
    * 메시지 페이로드가 같은 타입의 컬렉션을 포함. 각 메시지 페이로드 별 처리
      * ex) 여러 종류의 제품에 대해 제품 리스트를 전달하는 메시지는 제품의 종류별로 페이로드를 갖는 다수의 메시지로 분할
    * 연관된 정보를 함께 전달하는 하나의 메시지 페이로드를 두 개 이상의 다른 타입 메시지로 분할
      * ex) 주문 메시지를 각 정보별로 하위 플로우에서 처리. 분배기 다음에 페이로드 타입별로 메시지 전달을 위한 라우터가 연결
  * 구현 예시
    * 컬렉션을 메시지 페이로드 별 처리
    ```
    @Splitter(inputChannel="lineItemsChannel", outputChannel="lineItemChannel")
    public List<LineItem> lineItemSplitter(List<LineItem> lineItems) {
      return lineItems;
    }
    ```
    * 연관된 정보의 메시지 페이로드를 두 개 이상으로 분리
    ```
    // 분할을 위한 POJO 정의
    public class OrderSplitter {
      public Collection<Object> splitOrderIntoParts(PurchaseOrder po) {
        ArrayList<Object> parts = new ArrayList<>();
        parts.add(po.getBillingInfo());
        parts.add(po.getLineItems());
        return parts;
      }
    }
    
    // 분배기 정의
    @Bean
    @Splitter(inputChannel="poChannel", outputChannel="splitOrderChannel")
    public OrderSplitter orderSplitter() {
      return new OrderSplitter();
    }
    
    // 하위 플로우 전달을 위한 라우터 정의
    @Bean
    @Router(inputChannel="splitOrderChannel")
    public MessageRouter splitOrderRouter() {
      PayloadTypeRouter router = new PayloadTypeRouter();
      router.setChannelMapping(
          BillingInfo.class.getName(), "billingInfoChannel");
      router.setChannelMapping(
          List.class.getName(), "lineItemsChannel");
      return router;
    }
    ```
* 서비스 액티베이터
  * 메시지 처리를 위해 자바 메소드로 메시지를 전달 후 반환값을 출력 채널로 전송
  * 반환값은 MessageHandler 인터페이스를 구현한 클래스에 전달
  ```
  @Bean
  @ServiceActivator(inputChannel="someChannel")
  public MessageHandler sysoutHandler() {
    // 메시지 수신 시 페이로드를 표준 출력으로 내보냄
    return message -> {
      System.out.println("Message payload: " + message.getPayload());
    };
  }
  ```
  * GenericeHandler를 활용하여 새로운 페이로드를 반환하는 새로운 서비스 액티베이터 구현 가능
  ```
  @Bean
  @ServiceActivator(inputChannel="orderChannel", outputChannel="completeChannel")
  public GenericHandler<Order> orderHandler(OrderRepository orderRepo) {
    // 주문 메시지 도착시 리포지토리에 저장. 저장된 객체 반환 시 completeChannel 출력 채널로 전달
    return (payload, headers) -> {
      return orderRepo.save(payload);
    };
  }
  ```
* 채널 어댑터
  * 외부 시스템에 채널 연결. 외부로부터 입력을 받거나 쓰기 가능
  * 인바운드 채널 어댑터. 아웃바운드 채널 어댑터 존재
* 게이트웨이
  * 인터페이스르 통해 통합 플로우로 데이터 전달하고 선택적으로 플로우의 처리결과를 수신
  * FileWriterGateway: 단방향 게이트웨이. 파일에 쓰기 위해 문자열을 인자로 받고 void 반환
* 엔드포인트 모듈
  * 주요 모듈 (24개)

| 모듈             | 의존성 ID (그룹ID: org.springframework.integration) |
|----------------|------------------------------------------------|
| AMQP           | spring-integration-amqp                        |
| 스프링 애플리케이션 이벤트 | spring-integration-event                       |
| RSS. Atom      | spring-integration-feed                        |
| 파일 시스템         | spring-integration-file                        |
| FTP/FTPS       | spring-integration-ftp                         |
| GemFire        | spring-integration-gemfire                     |
| HTTP           | spring-integration-http                        |
| JDBC           | spring-integration-jdbc                        |
| JPA            | spring-integration-jpa                         |
| JMS            | spring-integration-jms                         |
| 이메일            | spring-integration-mail                        |
| MongoDB        | spring-integration-mongodb                     |
| MQTT           | spring-integration-mqtt                        |
| Redis          | spring-integration-redis                       |
| RMI            | spring-integration-rmi                         |
| SFTP           | spring-integration-sftp                        |
| STOMP          | spring-integration-stomp                       |
| 스트림            | spring-integration-stream                      |
| Syslog         | spring-integration-syslog                      |
| TCP/UDP        | spring-integration-ip                          |
| Twitter        | spring-integration-twitter                     |
| 웹 서비스          | spring-integration-ws                          |
| WebFlux        | spring-integration-webflux                     |
| WebSocket      | spring-integration-websocket                   |
| XMPP           | spring-integration-xmpp                        |
| ZooKeeper      | spring-integration-zookeeper                   |
***