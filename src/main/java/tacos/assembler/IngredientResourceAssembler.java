package tacos.assembler;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import tacos.controller.IngredientController;
import tacos.domain.Ingredient;
import tacos.resource.IngredientResource;

public class IngredientResourceAssembler extends
        RepresentationModelAssemblerSupport<Ingredient, IngredientResource> {

    public IngredientResourceAssembler() {
        super(IngredientController.class, IngredientResource.class);
    }

    @Override
    public IngredientResource toModel(Ingredient ingredient) {
        return createModelWithId(ingredient.getId(), ingredient);
    }

    @Override
    protected IngredientResource instantiateModel(Ingredient ingredient) {
        return new IngredientResource(ingredient);
    }
}
