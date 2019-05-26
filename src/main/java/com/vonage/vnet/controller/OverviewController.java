package com.vonage.vnet.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;



/**
 * Controller class for all user actions on overview screen.
 * 
 */
@Controller
public class OverviewController {

    private static final Logger log = LoggerFactory.getLogger(OverviewController.class);

    @Autowired
    private ExhibitController exhibitController;

    @Autowired
    private OverviewService overviewService;

    /**
     * addExhibitToDealerDashboard(Model, GenericForm) method is invoked when user clicks on 'Add to Dealer Dashboard' button on the Overview screen. Updates Dealer Visible flag.
     * 
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = "/overview", params = "addToDealerDashboard", method = RequestMethod.POST)
    public String addExhibitToDealerDashboard(Model model, GenericForm form) {
        overviewService.addExhibitToDealerDashboard(form.getExhibitRequestId(), form.getPackageRequestId());
        return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
    }

    /**
     * approveExhibit(Model, GenericForm) method is invoked when user clicks on 'Approve' button on the Overview screen. Updates Exhibit Status based on the user role.
     * 
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = "/overview", params = "approve", method = RequestMethod.POST)
    public String approveExhibit(Model model, GenericForm form) {
        overviewService.approveExhibit(form.getPackageRequestId(), form.getExhibitRequestId());
        return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
    }

    /**
     * rejectExhibit(Model, GenericForm) method is invoked when user clicks on 'Reject' button on the Overview screen. Updates Exhibit Status and Dealer Visible flag.
     * 
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = "/overview", params = "reject", method = RequestMethod.POST)
    public String rejectExhibit(Model model, GenericForm form) {
        overviewService.rejectExhibit(form.getExhibitRequestId());
        return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
    }

    /**
     * waiveExhibit(Model, GenericForm) method is invoked when user clicks on 'Waive' button on the Overview screen. Updates Exhibit Status.
     * 
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = "/overview", params = "waive", method = RequestMethod.POST)
    public String waiveExhibit(Model model, GenericForm form) {
        overviewService.waiveExhibit(form.getPackageRequestId(), form.getExhibitRequestId());
        return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
    }

    /**
     * requireExhibit(Model, GenericForm) method is invoked when user clicks on 'Require' button on the Overview screen. Updates Exhibit Status and Dealer Visible flag.
     * 
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = "/overview", params = "require", method = RequestMethod.POST)
    public String requireExhibit(Model model, GenericForm form) {
        overviewService.requireExhibit(form.getExhibitRequestId());
        return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
    }

    /**
     * unapproveExhibit(Model, GenericForm) method is invoked when user clicks on 'Unapprove' button on the Overview screen. Updates Exhibit Status and Dealer Visible flag.
     * 
     * @param model
     * @param form
     * @return
     */
    @RequestMapping(value = "/overview", params = "unapprove", method = RequestMethod.POST)
    public String unapproveExhibit(Model model, GenericForm form) {
        overviewService.unapproveExhibit(form.getExhibitRequestId(), form.getPackageRequestId());
        return exhibitController.showExhibit(model, form.getPackageRequestId(), form.getPackageSetId(), form.getPackageExhibitXrefId(), form.getComponentId());
    }

    /**
     * generateZip(GenericForm, HttpServletRequest, HttpServletResponse, String, String) method is invoked when user clicks on 'Generate Zip' button on the Overview screen.
     * 
     * @param form
     * @param request
     * @param response
     * @param dealerNumber
     * @param exhibitName
     */
    @RequestMapping(value = "/overview", params = "generateZip", method = RequestMethod.POST)
    public void generateZip(GenericForm form, HttpServletRequest request, HttpServletResponse response, String dealerNumber, String exhibitName) {
        // TODO: Validate authorization to view document
        FileInputStream fileInputStream = null;
        String zipFilename = null;
        try {
            zipFilename = overviewService.generateZip(form.getExhibitRequestId(), dealerNumber, exhibitName);

            if (zipFilename == null) {
                // TODO: RETURN ERROR BACK TO USER
                return;
            }
            ServletContext context = request.getServletContext();
            String mimeType = context.getMimeType(zipFilename);
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", FilenameUtils.getName(zipFilename)));
            fileInputStream = new FileInputStream(zipFilename);
            FileCopyUtils.copy(fileInputStream, response.getOutputStream());
        } catch (IOException ioe) {
            log.error("Error generating zip file", ioe);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                    // Delete the generated Zip file residing at /app/docs/tmp.
                    FileSystemUtils.deleteRecursively(new File(zipFilename));
                }
            } catch (IOException ioe) {
                log.error("Error closing zip file", ioe);
            }
        }
    }
}
