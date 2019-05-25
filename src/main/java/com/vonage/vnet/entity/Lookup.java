package com.vonage.vnet.entity;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.subaru.snet.csf.domain.converter.BooleanToYNStringConverter;

/**
 * Entity class for DPT_LOOKUP table.
 */

@Entity
@Table(name = "DPT_LOOKUP")
public class Lookup implements Serializable, Comparator<Lookup> {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@SequenceGenerator(name = "dptLookupPkSeq", sequenceName = "dpt_lookup_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dptLookupPkSeq")
	@Column(name = "dpt_lookup_id")
	private Long id;

	@Column(name = "lookup_type")
	private String type;

	@Column(name = "lookup_field_name")
	private String fieldName;

	@Column(name = "lookup_value")
	private String value;

	@Column(name = "lookup_status")
	private String status;

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

	@Column(name = "sort_order")
	private Integer sortOrder;

	@Column(name = "default_ind")
	@Convert(converter = BooleanToYNStringConverter.class)
	private Boolean lookupDefault;

	@Column(name = "ddms_id")
	private Long ddmsId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Boolean getLookupDefault() {
		return lookupDefault;
	}

	public void setLookupDefault(Boolean lookupDefault) {
		this.lookupDefault = lookupDefault;
	}

	public Long getDdmsId() {
		return ddmsId;
	}

	public void setDdmsId(Long ddmsId) {
		this.ddmsId = ddmsId;
	}

	public int compare(Lookup lookupObject1, Lookup lookupObject2) {
		return lookupObject1.getValue().compareToIgnoreCase(lookupObject2.getValue());
	}
}