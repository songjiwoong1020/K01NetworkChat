package test;
/**
jdbc연동용
 */
public interface IConnect {
	
	String ORACLE_DRIVER = "oracle.jdbc.OracleDriver";
	String ORACLE_URL = "jdbc:oracle:thin://@localhost:1521:orcl";
	
	void connect(String user, String pass);//DB연결
	void close();//자원반납

}
