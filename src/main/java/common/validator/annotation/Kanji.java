package common.validator.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Constraint;
import javax.validation.Payload;

import common.validator.KanjiValidator;


@Constraint(validatedBy = KanjiValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Kanji {
	enum CheckType{ZENKAKU, ZENKAKU_LINES, HANKAKU, HIRAGANA, KATAKANA, NUMBER;
		static final public Map<CheckType, String> map = new HashMap<CheckType, String>();
		static{
			map.put(CheckType.ZENKAKU, "全角");
			map.put(CheckType.HIRAGANA, "ひらがな");
			map.put(CheckType.KATAKANA, "カタカナ");
			map.put(CheckType.ZENKAKU, "全角");
			map.put(CheckType.ZENKAKU_LINES, "全角");
			map.put(CheckType.NUMBER, "全角数字");
		}
		@Override
		public String toString() {
			return map.get(this);
		}
	};

	//
	String message() default "{sample.validation.constraints.Kanji.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	CheckType type() default CheckType.ZENKAKU;
	String typeStr() default "全角";
}
