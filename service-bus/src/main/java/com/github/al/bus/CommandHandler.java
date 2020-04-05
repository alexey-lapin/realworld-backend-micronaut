package com.github.al.bus;

public interface CommandHandler<R, C extends Command<R>> {

    R handle(C command);

}
