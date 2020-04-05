package com.github.al.realworld.api.command;

import com.github.al.realworld.api.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AddCommentResult {

    private CommentDto comment;

}
