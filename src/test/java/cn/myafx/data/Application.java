package cn.myafx.data;

import java.util.HashMap;
import java.util.Map;

public class Application {
    public static void main(String[] args) throws Exception {
        try (var db = new MySqlDatabase(
                "jdbc:mysql://127.0.0.1:3306/test?characterEncoding=utf8&characterSetResults=utf8", "admin",
                "123456")) {

            Map<String, Object> setParam = new HashMap<String, Object>();
            setParam.put("name", "3333");
            setParam.put("account", "33322");

            Map<String, Object> whereParam = new HashMap<String, Object>();
            whereParam.put("id", 2);
            db.beginTransaction();
            db.update(tb1.class, setParam, whereParam);
            // db.update("tb1", setParam, whereParam);
            var m = db.queryOne("select * from tb1 where id = ${id}", tb1.class, whereParam);
            // var list = db.queryList("select * from tb1 where id < ${id}", tb1.class,
            // whereParam);
            // db.delete(tb1.class, whereParam);
            // db.delete("tb1", whereParam);
            db.commit();
            System.out.println(m.account);
            System.out.print("end...............");
        }
    }
}
