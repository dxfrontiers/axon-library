package de.dxfrontiers.demo.axon.library.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "CATALOG")
@Data
public class CatalogEntity {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "ISBN", unique = true)
    private String isbn;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AVAILABLE_COUNT")
    private int available = 0;

    @Column(name = "TOTAL_COUNT")
    private int total = 0;

}
