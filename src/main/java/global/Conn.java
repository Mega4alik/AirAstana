package global;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.dbcp.BasicDataSource;


public class Conn {
    //private static DataSource ngds = null;
    private static BasicDataSource ngds = null;
    static Global g = new Global();


    public static Connection ConnectMain(){
            String url = "jdbc:mysql://localhost:3306/"+g.mysqlDB+"?characterEncoding=utf8&autoReconnect=true",username = g.mysqlUser,password = g.mysqlPassword;
            //String url = "jdbc:mysql://localhost:3306/"+g.mysqlDB+"?characterEncoding=utf8&autoReconnect=true&useSSL=false",username = g.mysqlUser,password = g.mysqlPassword;
            try {
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                Connection connection = DriverManager.getConnection(url, username, password);            
                return connection;
            } catch (Exception e) {
                e.printStackTrace();
            }        
            return null;
    } 
    

    /*
    public static Connection ConnectMain() throws SQLException, NamingException {
	if (ngds == null) { 
            ngds = new BasicDataSource();                
            ngds.setDriverClassName("com.mysql.jdbc.Driver");
            ngds.setMaxActive(100);
            ngds.setMaxWait(10000);//100
            ngds.setMaxIdle(30);
            ngds.setUsername(g.mysqlUser);
            ngds.setPassword(g.mysqlPassword);
            ngds.setUrl("jdbc:mysql://localhost:3306/"+g.mysqlDB+"?characterEncoding=utf8&autoReconnect=true&useSSL=false");

            //Context initialContext = new InitialContext();                
            //String DATASOURCE_CONTEXT = "java:comp/env/jdbc/kkbDS";
            //ngds = (DataSource) initialContext.lookup(DATASOURCE_CONTEXT);                
        }
        return ngds.getConnection();	             
    } 
    */
    
}


