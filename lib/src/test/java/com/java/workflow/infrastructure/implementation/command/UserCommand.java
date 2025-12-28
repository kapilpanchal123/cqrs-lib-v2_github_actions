package com.java.workflow.infrastructure.implementation.command;

import com.java.workflow.infrastructure.core.Command;
import com.java.workflow.infrastructure.implementation.data.TestPayloadRequest;
import java.io.Serial;

public class UserCommand extends Command<TestPayloadRequest> {

  @Serial
  private static final long serialVersionUID = 1L;
}
