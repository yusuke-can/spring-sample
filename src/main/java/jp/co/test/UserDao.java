package jp.co.test;

import java.util.List;

public interface UserDao {
	public List<UserModel> getUser(String id);
	public List<UserModel> getUsers();
}
