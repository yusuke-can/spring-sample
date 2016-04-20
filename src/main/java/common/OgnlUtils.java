package common;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * OGNLで使用するためのメソッド
 *
 */
public abstract class OgnlUtils {

	/**
	 * OGNL式で使用するためのメソッド。プロパティが存在するかどうか
	 *
	 * @param bean [in]MyBatisのOGNLでは、_parameterを設定すればよい。
	 * @param name [in]プロパティ名
	 * @return 存在すればtrue
	 */
	static public boolean isPresent(Object bean, String name){
		try{
			PropertyUtils.getProperty(bean, name);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	/**
	 * 指定のプロパティがnullか、空文字のときにtrue。
	 * @param bean [in]対象オブジェクト
	 * @param name [in]プロパティ名
	 * @return emptyのときtrue
	 * @see com.sampletool.common.CommonUtils#isValueEmpty(Object)
	 */
	static public boolean isEmpty(Object bean, String name){
		try{
			Object obj = PropertyUtils.getProperty(bean, name);
			return CommonUtils.isValueEmpty(obj);
		}catch(Exception e){
			return true;
		}
	}


}
