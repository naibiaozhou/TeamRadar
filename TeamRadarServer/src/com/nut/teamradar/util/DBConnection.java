package com.nut.teamradar.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.nut.teamradar.UserInformation;
import com.nut.teamradar.base.Constant;



public class DBConnection {
	//private Connection conn = null;
	private ResultSet rs = null;
	//private Statement stmt = null;
	private DataSource ds = null;

	private BasicDataSource dataSource =null;
	//private DataSourceConnectionFactory  connectionFactory = null;
	private Properties Propertie = null;
	private static String URL = "jdbc:mysql://localhost:3306/teamradar";
	private static String Username = "xxxxxxx";
	private static String Password = "xxxxxxx";
	private List<Connection> ConnectionList = null;
	
	private Boolean ConnectionOverflow = false;
	private int Connections = 0;
	private int UseIndex = 0;
	private Thread mThread;
	private Integer ConnectionTime=0;
	private int updateCount=0;

	private String DBDriver = "com.mysql.jdbc.Driver";
	
	private static DBConnection _instantce = null;
	private DBConnection(){
		try {
			ConnectionList = new LinkedList<Connection>();
			dataSource=new BasicDataSource();  

			dataSource.setUrl(URL);  
			dataSource.setUsername(Username);  
			dataSource.setPassword(Password);  
			dataSource.setDriverClassName(DBDriver);  
			dataSource.setMaxActive(8000);
			dataSource.setInitialSize(8000);  
			dataSource.setTestWhileIdle(true);
			dataSource.setMinEvictableIdleTimeMillis(10000);
			dataSource.setTimeBetweenEvictionRunsMillis(8000);
			dataSource.setMaxWait(10000);
			dataSource.setMaxIdle(30);
			//connectionFactory=new DataSourceConnectionFactory(dataSource);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		mThread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(true)
				{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized(ConnectionTime)
					{
						ConnectionTime++;
					}
				}
			}
		});
		mThread.start();
	}
	public static synchronized DBConnection getInstance()
	{
		if(_instantce == null)
		{
			if(_instantce == null)
			{
				_instantce = new DBConnection();
			}
		}
		return _instantce;
	}

	private synchronized Connection getConnection()
	{
		Connection conn = null;
		try {
			if(ConnectionOverflow)
			{
				conn = ConnectionList.get(UseIndex%Connections);
				synchronized(ConnectionTime)
				{
					if(ConnectionTime > 28800)
					{
						conn.close();
						conn=null;
						conn=(Connection)dataSource.getConnection();
						ConnectionList.set(UseIndex%Connections, conn);
						System.out.println("Update connection "+String.valueOf(UseIndex%Connections));
						updateCount++;
						if(updateCount == Connections)
						{
							ConnectionTime = 0;
							updateCount = 0;
						}
					}
				}
				UseIndex++;
			}
			else
			{
				conn =(Connection)dataSource.getConnection();
				ConnectionList.add(conn);
				Connections++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("connection numbers :"+String.valueOf(Connections));
			ConnectionOverflow = true;
			
			try {
				ConnectionList.get(0).close();
				ConnectionList.remove(0);
				Connections = ConnectionList.size();
				conn = ConnectionList.get(UseIndex%Connections);
				UseIndex++;	
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return conn;
	}
	private synchronized void ReloadConnections()
	{
		Connection conn = null;
		for(int i=0;i<Connections;i++ )
		{
			try {
				conn.close();
				conn =(Connection)dataSource.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public ResultSet doQuery(String SQL)
	{
		try {
			Connection conn = getConnection();
			Statement stmt=conn.createStatement();
			rs = stmt.executeQuery(SQL);
		}
		catch (CommunicationsException exp)
		{
			ReloadConnections();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	public boolean doUpdate(String SQL)
	{
		boolean ret = false;
		try {
			Connection conn = getConnection();
			Statement stmt=conn.createStatement();
			ret = (stmt.executeUpdate(SQL) > -1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public boolean doInsert(String SQL)
	{
		boolean ret = false;
		try {
			Connection conn = getConnection();
			Statement stmt=conn.createStatement();
			ret = (stmt.executeUpdate(SQL) > -1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public boolean doDelete(String SQL)
	{
		boolean ret = false;
		try {
			Connection conn = getConnection();
			Statement stmt=conn.createStatement();
			ret = stmt.execute(SQL) ;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public  void Close()
	{
		/*
		try {
			conn.close();
			_instantce  = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
	}
	
}
