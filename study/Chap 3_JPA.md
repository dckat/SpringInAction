## 3.2. 스프링 데이터 JPA를 사용해서 데이터 저장하고 사용하기
* 스프링 데이터 JPA
  * ORM(Object-Relational Mapping)
    * 관계형 테이블을 클래스 객체를 자동으로 매핑해주는 것
    * 객체 모델과 관계형 모델 사이의 불일치 해결을 위해 필요
    * 장점: 재사용 및 유지보수의 용이성 증가, DBMS 종속성 감소
    * 단점: 프로시저가 많은 시스템에서 구현하기 어려움, 자주 사용되는 대형 쿼리에 대한 별도 튜닝 필요
    * ex: JPA, Hibernate 등
  * JPA
    * Java 애플리케이션에서 관계형 데이터베이스를 사용방식을 정의한 인터페이스
    * 반복적인 CRUD SQL 처리에 활용
    * 관계매핑 또는 성능 이슈에 대해 직접 쿼리를 작성하는 네이티브 SQL 제공
* 다양한 스프링 데이터 프로젝트
  * 스프링 데이터 JPA: 관계형 데이터베이스의 JPA 퍼시스턴스
  * 스프링 데이터 MongoDB: 몽고 문서형 데이터베이스의 퍼시스턴스
  * 스프링 데이터 Neo4: Neo4j 그래프 데이터베이스의 퍼시스턴스
  * 스프링 데이터 Redis: 레디스 키-값 스토어의 퍼시스턴스
  * 스프링 데이터 Cassandra: 카산드라 데이터베이스의 퍼시스턴스
* 스프링 데이터 JPA 주요 어노테이션
  * Entity: 해당 클래스를 JPA entity로 선언
  * Table: 해당 entity가 특정 테이블에 저장되어야 함
* CrudRepository 인터페이스
  * Create(생성), Read(읽기), Update(변경), Delete(삭제) 연산을 위한 인터페이스
  * 매개변수로 저장되는 엔티티 타입과 ID 속성타입을 받음
    
    ex) CrudRepository<Ingredient, Long>: 엔티티타입은 Ingredient, ID 속성타입은 Long
* JPA 리포지토리 커스터마이징
  * CRUD 연산에 추가하여 특정 속성값에 해당하는 쿼리가 필요한 경우 메소드 추가
  * 리포지토리 구현체 생성 시: 스프링 데이터가 인터페이스에 정의된 메소드의 이름 분석 후 용도 파악
  * 메소드 시그니처의 예시
    * findByDeliveryZip(String deliveryZip): deliveryZip과 일치하는 결과
    * findByDeliveryCityOrderByDeliveryTo(String city): city와 일치하는 결과를 DeliveryTo 기준 정렬
  * 복잡한 쿼리의 경우 Query 어노테이션으로 쿼리 작성 가능
        
  