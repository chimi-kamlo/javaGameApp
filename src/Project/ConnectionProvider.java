/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Project;
import java.sql.* ;


public class ConnectionProvider {
    
    public static Connection getCon(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver") ;
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ict308","root","root");
            return con ;
        }catch(ClassNotFoundException | SQLException  e){
        
        return null ;
        
        }
        
    }
    
    
    
     public static void main(String[] args) {
        Connection connection = ConnectionProvider.getCon();

        if (connection != null) {
            System.out.println("Connection established successfully!");
        } else {
            System.out.println("Failed to establish connection.");
        }
    }
}
