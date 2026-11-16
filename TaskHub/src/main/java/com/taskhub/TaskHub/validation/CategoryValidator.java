package com.taskhub.TaskHub.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class CategoryValidator implements ConstraintValidator<ValidCategory,String> {
    private static final List<String> ALLOWED_CATEGORIES = List.of("Backend","Frontend","Devops","Other");
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
       if (value == null){
           return true;
       }
       return ALLOWED_CATEGORIES.contains(value);
    }
}
