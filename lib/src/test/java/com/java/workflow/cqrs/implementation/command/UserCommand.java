package com.java.workflow.cqrs.implementation.command;

import com.java.workflow.cqrs.core.Command;
import com.java.workflow.cqrs.implementation.data.TestPayloadRequest;
import java.io.Serial;

public class UserCommand extends Command<TestPayloadRequest> {

  @Serial
  private static final long serialVersionUID = 1L;
}
