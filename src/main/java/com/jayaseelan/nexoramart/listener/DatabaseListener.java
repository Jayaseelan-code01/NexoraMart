package com.jayaseelan.nexoramart.listener;
import com.zaxxer.hikari.HikariConfig; import com.zaxxer.hikari.HikariDataSource;
import javax.servlet.ServletContextEvent; import javax.servlet.ServletContextListener; import java.io.*; import java.nio.charset.StandardCharsets; import java.sql.*;
public class DatabaseListener implements ServletContextListener {
 private HikariDataSource ds;
 public void contextInitialized(ServletContextEvent e){try{Class.forName("org.h2.Driver"); HikariConfig c=new HikariConfig(); c.setDriverClassName("org.h2.Driver"); c.setJdbcUrl("jdbc:h2:file:"+System.getProperty("catalina.base")+"/data/nexoramart"); c.setUsername("sa"); c.setPassword(""); c.setMaximumPoolSize(10); c.setPoolName("NexoraPool"); ds=new HikariDataSource(c); run("/db/schema.sql"); run("/db/seed.sql"); e.getServletContext().setAttribute("dataSource",ds);}catch(Exception ex){throw new IllegalStateException("Database initialization failed: "+ex.getMessage(),ex);}}
 private void run(String resource)throws Exception{InputStream in=getClass().getResourceAsStream(resource); if(in==null)throw new IllegalStateException("Missing "+resource); StringBuilder s=new StringBuilder(); try(BufferedReader r=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8))){String line; while((line=r.readLine())!=null){if(!line.trim().isEmpty()&&!line.trim().startsWith("--"))s.append(line).append('\n');}} try(Connection c=ds.getConnection()){for(String q:s.toString().split(";")){if(q.trim().isEmpty())continue;try(PreparedStatement p=c.prepareStatement(q.trim())){p.execute();}}}}
 public void contextDestroyed(ServletContextEvent e){if(ds!=null&&!ds.isClosed())ds.close();}
}
