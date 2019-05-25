package com.vonage.vnet.domain;

import java.text.ParseException;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

    Logger logger = LoggerFactory.getLogger(DateValidator.class);
    
    private boolean isRequired;
    private DateValidationType validationType;
    private String regex;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        this.isRequired = constraintAnnotation.isRequired();
        this.validationType = constraintAnnotation.dateValidationType();
        this.regex = constraintAnnotation.regex();
    }

    @Override
    public boolean isValid(String date, ConstraintValidatorContext context) {
        if (isRequired) {
            return (StringUtils.isNotBlank(date) && date.matches(regex)) ? validate(date) : false;
        } else {
            return StringUtils.isBlank(date) ? true : (date.matches(regex) ? validate(date) : false);
        }
    }

    /**
     * validate(String) method is responsible for validating a date based on the DateValidationType.
     * 
     * @param dateToValidate
     * @return
     */
    private boolean validate(String dateToValidate) {
        boolean isValid = false;
        if (dateToValidate.matches(regex)) {
            try {
                Date date = DateUtils.parseDate(dateToValidate, "MM/dd/yyyy");
                Date currentDate = new Date();
                boolean isSameDay = DateUtils.isSameDay(date, currentDate);
                switch (validationType) {
                    case FUTURE_DATE:
                        isValid = (!isSameDay && date.after(currentDate)) ? true : false;
                        break;
                    case PAST_DATE:
                        isValid = (!isSameDay && date.before(currentDate)) ? true : false;
                        break;
                    case ONGOING:
                        isValid = (isSameDay || date.after(currentDate)) ? true : false;
                        break;
                }
            } catch (ParseException e) {
                logger.error("Unable to parse date [" + dateToValidate + "]");
            }
        }
        return isValid;
    }
}
