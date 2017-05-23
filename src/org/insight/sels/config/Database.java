package org.insight.sels.config;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.insight.sels.datasources.DataSource;

public class Database {

	private static Database datasource;
	private BasicDataSource ds;

	private Database(DataSource d) throws IOException, SQLException, PropertyVetoException {
		ds = new BasicDataSource();
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUsername(d.getUserName());
		ds.setPassword(d.getPassword());
		ds.setUrl(d.getDataSourceURL());

		// the settings below are optional -- dbcp can work with defaults
		ds.setMinIdle(5);
		ds.setMaxIdle(20);
		ds.setMaxOpenPreparedStatements(180);

	}

	public static Database getInstance(DataSource d) throws IOException, SQLException, PropertyVetoException {
		if (datasource == null) {
			datasource = new Database(d);
			return datasource;
		} else {
			return datasource;
		}
	}

	public Connection getConnection() throws SQLException {
		return this.ds.getConnection();
	}
	
	

}
