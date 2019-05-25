package com.vonage.vnet.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Future;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.subaru.snet.dpt.domain.Attachment;
import com.subaru.snet.dpt.domain.AttachmentType;
import com.subaru.snet.dpt.domain.TransactionData;
import com.subaru.snet.dpt.domain.form.UploadForm;
import com.subaru.snet.dpt.service.DocumentManagementService;
import com.subaru.snet.dpt.service.DocumentServicesService;

@Controller
public class DocumentController {
	private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

	@Autowired
	private DocumentManagementService documentManagementService;

	@Autowired
	private DocumentServicesService documentServicesService;

	@Autowired
	private ExhibitController exhibitController;
	
	@Autowired
    private TransactionData transactionData;
	
	@Value("${file.upload.size.maximum:10485760}")
	private Integer maximumFileSize;

	/**
	 * uploadDocument(UploadForm) method is responsible for uploading a document to Web Center and returns upload status to the view.
	 * 
	 * @param form
	 * @return
	 */
	@RequestMapping(value = "/uploadDocument", method = RequestMethod.POST)
	@ResponseBody
    public String uploadDocument(UploadForm form) {
        if (form.getFile() != null && !form.getFile().isEmpty()) {
            if (form.getFile().getSize() > maximumFileSize) {
                return "FILE_SIZE_ERROR";
            } else {
                try {
                    String transactionId = form.getPackageRequestId().toString() + System.currentTimeMillis();
                    Future<String> uploadStatus = documentManagementService.uploadFile(form.getFile().getName(), form.getFile().getBytes(), form.getFile().getOriginalFilename(), AttachmentType.ATTACHMENT,
                                                    form.getDescription().trim(), form.getPackageRequestId(), form.getExhibitRequestId(), form.getComponentRequestId());
                    transactionData.getTransactionMap().put(transactionId, uploadStatus);
                    return transactionId;
                } catch (Exception e) {
                    logger.error("Exception occurred in uploading the file with name: {}", form.getFile().getName(), e);
                    return "ERROR";
                }
            }
        } else {
            logger.error("Unable to upload empty file");
            return "EMPTY_FILE";
        }
    }

	/**
	 * deleteDocument(UploadForm, BindingResult, Model) method is responsible for deleting a document from Web Center.
	 * 
	 * @param form
	 * @param result
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/uploadDocument", params = "delete")
	public String deleteDocument(@Valid UploadForm form, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			try {
				documentManagementService.deleteComponentFile(form.getDocumentId(), form.getComponentRequestId());
			} catch (Exception e) {
			    logger.error("Exception occurred in deleting the document with documentId: {}", form.getDocumentId(), e);
				result.addError(new ObjectError("file", "Unable to delete file."));
			}
		}
		return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
	}

	/**
	 * getDocument(String, HttpServletRequest, HttServletResponse) method is responsible for retrieving a document from Web Center.
	 * 
	 * @param documentId
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value = "/getDocument/{documentId}")
	public void getDocument(@PathVariable String documentId, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Attachment attachment = documentServicesService.getAttachment(documentId);

		// TODO: Validate authorization to view document

		ServletContext context = request.getServletContext();

		String mimeType = null;
		if (StringUtils.isNotBlank(attachment.getFileName())) {
			mimeType = context.getMimeType(attachment.getFileName());
		}
		if (mimeType == null) {
			mimeType = "application/octet-stream";
		}

		response.setContentType(mimeType);
		response.setContentLength(attachment.getContent().length);

		response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", attachment.getFileName()));

        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            outputStream.write(attachment.getContent());
        } catch (Exception e) {
            logger.error("Exception occurred in getting the document with documentId: {}", documentId, e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
	}
}