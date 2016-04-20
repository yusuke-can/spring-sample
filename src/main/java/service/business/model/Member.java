package service.business.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;
import org.joda.time.DateTime;

import common.model.BaseObject;
import common.validator.annotation.Kanji;
import common.validator.annotation.Kanji.CheckType;



public class Member extends BaseObject{

	@Range(min=0)
	private int id;

	@NotNull
	@Kanji(type=CheckType.ZENKAKU)
	@Size(max=20)
	private String name;

	@Range(min=0, max=200)
	private int age;

	@NotNull
	@Kanji(type=CheckType.HANKAKU)
	@Size(min=3, max=20)
	private String loginId;

	@NotNull
	@Kanji(type=CheckType.HANKAKU)
	@Size(min=3, max=20)
	private String loginPw;

	private DateTime upDate; //更新日

	@NotNull
	@Kanji(type=CheckType.HANKAKU)
	@Pattern(regexp="ROLE_(ADMIN|UPDATE|READ)")
	private String role;

	private int version;

	public int getId() {
	  return id;
	}
	public void setId(int id) {
	  this.id = id;
	}
	public String getName() {
	  return name;
	}
	public void setName(String name) {
	  this.name = name;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public String getLoginPw() {
		return loginPw;
	}
	public void setLoginPw(String passwd) {
		this.loginPw = passwd;
	}
	public int getAge() {
	  return age;
	 }
	 public void setAge(int age) {
	  this.age = age;
	 }
	 public DateTime getUpDate() {
	  return upDate;
	 }
	 public void setUpDate(DateTime upDate) {
	  this.upDate = upDate;
	 }
	 public String getRole() {
		return role;
	}
	 public void setRole(String role) {
		this.role = role;
	}
	 public int getVersion() {
		return version;
	}
	 public void setVersion(int version) {
		this.version = version;
	}
}
