package com.example.financial_tracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TransactionAmountValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTransactionAmount {
  String message() default "Invalid transaction amount for the selected category";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
