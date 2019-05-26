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


/**
 * Entity class for DPT_COMPONENT_REQ table.
 */

@Entity
@Table(name = "DPT_COMPONENT_REQ")
public class ComponentRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@SequenceGenerator(name = "dptComponentReqPkSeq", sequenceName = "dpt_component_req_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dptComponentReqPkSeq")
	@Column(name = "dpt_component_req_id")
	private Long id;

	@Column(name = "dpt_exhibit_req_id")
	private Long exhibitRequestId;

	@Column(name = "dpt_package_req_id")
	private Long packageRequestId;
	
	@Column(name = "dpt_pack_set_req_id")
	private Long packageSetRequestId;

	@Column(name = "dpt_exhibit_component_xref_id")
	private Long exhibitComponentXrefId;

	@Column(name = "initial_file_ind")
	@Convert(converter = BooleanToYNStringConverter.class)
	private Boolean initialFile;

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

	@Column(name = "completed_ind")
	@Convert(converter = BooleanToYNStringConverter.class)
	private Boolean completed;

	@ManyToOne(optional = false)
	@JoinColumn(name = "dpt_pack_set_req_id", insertable = false, updatable = false)
	private PackageSetRequest packageSetRequest;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "dpt_package_req_id", insertable = false, updatable = false)
	private PackageRequest packageRequest;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "dpt_exhibit_req_id", insertable = false, updatable = false)
	private ExhibitRequest exhibitRequest;

	@ManyToOne(optional = false)
	@JoinColumn(name = "dpt_exhibit_component_xref_id", insertable = false, updatable = false)
	private ExhibitComponentXref exhibitComponentXref;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getExhibitRequestId() {
        return exhibitRequestId;
    }

    public void setExhibitRequestId(Long exhibitRequestId) {
        this.exhibitRequestId = exhibitRequestId;
    }

    public Long getPackageRequestId() {
        return packageRequestId;
    }

    public void setPackageRequestId(Long packageRequestId) {
        this.packageRequestId = packageRequestId;
    }

    public Long getPackageSetRequestId() {
        return packageSetRequestId;
    }

    public void setPackageSetRequestId(Long packageSetRequestId) {
        this.packageSetRequestId = packageSetRequestId;
    }

    public Long getExhibitComponentXrefId() {
        return exhibitComponentXrefId;
    }

    public void setExhibitComponentXrefId(Long exhibitComponentXrefId) {
        this.exhibitComponentXrefId = exhibitComponentXrefId;
    }

    public Boolean getInitialFile() {
        return initialFile;
    }

    public void setInitialFile(Boolean initialFile) {
        this.initialFile = initialFile;
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public PackageSetRequest getPackageSetRequest() {
        return packageSetRequest;
    }

    public void setPackageSetRequest(PackageSetRequest packageSetRequest) {
        this.packageSetRequest = packageSetRequest;
    }

    public PackageRequest getPackageRequest() {
        return packageRequest;
    }

    public void setPackageRequest(PackageRequest packageRequest) {
        this.packageRequest = packageRequest;
    }

    public ExhibitRequest getExhibitRequest() {
        return exhibitRequest;
    }

    public void setExhibitRequest(ExhibitRequest exhibitRequest) {
        this.exhibitRequest = exhibitRequest;
    }

    public ExhibitComponentXref getExhibitComponentXref() {
        return exhibitComponentXref;
    }

    public void setExhibitComponentXref(ExhibitComponentXref exhibitComponentXref) {
        this.exhibitComponentXref = exhibitComponentXref;
    }

}