package service.common;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

import common.CommonUtils;
import common.model.BaseSearchKeys;


/**
 * 監査ログなどのメッセージを作成するメソッドを詰め込んだクラス。
 * DIするとメッセージリソースを内部に保存し、そのメッセージソースからメッセージを作成する。
 */
public class MsgMaker implements BeanFactoryAware,InitializingBean {
	private BeanFactory beanFactory;
	private MessageSourceAccessor messages;

	public void setMessages(MessageSource messages) {
		this.messages = new MessageSourceAccessor(messages);;
	}

	public String auditAccessInvalid(int inputUserId){
		return this.messages.getMessage("audit.access.invalid", new Object[]{inputUserId});
	}

	public String auditAccessInvalid(String loginId){
		return this.messages.getMessage("audit.access.invalid", new Object[]{loginId});
	}

	public String auditDataBefore(Object value){
		return this.messages.getMessage("audit.data.before", new Object[]{value});
	}
	public String auditDataAfter(Object value){
		return this.messages.getMessage("audit.data.after", new Object[]{value});
	}
	public <T> String auditDataDiff(T before, T after){
		try {
			return this.messages.getMessage("audit.data.diff",
				new Object[]{CommonUtils.diff(before, after)});
		} catch (Exception e) {
			return "エラー発生。更新データの差分をうまく抽出できませんでした。" + e.getMessage();
		}
	}

	public String auditFindData(BaseSearchKeys searchKeys){
		return this.messages.getMessage("audit.find.data", new Object[]{searchKeys});
	}

	@Override
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		this.beanFactory = factory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.messages = new MessageSourceAccessor(this.beanFactory.getBean(MessageSource.class));
	}
}
