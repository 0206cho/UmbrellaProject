package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import javax.swing.JOptionPane;

public class DB {

   public static Connection conn;
   public static Statement stmt;

   // 오라클 연결
   public static void init() {

      try {

         Class.forName("oracle.jdbc.driver.OracleDriver");

         conn = DriverManager.getConnection("", "", "");

         stmt = conn.createStatement();
         
      } catch (ClassNotFoundException e) {
         System.out.println("해당 드라이버가 없습니다.");
         e.printStackTrace();
      } catch (SQLException e) {
         System.out.println("접속 오류 / SQL 오류");
         e.printStackTrace();
      }
   }

   // 조회
   public static ResultSet getResultSet(String sql) {
      try {
         //Statement stmt = conn.createStatement();
         return stmt.executeQuery(sql);
   
      }catch(Exception e){
         System.out.println(e);   //오류 메시지
         
         return null;
      }
   }
   
   // 삽입, 수정, 삭제 
   public static void executeQuery(String sql) {
      try {
         stmt.executeUpdate(sql);
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }
   
   public static void executeQueryP(String sql) {
	      try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, "이미 존재하는 코드입니다.");
		}
	   }
   
}
