package com.anaptecs.jeaf.rest.resource.impl.validation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

public class ClassA {
  @NotNull
  public String name;

  @Null
  public String nullProperty;

}
