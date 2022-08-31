# Chap 3. 데이터로 작업하기
## 3.1. JDBC를 사용해서 데이터 읽고 쓰기
* JDBC(Java Database Connectivity)
  * 정의: 자바에서 데이터베이스에 접속이 가능하도록 하는 API
  * JDBC 주요 구성
    ![구성도](../../../Downloads/그림.png)
    * JDBC 드라이버
      * DBMS와 통신을 담당하는 자바 클래스
      * DBMS에 따라 맞는 드라이버가 다름
        
        ex: com.mysql.jdbc.Driver(mySQL), oracle.jdbc.driver.OracleDriver(오라클)
    * JDBC URL
      * DBMS 연결을 위한 식별값
      * JDBC 드라이버에 따라 형식이 다름
      * 구성: jdbc:[DBMS]:[DB식별자]
  * JdbcTemplate
    * 스프링에서 JDBC 지원을 위한 클래스
    * JDBC 사용 시 요구되는 형식적인 코드없이 SQL 연산을 수행하도록 제공
    * JdbcTemplate 사용 여부에 따른 비교
      * 미적용 시
      ```
      @Override
      public Ingredient findById(String id) {
        Connection connection = null;       // DB 연결을 위한 객체
        PreparedStatement statement = null; // 쿼리문 정의를 위한 객체
        Resultset resultSet = null;         // 결과값 저장을 위한 객체
    
        try {
            connection = dataSource.getConnection(); // DB 연결
            statement = connection.preparedStatement(
                "select id, name, type from Ingredient where id = ?");
            statement.setString(1, id);
            resultSet = statement.executeQuery(); // 쿼리 수행 및 결과 저장
            
            Ingredient ingredient = null;  // 쿼리 결과를 객체에 매핑시키기 위함
            if (resultSet.next()) {
                ingredient = new Ingredient(
                    resultSet.getString("id"),
                    resultSet.getString("name"),
                    Ingredient.Type.valueOf(resultSet.getString("type")));
            }
            return ingredient;
        } catch (SQLException e) {
            // SQL 수행에 관한 예외 처리
        } finally {
            // 쿼리 수행 후 관련 객체 해제
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {}
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {}
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {}
            }
        }
        return null;
      }
      ```
      * 적용 시
      ```
      private JdbcTemplate jdbc; // JdbcTemplate 활용 쿼리 수행을 위한 객체
      
      @Override
      public Ingredient findById(String id) {
        return jdbc.queryForObject(
            "select id, name, type from Ingredient where id = ?",
            this::mapRowToIngredient, id);
      }
      
      private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(
            rs.getString("id");
            rs.getString("name");
            Ingredient.Type.valueOf(rs.getString("type")));
      }
      ```
     * 비교 결과
       * 연결 객체를 생성 및 메소드 실행 후 클린업하는 코드 필요 X
       * catch 블록에서 실행되어야 하는 예외처리 구문 필요 X
       * 쿼리 수행 및 결과를 객체로 생성하는 작업에 대한 코드만 필요
  * 주요 메소드 및 어노테이션
    * 어노테이션
      * Repository: Component에서 데이터 접근을 위한 어노테이션
      * SessionAttributes: 설정한 이름에 해당하는 모델 정보를 자동으로 세션에 넣는 어노테이션. 여러 요청에서 사용하는 객체의 경우에 사용
      * ModelAttribute: 해당 객체가 모델로부터 생성되거나 전달되어야 함을 알려주는 어노테이션
    * 메소드
      * queryForObject: 하나의 객체를 반환하도록 하는 쿼리 메소드 (파라미터로 검색할 행의 id를 추가로 받음)
      * query: 객체의 List를 반환하도록 하는 쿼리 메소드
      * update: 데이터베이스에 데이터를 추가하거나 수정하는 쿼리 수행을 위한 메소드
  * 데이터 추가를 위한 방법
    1) 직접 update 메소드를 사용
       * 필요 인자: PreparedStatementCreator(쿼리문), KeyHolder(고유한 값 제공) 객체
    2) SimpleJdbcInsert 사용
       * SimpleJdbcInsert: 데이터를 더 쉽게 추가하기 위해 JdbcTemplate를 래핑한 클래스
         * execute / executeAndReturnKey: 데이터 추가를 위한 메소드 [인자: Map<String, Object> → key: 컬럼이름, value: 추가되는 값]