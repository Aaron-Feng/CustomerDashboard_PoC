package com.vonage.vnet.param;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component("memo")
@Scope("prototype")
public class MemoForm extends GenericForm {
	private boolean internal;
	
	@NotBlank(message = "Memo text is required.")
	@Size(max = 2000, message = "Memo text must be {max} or less characters.")
	private String text;

	public boolean isInternal() {
		return internal;
	}

	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}