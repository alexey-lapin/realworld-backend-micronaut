package com.github.al.realworld.application;

import com.github.al.realworld.api.dto.CommentDto;
import com.github.al.realworld.domain.model.Comment;
import com.github.al.realworld.domain.model.Profile;

public class CommentAssembler {

    public static CommentDto assemble(Comment comment, Profile currentProfile) {
        return CommentDto.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .body(comment.getBody())
                .author(ProfileAssembler.assemble(comment.getAuthor(), currentProfile))
                .build();
    }

}
