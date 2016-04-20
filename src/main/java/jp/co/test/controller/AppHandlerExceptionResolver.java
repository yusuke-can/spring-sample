package jp.co.test.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import service.common.ErrCodeConstant;

/**<pre>
 * このWEBアプリの例外ハンドラ。
 * </pre>
 */
public class AppHandlerExceptionResolver implements HandlerExceptionResolver, Ordered {
	private final static Logger log = LoggerFactory.getLogger(AppHandlerExceptionResolver.class);

	/**<pre>例外ハンドラの優先順位を1番にする。
	 * DispatcherServletに保管されている他のHandlerExceptionResolverより先に実行されるように
	 * オーダーを0にする。
	 * これをしないと、@RequestParamアノテーションで指定した引数の型変換エラー時にこのハンドラが例外をキャッチできない。
	 * </pre>
	 */
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest req,
			HttpServletResponse res, Object handler, Exception e) {
		//
		ModelAndView mv = new ModelAndView();

		if(AccessDeniedException.class.isAssignableFrom(e.getClass())){
			//アクセス認可エラー
			log.warn("エラーが発生しました", e);
			mv.addObject("errCode", ErrCodeConstant.ACCESS_DENIED);
			res.setStatus(403);
		}else if(DataAccessException.class.isAssignableFrom(e.getClass())){
			//DBエラー
			log.error("エラーが発生しました", e);
			mv.addObject("errCode", ErrCodeConstant.DB);
			res.setStatus(500);
		}else{
			//システムエラー
			log.error("エラーが発生しました", e);
			mv.addObject("errCode", ErrCodeConstant.UNEXPECTED);
			res.setStatus(500);
		}

		//表示JSPを設定します
		mv.setViewName("/error/app-error");
		return mv;
	}


}
