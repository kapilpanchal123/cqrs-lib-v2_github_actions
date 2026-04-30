/**
 * General utility abstractions and helpers used across the CQRS framework.
 *
 * <h2>Overview</h2>
 * <p>This package provides reusable utility components that support
 * common cross-cutting concerns such as:
 * <ul>
 *   <li>Component ordering</li>
 *   <li>JSON serialization and mapping</li>
 *   <li>Type conversion utilities</li>
 * </ul>
 *
 * <p>These utilities are intentionally lightweight and framework-agnostic,
 * ensuring they can be reused across different parts of the CQRS pipeline
 * without introducing tight coupling.
 *
 * <h2>Key Components</h2>
 * <ul>
 *   <li><b>Ordering:</b> {@link org.workflow.cqrs.utils.Ordered} enables
 *       deterministic execution of pipeline components.</li>
 *   <li><b>JSON Mapping:</b> Utilities for serializing and deserializing
 *       command payloads while preserving type information.</li>
 *   <li><b>Converters:</b> Helpers for integrating JSON with persistence layers.</li>
 * </ul>
 *
 * <h2>Design Principles</h2>
 * <ul>
 *   <li><b>Reusability:</b> Designed to be used across multiple layers</li>
 *   <li><b>Framework Independence:</b> Avoids reliance on specific frameworks</li>
 *   <li><b>Consistency:</b> Ensures uniform behavior for serialization and ordering</li>
 * </ul>
 *
 * <h2>Thread Safety</h2>
 * <p>Most utilities are stateless or rely on thread-safe components
 * (e.g., Jackson {@code ObjectMapper}) and are safe for concurrent use.
 *
 * @since 1.0
 */
package org.workflow.cqrs.utils;