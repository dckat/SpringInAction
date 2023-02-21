package tacos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import tacos.domain.Taco;

@Configuration
public class SpringDataRestConfig {

    @Bean
    public RepresentationModelProcessor<PagedModel<CollectionModel<EntityModel<Taco>>>>
    tacoProcessor(EntityLinks links) {
        return new RepresentationModelProcessor<PagedModel<CollectionModel<EntityModel<Taco>>>>() {
            @Override
            public PagedModel<CollectionModel<EntityModel<Taco>>> process(
                    PagedModel<CollectionModel<EntityModel<Taco>>> model) {
                model.add(
                        links.linkFor(Taco.class)
                                .slash("recent")
                                .withRel("recents"));
                return model;
            }
        };
    }
}
