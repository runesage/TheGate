package thegate.main.save;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.bukkit.plugin.Plugin;

import thegate.main.TheGateMain;


enum DatabaseFormat{
	MYSQL,
	SQLITE
}

public class DatabaseManager {

	private Thread SqlStatementThread;

	private boolean StatementThread = true;
	private Semaphore lock = new Semaphore(1);
	
	private ArrayList<SqlStatement> SQLStatements = new ArrayList<SqlStatement>();
	
	private DatabaseInfo dbInfo;
	
	private Connection connection;
	
	public DatabaseManager(DatabaseInfo dbInfo) {
		this.dbInfo = dbInfo;
	}
	
	
	/**
	 * This function sets up the database access.
	 * 
	 * @param plugin Is needed to find the datafolder.\nCan be null if the format is not SQLITE
	 * 
	 * */
	public boolean createDatabaseConnection(Plugin plugin) {
		switch (dbInfo.getDbFormat()) {
		case SQLITE:
			
			File sqlitefile = new File(plugin.getDataFolder(), File.separator + dbInfo.getFileName() + ".sqlite");
			if (!sqlitefile.exists()) {
				try {
					sqlitefile.createNewFile();
				} catch (IOException e) {
					plugin.getLogger().log(Level.WARNING, "Could not create new save file!");
					return false;
				}
			}
			try {
				this.connection = openNewConnection();
				plugin.getLogger().log(Level.INFO, "Database status: " + (testConnection() ? "Connected" : "Offline"));
				plugin.getLogger().log(Level.INFO, "Using SQLite as save format.");
			} catch (SQLException e) {
				plugin.getLogger().log(Level.WARNING, "Connection to SQLite database failed!");
				plugin.getLogger().log(Level.WARNING, "Check if your connection information is correct ->");
				plugin.getLogger().log(Level.WARNING, "Path: " + dbInfo.getUrl());
				plugin.getLogger().log(Level.WARNING, e.toString());
				return false;
			}
			break;
		case MYSQL:
			try {
				this.connection = openNewConnection();
				plugin.getLogger().log(Level.INFO, "Database status: " + (testConnection() ? "Connected" : "Offline"));
				plugin.getLogger().log(Level.INFO, "Using MySQL as save format.");
			} catch (SQLException e) {
				plugin.getLogger().log(Level.WARNING, "Connection to MySQL database failed!");
				plugin.getLogger().log(Level.WARNING, "Check if your connection information is correct ->");
				plugin.getLogger().log(Level.WARNING, "Path: " + this.dbInfo.getUrl());
				plugin.getLogger().log(Level.WARNING, "UserName: " + this.dbInfo.getUser());
				plugin.getLogger().log(Level.WARNING, "UserPassword: ***");
				plugin.getLogger().log(Level.WARNING, e.toString());
				return false;
			}
			break;
		default:
			plugin.getLogger().log(Level.WARNING, "No correct information given for the save format!");
			plugin.getLogger().log(Level.WARNING, "Check config!");
			return false;
		}
		return true;
	}
	
	public void closeConnection() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void startAsyncWorker() {
		SqlStatementThread = new Thread(() -> {
			while(StatementThread) {
				try {
					lock.acquire();
				} catch (InterruptedException e) {
					break;
				}
				executePendingStatements();
			}
			// Statements queued right before shutdown (like the final save) would otherwise be lost.
			executePendingStatements();
		});
		SqlStatementThread.start();
	}
	
	private void executePendingStatements() {
		List<SqlStatement> pending;
		synchronized(this) {
			if(SQLStatements.isEmpty()) return;
			pending = new ArrayList<>(SQLStatements);
			SQLStatements.clear();
		}
		try (Connection conn = openNewConnection()) {
			for(SqlStatement s : pending) {
				try (PreparedStatement statement = prepare(conn, s.getSql(), s.getParams())) {
					statement.execute();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void stopAsyncWorker() {
		this.StatementThread = false;
		lock.release();
		try {
			SqlStatementThread.join();
		}catch(InterruptedException e) {
			TheGateMain.theGateMain.getLogger().log(Level.WARNING, e.toString());
		}
	}
	
	public synchronized void asyncSqlStatements(List<SqlStatement> list) {
		SQLStatements.addAll(list);
		lock.release();
	}
	
	public synchronized void asyncSqlStatement(SqlStatement statement) {
		SQLStatements.add(statement);
		lock.release();
	}
	
	public void asyncSqlStatement(String sql, Object... params) {
		asyncSqlStatement(new SqlStatement(sql, params));
	}
	
	private static PreparedStatement prepare(Connection conn, String sql, Object... params) throws SQLException {
		PreparedStatement statement = conn.prepareStatement(sql);
		Object[] values = new SqlStatement(sql, params).getParams();
		for(int i = 0; i < values.length; i++) {
			statement.setObject(i + 1, values[i]);
		}
		return statement;
	}
	
	public boolean executeQuerry(String querry) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(querry);
		boolean out = statement.execute();
		return out;
	}
	
	/** Runs an INSERT and returns the generated id of the new row, or -1 if none was created. */
	public long executeInsert(String querry, Object... params) throws SQLException {
		Object[] values = new SqlStatement(querry, params).getParams();
		try (PreparedStatement statement = connection.prepareStatement(querry, Statement.RETURN_GENERATED_KEYS)) {
			for(int i = 0; i < values.length; i++) {
				statement.setObject(i + 1, values[i]);
			}
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				return keys.next() ? keys.getLong(1) : -1;
			}
		}
	}
	
	public int executeUpdate(String querry, Object... params) throws SQLException {
		PreparedStatement statement = prepare(connection, querry, params);
		int out = statement.executeUpdate();
		return out;
	}
	
	public boolean testConnection() {
		if(this.connection == null) return false;
		try {
			if(executeQuerry("SELECT 1;")) return true;
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	public boolean testConnection(Connection con) {
		if(con == null) return false;
		try {
			if(con.prepareStatement("SELECT 1;").execute()) return true;
		} catch (SQLException e) {
			return false;
		}
		return false;
	}
	
	public boolean hasTable(String TableName) {
		String querry = "SELECT * FROM " + TableName;
		try {
			return executeQuerry(querry);
		} catch (SQLException e) {
			return false;
		}
	}
	
	public ResultSet getData(String querry, Object... params) throws SQLException {
		return prepare(connection, querry, params).executeQuery();
	}
	
	public static DatabaseInfo getDatabaseInfo(String url, String name, String pass, DatabaseFormat format) {
		return new DatabaseInfo(url, name, pass, format);
	}
	
	public static DatabaseInfo getDatabaseInfo(String url, String name, String pass, String format) throws Exception{
		DatabaseFormat form = null;
		for(DatabaseFormat f : DatabaseFormat.values()) {
			if(f.toString().toUpperCase().equals(format.toUpperCase())) form = f; 
		}
		if(form == null) throw new Exception("Format not valid: " + format);
		return getDatabaseInfo(url, name, pass, form);
	}
	
	public static DatabaseInfo getDatabaseInfo(Plugin plugin, String fileName) {
		return new DatabaseInfo(plugin, fileName);
	}
	
	public Connection openNewConnection() throws SQLException {
		return (this.dbInfo.getUser() != null && this.dbInfo.getPass() != null ? DriverManager.getConnection(this.dbInfo.getUrl(), this.dbInfo.getUser(), this.dbInfo.getPass()) : DriverManager.getConnection(this.dbInfo.getUrl()));
	}


	public Connection getConnection() {
		return connection;
	}


	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	
}

class DatabaseInfo{
	
	private final String url;
	private final String user;
	private final String pass;
	private final DatabaseFormat dbFormat;
	private final String fileName;
	
	/**
	 * For sqlite the url has to be of the format:\n
	 * SQLiteUrl =  plugin.getDataFolder() + File.separator + fileName
	 * No file ending is needed.
	 * 
	 * */
	public DatabaseInfo(String url, String name, String pass, DatabaseFormat dbFormat){

		//String SQLiteUrl =  plugin.getDataFolder() + File.separator + fileName + ".sqlite";
		switch(dbFormat) {
		case MYSQL:
			this.url = "jdbc:mysql:" + url;
			this.user = name;
			this.pass = pass;
			this.dbFormat = dbFormat;
			break;
		case SQLITE:
			this.url = "jdbc:sqlite:" + url + ".sqlite";
			this.user = null;
			this.pass = null;
			this.dbFormat = dbFormat;
			break;
			default:
				this.url = url;
				this.user = name;
				this.pass = pass;
				this.dbFormat = dbFormat;
		}

		this.fileName = "";
	}
	
	/**
	 * This constructor is preset for SQLITE files
	 * 
	 * */
	public DatabaseInfo(Plugin plugin, String fileName){
		this.dbFormat = DatabaseFormat.SQLITE;
		this.url = "jdbc:sqlite:" + plugin.getDataFolder() + File.separator + fileName + ".sqlite";
		this.user = null;
		this.pass = null;

		this.fileName = fileName;
	}
	
	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public DatabaseFormat getDbFormat() {
		return dbFormat;
	}

	public String getFileName() {
		return fileName;
	}
	
	@Override
	public String toString() {
		return "URL:\t" + (url != null ? url : "null") + "\nName:\t" + (user != null ? user : "null") + "\nDBFormat:\t" + (this.dbFormat.toString() != null ? this.dbFormat.toString() : "null") + "\nFileName:\t" + (fileName != null ? fileName : "null");
	}
	
}



//create async worker for getting data from database

//interface AsyncDataWorker{
//	
//	void work(ResultSet results);
//	
//}
