package com.vonage.vnet.param;

import java.util.List;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.subaru.snet.csf.annotation.IgnoreField;
import com.subaru.snet.dpt.domain.GenericForm;
import com.subaru.snet.dpt.domain.LookupType;
import com.subaru.snet.dpt.service.LookupService;

/**
 * Form bean class for Personal References Component.
 */

@Component("personalreferences")
@Scope("prototype")
public class PersonalReferencesForm extends GenericForm {

	@Autowired
	private LookupService lookupService;

	@NotBlank(message = "Name 1 is required.")
	@Size(max = 500, message = "Name 1 must be {max} or less characters.")
	private String ref1Name;

	@NotBlank(message = "Occupation 1 is required.")
	@Size(max = 500, message = "Occupation 1 must be {max} or less characters.")
	private String ref1Occ;

	@NotBlank(message = "Known how long 1 is required.")
	@Size(max = 2, message = "Known how long 1 must be between 1 and 99.")
	private String ref1Yrs;

	@NotBlank(message = "Address 1 is required.")
	@Size(max = 500, message = "Address 1 must be {max} or less characters.")
	private String ref1Street;

	@NotBlank(message = "City 1 is required.")
	@Size(max = 500, message = "City 1 must be {max} or less characters.")
	private String ref1City;

	@NotBlank(message = "State 1 is required.")
	@Size(max = 2, message = "State 1 must be {max} or less characters.")
	private String ref1ST;

	@NotBlank(message = "Phone 1 is required.")
	@Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Please enter Phone 1 with format xxx-xxx-xxxx.")
	private String ref1Phone;

	@NotBlank(message = "Name 2 is required.")
	@Size(max = 500, message = "Name 2 must be {max} or less characters.")
	private String ref2Name;

	@NotBlank(message = "Occupation 2 is required.")
	@Size(max = 500, message = "Occupation 2 must be {max} or less characters.")
	private String ref2Occ;

	@NotBlank(message = "Known how long 2 is required.")
	@Size(max = 2, message = "Known how long 2 must be between 1 and 99.")
	private String ref2Yrs;

	@NotBlank(message = "Address 2 is required.")
	@Size(max = 500, message = "Address 2 must be {max} or less characters.")
	private String ref2Street;

	@NotBlank(message = "City 2 is required.")
	@Size(max = 500, message = "City 2 must be {max} or less characters.")
	private String ref2City;

	@NotBlank(message = "State 2 is required.")
	@Size(max = 2, message = "State 2 must be {max} or less characters.")
	private String ref2ST;

	@NotBlank(message = "Phone 2 is required.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Please enter Phone 2 with format xxx-xxx-xxxx.")
	private String ref2Phone;

	@NotBlank(message = "Name 3 is required.")
	@Size(max = 500, message = "Name 3 must be {max} or less characters.")
	private String ref3Name;

	@NotBlank(message = "Occupation 3 is required.")
	@Size(max = 500, message = "Occupation 3 must be {max} or less characters.")
	private String ref3Occ;

	@NotBlank(message = "Known how long 3 is required.")
	@Size(max = 2, message = "Known how long 3 must be between 1 and 99.")
	private String ref3Yrs;

	@NotBlank(message = "Address 3 is required.")
	@Size(max = 500, message = "Address 3 must be {max} or less characters.")
	private String ref3Street;

	@NotBlank(message = "City 3 is required.")
	@Size(max = 500, message = "City 3 must be {max} or less characters.")
	private String ref3City;

	@NotBlank(message = "State 3 is required.")
	@Size(max = 2, message = "State 3 must be {max} or less characters.")
	private String ref3ST;

	@NotBlank(message = "Phone 3 is required.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3}-[0-9]{4}$", message = "Please enter Phone 3 with format xxx-xxx-xxxx.")
	private String ref3Phone;

	public String getRef1Name() {
		return ref1Name;
	}

	public void setRef1Name(String ref1Name) {
		this.ref1Name = ref1Name;
	}

	public String getRef1Occ() {
		return ref1Occ;
	}

	public void setRef1Occ(String ref1Occ) {
		this.ref1Occ = ref1Occ;
	}

	public String getRef1Yrs() {
		return ref1Yrs;
	}

	public void setRef1Yrs(String ref1Yrs) {
		this.ref1Yrs = ref1Yrs;
	}

	public String getRef1Street() {
		return ref1Street;
	}

	public void setRef1Street(String ref1Street) {
		this.ref1Street = ref1Street;
	}

	public String getRef1City() {
		return ref1City;
	}

	public void setRef1City(String ref1City) {
		this.ref1City = ref1City;
	}

	public String getRef1ST() {
		return ref1ST;
	}

	public void setRef1ST(String ref1st) {
		ref1ST = ref1st;
	}

	public String getRef1Phone() {
		return ref1Phone;
	}

	public void setRef1Phone(String ref1Phone) {
		this.ref1Phone = ref1Phone;
	}

	public String getRef2Name() {
		return ref2Name;
	}

	public void setRef2Name(String ref2Name) {
		this.ref2Name = ref2Name;
	}

	public String getRef2Occ() {
		return ref2Occ;
	}

	public void setRef2Occ(String ref2Occ) {
		this.ref2Occ = ref2Occ;
	}

	public String getRef2Yrs() {
		return ref2Yrs;
	}

	public void setRef2Yrs(String ref2Yrs) {
		this.ref2Yrs = ref2Yrs;
	}

	public String getRef2Street() {
		return ref2Street;
	}

	public void setRef2Street(String ref2Street) {
		this.ref2Street = ref2Street;
	}

	public String getRef2City() {
		return ref2City;
	}

	public void setRef2City(String ref2City) {
		this.ref2City = ref2City;
	}

	public String getRef2ST() {
		return ref2ST;
	}

	public void setRef2ST(String ref2st) {
		ref2ST = ref2st;
	}

	public String getRef2Phone() {
		return ref2Phone;
	}

	public void setRef2Phone(String ref2Phone) {
		this.ref2Phone = ref2Phone;
	}

	public String getRef3Name() {
		return ref3Name;
	}

	public void setRef3Name(String ref3Name) {
		this.ref3Name = ref3Name;
	}

	public String getRef3Occ() {
		return ref3Occ;
	}

	public void setRef3Occ(String ref3Occ) {
		this.ref3Occ = ref3Occ;
	}

	public String getRef3Yrs() {
		return ref3Yrs;
	}

	public void setRef3Yrs(String ref3Yrs) {
		this.ref3Yrs = ref3Yrs;
	}

	public String getRef3Street() {
		return ref3Street;
	}

	public void setRef3Street(String ref3Street) {
		this.ref3Street = ref3Street;
	}

	public String getRef3City() {
		return ref3City;
	}

	public void setRef3City(String ref3City) {
		this.ref3City = ref3City;
	}

	public String getRef3ST() {
		return ref3ST;
	}

	public void setRef3ST(String ref3st) {
		ref3ST = ref3st;
	}

	public String getRef3Phone() {
		return ref3Phone;
	}

	public void setRef3Phone(String ref3Phone) {
		this.ref3Phone = ref3Phone;
	}
	
	@IgnoreField
	public List<String> getStateOptions() {
		return lookupService.getLookupValues(LookupType.STATE_CODE);
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PersonalReferencesForm [ref1Name=");
        builder.append(ref1Name);
        builder.append(", ref1Occ=");
        builder.append(ref1Occ);
        builder.append(", ref1Yrs=");
        builder.append(ref1Yrs);
        builder.append(", ref1Street=");
        builder.append(ref1Street);
        builder.append(", ref1City=");
        builder.append(ref1City);
        builder.append(", ref1ST=");
        builder.append(ref1ST);
        builder.append(", ref1Phone=");
        builder.append(ref1Phone);
        builder.append(", ref2Name=");
        builder.append(ref2Name);
        builder.append(", ref2Occ=");
        builder.append(ref2Occ);
        builder.append(", ref2Yrs=");
        builder.append(ref2Yrs);
        builder.append(", ref2Street=");
        builder.append(ref2Street);
        builder.append(", ref2City=");
        builder.append(ref2City);
        builder.append(", ref2ST=");
        builder.append(ref2ST);
        builder.append(", ref2Phone=");
        builder.append(ref2Phone);
        builder.append(", ref3Name=");
        builder.append(ref3Name);
        builder.append(", ref3Occ=");
        builder.append(ref3Occ);
        builder.append(", ref3Yrs=");
        builder.append(ref3Yrs);
        builder.append(", ref3Street=");
        builder.append(ref3Street);
        builder.append(", ref3City=");
        builder.append(ref3City);
        builder.append(", ref3ST=");
        builder.append(ref3ST);
        builder.append(", ref3Phone=");
        builder.append(ref3Phone);
        builder.append("]");
        return builder.toString();
    }

}