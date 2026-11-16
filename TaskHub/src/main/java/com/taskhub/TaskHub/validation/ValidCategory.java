package com.taskhub.TaskHub.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryValidator.class)
@Documented
public @interface ValidCategory {
    String message() default "Nieprawidłowa kategoria! dozwolone wartości to: Backend, Frontend, DevOps, Other ";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
