package service.dao.mybatis;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import service.business.model.Member;
import service.business.model.MemberSearchKeys;
import service.dao.MemberDao;

/**<pre>
 * MyBatis版、Member。
 * </pre>
 */
@Repository
public class MemberDaoImpl extends SqlSessionDaoSupport implements MemberDao{
	@Autowired
	public MemberDaoImpl(SqlSessionFactory sqlSessionFactory) {
		super.setSqlSessionFactory(sqlSessionFactory);
	}

	@Override
	public Member obtainMember(int id) {
		Member user = getSqlSession().selectOne("mybatis.sample.Member.obtain", id);
		return user;
	}

	@Override
	public List<Member> findMembers(MemberSearchKeys searchKeys) {
		return getSqlSession().selectList("mybatis.sample.Member.find", searchKeys);
	}

	@Override
	public int countMembers(MemberSearchKeys searchKeys) {
		return getSqlSession().selectOne("mybatis.sample.Member.count", searchKeys);
	}

	@Override
	public void updateMember(Member user) throws OptimisticLockingFailureException {
		user.setUpDate(new DateTime());
		int num = getSqlSession().update("mybatis.sample.Member.update", user);
		//楽観的ロック失敗時
		if(num != 1) throw new ObjectOptimisticLockingFailureException(user.getClass(), user);
		//更新がうまくいったのでバージョンのインクリメントをする。
		user.setVersion(user.getVersion() + 1);
	}

	@Override
	public void insertMember(Member user) {
		user.setUpDate(new DateTime());
		getSqlSession().insert("mybatis.sample.Member.insert", user);
		user.setVersion(0);
	}

}
