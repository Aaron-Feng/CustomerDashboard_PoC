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

import com.csf.converter.BooleanToYNStringConverter;

/**
 * Entity class for DPT_EXHIBIT table.
 */

@Entity
@Table(name = "DPT_EXHIBIT")
public class Exhibit implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @SequenceGenerator(name = "dptExhibitPkSeq", sequenceName = "dpt_exhibit_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dptExhibitPkSeq")
    @Column(name = "dpt_exhibit_id")
    private Long id;

    @Column(name = "exhibit_number")
    private Integer number;

    @Column(name = "exhibit_name")
    private String name;

    @Column(name = "exhibit_code")
    private String code;

    @Column(name = "exhibit_desc")
    private String description;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "doc_name")
    private String documentName;

    @Column(name = "include_in_final_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean includeInFinal;

    @Column(name = "final_template")
    private String finalTemplateName;

    @Column(name = "final_doc_name")
    private String finalDocumentName;

    @Column(name = "active_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean active;

    @Column(name = "change_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean changed;

    @Column(name = "original_id")
    private Long originalExhibitId;

    @Column(name = "amendment_exhibit_code")
    private String amendmentExhibitCode;

    @Column(name = "dpt_tracking_type_id")
    private Long trackingTypeId;

    @Column(name = "include_in_agreement_docs_ind")
    @Convert(converter = BooleanToYNStringConverter.class)
    private Boolean includeInAgreementDocsInd;

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
    @JoinColumn(name = "dpt_tracking_type_id", insertable = false, updatable = false)
    private TrackingType trackingType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public Boolean getIncludeInFinal() {
        return includeInFinal;
    }

    public void setIncludeInFinal(Boolean includeInFinal) {
        this.includeInFinal = includeInFinal;
    }

    public String getFinalTemplateName() {
        return finalTemplateName;
    }

    public void setFinalTemplateName(String finalTemplateName) {
        this.finalTemplateName = finalTemplateName;
    }

    public String getFinalDocumentName() {
        return finalDocumentName;
    }

    public void setFinalDocumentName(String finalDocumentName) {
        this.finalDocumentName = finalDocumentName;
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

    public Long getOriginalExhibitId() {
        return originalExhibitId;
    }

    public void setOriginalExhibitId(Long originalExhibitId) {
        this.originalExhibitId = originalExhibitId;
    }

    public String getAmendmentExhibitCode() {
        return amendmentExhibitCode;
    }

    public void setAmendmentExhibitCode(String amendmentExhibitCode) {
        this.amendmentExhibitCode = amendmentExhibitCode;
    }

    public Long getTrackingTypeId() {
        return trackingTypeId;
    }

    public void setTrackingTypeId(Long trackingTypeId) {
        this.trackingTypeId = trackingTypeId;
    }

    public Boolean getIncludeInAgreementDocsInd() {
        return includeInAgreementDocsInd;
    }

    public void setIncludeInAgreementDocsInd(Boolean includeInAgreementDocsInd) {
        this.includeInAgreementDocsInd = includeInAgreementDocsInd;
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

    public TrackingType getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(TrackingType trackingType) {
        this.trackingType = trackingType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Exhibit [id=");
        builder.append(id);
        builder.append(", number=");
        builder.append(number);
        builder.append(", name=");
        builder.append(name);
        builder.append(", code=");
        builder.append(code);
        builder.append(", description=");
        builder.append(description);
        builder.append(", templateName=");
        builder.append(templateName);
        builder.append(", documentName=");
        builder.append(documentName);
        builder.append(", includeInFinal=");
        builder.append(includeInFinal);
        builder.append(", finalTemplateName=");
        builder.append(finalTemplateName);
        builder.append(", finalDocumentName=");
        builder.append(finalDocumentName);
        builder.append(", active=");
        builder.append(active);
        builder.append(", changed=");
        builder.append(changed);
        builder.append(", originalExhibitId=");
        builder.append(originalExhibitId);
        builder.append(", amendmentExhibitCode=");
        builder.append(amendmentExhibitCode);
        builder.append(", trackingTypeId=");
        builder.append(trackingTypeId);
        builder.append(", includeInAgreementDocsInd=");
        builder.append(includeInAgreementDocsInd);
        builder.append(", createDate=");
        builder.append(createDate);
        builder.append(", createUser=");
        builder.append(createUser);
        builder.append(", updateDate=");
        builder.append(updateDate);
        builder.append(", updateUser=");
        builder.append(updateUser);
        builder.append(", trackingType=");
        builder.append(trackingType);
        builder.append("]");
        return builder.toString();
    }

}