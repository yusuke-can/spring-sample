package service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;


public abstract class ErrorUtil {
	static Logger log = LoggerFactory.getLogger(ErrorUtil.class);

	/**
	 * resultがエラーかどうかをチェックし、エラーの場合は妥当性チェックエラーのログを出力する。
	 * @param result [in]チェック対象
	 * @return エラーの場合：true
	 */
	static public boolean checkInvalidAndWriteLog(BindingResult result){
		if(result.hasErrors()){
			log.debug("妥当性チェックエラー： {}", new Object[]{result});
			return true;
		}
		return false;
	}

	/**
	 * 検索結果の数が多すぎないかをチェックする。
	 * @param result [out]結果数多すぎる場合は、エラーが設定される。
	 * @param count [in]検索結果数
	 * @param maxCount [in]最大結果数
	 * @return エラーの場合：true
	 */
	static public boolean checkListOverFlowCount(BindingResult result, int count, int maxCount){
		if(count > maxCount){
			log.warn("検索結果が最大値({})を超えています： {}", new Object[]{maxCount, result});
			//エラーを設定する。
			result.reject(ErrCodeConstant.LIST_OVER_FLOW, new Object[]{maxCount, count},
				"検索結果が最大値を超えています。");
			return true;
		}
		return false;
	}

	/**
	 * 楽観的ロックエラーを設定する。（つまり他の人が更新していた場合のエラー）
	 * ログ出力も行う。
	 * @param result
	 */
	static public void setOptimisticLockFailure(BindingResult result){
		//ログ出力
		log.warn("楽観的ロックエラー：他の人が更新しています。{}", new Object[]{result});

		//エラーを設定する。
		result.reject(ErrCodeConstant.OPTIMISTIC_LOCK);
	}
}
