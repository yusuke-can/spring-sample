package service.business.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import service.business.model.Member;
import service.business.model.MemberSearchKeys;
import service.dao.MemberDao;


/**<pre>
 * メンバー情報サービス
 * </pre>
 */
@Service
public class MemberServiceImpl implements MemberService {
	private MemberDao memberDao;

	public MemberDao getMemberDao() {
		return memberDao;
	}

	@Autowired
	public void setMemberDao(MemberDao memberDao) {
		this.memberDao = memberDao;
	}

	@Override
	public Member obtainMember(int id){
		return this.memberDao.obtainMember(id);
	}

	@Override
	public void updateMember(Member user) throws OptimisticLockingFailureException {
		this.memberDao.updateMember(user);
	}

	@Override
	public List<Member> findMembers(MemberSearchKeys searchKeys) {
		return this.memberDao.findMembers(searchKeys);
	}

	@Override
	public int countMembers(MemberSearchKeys searchKeys) {
		return this.memberDao.countMembers(searchKeys);
	}

	@Override
	public InsertResult insertMember(Member user) {
		try{
			user.setId(-1);
			this.memberDao.insertMember(user);
			return InsertResult.OK;
		}catch(DuplicateKeyException e){
			return InsertResult.ERR_ACCOUT_DUPLICATED;
		}catch(Exception e){
		}
		return InsertResult.ERR_SYSTEM;
	}
}
