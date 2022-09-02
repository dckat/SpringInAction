package tacos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tacos.domain.Ingredient;
import tacos.domain.Ingredient.Type;
import tacos.repository.IngredientRepository;

@SpringBootApplication
public class TacoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}

	@Bean // 식자재 초기설정을 위한 메소드
	public CommandLineRunner dataLoader(IngredientRepository ingredientRepository) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				ingredientRepository.save(new Ingredient("FLTO", "Flour Tortilla", Type.WRAP));
				ingredientRepository.save(new Ingredient("COTO", "Corn Tortilla", Type.WRAP));
				ingredientRepository.save(new Ingredient("GRBF", "Ground Beef", Type.PROTEIN));
				ingredientRepository.save(new Ingredient("CARN", "Carnitas", Type.PROTEIN));
				ingredientRepository.save(new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES));
				ingredientRepository.save(new Ingredient("LETC", "Lettuce", Type.VEGGIES));
				ingredientRepository.save(new Ingredient("CHED", "Cheddar", Type.CHEESE));
				ingredientRepository.save(new Ingredient("JACK", "Monterrey Jack", Type.CHEESE));
				ingredientRepository.save(new Ingredient("SLSA", "Salsa", Type.SAUCE));
				ingredientRepository.save(new Ingredient("SRCR", "Sour Cream", Type.SAUCE));
			}
		};
	}

}
