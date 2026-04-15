package com.java.workflow.infrastructure.implementation.data;

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
