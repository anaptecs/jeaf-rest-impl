package com.anaptecs.jeaf.rest.resource.impl.validation;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;

import com.anaptecs.jeaf.rest.resource.api.ValidationExecutor;
import com.anaptecs.jeaf.rest.resource.impl.ValidationExecutorImpl;

public class ValidationExecutorTest {

  @Test
  void testRequestValidation( ) {
    ValidationExecutor lValidationExecutor = new ValidationExecutorImpl();

    ClassA lA = new ClassA();
    lA.name = "Hello";
    ClassA lB = new ClassA();
    lB.name = null;

    ClassA lC = new ClassA();
    lC.name = "Yet another name";
    lC.nullProperty = "not null";

    ClassA lD = new ClassA();
    lD.name = "Yet another name";
    lD.nullProperty = "not null";

    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, lA, lB);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(1, e.getConstraintViolations().size());
      assertEquals("{javax.validation.constraints.NotNull.message}",
          e.getConstraintViolations().iterator().next().getMessageTemplate());
    }

    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, lA, lB, lC);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(2, e.getConstraintViolations().size());
    }

    // Test arrays as parameter
    ClassA[] lArrayParam = new ClassA[] { lA, lB, lC };
    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, (Object) lArrayParam);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(2, e.getConstraintViolations().size());
    }

    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, lD, lArrayParam);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(3, e.getConstraintViolations().size());
    }

    // Test usage of primitive arrays
    int[] lIntArray = new int[] { 1, 2, 3 };
    lValidationExecutor.validateRequest(ValidationExecutorTest.class, lIntArray);
    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, lIntArray, lD);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(1, e.getConstraintViolations().size());
    }

    // Test collections as param
    List<ClassA> lList = Arrays.asList(lA, lB, lC);
    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, lList);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(2, e.getConstraintViolations().size());
    }

    try {
      lValidationExecutor.validateRequest(ValidationExecutorTest.class, lD, lList);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(3, e.getConstraintViolations().size());
    }
  }

  @Test
  void testResponseValidation( ) {
    ClassA lA = new ClassA();
    lA.name = "Hello";
    ClassA lB = new ClassA();
    lB.name = null;

    ValidationExecutor lValidationExecutor = new ValidationExecutorImpl();
    lValidationExecutor.validateResponse(getClass(), lA);

    try {
      lValidationExecutor.validateResponse(getClass(), lB);
      fail();
    }
    catch (ConstraintViolationException e) {
      assertEquals(1, e.getConstraintViolations().size());
    }
  }
}
