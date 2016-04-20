package jp.co.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.tree.RowMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public List<UserModel> getUser(String id) {
		List<UserModel> list = jdbcTemplate.query("select * from m_user where id= ?", new Object[] { id },
				new RowMapper<UserModel>() {
					public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
						UserModel user = new UserModel();
						user.setId(rs.getString("id"));
						user.setName(rs.getString("name"));
						return user;
					}
				});
		return list;
	}

	@Override
	public List<UserModel> getUsers() {
		List<UserModel> list = jdbcTemplate.query("select * from m_user", new Object[]{},
				new RowMapper<UserModel>() {
					public UserModel mapRow(ResultSet rs, int rowNum) throws SQLException {
						UserModel user = new UserModel();
						user.setId(rs.getString("id"));
						user.setName(rs.getString("name"));
						return user;
					}
				});
		return list;
	}
}
