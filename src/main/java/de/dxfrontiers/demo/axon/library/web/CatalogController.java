package de.dxfrontiers.demo.axon.library.web;

import de.dxfrontiers.demo.axon.library.persistence.CatalogEntity;
import de.dxfrontiers.demo.axon.library.persistence.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogRepository repository;

    @GetMapping("/by-title")
    public List<CatalogEntity> findByTitle(@RequestParam String title) {
        return repository.findByTitle(title);
    }

    @GetMapping("/by-author")
    public List<CatalogEntity> findByAuthor(@RequestParam String author) {
        return repository.findByAuthor(author);
    }
}
