package common.log;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;



/**<pre>
 * MDCクラスに値を設定する。リクエストの開始時点の情報を保存するので、処理中に値が変わっても反映されないことに注意。
 * MDCに値を設定すると、ログレイアウトに%X{キー名}が使用できるようになる。
 *
 * 【web.xmlのinit-param設定例】
&lt;param-name>putMdcKeys&lt;/param-name>
&lt;param-value>
	req.=com.sampletool.common.log.RequestMdcPutter
	sec.=com.sampletool.common.log.SpringSecurityMdcPutter
&lt;/param-value>

 * 【logbackのPatternの設定例】
 * 　　%d %level %X{req.requestURI} %X{req.sessionId} %X{sec.username} %m%n
 *
 * </pre>
 *
 * @see MdcPutter
 */
public class CommonMDCInsertingServletFilter implements Filter  {
	static public final String FILTER_INIT_KEY = "putMdcKeys";
	Map<String, MdcPutter> mdcGetterMap;


	@Override
	public void init(FilterConfig config) throws ServletException {
		String classNames = config.getInitParameter(FILTER_INIT_KEY);
		this.mdcGetterMap = createMdcGetters(classNames);
	}

	@Override
	public void destroy() {}


	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		try {
			//MDC用のオブジェクトを生成&設定
			insertIntoMDC(req);
			chain.doFilter(req, res);
		} finally {
			clearMDC();
		}
	}


	/**
	 * クラス名のリスト文字列からコンストラクタのリストを作成する。<br>
	 * 例：MDCプレフィックス=クラスフルパス名\n<br>
	 * @param initStr
	 * @return
	 * @throws ServletException
	 */
	protected Map<String, MdcPutter> createMdcGetters(String initStr) throws ServletException{
		Map<String, MdcPutter> ret;
		ret = new HashMap<String, MdcPutter>();
		if(initStr == null) return ret;
		String[] classNames = initStr.split("\n");

		try{
			for(String name : classNames){
				if(name.trim().isEmpty()) continue;
				//
				String[] tmp = name.split("=");
				if(tmp.length != 2) throw new ServletException("キー名のinit-param設定の書式は" +
						"「キー名=クラスパス」です。値=" + name);
				String key = tmp[0].trim();
				String className = tmp[1].trim();
				//
				Constructor<? extends MdcPutter> con = createConstructor(className);
				ret.put(key, con.newInstance());
			}
		}catch(ServletException e){
			throw e;
		}catch(Exception e){
			throw new ServletException(e);
		}
		return ret;
	}


	/**
	 * 指定のクラスのコンストラクタを作成する。
	 * @param className
	 * @return
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	protected Constructor<? extends MdcPutter> createConstructor(String className) throws ServletException {
		Constructor<? extends MdcPutter> con = null;

		try {
			if(className == null)
				throw new ServletException("クラス名を指定してください。");

			//コンストラクタを取得
			Class<?> cl = Class.forName(className);
			if(MdcPutter.class.isAssignableFrom(cl)){
				con = (Constructor<? extends MdcPutter>) cl.getConstructor();
			}else{
				throw new ServletException("クラスはMdcPutterの派生クラスである必要があります。" +
						"class=" + className);
			}

			return con;

		}catch(ServletException e){
			throw e;
		}catch (Exception e) {
			throw new ServletException("Log4jMdcSetterFilter初期化中にエラー発生。className=" + className
					+ ", error=" + e.getClass().getName(), e);
		}
	}


	/**
	 * MDCにキーを設定していく。
	 */
	protected void insertIntoMDC(ServletRequest req) throws ServletException{
		if(this.mdcGetterMap == null ) return;
		Set<Entry<String, MdcPutter>>  set = this.mdcGetterMap.entrySet();
		for(Entry<String, MdcPutter> entry : set){
			MdcPutter putter;
			putter = entry.getValue();
			putter.put(entry.getKey(), req);
		}
	}


	/**
	 * MDCに設定したキーを削除する
	 */
	protected void clearMDC(){
		Set<Entry<String, MdcPutter>>  set = this.mdcGetterMap.entrySet();
		for(Entry<String, MdcPutter> entry : set){
			MdcPutter putter;
			putter = entry.getValue();
			putter.remove(entry.getKey());
		}
	}
}
