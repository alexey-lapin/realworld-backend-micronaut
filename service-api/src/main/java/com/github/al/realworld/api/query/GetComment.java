package com.github.al.realworld.api.query;

import com.github.al.bus.Query;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GetComment implements Query<GetCommentResult> {

    private String currentUsername;
    private String slug;
    private Long id;

}
