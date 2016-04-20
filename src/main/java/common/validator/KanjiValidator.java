package common.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import common.StringCheckUtils;
import common.validator.annotation.Kanji;


/**<pre>
 * 漢字の妥当性チェック。
 * 全角、半角、ひらがな、カタカナをチェックする。
 * </pre>
 */
public class KanjiValidator implements ConstraintValidator<Kanji, String> {
	private Kanji.CheckType checkType;

	@Override
	public void initialize(Kanji kanji) {
		this.checkType = kanji.type();
	}


	@Override
	public boolean isValid(String value, ConstraintValidatorContext ctx) {
		switch(this.checkType){
		case ZENKAKU:
			return StringCheckUtils.isZenkaku(value);
		case ZENKAKU_LINES:
			return StringCheckUtils.isZenkakuMultiline(value);
		case HANKAKU:
			return StringCheckUtils.isHankaku(value);
		case HIRAGANA:
			return StringCheckUtils.isHiragana(value);
		case KATAKANA:
			return StringCheckUtils.isKatakana(value);
		case NUMBER:
			return StringCheckUtils.isZenkakuNumber(value);
		}

		throw new IllegalArgumentException("checkTypeがおかしい。value=" + value + ", " + this.checkType);
	}

}
