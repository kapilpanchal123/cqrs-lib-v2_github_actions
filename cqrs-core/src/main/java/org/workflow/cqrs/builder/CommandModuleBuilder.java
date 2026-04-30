package org.workflow.cqrs.builder;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.workflow.cqrs.core.CommandExecutor;
import org.workflow.cqrs.core.CommandHandler;
import org.workflow.cqrs.core.CommandMiddleware;
import org.workflow.cqrs.core.CommandPipeline;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.CommandRouter;
import org.workflow.cqrs.core.CommandStore;
import org.workflow.cqrs.defaults.DefaultCommandPersistenceMiddleware;
import org.workflow.cqrs.defaults.DefaultCommandPipeline;
import org.workflow.cqrs.defaults.DefaultCommandPostProcessor;
import org.workflow.cqrs.defaults.DefaultCommandRouter;
import org.workflow.cqrs.defaults.DefaultSynchronousCommandExecutor;
import org.workflow.cqrs.utils.Ordered;

public class CommandModuleBuilder {

//  private CommandStore commandStore;
//  private ObjectMapper objectMapper;
//  private List<CommandHandler<?,?>> handlers = new ArrayList<>();
//
//  private final List<CommandMiddleware> middlewares = new ArrayList<>();
//  private final List<CommandPostProcessor<?>> postProcessors = new ArrayList<>();
//
//  private Boolean useDefaultMiddleware = Boolean.TRUE;
//  private Boolean useDefaultPostProcessors = Boolean.TRUE;
//
//  public CommandModuleBuilder commandStore(final CommandStore commandStore) {
//    this.commandStore = commandStore;
//    return this;
//  }
//
//  public CommandModuleBuilder objectMapper(final ObjectMapper objectMapper) {
//    this.objectMapper = objectMapper;
//    return this;
//  }
//
//  public CommandModuleBuilder handlers(final List<CommandHandler<?,?>> handlers) {
//    if(handlers != null) {
//      this.handlers = handlers;
//    }
//    return this;
//  }
//
//  public CommandModuleBuilder addMiddleware(final CommandMiddleware middleware) {
//    if(middleware != null) {
//      this.middlewares.add(middleware);
//    }
//    return this;
//  }
//
//  public CommandModuleBuilder addMiddlewares(Collection<? extends CommandMiddleware> middlewares) {
//    if(middlewares != null) {
//      this.middlewares.addAll(middlewares);
//    }
//    return this;
//  }
//
//  public CommandModuleBuilder disableDefaultMiddleware() {
//    this.useDefaultMiddleware = Boolean.FALSE;
//    return this;
//  }
//
//  public CommandModuleBuilder addPostProcessor(final CommandPostProcessor<?> postProcessor) {
//    if(postProcessor != null) {
//      this.postProcessors.add(postProcessor);
//    }
//    return this;
//  }
//
//  public CommandModuleBuilder addPostProcessors(final Collection<? extends CommandPostProcessor<?>> processors) {
//    if(processors != null) {
//      this.postProcessors.addAll(processors);
//    }
//    return this;
//  }
//
//  public CommandModuleBuilder disablePostProcessor() {
//    this.useDefaultPostProcessors = Boolean.FALSE;
//    return this;
//  }
//
//  public CommandPipeline build() {
//    validate();
//
//    final CommandStore store = this.commandStore;
//
//    final Set<CommandMiddleware> middlewareSet = new LinkedHashSet<>();
//
//    if(useDefaultMiddleware) {
//      middlewareSet.add(new DefaultCommandPersistenceMiddleware(store));
//    }
//
//    middlewareSet.addAll(this.middlewares);
//
//    final List<CommandMiddleware> finalMiddlewares = new ArrayList<>(middlewareSet);
//    sortIfOrdered(finalMiddlewares);
//
//    final CommandRouter router = new DefaultCommandRouter(handlers);
//    final CommandExecutor executor = new DefaultSynchronousCommandExecutor(finalMiddlewares, router);
//
//    final Set<CommandPostProcessor<?>>  processorSet = new LinkedHashSet<>();
//
//    if(useDefaultPostProcessors) {
//      processorSet.add(new DefaultCommandPostProcessor<>(store));
//    }
//
//    processorSet.addAll(postProcessors);
//
//    final List<CommandPostProcessor<?>> finalPostProcessors = new ArrayList<>(processorSet);
//    sortIfOrdered(finalPostProcessors);
//
//    return new DefaultCommandPipeline(executor, finalPostProcessors);
//  }
//
//  private void validate() {
//    if(commandStore == null) {
//      throw new IllegalStateException("CommandStore must not be null");
//    }
//    if(objectMapper == null) {
//      throw new IllegalStateException("ObjectMapper must not be null");
//    }
//    if(handlers == null || handlers.isEmpty()) {
//      throw new IllegalStateException("Atleast one Command handler is required");
//    }
//  }
//
//  private <T> void sortIfOrdered(final List<T> list) {
//    list.sort(Comparator.comparingInt(this::resolveOrder));
//  }
//
//  private int resolveOrder(final Object object) {
//    return (object instanceof Ordered ordered) ? ordered.getOrder() : 0;
//  }
}
