package com.github.al.realworld.api.command;

import com.github.al.bus.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UnfollowProfile implements Command<UnfollowProfileResult> {

    private String follower;
    private String followee;

}
