package com.github.al.realworld.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity
public class Tag {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    public Tag(String name) {
        this.name = name;
    }
}
