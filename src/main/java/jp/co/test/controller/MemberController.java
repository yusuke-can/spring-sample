package jp.co.test.controller;

import java.lang.reflect.Member;

import org.joda.time.DateTime;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import common.SecurityUtils;
import common.joda.JodaDateTimeEditor;

@Controller
@RequestMapping(value="/cust/member/")
public class MemberController extends MemberControllerBase {

	public MemberController() {
		super("cust/");
	}


	/*リクエスト初期化処理===========================================
	/**
	 * formモデルのバインダーの初期化。リクエストパラメタをモデルに変換するたびに呼ばれる。
	 */
	@InitBinder("form")
	public void initBinderForm(WebDataBinder binder) {
		//バインドするときの日付のフォーマット指定。
		binder.registerCustomEditor(DateTime.class, "member.upDate",
			new JodaDateTimeEditor("yyyy/MM/dd HH:mm:ss.SSS"));

		//Memberオブジェクトのうち、member.nameパラメタを受け取りたくない場合。攻撃による期待しない値の変更を防ぐ。
		binder.setAllowedFields("member.name", "member.age", "member.version");
	}


	/**
	 * モデルオブジェクトの初期化
	 * MemberオブジェクトをDBから取得する。
	 * 入力画面、確認画面、完了画面それぞれで使用される。
	 * setAllowedFieldsでパラメタの値が設定されないプロパティはここで取得したDBの値が設定されている。
	 * そのため、後でDaoがPOJOの値ですべてのカラムを更新しても、nullになったりしない。
	 */
	@ModelAttribute("form")
	public Form newRequest(
			@RequestParam(required=false, value="member.id") Integer memberId
	) {
		//ログインユーザ情報の取得
		String loginId = SecurityUtils.getLoginId();

		//ユーザ情報取得
		Member mem = this.memberService.obtainMember(memberId);

		//ログインしたIDと違うユーザIDのパラメタが送られてきた場合はエラーにする。なりすましを防ぐ。
		//今回の場合であれば、ユーザIDをパラメタで受け取らず、セッションに保管されたログインユーザからユーザIDを取得し、
		//ブラウザからはユーザIDを一切、受け取らない設計にしても良い。
		if(mem == null || !loginId.equals(mem.getLoginId())){
			log.info(auditAcMarker, this.msg.auditAccessInvalid(memberId));
			throw new AccessDeniedException("不正アクセス");
		}
		//
		Form f = new Form();
		f.setMember(mem);
		f.setAdmin(false);
		return f;
	}


}
