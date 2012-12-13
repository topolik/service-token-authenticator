package cz.topolik.servicetokenauth;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.RoleConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portlet.expando.NoSuchTableException;
import com.liferay.portlet.expando.model.ExpandoBridge;
import com.liferay.portlet.expando.model.ExpandoColumn;
import com.liferay.portlet.expando.model.ExpandoColumnConstants;
import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.model.ExpandoTableConstants;
import com.liferay.portlet.expando.service.ExpandoColumnLocalServiceUtil;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;
import com.liferay.portlet.expando.util.ExpandoBridgeFactoryUtil;

/**
 * @author Tomas Polesovsky
 */
public class ExpandoUtil {

	/*
	 * Code below based on http://www.liferay.com/community/wiki/-/wiki/Main/Adding+User+Custom+Attributes+%28Liferay+v6%29
	 */

	public long getColumnId(long companyId) throws Exception{
		ExpandoTable userExpandoTable = null;
		try {
			userExpandoTable = ExpandoTableLocalServiceUtil.getDefaultTable(companyId, User.class.getName());
		} catch (NoSuchTableException nste) {
			// Updates the Liferay "ExpandoTable" table
			userExpandoTable = ExpandoTableLocalServiceUtil.addDefaultTable(companyId, User.class.getName());
		}

		ExpandoColumn expandoColumn = addUserCustomAttribute(companyId, userExpandoTable, "application-tokens", ExpandoColumnConstants.STRING_ARRAY);

		return expandoColumn.getColumnId();
	}

	private ExpandoColumn addUserCustomAttribute(
		long companyId, ExpandoTable userExpandoTable, String attributeName, int type)
		throws PortalException, SystemException {

		ExpandoColumn expandoColumn = ExpandoColumnLocalServiceUtil.getColumn(userExpandoTable.getTableId(), attributeName);

		if(expandoColumn == null){
			// Updates the Liferay "ExpandoColumn" table
			expandoColumn = ExpandoColumnLocalServiceUtil.addColumn(userExpandoTable.getTableId(), attributeName, type);
			setAttributePermissions(companyId, attributeName);
		}

		return expandoColumn;
	}

	private void setAttributePermissions(long companyId, String attributeName)
		throws PortalException, SystemException {

		ExpandoBridge expandoBridge = ExpandoBridgeFactoryUtil.getExpandoBridge(companyId, User.class.getName());

		Role userRole = RoleLocalServiceUtil.getRole(companyId, RoleConstants.USER);

		ExpandoColumn column =
			ExpandoColumnLocalServiceUtil.getColumn(
				companyId, expandoBridge.getClassName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME, attributeName);

		// Give ordinary users read / write power of the new attributes
		// Updates the Liferay "ResourcePermission" table
		ResourcePermissionLocalServiceUtil.setResourcePermissions(
			companyId, ExpandoColumn.class.getName(), ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(column.getColumnId()), userRole.getRoleId(),
			new String[]{ActionKeys.VIEW, ActionKeys.UPDATE});

	}

}
