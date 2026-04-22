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
package com.java.workflow.cqrs.persistence.middlewares;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.core.CommandMiddleware;
import com.java.workflow.cqrs.persistence.repository.CommandStore;

public class DefaultCommandPersistenceMiddleware implements CommandMiddleware {

  private final CommandStore commandStore;

  public DefaultCommandPersistenceMiddleware(final CommandStore commandStore) {
    this.commandStore = commandStore;
  }

  @Override
  public void invoke(final Command<?> command) {
    commandStore.save(command);
  }
}
