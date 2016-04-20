package common;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public abstract class CommonUtils {

	/**
	 * 配列をMapに変換する。可変引数にはString,Objectのセットで順番に設定すること。
	 * @param args [in]
	 * @return Map
	 */
	static public Map<String, Object> map(Object ...args){
		Map<String, Object> ret = new LinkedHashMap<String, Object>();
		for(int i=0; i<args.length; i+=2){
			String key = (String)args[i];
			Object value = (i+1 < args.length ? args[i+1] : "");
			ret.put(key, value);
		}
		return ret;
	}


	/**
	 * 文字列で書かれた配列をMapに変換する。
	 * @param args [in]文字列で書かれた配列（例："name,太郎,id,11"）
	 * @return
	 * @see #map(Object...)
	 */
	static public Map <String, Object> strToMap(String args){
		if(args == null) return null;
		String[] params = args.split("\\s*,\\s*");
		return map((Object[])params);
	}

	/**
	 * 引数をvalueにしたMapにする。Mapのキーはvalueそれぞれのクラス名。
	 * @param args
	 * @return 作成されたMap
	 * @see #getClassName(Object)
	 */
	static public Map<String, Object> nameMap(Object ...args){
		Map<String, Object> ret = new HashMap<String, Object>();
		for(Object obj : args){
			String key = getClassName(obj);
			ret.put(key, obj);
		}
		return ret;
	}

	/**
	 * 2つの値が同じかどうかを返す。2つともnullの場合はtrue。
	 * @param v1 [in]
	 * @param v2 [in]
	 * @return 比較の結果
	 */
	static public <T> boolean isEquals(T v1, T v2){
		if(v1 == v2) return true;
		if(v1 == null || v2 == null) return false;
		return v1.equals(v2);
	}

	/**
	 * Object[]かListの時trueを返す
	 * @param target
	 * @return
	 */
	static public boolean isArray(Object target){
		if(target == null) return false;
		if(target instanceof List) return true;
		if(ObjectUtils.isArray(target)) return true;
		return false;
	}

	/**
	 * valueがemptyかどうかを返す。もしvalueが配列かListのときは、要素数が0のときemptyとみなす。
	 * @param value
	 * @return
	 */
	static public boolean isValueEmpty(Object value){
		if(value == null) return true;
		if(value instanceof List){
			return CollectionUtils.isEmpty((List)value);
		}
		if(ObjectUtils.isArray(value)){
			return ObjectUtils.isEmpty((Object[])value);
		}
		if(value instanceof String) return ((String)value).isEmpty();
		return false;
	}

	/**
	 * リクエストにモデルを設定する。
	 * @param req [out]modelsにオブジェクトを設定されたリクエスト
	 * @param args [in]オブジェクトを引数にする。キー名にオブジェクトのクラス名、値にオブジェクトを設定する、
	 */
	static public void putAttribute(HttpServletRequest req, Object ...args){
		Map<String, Object> ret = nameMap(args);
		req.setAttribute("models", ret);
	}

	/**
	 * POJO名に使用される形式のクラス名を取得する。（例：　TestClassなら"testClass"）
	 * @param obj
	 * @return
	 */
	static public String getClassName(Object obj){
		String shortClassName = obj.getClass().getSimpleName();
		return Introspector.decapitalize(shortClassName);
		//String name = obj.getClass().getSimpleName();
		//return name.substring(0, 1).toLowerCase() + name.substring(1);
	}


	/**
	 * getClass()を除いたプロパティを取得する。
	 * @param c
	 * @return
	 * @throws Exception
	 */
	static public List<PropertyDescriptor> getPropertyDesc(Class<?> c)throws Exception{
		List<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
		PropertyDescriptor[] desc = PropertyUtils.getPropertyDescriptors(c);
		for(PropertyDescriptor d : desc){
			if(d.getName().equals("class")) continue;
			list.add(d);
		}
		return list;
	}

	/**
	 * 更新前後の2つのPOJOの差異を検査する。結果はMap。キーはプロパティ名。値は更新前、更新後の値を示した文字列。
	 * @param before [in]更新前のオブジェクト
	 * @param after [in]更新後のオブジェクト
	 * @return 差異
	 * @throws Exception
	 */
	static public <T> Map<String, String> diff(T before, T after) throws Exception{
		Map<String, String> ret = new HashMap<String, String>();
		List<PropertyDescriptor> desc = getPropertyDesc(before.getClass());
		for(PropertyDescriptor d : desc){
			Method method = d.getReadMethod();
			Object b = method.invoke(before);
			Object a = method.invoke(after);
			if(b != null && b.equals(a)) continue;
			if(a == b) continue;
			//違いがある場合
			ret.put(d.getName(), "before[" + b + "]: after[" + a + "]");
		}
		return ret;
	}

	/**
	 * listの要素のプロパティ（propertyName）を新たなListにして返す。
	 * @param list [in]
	 * @param propertyName [in]プロパティ名
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	static public List projectionList(List list, String propertyName)
	throws Exception{
		if(!StringUtils.hasText(propertyName)) return list;
		List<Object> ret = new ArrayList<Object>();
		for(Object item : list){
			Object value = PropertyUtils.getProperty(item, propertyName);
			ret.add(value);
		}
		return ret;
	}


	/**
	 * origの指定のプロパティ（getter）の値をコピーする。
	 * @param <T>
	 * @param orig
	 * @param propertyName
	 * @param c
	 * @return
	 * @throws Exception
	 */
	static public <T> T copyProperty(Object orig, String propertyName, Class<T> c) throws Exception {
		T bean = org.springframework.beans.BeanUtils.instantiateClass(c);
		Object obj = PropertyUtils.getProperty(orig, propertyName);
		BeanUtils.copyProperties(bean, obj);
		return bean;
	}


	/**
	 * 指定のファイルにストリームの内容を保存する。
	 * Stringなどを保存したい場合は、FileUtils.writeXxx（）などを使用する。
	 * @param file
	 * @param is
	 * @throws IOException
	 */
	static public void saveFile(File file, InputStream is) throws IOException{
		byte[] buf = new byte[1024];
		int size = 0;
		OutputStream os = null;

		try{
			os = new BufferedOutputStream(new FileOutputStream(file));
			while((size = is.read(buf)) != -1){
				os.write(buf, 0, size);
			}
		}finally{
			if(os != null) try{ os.close(); }catch(Exception e){}
		}
	}



	/**
	 * valueからPropertyEditorを生成する。
	 * 接頭子と書式を制定する。(例："date:yyyyMMdd")
	 * 接頭子には、"date","num"がある。
	 * @param map [in]keyにプロパティ名、valueに接頭子つき書式。
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Deprecated
	static public Map<String, PropertyEditor> convertToPropertyEditor(Map<String, String> map, Class<?> c){
		Map<String, PropertyEditor> ret = new LinkedHashMap<String, PropertyEditor>();
		for(Entry<String, String> entry : map.entrySet()){
			String name = entry.getKey();
			String value = entry.getValue();
			Class<?> type = org.springframework.beans.BeanUtils.findPropertyType(
					name, new Class<?>[]{c});

			//接頭子からEditorを生成する
			PropertyEditor editor = null;
			if(value.startsWith("num:") && type.isAssignableFrom(Number.class)){
				String format = value.substring(4);
				NumberFormat n = new DecimalFormat(format);
				editor = new CustomNumberEditor((Class<? extends Number>) type, n, true);
			}else if(value.startsWith("date:") && type.isAssignableFrom(Date.class)){
				String format = value.substring(5);
				DateFormat n = new SimpleDateFormat(format);
				editor = new CustomDateEditor(n, true);
			}else if(value.isEmpty()){
				editor = new PropertyEditorSupport();
			}else{
				throw new IllegalArgumentException("存在しない接頭子が使用されたか、型とあっていません。"
					+ value + ",type=" + type);
			}
			//設定
			ret.put(name, editor);
		}
		return ret;
	}

	/**
	 * 入力のサイズを返す。
	 * isの状態を変更して計算するため、使用するInputStreamの状態が元に戻っていない
	 * 可能性もあることに注意。
	 *
	 * @param is [in]
	 * @return
	 * @throws Exception
	 */
	static public long inputStreamSize(InputStream is) throws Exception{
		byte[] buf = new byte[1024];
		long size = 0;
		long cnt = 0;

		try{
			is.mark(1000000000);
			while((cnt = is.read(buf)) != -1){
				size += cnt;
			}
			return size;
		}finally{
			is.reset();
		}
	}


}
