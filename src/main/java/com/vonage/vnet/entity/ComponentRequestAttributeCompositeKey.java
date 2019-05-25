package com.vonage.vnet.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Class for ComponentRequestAttribute CompositeKey.
 */

@Embeddable
public class ComponentRequestAttributeCompositeKey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "dpt_component_req_id")
	private Long componentRequestId;

	@Column(name = "attrib_name")
	private String name;

	public ComponentRequestAttributeCompositeKey() {
	}

	public ComponentRequestAttributeCompositeKey(Long componentRequestId, String name) {
		super();
		this.componentRequestId = componentRequestId;
		this.name = name;
	}

	public Long getComponentRequestId() {
		return componentRequestId;
	}

	public void setComponentRequestId(Long componentRequestId) {
		this.componentRequestId = componentRequestId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(10, 50).append(componentRequestId).append(name).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}

		ComponentRequestAttributeCompositeKey rhs = (ComponentRequestAttributeCompositeKey) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(componentRequestId, rhs.componentRequestId).append(name, rhs.name).isEquals();
	}
}