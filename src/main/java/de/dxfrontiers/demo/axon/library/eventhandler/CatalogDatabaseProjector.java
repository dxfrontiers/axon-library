package de.dxfrontiers.demo.axon.library.eventhandler;

import de.dxfrontiers.demo.axon.library.book.event.BookAddedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalEndedEvent;
import de.dxfrontiers.demo.axon.library.book.event.RentalStartedEvent;
import de.dxfrontiers.demo.axon.library.config.axon.ProcessingGroups;
import de.dxfrontiers.demo.axon.library.exception.EntityNotFoundException;
import de.dxfrontiers.demo.axon.library.persistence.BookEntity;
import de.dxfrontiers.demo.axon.library.persistence.BookRepository;
import de.dxfrontiers.demo.axon.library.persistence.CatalogEntity;
import de.dxfrontiers.demo.axon.library.persistence.CatalogRepository;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ProcessingGroup(ProcessingGroups.CATALOG_DB)
@RequiredArgsConstructor
public class CatalogDatabaseProjector {

    private final CatalogRepository repository;
    private final BookRepository bookRepository;

    @EventHandler
    public void on(BookAddedEvent event) {
        CatalogEntity catalogEntity = repository
            .findOneByIsbn(event.getIsbn())
            .orElseGet(() -> new CatalogEntity()
                .setIsbn(event.getIsbn())
                .setAuthor(event.getAuthor())
                .setTitle(event.getTitle())
            );

        catalogEntity.setAvailable(catalogEntity.getAvailable() + 1);
        catalogEntity.setTotal(catalogEntity.getTotal() + 1);

        repository.save(catalogEntity);
    }

    @EventHandler
    public void on(RentalStartedEvent event) {
        BookEntity book = requireBookEntity(event.getBookId());
        CatalogEntity catalogEntity = requireCatalogEntity(book.getIsbn());

        catalogEntity.setAvailable(catalogEntity.getAvailable() - 1);

        repository.save(catalogEntity);
    }

    @EventHandler
    public void on(RentalEndedEvent event) {
        BookEntity book = requireBookEntity(event.getBookId());
        CatalogEntity catalogEntity = requireCatalogEntity(book.getIsbn());

        catalogEntity.setAvailable(catalogEntity.getAvailable() + 1);

        repository.save(catalogEntity);
    }

    private CatalogEntity requireCatalogEntity(String isbn) {
        CatalogEntity catalogEntity = repository
            .findOneByIsbn(isbn)
            .orElseThrow(EntityNotFoundException::new);
        return catalogEntity;
    }

    private BookEntity requireBookEntity(UUID bookId) {
        BookEntity book = bookRepository
            .findOneByBookId(bookId.toString())
            .orElseThrow(EntityNotFoundException::new);
        return book;
    }
}
