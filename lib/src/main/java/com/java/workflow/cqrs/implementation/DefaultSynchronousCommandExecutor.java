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

import com.java.workflow.cqrs.core.*;
import java.util.List;
import java.util.function.Supplier;

public class DefaultSynchronousCommandExecutor implements CommandExecutor {
  private final List<CommandMiddleware> middlewares;
  private final CommandRouter router;

  public DefaultSynchronousCommandExecutor(
      final List<CommandMiddleware> middlewares,
      final CommandRouter router) {
    this.middlewares = middlewares;
    this.router = router;
  }

  @Override
  public <REQ, RES> Supplier<RES> execute(final Command<REQ> command) {
    for(final CommandMiddleware middleware : middlewares) {
      middleware.invoke(command);
    }
    final CommandHandler<REQ,RES> handler = router.route(command);
    return(() -> handler.handle(command));
  }
}
