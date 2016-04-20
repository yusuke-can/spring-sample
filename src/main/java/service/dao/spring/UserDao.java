package service.dao.spring;

import org.joda.time.DateTime;

import service.business.model.Member;

//本当はインターフェースからimplemetsすべきですが、ここでは簡略のためそのままクラスを作っています。
public class UserDao {
	public Member getUser(int id){
        //ここではハードコードしていますが、本当はDBから値を取得します。
        Member user = new service.business.model.Member();
		try {
			user.setId(id);
	        user.setName("未華子");
	        user.setAge(21);
	        //SimpleDateFormat f =new SimpleDateFormat("yyyy/MM/dd");
			user.setUpDate(DateTime.parse("2012/04/01"));
			user.setLoginPw("hana");
		} catch (Exception e) {}
        return user;
    }

    public void updateUser(Member user){
        //DBに値を更新する処理を書きます。ここでは省略。
    }

}
