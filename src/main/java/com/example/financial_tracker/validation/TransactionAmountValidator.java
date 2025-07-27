package com.example.financial_tracker.validation;

import com.example.financial_tracker.dto.TransactionDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class TransactionAmountValidator implements ConstraintValidator<ValidTransactionAmount, TransactionDTO> {

  @Override
  public void initialize(ValidTransactionAmount constraintAnnotation) {
  }

  @Override
  public boolean isValid(TransactionDTO transaction, ConstraintValidatorContext context) {
    if (transaction == null || transaction.getAmount() == null || transaction.getType() == null) {
      return true;
    }

    BigDecimal amount = transaction.getAmount();
    String type = transaction.getType();

    if ("EXPENSE".equals(type) && amount.compareTo(new BigDecimal("10000")) > 0) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Expense amount exceeds limit of $10,000")
        .addPropertyNode("amount")
        .addConstraintViolation();
      return false;
    }

    if ("INCOME".equals(type) && amount.compareTo(new BigDecimal("1000000")) > 0) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Income amount exceeds limit of $1,000,000")
        .addPropertyNode("amount")
        .addConstraintViolation();
      return false;
    }

    return true;
  }
}
