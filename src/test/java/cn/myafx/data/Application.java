package cn.myafx.data;

// import java.io.Console;
// import java.util.Date;
// import java.util.LinkedHashMap;
// import java.util.Map;

// public class Application {
//     public static void main(String[] args) throws Exception {
//         try(var db = new MysqlDatabase("jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8&characterSetResults=utf8", "admin", "123456")){
            
//             Map<String, Object> setParam = new LinkedHashMap<>();
//             setParam.put("name", "3333");
//             setParam.put("account", "33322");
//             Map<String, Object> whereParam = new LinkedHashMap<>();
//             whereParam.put("id", 2);
//             var m = db.queryOne("select * from tb1 where id = ${id}", tb1.class, whereParam);
//             // db.delete(tb1.class, whereParam);

//             System.out.print("end...............");
//         }
//     }
// }
