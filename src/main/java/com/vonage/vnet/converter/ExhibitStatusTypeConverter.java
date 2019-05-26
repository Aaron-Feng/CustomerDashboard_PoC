package com.vonage.vnet.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


/*
 * This class is necessary to convert uppercase enumerations to proper lowercase database values
 */
@Converter
public class ExhibitStatusTypeConverter implements AttributeConverter<ExhibitStatusType, String> {
	@Override
	public String convertToDatabaseColumn(ExhibitStatusType value) {
		return value != null ? value.toString().toLowerCase() : null;
	}
	
	@Override
	public ExhibitStatusType convertToEntityAttribute(String value) {
		return value != null ? ExhibitStatusType.valueOf(value.toUpperCase()) : null;
	}
}