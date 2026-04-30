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
package org.workflow.cqrs.support.data;

import java.io.Serial;
import java.io.Serializable;

public class TestPayloadRequest implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String username;
  private String email;
  private String firstname;
  private String lastname;
  private Integer age;

  public TestPayloadRequest() {
  }

  public TestPayloadRequest(String username, String email, String firstname, String lastname, Integer age) {
    this.username = username;
    this.email = email;
    this.firstname = firstname;
    this.lastname = lastname;
    this.age = age;
  }

  public String getUsername() {
    return username;
  }

  public Integer getAge() {
    return age;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstname() {
    return firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public void setAge(Integer age) {
    this.age = age;
  }
}
