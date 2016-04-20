package common.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.joda.time.DateTime;



/**
 * JodaのDateTimeを扱うMyBatisのハンドラ。
 *
 */
public class JodaDateTimeTypeHandler implements TypeHandler<DateTime> {

	@Override
	public DateTime getResult(ResultSet rs, String name) throws SQLException {
		Timestamp ts = rs.getTimestamp(name);
		if(ts == null) return null;
		return new DateTime(ts.getTime());
	}

	@Override
	public DateTime getResult(ResultSet rs, int index) throws SQLException {
		Timestamp ts = rs.getTimestamp(index);
		if(ts == null) return null;
		return new DateTime(ts.getTime());
	}

	@Override
	public DateTime getResult(CallableStatement cs, int index)
			throws SQLException {
		Timestamp ts = cs.getTimestamp(index);
		if(ts == null) return null;
		return new DateTime(ts.getTime());
	}

	@Override
	public void setParameter(PreparedStatement ps, int index, DateTime data,
			JdbcType type) throws SQLException {
		try {
			if(data == null){
				ps.setTimestamp(index, null);
				return;
			}

			ps.setTimestamp(index, new Timestamp(data.getMillis()));
		} catch (Exception e) {
			throw new SQLException("FileDataのデータ設定中に予期せぬエラー発生", e);
		}
	}

}
