package com.github.al.realworld.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class Comment {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue
    private Long id;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    @Lob
    private String body;
    @OneToOne
    private Profile author;

}
