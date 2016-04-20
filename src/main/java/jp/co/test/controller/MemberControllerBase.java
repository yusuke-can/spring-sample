package jp.co.test.controller;

import java.lang.reflect.Member;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.business.model.MemberSearchKeys;
import service.business.service.MemberService;
import service.common.ErrorUtil;
import service.common.MarkerConst;
import service.common.MsgMaker;

import common.web.WebPaging;


/**<pre>
 * Member画面のベースクラス。
 * 今回は管理者画面とエンドユーザ用の画面の更新処理が同じだったので、その処理を共通化しました。
 * そのほかの画面処理は派生クラスで実装する。
 * ちなみに、@RequestMappingアノテーションは派生クラスのメソッドで上書きすることもできます。
 * 【注意】
 * 基底クラスに共通メソッドとFormを持たせることでコードを減らせますが、２つの処理の差異が大きくなったとき、
 * 別々のコードを書く部分が多くなり、複雑でメンテがしづらくなる可能性もあります。
 * ケースバイケースでやり方を考えていただければと思います。
 * </pre>
 */
public class MemberControllerBase {
	static Logger log = LoggerFactory.getLogger(MemberControllerBase.class);
	//監査ログ用のマーカー
	static Marker auditAcMarker = MarkerFactory.getMarker(MarkerConst.AUTH);
	static Marker auditDaMarker = MarkerFactory.getMarker(MarkerConst.ACCESS_DATA);

	//メッセージ作成クラス
	@Autowired
	protected MsgMaker msg;

	@Autowired
	protected MemberService memberService;

	//
	protected String viewPrefix;


	public MemberControllerBase(String viewPrefix) {
		Assert.hasLength(viewPrefix, "viewPrefixが空です");
		this.viewPrefix = viewPrefix;
	}


	/*リクエスト処理===========================================
	更新と参照などの処理ごとにControllerを作成しても良いと思う。
	あまりクラス内のコード量が増えると見えずらくなるので注意。
	*/


	//ユーザ情報参照------------------------------------------
	///ユーザ情報更新内容の入力処理
	@RequestMapping(value="ref", method=RequestMethod.GET)
	public String reference(Form form) {
		//既にnewRequestでモデルをDBから取り出し、設定しているので何もする必要がない
		return this.viewPrefix + "member-Ref-Detail";
	}

	//ユーザ情報更新------------------------------------------
	///ユーザ情報更新内容の入力処理
	@RequestMapping(value="edit/input", method={RequestMethod.GET, RequestMethod.POST})
	public String input(Form form, HttpServletRequest req) {
		//既にnewRequestでモデルをDBから取り出し、設定しているので何もする必要がない
		return this.viewPrefix + "member-Edit-Input";
	}

	///ユーザ情報更新内容の確認処理
	@RequestMapping(value="edit/confirm", method=RequestMethod.POST)
	public String confirm(@Valid Form form, BindingResult result) {
		//@Validを指定したモデルは妥当性チェックが実行される。
		if(ErrorUtil.checkInvalidAndWriteLog(result)){
			return this.viewPrefix + "member-Edit-Input";
		}
		return this.viewPrefix + "member-Edit-Confirm";
	}

	///ユーザ情報更新処理をする（画面は表示せず、finishにリダイレクトする）
	@RequestMapping(value="edit/transactfinish", method=RequestMethod.POST)
	public String transactfinish(@Valid Form form, BindingResult result, RedirectAttributes redirectAtt,
			HttpServletRequest req)
	 throws Exception {
		//パラメタに問題がある場合は入力画面へ
		if(ErrorUtil.checkInvalidAndWriteLog(result)){
			return this.viewPrefix + "member-Edit-Input";
		}

		//更新前の値を取得
		Member memBefore = this.memberService.obtainMember(form.member.getId());

		//データ更新
		try{
			this.memberService.updateMember(form.member);
		}catch(OptimisticLockingFailureException e){
			//楽観的ロックエラーの場合。つまり他の人が更新していた場合。
			ErrorUtil.setOptimisticLockFailure(result);
			//現在のDBの値を設定する
			form.setMember(memBefore);
			return this.viewPrefix + "member-Edit-Input";
		}

		//更新後の値を取得
		Member memAfter = this.memberService.obtainMember(form.member.getId());

		//監査ログ
		//log.info(auditDaMarker, this.msg.auditDataBefore(memBefore));
		//log.info(auditDaMarker, this.msg.auditDataAfter(memAfter));
		log.info(auditDaMarker, this.msg.auditDataDiff(memBefore, memAfter));

		/*リダイレクトすることで、リロードで何度もこのDB更新処理が呼び出されてしまう問題を回避する
		リダイレクトでは次の画面にページング情報を送れないのでSpringのaddFlashAttributeを使用する。
		これを使うとリダイレクト先のJSPに値を渡すことができる。例：${searchKeys}。
		ただし、1回のみしか値が渡らないので、リダイレクト先の画面でリロードされると値が渡らずnullになることは注意点。
		この仕組みは、一時的にセッションに値を保存しているようです。
		 */
		redirectAtt.addFlashAttribute("searchKeys", form.getPaging().getSearchKeys());
		return "redirect:finish.html?member.id=" + form.member.getId();
	}

	///ユーザ情報更新後の完了画面を表示する。画面表示だけを行う。リロード対策。
	@RequestMapping(value="edit/finish", method=RequestMethod.GET)
	public String finish(@Valid Form form, BindingResult result, HttpServletRequest req) {
		return this.viewPrefix + "member-Edit-Finish";
	}


	//===========================================
	//フォーム(HTML用のパラメタを受け取れるように作っておいた方がよいと思います)
	public static class Form{
		static final String[] ROLE_LIST = new String[]{"ROLE_ADMIN", "ROLE_UPDATE", "ROLE_READ"};
		@Valid
		private MemberSearchKeys memberSearchKeys;
		@Valid
		private WebPaging paging;
		private List<Member> memberList;
		@Valid
		private Member member;
		private boolean isAdmin = false;

		public Form() {
			this.memberSearchKeys = new MemberSearchKeys();
			this.memberSearchKeys.setOrderBy(MemberSearchKeys.ORDER_ID);
			this.paging = new WebPaging(this.memberSearchKeys, 0);
			this.paging.setPageListSize(5);
			this.paging.setPageSize(5);
			this.paging.putValid(100, 50);
		}

		public String[] getRoleList() {
			return ROLE_LIST;
		}

		public WebPaging getPaging() {
			return paging;
		}
		public void setPaging(WebPaging paging) {
			this.paging = paging;
		}

		public MemberSearchKeys getMemberSearchKeys() {
			return memberSearchKeys;
		}
		public void setMemberSearchKeys(MemberSearchKeys memberSearchKeys) {
			this.memberSearchKeys = memberSearchKeys;
		}

		public List<Member> getMemberList() {
			return memberList;
		}
		public void setMemberList(List<Member> memberList) {
			this.memberList = memberList;
		}

		public Member getMember() {
			return member;
		}
		public void setMember(Member member) {
			this.member = member;
		}

		public boolean isAdmin() {
			return isAdmin;
		}
		public void setAdmin(boolean isAdmin) {
			this.isAdmin = isAdmin;
		}
	}

}
