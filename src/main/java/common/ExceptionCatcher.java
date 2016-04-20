package common;

import org.springframework.util.ObjectUtils;

/**<pre>
 * 指定の例外が投げられたときに、指定の値を返却する。
 * </pre>
 * @param <T> 返り値の型
 */
@Deprecated
public abstract class ExceptionCatcher<T> {
	protected Class<?>[] ex;

	public ExceptionCatcher(Class<?> ...e){
		this.ex = e;
	}

	public T exec(T ret) throws Exception{
		try{
			return transact();
		}catch(Exception e){
			if(ObjectUtils.containsElement(this.ex, e)){
				return ret;
			}
			throw e;
		}
	}

	abstract protected T transact();
}
