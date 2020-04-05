package com.github.al.realworld.domain.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Getter
@Entity
public class User {

    @Id
    private String username;
    private String email;
    private String password;
    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    private Profile profile;

}
