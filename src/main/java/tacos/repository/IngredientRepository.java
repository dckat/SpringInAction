package tacos.repository;

import tacos.domain.Ingredient;

public interface IngredientRepository {
    Iterable<Ingredient> findAll(); // 모든 식자재 데이터 검색 쿼리
    Ingredient findById(String id); // id로 Ingredient 검색 쿼리
    Ingredient save(Ingredient ingredient); // Ingredient 객체 저장
}
