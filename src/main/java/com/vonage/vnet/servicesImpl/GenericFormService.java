package com.vonage.vnet.servicesImpl;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * This is the service class consisting methods for populating and saving data
 * to database for the components of type form.
 */

@Service
public class GenericFormService implements BeanFactoryAware {
	Logger logger = LoggerFactory.getLogger(GenericFormService.class);

	@Autowired
	private ComponentRequestAttributeRepository componentRequestAttributeRepository;

	@Autowired
	private ExhibitService exhibitService;

	@Autowired
	private ComponentRequestRepository componentRequestRepository;

	@Autowired
	private SNETUserContext userContext;

	private BeanFactory beanFactory;

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * getGenericForm(String ) is responsible to return the form bean from
	 * beanFactory for the given reference.
	 * 
	 * @param reference
	 * @return form
	 */
	public GenericForm getGenericForm(String reference) {
		GenericForm form = (GenericForm) beanFactory.getBean(reference);

		if (form == null) {
			logger.error("Invalid generic form requested: " + reference);
		}

		return form;
	}

	/**
	 * save(GenericForm) is responsible to save from data to database.
	 * 
	 * @param form
	 */
	public void save(GenericForm form) {
		exhibitService.createRequestComponents(form, true);

		Map<String, String> values = form.getValues();

		// get existing attributes for this componentRequestId
		List<ComponentRequestAttribute> attributeList = componentRequestAttributeRepository.findByComponentRequestId(form.getComponentRequestId());
		Date createDate = new Date();
		String userName = userContext.getUser().getUsername();
				
		if (!attributeList.isEmpty()) {
			// get create date from existing attributes to preserve it post deletion
			createDate = attributeList.get(0).getCreateDate(); 
		}

		Set<ComponentRequestAttribute> attributes = new HashSet<ComponentRequestAttribute>();
		for (Map.Entry<String, String> entry : values.entrySet()) {
			ComponentRequestAttribute attribute = new ComponentRequestAttribute();
			attribute.setComponentRequestAttributeId(form.getComponentRequestId(), entry.getKey());
			attribute.setValue(entry.getValue());
			attribute.setCreateDate(createDate);
			attribute.setCreateUser(userName);
			attribute.setUpdateDate(new Date());
			attribute.setUpdateUser(userName);
			attributes.add(attribute);
		}

		// Delete the existing attributes
		componentRequestAttributeRepository.delete(attributeList); 

		// Save the attributes those have been submitted, i.e. the actual form field names and values
		componentRequestAttributeRepository.save(attributes); 
		componentRequestRepository.updateCompletedInd(form.getComponentRequestId(), true, userName, new Date());
	}

	/**
	 * populate(GenericForm, Long, Long, Long) is responsible to populate form data for the
	 * given componentRequestId from database.
	 * 
	 * @param form
	 * @param packageRequestId
	 * @param exhibitRequestId
	 * @param componentRequestId
	 */
	public void populate(GenericForm form, Long packageRequestId, Long exhibitRequestId, Long componentRequestId) {
		form.setPackageRequestId(packageRequestId);
		form.setExhibitRequestId(exhibitRequestId);
		form.setComponentRequestId(componentRequestId);
		
		Collection<ComponentRequestAttribute> attributes = componentRequestAttributeRepository.findByComponentRequestId(componentRequestId);

		if (attributes != null && !attributes.isEmpty()) {
			for (ComponentRequestAttribute attribute : attributes) {
				form.setValue(attribute.getComponentRequestAttributeId().getName(), attribute.getValue());
			}
		}
		CSFBeanUtils.executeAnnotatedPostDataLoadMethods(form);
	}
}