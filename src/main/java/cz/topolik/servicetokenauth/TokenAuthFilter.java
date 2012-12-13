package cz.topolik.servicetokenauth;

import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.expando.model.ExpandoValue;
import com.liferay.portlet.expando.service.ExpandoValueLocalServiceUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.List;

/**
 * @author Tomas Polesovsky
 */
public class TokenAuthFilter implements Filter{

	public void destroy() {
	}

	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		if(!(servletRequest instanceof HttpServletRequest)){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

		// already authenticated, by JAAS ?
		if(httpServletRequest.getRemoteUser() != null){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		// parse header
		String applicationToken = httpServletRequest.getHeader("x-application-token");

		// parse parameter
		if(applicationToken == null || applicationToken.trim().length() == 0){
			applicationToken = httpServletRequest.getParameter("applicationToken");
		}

		if(applicationToken == null || applicationToken.trim().length() == 0){
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		try {
			long companyId = PortalUtil.getCompanyId(httpServletRequest);

			long columnId = new ExpandoUtil().getColumnId(companyId);

			// find all possible custom user attributes
			DynamicQuery query = DynamicQueryFactoryUtil.forClass(ExpandoValue.class).add(
				PropertyFactoryUtil.forName("data").like("%" + applicationToken + "%")).add(
					PropertyFactoryUtil.forName("columnId").eq(columnId));

			// find the our one
			long userId = 0;
			List<ExpandoValue> result = (List<ExpandoValue>) ExpandoValueLocalServiceUtil.dynamicQuery(query);
			for(ExpandoValue value : result) {
				String[] tokens = value.getStringArray();
				if (tokens.length == 1 && tokens[0].indexOf('\n') > -1){
					tokens = tokens[0].split("\n");
				}

				for (int i = 0; i < tokens.length; i++) {
					if (applicationToken.trim().equals(tokens[i].trim())){
						userId = value.getClassPK();
						break;
					}
				}
			}

			// override request with our custom one
			if(userId > 0){
				final String remoteUser = Long.toString(userId);
				servletRequest = new HttpServletRequestWrapper(httpServletRequest){
					/**
					 * Let's return our authenticated user
					 */
					@Override
					public String getRemoteUser() {
						return remoteUser;
					}

					/**
					 * Bypass p_auth JSONAction check
					 */
					@Override
					public String getAuthType() {
						return HttpServletRequest.BASIC_AUTH;
					}
				};
			}
		} catch(Exception e){
			e.printStackTrace();
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void init(FilterConfig filterConfig) {
	}
	
}
