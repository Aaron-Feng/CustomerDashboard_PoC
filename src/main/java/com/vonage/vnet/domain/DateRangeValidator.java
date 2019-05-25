package com.vonage.vnet.domain;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subaru.snet.csf.util.CSFBeanUtils;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    Logger logger = LoggerFactory.getLogger(DateRangeValidator.class);

    private boolean isRequired;
    private String regex;
    private String fromDate;
    private String toDate;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.isRequired = constraintAnnotation.required();
        this.regex = constraintAnnotation.regex();
        this.fromDate = constraintAnnotation.fromDate();
        this.toDate = constraintAnnotation.toDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Map<String, String> attributeMap = new HashMap<String, String>();
        CSFBeanUtils.populateValues(attributeMap, value, Object.class);
        String startDate = attributeMap.get(fromDate);
        String endDate = attributeMap.get(toDate);
        if (isRequired) {
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                return (startDate.matches(regex) && endDate.matches(regex)) ? validate(startDate, endDate) : false;
            }
            return false;
        } else {
            if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
                return (startDate.matches(regex) && endDate.matches(regex)) ? validate(startDate, endDate) : false;
            } else if (StringUtils.isBlank(startDate) && StringUtils.isBlank(endDate)) {
                return true;// none of dates are entered
            } else {// Either start date/end date is entered
                return (((StringUtils.isNotBlank(startDate) && startDate.matches(regex)) || (StringUtils.isNotBlank(endDate) && endDate.matches(regex)))); // either start or end date is entered
            }
        }
    }

    /**
     * validate(String , String) is responsible for checking if startDate is prior to endDate.
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    private boolean validate(String startDate, String endDate) {
        boolean isValidDate = false;
        try {
            String dateFormat = "MM/dd/yyyy";
            isValidDate = DateUtils.parseDate(startDate, dateFormat).before(DateUtils.parseDate(endDate, dateFormat));
        } catch (ParseException e) {
            logger.error("Unable to parse date [" + startDate + "," + endDate + "]");
        }
        return isValidDate;
    }
}
