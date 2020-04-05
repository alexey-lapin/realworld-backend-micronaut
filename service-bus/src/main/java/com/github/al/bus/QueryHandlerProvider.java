package com.github.al.bus;

import io.micronaut.context.ApplicationContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class QueryHandlerProvider<H extends QueryHandler<?, ?>> {

    private final ApplicationContext applicationContext;
    private final Class<H> type;

    H get() {
        return applicationContext.getBean(type);
    }

}
