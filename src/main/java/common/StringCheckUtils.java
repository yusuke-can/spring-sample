package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;


/**<pre>
 * 文字列をチェックするメソッド群。
 * Validatorのメソッドの中に直接実装を記述する人がいますが、共通クラスに分離させた方が良いと思います。
 * HibernateValidator以外で使いたくなった時に、使いづらくなるからです。
 * </pre>
 */
public abstract class StringCheckUtils {
	private static Pattern hiraganaPattern = Pattern.compile("[ぁ-んー]*", Pattern.MULTILINE);
	private static Pattern hiraganaNoHyphenPattern = Pattern.compile("[ぁ-ん]*", Pattern.MULTILINE);
	private static Pattern katakanaPattern = Pattern.compile("[ァ-ヶー]*", Pattern.MULTILINE);
	private static Pattern numberPattern = Pattern.compile("[０-９]*", Pattern.MULTILINE);


	/**
	 * 半角チェック
	 * @param str
	 * @return チェック結果
	 */
	public static boolean isHankaku(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		int len = str.length();

		for(int i=0; i<len; ++i){
			char c = str.charAt(i);
			if (c > 0x00FF) {
				// 全角!
				return false;
			}
		}
		return true;
	}


	/**
	 * 全角チェック。改行も半角と皆師NG。
	 * @param str
	 * @return チェック結果
	 */
	public static boolean isZenkaku(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		int len = str.length();

		for(int i=0; i<len; ++i){
			char c = str.charAt(i);
			if (c <= 0x00FF) {
				// 半角!
				return false;
			}
		}
		return true;
	}

	/**
	 * 全角チェック。改行は無視する。
	 * @param str
	 * @return チェック結果
	 */
	public static boolean isZenkakuMultiline(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		int len = str.length();

		for(int i=0; i<len; ++i){
			char c = str.charAt(i);
			if(c == '\r' || c == '\n') break;
			if(c <= 0x00FF){
				// 半角!
				return false;
			}
		}
		return true;
	}



	/**
	 * 全角ひらがなかどうかをチェックする。"ゑ"もひらがなとみなす。長音（"ー"）も対象。小さな"か"、"け"は環境依存文字なので対象外。
	 * @return チェック結果
	 */
	public static boolean isHiragana(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		Matcher matcher = hiraganaPattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}

		return true;
	}



	/**
	 * 全角ひらがなかどうかをチェックする。"ゑ"もひらがなとみなす。長音（"ー"）は対象外。小さな"か"、"け"は対象。
	 * @return チェック結果
	 */
	public static boolean isHiraganaNoHyphen(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		Matcher matcher = hiraganaNoHyphenPattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}

		return true;
	}

	/**
	 * 全角カタカナかどうかをチェックする。長音もOK。"ヰヱ"もひらがなとみなす。小さな"ヵ"、"ヶ"は対象。
	 * @return チェック結果
	 */
	public static boolean isKatakana(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		Matcher matcher = katakanaPattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}

		return true;
	}

	/**
	 * 全角数字かどうかをチェックする。
	 * @return チェック結果
	 */
	public static boolean isZenkakuNumber(String str) {
		if (StringUtils.isEmpty(str)) {
			return true;
		}

		Matcher matcher = numberPattern.matcher(str);
		if(!matcher.matches()){
			return false;
		}

		return true;
	}

}
