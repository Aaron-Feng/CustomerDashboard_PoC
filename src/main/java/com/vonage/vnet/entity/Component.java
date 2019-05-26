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
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;



/**
 * Entity class for DPT_COMPONENT table.
 */

@Entity
@Table(name = "DPT_COMPONENT")
public class Component implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@SequenceGenerator(name = "dptComponentPkSeq", sequenceName = "dpt_component_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dptComponentPkSeq")
	@Column(name = "dpt_component_id")
	private Long id;

	@Column(name = "component_name")
	private String name;

	@Column(name = "component_desc")
	private String description;

	@Column(name = "component_type")
	private String typeString;

	@Column(name = "initial_file_name")
	private String initialFilename;
	
	@Column(name = "component_text")
	@Lob
	private String text;
	
	@Column(name = "component_code")
	private String code;

    @Column(name = "active_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean active;

    @Column(name = "change_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean changed;

    @Column(name = "original_id")
    private Long originalComponentId;
    
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public ComponentType getType() {
		return ComponentType.valueOf(typeString.toUpperCase());
	}

	public void setComponentType(ComponentType type) {
		this.typeString = type.toString().toLowerCase();
	}

	public String getInitialFilename() {
		return initialFilename;
	}

	public void setInitialFilename(String initialFilename) {
		this.initialFilename = initialFilename;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    public Long getOriginalComponentId() {
        return originalComponentId;
    }

    public void setOriginalComponentId(Long originalComponentId) {
        this.originalComponentId = originalComponentId;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Component [id=");
        builder.append(id);
        builder.append(", name=");
        builder.append(name);
        builder.append(", description=");
        builder.append(description);
        builder.append(", typeString=");
        builder.append(typeString);
        builder.append(", initialFilename=");
        builder.append(initialFilename);
        builder.append(", text=");
        builder.append(text);
        builder.append(", code=");
        builder.append(code);
        builder.append(", active=");
        builder.append(active);
        builder.append(", changed=");
        builder.append(changed);
        builder.append(", originalComponentId=");
        builder.append(originalComponentId);
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
