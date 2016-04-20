package service.dao.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import service.business.model.Member;
import service.business.model.MemberSearchKeys;
import service.dao.MemberDao;
/**
 *
 *
 */
@Repository
public class MemberDaoImpl extends JdbcDaoSupport implements MemberDao{
	//JDBCテンプレート
	protected NamedParameterJdbcTemplate paramTemplate;
	protected JdbcTemplate jdbcTemplate;

	//会員情報マッピング（selectのためのマッピングです）
	protected class MemberRowMapper implements ParameterizedRowMapper<Member> {
		@Override
		public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
			Member mem = new Member();
			mem.setId(rs.getInt("id"));
			mem.setName(rs.getString("name"));
			mem.setLoginId(rs.getString("login_id"));
			mem.setLoginPw(rs.getString("login_pw"));
			mem.setAge(rs.getInt("age"));
			mem.setUpDate(new DateTime(rs.getTimestamp("up_date")));
				return mem;
		}
	}


	//初期化
	protected void initDao()
	{
		this.paramTemplate = new NamedParameterJdbcTemplate(getDataSource());
		this.jdbcTemplate = new JdbcTemplate(getDataSource());
	}


	@Override
	public Member obtainMember(int id) {
		String sql = "select * from t_member where id = ?";
		return this.jdbcTemplate.queryForObject(sql, new MemberRowMapper(), id);
	}

   /**
    * Userを検索する。
    * ちまたのサンプルですと、検索キーのクラスとselectしてくるクラスを同じにしている物が多いです。
    * しかし、そもそも意味合いも使い方も違うので、同じにすると使いづらくなります。
    * そこで、このようにselectするクラスと別に検索キークラスを作成し、それを検索キーにします。
    * @param searchKeys
    * @return
    */
	@Override
	public List<Member> findMembers(MemberSearchKeys searchKeys) {
		//sql作成
		String sql = "select * from t_member ";
		String where = makeWhere(searchKeys);
		if(where != null){
			sql += " where " + where;
		}
		//
		String orderBy = makeOrderBy(searchKeys);
		if(orderBy != null){
			sql += orderBy;
		}

		//会員リスト
		List<Member> memberList = this.paramTemplate.query(
				sql, new BeanPropertySqlParameterSource(searchKeys), new MemberRowMapper()
		);

		return memberList;
	}

	/**
	 * Where句をキーオブジェクトから作成する。
	 * @param searchKeys [in]検索キー
	 * @return　作成したWhere句
	 */
	protected String makeWhere(MemberSearchKeys searchKeys) {
		StringBuilder where = new StringBuilder();
		if(searchKeys.getId() != -1){
			where.append(" and id = :id");
		}
		if(searchKeys.getName() != null){
			where.append(" and name = :name");
		}
		if(searchKeys.getNameBW() != null){
			//\と%と_のエスケープも行い、前方一致の条件を追加する
			where.append(" and name like regexp_replace(:nameBW, '([\\\\%_])', '\\\\\\1', 'g') || '%'");
		}
		if(searchKeys.getAgeFrom() != -1){
			where.append(" and age >= :ageFrom");
		}
		if(searchKeys.getAgeTo() != -1){
			where.append(" and age >= :ageTo");
		}
		if(where.length() != 0){
			return where.substring(4);
		}

		return null;
	}

	/**
	 * order by 句を作成する。
	 * @param searchKeys [in]検索キー
	 * @return 作成したorder by句
	 */
	protected String makeOrderBy(MemberSearchKeys searchKeys){
		StringBuilder orderBy = new StringBuilder();
		if(searchKeys.getOrderBy() != null){
			for(int column : searchKeys.getOrderBy()){
				switch(Math.abs(column)){
				case MemberSearchKeys.ORDER_ID:
					orderBy.append(column > 0 ? ",id" : ",id desc");
					break;
				case MemberSearchKeys.ORDER_AGE:
					orderBy.append(column > 0 ? ",age" : ",age desc");
					break;
				}
			}
		}
		if(orderBy.length() == 0) return null;
		return orderBy.replace(0, 1, "order by ").toString();
	}


   /**
    * User更新サンプル。すべてのカラムを更新するSQLを作成する。よく見かけるコードではnameだけを更新するSQLで
    * １つのメソッドを作成し、ageだけを更新するSQLで1つメソッドを作成、nameとageを作成するメソッドを1つというように
    * 別々にメソッドを作成している。
    * しかし、それでは効率が悪いのですべてのカラムを更新するメソッドを１つだけ用意しておくのが効率が良い（し、一般的）。
    * そうすると変更のないカラムも更新することになるが、値が変わらないので問題ない。
    * こういったやり方をしないなら、WEBの画面ごとに独自のSQLを作成することになり、DB処理を共通化する仕組みであるDaoの
    * 意味がかなり薄れると個人的には思えます。
    * @param user
    */
	@Override
	public void updateMember(Member user) throws OptimisticLockingFailureException{
		Assert.notNull(user);
		Assert.state(user.getId() > 0);
		//SQL
		String sql = "update t_member set name = :name, login_id = :loginId, login_pw= :loginPw, age = :age, up_date = now() "
		+ "where id = :id and up_date = :upDate";
		//SQL実行
		int num = this.paramTemplate.update(sql, new BeanPropertySqlParameterSource(user));

		//楽観的ロック失敗時
		if(num != 1) throw new ObjectOptimisticLockingFailureException(user.getClass(), user);
	}


	@Override
	public void insertMember(Member user) {
		Assert.notNull(user);
		//ID取得
		int id = this.jdbcTemplate.queryForObject("select nextval('seq_member_id')", int.class);
		user.setId(id);

		//挿入
		String sql = "insert into t_member(id, name, age, up_date, login_id, login_pw, role)"
				+ " values(:id, :name, :age, :upDate, :loginId, :loginPw, :role)";

		//SQL実行
		this.paramTemplate.update(sql, new BeanPropertySqlParameterSource(user));
		user.setUpDate(null);
	}


	@Override
	public int countMembers(MemberSearchKeys searchKeys) {
		// TODO Auto-generated method stub
		return 0;
	}
}
