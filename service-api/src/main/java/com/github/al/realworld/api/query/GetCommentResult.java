package com.github.al.realworld.api.query;

import com.github.al.realworld.api.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetCommentResult {

    private CommentDto comment;

}
