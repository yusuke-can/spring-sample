package jp.co.test.controller;

import java.lang.reflect.Member;
import java.util.List;

import javax.validation.Valid;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import service.business.model.MemberSearchKeys;
import service.common.ErrorUtil;

import common.joda.JodaDateTimeEditor;

/**
 * このアノテーションをつけて、component-scanさせるとControllerとして扱われます。
 */
@Controller
@RequestMapping(value="/admin/member")
public class AdminMemberController extends MemberControllerBase {
	static Logger log = LoggerFactory.getLogger(AdminMemberController.class);

	//コンストラクタ===========================================-
	public AdminMemberController() {
		super("admin/");
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
		binder.setAllowedFields("member.name", "member.age", "member.version", "member.role",
			"memberSearchKeys.id", "memberSearchKeys.nameBW", "memberSearchKeys.loginId",
			"paging.searchKeys", "paging.pageSize", "paging.specifiedPageNum",
			"strSearchKeys");
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
		Form f = new Form();
		f.setAdmin(true);
		if(memberId != null){
			//ユーザ情報取得
			Member mem = this.memberService.obtainMember(memberId);
			f.setMember(mem);
		}

		return f;
	}



	/*リクエスト処理===========================================
	更新と参照などの処理ごとにControllerを作成しても良いと思う。
	あまりクラス内のコード量が増えると見えずらくなるので注意。
	*/

	///一覧検索の入力処理
	@RequestMapping(value="srch/input", method=RequestMethod.GET)
	public String findInput(Form form) {
		//既にnewRequestでモデルをDBから取り出し、設定しているので何もする必要がない
		form.getPaging().reset(1, 5);
		return "admin/member-Srch-Input";
	}

	///一覧検索の入力処理
	@RequestMapping(value="srch/input", method=RequestMethod.POST)
	public String findInputFromEdit(Form form) {
		return "admin/member-Srch-Input";
	}

	///一覧検索結果
	@RequestMapping(value="srch/list", method=RequestMethod.POST)
	public String findList(@Valid Form form, BindingResult result) {
		//@Validを指定したモデルは妥当性チェックが実行される。
		if(ErrorUtil.checkInvalidAndWriteLog(result)){
			return "admin/member-Srch-Input";
		}
		//サイズチェック
		if(form.getPaging().getPageSize() > 100) form.getPaging().setPageSize(100);
		form.getPaging().setPageListSize(5);

		//検索キー
		MemberSearchKeys searchKeys = form.getMemberSearchKeys();
		searchKeys.setOrderBy(MemberSearchKeys.ORDER_ID);

		//検索結果数を取得する
		int cnt = this.memberService.countMembers(searchKeys);
		if(ErrorUtil.checkListOverFlowCount(result, cnt, 1000)){
			return "admin/member-Srch-Input";
		}

		//検索をする
		List<Member> list = this.memberService.findMembers(searchKeys);
		form.setMemberList(list);
		form.getPaging().setCount(cnt);

		//監査ログ出力
		log.info(auditDaMarker, this.msg.auditFindData(searchKeys));

		return "admin/member-Srch-List";
	}


}
