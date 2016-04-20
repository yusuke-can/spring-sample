package common.log;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.MDC;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;


/**<pre>
 * マップに指定したメッセージに前方一致した場合、MDCにメッセージをセットする。
 * 「カテゴリ名」:「前方一致メッセージ文字列」=「MDCメッセージ」
 * &lt;ConvertMap>
 *   org.springframework.jdbc.datasource.DataSourceTransactionManager:Creating new transaction=トランザクション開始
 *   org.springframework.jdbc.datasource.DataSourceTransactionManager:Committing JDBC transaction=コミット
 * &lt;/ConvertMap>
 * </pre>
 */
public class MapFilter extends AbstractMatcherFilter<ILoggingEvent> {
	static public final String MDC_KEY = "MapFilterMsg";
	private Map<String[], String> convertProp;

	public MapFilter() {
	}

	public void setConvertMap(String convertMap) {
		String[] lines = convertMap.split("\n");
		this.convertProp = new LinkedHashMap<String[], String>();
		for(String line : lines){
			String[] split = line.split("=");
			if(split.length != 2) throw new IllegalArgumentException("キーの書式は[カテゴリ:マッチ文字列]=[出力文字列] です。");
			String key = split[0].trim();
			String val = split[1];
			String[] ary = key.split(":");
			if(ary.length != 2) throw new IllegalArgumentException("キーの書式は[カテゴリ:マッチ文字列]=[出力文字列] です。");
			this.convertProp.put(ary, val);
		}
	}


	@Override
	public FilterReply decide(ILoggingEvent event) {
		if (!(isStarted())) return FilterReply.NEUTRAL;

		for(Map.Entry<String[], String> entry : this.convertProp.entrySet() ){
			String category = entry.getKey()[0];
			String match = entry.getKey()[1];
			String msg = entry.getValue().toString();
			if(event.getLoggerName().startsWith(category)){
				if(event.getMessage().startsWith(match)){
					MDC.put(MDC_KEY, msg);
					return this.onMatch;
				}
			}
		}

		return this.onMismatch;
	}

	@Override
	public void start() {
		if(this.convertProp != null)
			super.start();
	}
}
