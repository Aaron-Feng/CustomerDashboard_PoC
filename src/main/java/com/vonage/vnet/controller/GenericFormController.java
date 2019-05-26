package com.vonage.vnet.controller;

import javax.servlet.ServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Controller class responsible for handling data submission pertaining to
 * generic forms.
 * 
 */

@Controller
public class GenericFormController {

	@Autowired
	private ExhibitController exhibitController;

	@Autowired
	private GenericFormService genericFormService;

	@Autowired
	private Validator validator;

	/**
	 * submitGenericForm(ServletRequest, Model, GenericForm, BindingResult) method is invoked when submit button on form component is clicked.
	 * 
	 * @param request
	 * @param model
	 * @param form
	 * @param result
	 * @return String
	 */
	@RequestMapping(value = "/submitGenericForm", method = RequestMethod.POST)
	public String submitGenericForm(ServletRequest request, Model model, GenericForm form, BindingResult result) {
		GenericForm newForm = genericFormService.getGenericForm(form.getComponentReference());
		ServletRequestDataBinder binder = new ServletRequestDataBinder(newForm, "form");
		binder.bind(request);
		binder.setValidator(validator);
		binder.validate();
		model.addAllAttributes(binder.getBindingResult().getModel());
		
		// If the form validates, save the form
		if (!binder.getBindingResult().hasErrors()) {
			// Run any @PostSubmit methods
			CSFBeanUtils.executeAnnotatedPostSubmitMethods(newForm);
			genericFormService.save(newForm);
			// Run any @PostDataSave methods
            CSFBeanUtils.executeAnnotatedPostDataSaveMethods(newForm);
		}
		
		return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
	}
}