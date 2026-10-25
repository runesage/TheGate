package thegate.main.save;

/**
 * A SQL statement with its parameters, bound with a PreparedStatement
 * so values never become part of the SQL text.
 */
public class SqlStatement {

	private final String sql;
	private final Object[] params;

	public SqlStatement(String sql, Object... params) {
		this.sql = sql;
		this.params = params;
		// Statements used to be built by string concatenation, which stored null as the
		// text "null". The plugin still relies on that marker, so keep storing it.
		for(int i = 0; i < params.length; i++) {
			if(params[i] == null) params[i] = "null";
		}
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}

}
