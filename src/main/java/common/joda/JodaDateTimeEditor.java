package common.joda;

import java.beans.PropertyEditorSupport;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


/**<pre>
 * JodaのDateTimeを扱うPropertyEditor。
 * </pre>
 */
public class JodaDateTimeEditor extends PropertyEditorSupport {
	DateTimeFormatter format;

	public JodaDateTimeEditor(String format) {
		this.format = DateTimeFormat.forPattern(format);
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		DateTime value = this.format.parseDateTime(text);
		setValue(value);
	}

	@Override
	public String getAsText() {
		DateTime value = (DateTime)getValue();
		return value.toString(this.format);
	}
}
