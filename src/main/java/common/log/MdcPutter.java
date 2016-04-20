package common.log;

import javax.servlet.ServletRequest;


/**
 * <pre>
 * CommonMDCInsertingServletFilterでMDCに値を設定するためのインタフェース。
 * Filterで使用できるクラスを識別するためのラベルになっている。
 * </pre>
 * @see com.sampletool.common.log.CommonMDCInsertingServletFilter
 */
public interface MdcPutter {
	/**
	 * MDCにキーと値を設定する。基本的にはキーは、keyをプレフィックスにして登録する。
	 * @param key [in]キー名のプレフィックス。この後ろに識別子をつける。
	 * 	         {@link com.sampletool.common.log.CommonMDCInsertingServletFilter}からフィルタの設定値が渡ってくる
	 * @param req [in]リクエストオブジェクト
	 */
	public void put(String key, final ServletRequest req);

	/**
	 * MDCからキーを削除する。
	 * @param key [in]キー名のプレフィックス。この後ろに識別子をつける。設定するときと同様。
	 */
	public void remove(String key);

}
