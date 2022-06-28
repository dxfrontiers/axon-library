package de.dxfrontiers.demo.axon.library.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "READER")
@Data
public class ReaderEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "READER_ID", unique = true)
    private String readerId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "ADDRESS")
    private String address;

    public ReaderEntity setReaderId(UUID readerId) {
        this.readerId = readerId.toString();
        return this;
    }

    public UUID getReaderId() {
        return UUID.fromString(readerId);
    }
}
