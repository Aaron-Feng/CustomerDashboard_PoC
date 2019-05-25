package com.vonage.vnet.domain;

public enum AttachmentType {
    DOCUMENT("document", "Dealer Document"), 
    SIGNATURE("signature", "Signature Page"), 
    ATTACHMENT("attachment", "Attachment"), 
    LETTER("letter", "Letter");

    private final String value;
    private final String label;

    AttachmentType(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    /**
     * getByValue(String) method is responsible for returning AttachmentType corresponding to the provided agreementType value.
     * 
     * @param documentType
     * @return
     */
    public static AttachmentType getByValue(String documentType) {
        for (AttachmentType type : values()) {
            if (type.getValue().equals(documentType)) {
                return type;
            }
        }
        return ATTACHMENT;
    }

    /**
     * getByLabel(String) method is responsible for returning AttachmentType corresponding to the provided agreementType label.
     * 
     * @param documentType
     * @return
     */
    public static AttachmentType getByLabel(String documentType) {
        for (AttachmentType type : values()) {
            if (type.getLabel().equals(documentType)) {
                return type;
            }
        }
        return ATTACHMENT;
    }
}