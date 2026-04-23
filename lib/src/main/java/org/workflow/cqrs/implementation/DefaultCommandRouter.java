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
package org.workflow.cqrs.implementation;

import java.util.List;
import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandHandler;
import org.workflow.cqrs.core.CommandRouter;

public class DefaultCommandRouter implements CommandRouter {
  private final List<CommandHandler<?,?>> commandHandlers;

  public DefaultCommandRouter(final List<CommandHandler<?,?>> commandHandlers) {
    this.commandHandlers = commandHandlers;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <REQ,RES> CommandHandler<REQ,RES> route(final Command<REQ> command) {
    if(command == null) {
      throw new IllegalArgumentException("Command must not be null");
    }

    return (CommandHandler<REQ,RES>) commandHandlers
        .stream()
        .filter(handler -> handler.matches(command))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Command Handler Not Found for CommandId = " + command.getId()));
  }
}
