package de.dxfrontiers.demo.axon.library.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReaderRepository extends JpaRepository<ReaderEntity, Long> {

    Optional<ReaderEntity> findOneByReaderId(String readerId);
}
