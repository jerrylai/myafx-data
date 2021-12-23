package afx.data;

// import java.sql.Connection;
// import java.sql.DriverManager;

// import com.afx.data.DatabaseImpl;

// public class MysqlDatabase extends DatabaseImpl {

//     private String url;
//     private String user;
//     private String password;

//     public MysqlDatabase(String url, String user, String password){
//         this.url = url;
//         this.user = user;
//         this.password = password;
//     }

//     @Override
//     protected String encodeColumn(String column) {
        
//         return String.format("`%s`", column);
//     }

//     @Override
//     protected Connection getConnection() throws Exception {
//         Class.forName("com.mysql.cj.jdbc.Driver");
       
//         return DriverManager.getConnection(this.url, this.user, this.password);
//     }
    
// }
