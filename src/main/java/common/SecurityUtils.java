package common;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;


/**
 * SpringSecurity用のユーティリティ。
 *
 */
public abstract class SecurityUtils {

	/**
	 * authからログインIDを取得する
	 * @param auth
	 * @return ログインID。見つからない場合はnull。
	 */
	static public String getLoginId(Authentication auth){
		if(auth == null) return null;
		Object principal = auth.getPrincipal();
		String loginUserId;
		if(principal == null){
			loginUserId = null;
		}else if(principal instanceof UserDetails){
			loginUserId = ((UserDetails)principal).getUsername();
		} else {
			loginUserId = principal.toString();
		}
		return loginUserId;
	}

	/**<pre>
	 * SecurityContextHolderからログインIDを取得する。
	 * SecurityContextHolderにSecurityContextが設定されるのは、SpringSecurityのフィルタを通った後。
	 * Controllerの処理時には設定されている。
	 * Session内にもSecurityContextはあり、基本的にはSecurityContextHolderと同じオブジェクトが保存されているが、
	 * リクエストの途中で変更されている可能性はある。
	 * また、SpringSecurityのフィルタを通過する前のフィルタ内では、SecurityContextHolderは設定されていない。
	 * </pre>
	 * @return ログインID。見つからない場合はnull。
	 */
	static public String getLoginId(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return getLoginId(auth);
	}

	/**<pre>
	 * Session内のSecurityContextを取得する。
	 * tomcatのフィルタ処理では、すでにSpringSecurityのフィルタを通過していればSecurityContextHolderに
	 * SecurityContextが設定されるが、通過する前のフィルタの場合は設定されていない。
	 * このときSessionには前回のリクエスト処理で保存されているSecurityContextが存在する。
	 * それを取得する。
	 * </pre>
	 * @param req [in]リクエストオブジェクト
	 * @return 取得したSecurityContext。Session内に存在しなかった場合null。
	 */
	static public SecurityContext getSecurityContextFromSession(ServletRequest req){
		if (!(req instanceof HttpServletRequest)) return null;
		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		HttpSession sess = httpServletRequest.getSession(false);
		if(sess == null) return null;
		//ログイン後の認証手形を取得
		SecurityContext ctx = (SecurityContext)sess.getAttribute(
			HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		return ctx;
	}
}
