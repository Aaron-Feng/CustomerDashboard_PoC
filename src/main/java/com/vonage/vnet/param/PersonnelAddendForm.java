package com.vonage.vnet.param;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;



/**
 * Form bean class for Personnel Addendum Component.
 */

@Component("personneladdend")
@Scope("prototype")
public class PersonnelAddendForm extends GenericForm {

	@NotBlank(message = "Personnel deficiencies is required.")
	@Size(max = 500, message = "Personnel deficiencies must be {max} or less characters.")
	private String personnelDef;

	public String getPersonnelDef() {
		return personnelDef;
	}

	public void setPersonnelDef(String personnelDef) {
		this.personnelDef = personnelDef;
	}
}
