package com.vonage.vnet.servicesImpl;

import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.subaru.snet.csf.auth.domain.SNETUserContext;
import com.subaru.snet.dpt.domain.form.MemoForm;
import com.subaru.snet.dpt.entity.Memo;
import com.subaru.snet.dpt.repository.dpt.ExhibitRequestRepository;
import com.subaru.snet.dpt.repository.dpt.MemoRepository;
import com.subaru.snet.dpt.repository.dpt.PackageRequestRepository;

/**
 * MemoService class contains methods for handling operations related to Memo functionality.
 */

@Service
public class MemoService {
    private static final Logger logger = LoggerFactory.getLogger(MemoService.class);
	
	@Autowired
	private ExhibitService exhibitService;
	
	@Autowired
	private MemoRepository memoRepository;
	
    @Autowired
    private ExhibitRequestRepository exhibitRequestRepository;

    @Autowired
    private PackageRequestRepository packageRequestRepository;

    @Autowired
    private SNETUserContext userContext;

	/**
	 * saveMemo(MemoForm) method is responsible for saving a memo to DPT_MEMO table.
	 * 
	 * @param form
	 */
	public void saveMemo(MemoForm form) {
		exhibitService.createRequestComponents(form, false);
		
		Memo memo = new Memo();
		Date currentDate = new Date();
		String currentUser = userContext.getUser().getUsername();
		memo.setExhibitRequestId(form.getExhibitRequestId());
		memo.setInternalMemo(form.isInternal());
		memo.setText(form.getText());
		memo.setCreateDate(currentDate);
		memo.setCreateUser(currentUser);
		memoRepository.save(memo);
		
        // Update ExhibitRequest with recent activity(Add Memo) performed on the exhibit to display on Exhibit Dashboard screen.
        exhibitRequestRepository.updateAuditData(form.getExhibitRequestId(), currentDate, currentUser);

        // Update PackageRequest with recent activity(Add Memo) performed on the exhibit to display on Package Dashboard screen.
        packageRequestRepository.updateLastSubmitDate(form.getPackageRequestId(), currentDate);
		logger.debug("Saving memo: {}", memo.getText());
	}
	
	/**
	 * getMemos(Long) method is responsible for getting the memos associated with a given exhibitRequestId.
	 * 
	 * @param exhibitRequestId
	 * @return
	 */
	public Collection<Memo> getMemos(Long exhibitRequestId) {
		return memoRepository.findAllByExhibitRequestIdOrderByCreateDateDesc(exhibitRequestId);
	}
}