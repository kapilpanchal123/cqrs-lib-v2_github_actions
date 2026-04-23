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
package org.workflow.cqrs.postprocess;

import org.workflow.cqrs.core.Command;
import org.workflow.cqrs.core.CommandPostProcessor;
import org.workflow.cqrs.core.CommandStatus;
import org.workflow.cqrs.persistence.repository.CommandStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResiliencePostProcessor<T> implements CommandPostProcessor<T> {

  private static final String STATUS = "SUCCESSFUL";
  private static final Logger log = LoggerFactory.getLogger(DefaultResiliencePostProcessor.class);

  private final CommandStore commandStore;

  public DefaultResiliencePostProcessor(final CommandStore commandStore) {
    this.commandStore = commandStore;
  }

  @Override
  public void run(final Command<T> command) {
    commandStore.updateStatus(command.getId(), CommandStatus.COMPLETED);
  }
}
