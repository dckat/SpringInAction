package tacos.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import tacos.assembler.TacoResourceAssembler;
import tacos.domain.Taco;
import tacos.repository.TacoRepository;
import tacos.resource.TacoResource;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RepositoryRestController
public class RecentTacosController {

    private TacoRepository tacoRepository;

    public RecentTacosController(TacoRepository tacoRepository) {
        this.tacoRepository = tacoRepository;
    }

    @GetMapping(path="/tacos/recent", produces="application/hal+json")
    public ResponseEntity<CollectionModel<TacoResource>> recentTacos() {
        PageRequest page = PageRequest.of(0, 12, Sort.by("createdAt").descending());
        List<Taco> tacos = tacoRepository.findAll(page).getContent();

        CollectionModel<TacoResource> recentTacoResources = new TacoResourceAssembler().toCollectionModel(tacos);
        recentTacoResources.add(
                linkTo(methodOn(RecentTacosController.class).recentTacos())
                        .withRel("recents"));

        return new ResponseEntity<>(recentTacoResources, HttpStatus.OK);
    }
}
