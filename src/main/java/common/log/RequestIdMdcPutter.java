package common.log;

import javax.servlet.ServletRequest;

import org.slf4j.MDC;

/**<pre>
 * リクエスト毎にインクリメントするIDを設定する。
 * 最大値に達すると1からカウントしなおす。
 * 【キー名】
 * プレフィックス＋reqId
 * </pre>
 */
public class RequestIdMdcPutter implements MdcPutter {
	public static final String REQ_ID = "reqId";
	private int count = 0;
	static final public int MAX_COUNT = 999999999;

	///単体テストのためのメソッド
	public void setCount(int count){
		this.count = count;
	}

	@Override
	public void put(String key, ServletRequest req) {
		++this.count;
		if(this.count > MAX_COUNT) count = 1;
		MDC.put(key + REQ_ID, "" + this.count);
	}

	@Override
	public void remove(String key) {
		MDC.remove(key + REQ_ID);
	}

}
