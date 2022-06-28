package de.dxfrontiers.demo.axon.library.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    Optional<CatalogEntity> findOneByIsbn(String isbn);
    List<CatalogEntity> findByTitle(String title);
    List<CatalogEntity> findByAuthor(String author);
}
