package com.github.al.realworld.api.query;

import com.github.al.realworld.api.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class GetCommentsResult {

    private List<CommentDto> comments;

}
