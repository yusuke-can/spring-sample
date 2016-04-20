package common.log;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;

import ch.qos.logback.classic.ClassicConstants;


/**<pre>
 * リクエストに保管された値を設定する。
 * キー名のプレフィックスは無視される。
 * 【キー名】
 * {@link ch.qos.logback.classic.helpers.MDCInsertingServletFilter}のキー
 * req.sessionId
 * req.path
 * </pre>
 * @see ch.qos.logback.classic.helpers.MDCInsertingServletFilter
 */
public class RequestMdcPutter implements MdcPutter {
	public static final String REQUEST_SESSION_ID = "req.sessionId";
	public static final String REQUEST_PATH = "req.path";

	@Override
	public void put(String key, ServletRequest req) {
		//設定
		MDC.put(ClassicConstants.REQUEST_REMOTE_HOST_MDC_KEY, req.getRemoteHost());
		if (req instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) req;
			MDC.put(ClassicConstants.REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
			StringBuffer requestURL = httpServletRequest.getRequestURL();
			if (requestURL != null) {
				MDC.put(ClassicConstants.REQUEST_REQUEST_URL, requestURL.toString());
			}
			MDC.put(ClassicConstants.REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
			MDC.put(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY, httpServletRequest.getHeader("User-Agent"));
			MDC.put(ClassicConstants.REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));

			//リクエストのパス
			MDC.put(REQUEST_PATH, httpServletRequest.getServletPath());

			//セッションID
			HttpSession sess = httpServletRequest.getSession(false);
			String sessionId = (sess != null ? sess.getId() : "null");
			MDC.put(REQUEST_SESSION_ID, sessionId);
		}
	}

	@Override
	public void remove(String key) {
		MDC.remove(ClassicConstants.REQUEST_REMOTE_HOST_MDC_KEY);
		MDC.remove(ClassicConstants.REQUEST_REQUEST_URI);
		MDC.remove(ClassicConstants.REQUEST_QUERY_STRING);
		// removing possibly inexistent item is OK
		MDC.remove(ClassicConstants.REQUEST_REQUEST_URL);
		MDC.remove(ClassicConstants.REQUEST_USER_AGENT_MDC_KEY);
		MDC.remove(ClassicConstants.REQUEST_X_FORWARDED_FOR);
		//
		MDC.remove(REQUEST_PATH);
		MDC.remove(REQUEST_SESSION_ID);
	}

}
