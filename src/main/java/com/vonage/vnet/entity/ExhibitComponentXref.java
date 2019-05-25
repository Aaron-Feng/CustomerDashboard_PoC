package com.vonage.vnet.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.subaru.snet.csf.domain.converter.BooleanToYNStringConverter;

/**
 * Entity class for DPT_EXHIBIT_COMPONENT_XREF table.
 */

@Entity
@Table(name = "DPT_EXHIBIT_COMPONENT_XREF")
public class ExhibitComponentXref implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@SequenceGenerator(name = "dptExhibitComponentXrefPkSeq", sequenceName = "dpt_exhibit_component_xref_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dptExhibitComponentXrefPkSeq")
	@Column(name = "dpt_exhibit_component_xref_id")
	private Long id;

	@Column(name = "dpt_exhibit_id")
	private Long exhibitId;

	@Column(name = "dpt_component_id")
	private Long componentId;

	@Column(name = "active_ind")
	@Convert(converter = BooleanToYNStringConverter.class)
	private Boolean active;

	@Column(name = "required_ind")
	@Convert(converter = BooleanToYNStringConverter.class)
	private Boolean required;

	@Column(name = "dealer_component_vis_ind")
	@Convert(converter = BooleanToYNStringConverter.class)
	private Boolean dealerComponentVisible;

	@Column(name = "tab_order")
	private Integer tabOrder;

    @Column(name = "change_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean changed;

    @Column(name = "original_xref_id")
    private Long originalExhibitComponentXrefId;
	
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

	@ManyToOne(optional = false)
	@JoinColumn(name = "dpt_component_id", insertable = false, updatable = false)
	private Component component;

	@ManyToOne(optional = false)
	@JoinColumn(name = "dpt_exhibit_id", insertable = false, updatable = false)
	private Exhibit exhibit;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExhibitId() {
		return exhibitId;
	}

	public void setExhibitId(Long exhibitId) {
		this.exhibitId = exhibitId;
	}

	public Long getComponentId() {
		return componentId;
	}

	public void setComponentId(Long componentId) {
		this.componentId = componentId;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public Boolean getDealerComponentVisible() {
		return dealerComponentVisible;
	}

	public void setDealerComponentVisible(Boolean dealerComponentVisible) {
		this.dealerComponentVisible = dealerComponentVisible;
	}

	public Integer getTabOrder() {
		return tabOrder;
	}

	public void setTabOrder(Integer tabOrder) {
		this.tabOrder = tabOrder;
	}

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    public Long getOriginalExhibitComponentXrefId() {
        return originalExhibitComponentXrefId;
    }

    public void setOriginalExhibitComponentXrefId(Long originalExhibitComponentXrefId) {
        this.originalExhibitComponentXrefId = originalExhibitComponentXrefId;
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

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public Exhibit getExhibit() {
		return exhibit;
	}

	public void setExhibit(Exhibit exhibit) {
		this.exhibit = exhibit;
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ExhibitComponentXref [id=");
        builder.append(id);
        builder.append(", exhibitId=");
        builder.append(exhibitId);
        builder.append(", componentId=");
        builder.append(componentId);
        builder.append(", active=");
        builder.append(active);
        builder.append(", required=");
        builder.append(required);
        builder.append(", dealerComponentVisible=");
        builder.append(dealerComponentVisible);
        builder.append(", tabOrder=");
        builder.append(tabOrder);
        builder.append(", changed=");
        builder.append(changed);
        builder.append(", originalExhibitComponentXrefId=");
        builder.append(originalExhibitComponentXrefId);
        builder.append(", createDate=");
        builder.append(createDate);
        builder.append(", createUser=");
        builder.append(createUser);
        builder.append(", updateDate=");
        builder.append(updateDate);
        builder.append(", updateUser=");
        builder.append(updateUser);
        builder.append("]");
        return builder.toString();
    }
}