package com.github.al.bus;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.reflect.GenericTypeUtils;
import io.micronaut.inject.BeanDefinition;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
@Singleton
public class Registry {

    private final Map<Class<? extends Command>, CommandHandlerProvider> commandHandlerProviders = new HashMap<>();
    private final Map<Class<? extends Query>, QueryHandlerProvider> queryHandlerProviders = new HashMap<>();

    public Registry(ApplicationContext applicationContext) {
        Collection<BeanDefinition<CommandHandler>> commandHandlers = applicationContext.getBeanDefinitions(CommandHandler.class);
        commandHandlers.forEach(h -> registerCommandHandler(applicationContext, h));

        Collection<BeanDefinition<QueryHandler>> queryHandlers = applicationContext.getBeanDefinitions(QueryHandler.class);
        queryHandlers.forEach(h -> registerQueryHandler(applicationContext, h));
    }

    private void registerCommandHandler(ApplicationContext applicationContext, BeanDefinition<CommandHandler> commandHandler) {
        Class<?>[] handlerTypes = GenericTypeUtils.resolveInterfaceTypeArguments(commandHandler.getBeanType(), CommandHandler.class);
        Class<? extends Command> commandType = (Class<? extends Command>) handlerTypes[1];
        commandHandlerProviders.put(commandType, new CommandHandlerProvider(applicationContext, commandHandler.getBeanType()));
    }

    private void registerQueryHandler(ApplicationContext applicationContext, BeanDefinition<QueryHandler> queryHandler) {
        Class<?>[] handlerTypes = GenericTypeUtils.resolveInterfaceTypeArguments(queryHandler.getBeanType(), QueryHandler.class);
        Class<? extends Query> queryType = (Class<? extends Query>) handlerTypes[1];
        queryHandlerProviders.put(queryType, new QueryHandlerProvider(applicationContext, queryHandler.getBeanType()));
    }

    <R, C extends Command<R>> CommandHandler<R, C> getCommandHandler(Class<C> commandType) {
        return commandHandlerProviders.get(commandType).get();
    }

    <R, Q extends Query<R>> QueryHandler<R, Q> getQueryHandler(Class<Q> queryType) {
        return queryHandlerProviders.get(queryType).get();
    }

}
