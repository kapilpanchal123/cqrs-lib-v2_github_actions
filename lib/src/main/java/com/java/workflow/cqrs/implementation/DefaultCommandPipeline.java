/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.java.workflow.cqrs.implementation;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.core.CommandExecutor;
import com.java.workflow.cqrs.core.CommandPipeline;
import com.java.workflow.cqrs.core.CommandPostProcessor;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultCommandPipeline implements CommandPipeline {

  private final CommandExecutor executor;
  private final List<CommandPostProcessor<?>> postProcessor;

  public DefaultCommandPipeline(final CommandExecutor executor,
                                final List<CommandPostProcessor<?>> postProcessor) {
    this.executor = executor;
    this.postProcessor = postProcessor;
  }

  @Override
  public <REQ,RES> Supplier<RES> send(final Command<REQ> command) {
    Objects.requireNonNull(command, "Command Must Not be Null.");
    Supplier<RES> baseSupplier = executor.execute(command);

    // Wrap base supplier with post-processing
    return () -> {
      RES result = null;
      Throwable exception = null;

      try {
        result = baseSupplier.get();
      } catch(Throwable e) {
        exception = e;
      }

      // Execute post-processing with both result and exception
      for(CommandPostProcessor<?> postProcess : postProcessor) {
        CommandPostProcessor<REQ> typedProcessor = (CommandPostProcessor<REQ>) postProcess;
        typedProcessor.run(command);
      }

      // If exception occurred, propagate it
      if(exception != null) {
        throw new RuntimeException("Handler Execution Failed: " + exception);
      }
      return result;
    };
  }
}
