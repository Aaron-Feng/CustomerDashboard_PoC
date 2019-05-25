package com.vonage.vnet.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity class for DPT_COMPONENT_REQ_ATTR table.
 */

@Entity
@Table(name = "DPT_COMPONENT_REQ_ATTR")
public class ComponentRequestAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ComponentRequestAttributeCompositeKey componentRequestAttributeId;

	@Column(name = "attrib_value")
	private String value;

	@Column(name = "create_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createDate;

	@Column(name = "create_user")
	private String createUser;

	@Column(name = "update_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;

	@Column(name = "update_user")
	private String updateUser;
	
	public ComponentRequestAttribute() {
		
	}
	
	public ComponentRequestAttribute(Long componentRequestId, String name, String value) {
		ComponentRequestAttributeCompositeKey key = new ComponentRequestAttributeCompositeKey();
		key.setComponentRequestId(componentRequestId);
		key.setName(name);
		setComponentRequestAttributeId(key);
		setValue(value);
	}

	public ComponentRequestAttributeCompositeKey getComponentRequestAttributeId() {
		return componentRequestAttributeId;
	}

	public void setComponentRequestAttributeId(ComponentRequestAttributeCompositeKey componentRequestAttributeId) {
		this.componentRequestAttributeId = componentRequestAttributeId;
	}

	public void setComponentRequestAttributeId(Long id, String name) {
		this.componentRequestAttributeId = new ComponentRequestAttributeCompositeKey(id, name);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
}