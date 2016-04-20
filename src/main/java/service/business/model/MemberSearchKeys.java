package service.business.model;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Range;

import common.model.BaseSearchKeys;

public class MemberSearchKeys extends BaseSearchKeys {
	//抽出順序--------
	static public final int ORDER_ID = 1;
	static public final int ORDER_NAME = 2;
	static public final int ORDER_AGE = 32;

	//プロパティ---------
	@Range(min=1)
	private Integer id;
	@Size(max=20)
	private String name;
	///名前前方一致（BeginWith）
	@Size(max=20)
	private String nameBW;
	@Range(min=-1, max=200)
	private int ageFrom = -1;
	@Range(min=-1, max=200)
	private int ageTo = -1;
	@Size(max=20)
	private String loginId;
	//------
	private int[] orderBy;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNameBW() {
		return nameBW;
	}
	public void setNameBW(String nameBW) {
		this.nameBW = nameBW;
	}
	public int getAgeFrom() {
		return ageFrom;
	}
	public void setAgeFrom(int ageFrom) {
		this.ageFrom = ageFrom;
	}
	public int getAgeTo() {
		return ageTo;
	}
	public void setAgeTo(int ageTo) {
		this.ageTo = ageTo;
	}
	public int[] getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(int... orderBy) {
		this.orderBy = orderBy;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
}
