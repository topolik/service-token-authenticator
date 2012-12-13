package cz.topolik.servicetokenauth;

import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.SimpleAction;

/**
 * @author Tomas Polesovsky
 */
public class StartupEvent extends SimpleAction {
	@Override
	public void run(String[] companyIds) throws ActionException {
		ExpandoUtil expandoUtil = new ExpandoUtil();
		for(String companyIdStr : companyIds){
			long companyId = Long.parseLong(companyIdStr);
			try {
				expandoUtil.getColumnId(companyId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
