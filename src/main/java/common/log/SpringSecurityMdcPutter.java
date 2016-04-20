package common.log;


import javax.servlet.ServletRequest;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import common.SecurityUtils;


/**<pre>
 * SpringSecurityで設定された値をMDCに設定する。
 * 【キー名】
 * プレフィックス＋username　（ログインID）
 * プレフィックス＋isAuthed　（認証されている場合"authed"が出力される。認証されていない場合""）
 * プレフィックス＋roles　　　（ロール。複数出力）
 * </pre>
 */
public class SpringSecurityMdcPutter implements MdcPutter {
	public static final String SEC_USERNAME = "username";
	public static final String SEC_IS_AUTHED = "isAuthed";
	public static final String SEC_ROLES = "roles";

	@Override
	public void put(String key, ServletRequest req) {
		//ログイン後の認証手形を取得
		SecurityContext ctx = SecurityUtils.getSecurityContextFromSession(req);
		if(ctx == null) return;
		Authentication auth = ctx.getAuthentication();
		if(auth == null) return;
		//ログインIDを取得
		String loginUserId = SecurityUtils.getLoginId(auth);
		//
		MDC.put(key + SEC_USERNAME, loginUserId);
		MDC.put(key + SEC_IS_AUTHED, (auth.isAuthenticated() ? "authed" : ""));
		MDC.put(key + SEC_ROLES, auth.getAuthorities().toString());
	}

	@Override
	public void remove(String key) {
		MDC.remove(key + SEC_USERNAME);
		MDC.remove(key + SEC_IS_AUTHED);
		MDC.remove(key + SEC_ROLES);
	}

}
