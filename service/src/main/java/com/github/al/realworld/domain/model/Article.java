package com.github.al.realworld.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Entity
public class Article {

    @Id
    private UUID id;
    private String slug;
    private String title;
    private String description;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Lob
    private String body;

    @OneToOne
    private Profile author;

    @Singular
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "article_id")
    private Set<Comment> comments;

    @Singular
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags;

    @Singular
    @ManyToMany
    @JoinTable(name = "article_favorites",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "username")
    )
    private Set<Profile> favoredProfiles;

}
