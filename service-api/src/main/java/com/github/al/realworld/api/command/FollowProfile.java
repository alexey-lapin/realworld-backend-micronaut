package com.github.al.realworld.api.command;

import com.github.al.bus.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FollowProfile implements Command<FollowProfileResult> {

    private String follower;
    private String followee;

}
