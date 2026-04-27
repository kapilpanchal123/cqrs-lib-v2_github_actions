package org.workflow.cqrs.core;

public sealed interface PipelineResult<T> permits PipelineResult.Success, PipelineResult.Failure {

  record Success<T> (T value) implements PipelineResult<T> {}
  record Failure<T> (RuntimeException cause) implements PipelineResult<T> {}

  static <T> PipelineResult<T> success(final T value) {
    return new Success<>(value);
  }

  static <T> PipelineResult<T> failure(final RuntimeException cause) {
    return new Failure<>(cause);
  }

  default T getOrThrow() {
    return switch(this) {
      case Success<T> s -> s.value();
      case Failure<T> f -> throw f.cause();
    };
  }
}
