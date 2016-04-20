package service.business.service;

import java.util.List;

import org.springframework.dao.OptimisticLockingFailureException;

import service.business.model.Member;
import service.business.model.MemberSearchKeys;

public interface MemberService {

	/**
	 * メンバー登録結果
	 */
	enum InsertResult{
		/** 成功 */
		OK,
		/** 重複以外のエラー */
		ERR_SYSTEM,
		/** アカウントの重複エラー */
		ERR_ACCOUT_DUPLICATED
	};

	/**
	 * メンバー情報を取得する
	 * @param id
	 * @return
	 */
	public Member obtainMember(int id);

	/**
	 * 検索キーでメンバーのリストを取得する
	 * @param searchKeys
	 * @return
	 */
	public List<Member> findMembers(MemberSearchKeys searchKeys);

	/**
	 * 検索キーでマッチしたメンバーの数を取得する
	 * @param searchKeys
	 * @return
	 */
	public int countMembers(MemberSearchKeys searchKeys);

	/**
	 * メンバー情報を更新する。
	 * @param user
	 * @throws OptimisticLockingFailureException 他のユーザが更新しているとき
	 */
	public void updateMember(Member user)
			throws OptimisticLockingFailureException;

	/**
	 * メンバー登録。例外は返さないこと。
	 * 失敗時は、id=-1　を設定して返すこと。
	 * @return 登録結果。
	 */
	public InsertResult insertMember(Member user);
}