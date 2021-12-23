package cn.myafx.data;

import java.util.List;
import java.util.Map;

/**
 * database
 */
public interface Database extends AutoCloseable {

    Boolean isClose();

    Boolean isTransaction();

    void open() throws Exception;

    void beginTransaction() throws Exception;
    void beginTransaction(IsolationLevel level) throws Exception;

    void commit() throws Exception;

    void rollback() throws Exception;
    
    int execute(String sql, Object... param) throws Exception;

    <T> T queryOne(String sql, Class<T> clazz, Object... param) throws Exception;
    <T> List<T> queryList(String sql, Class<T> clazz, Object... param) throws Exception;

    Map<String, Object> queryOneMap(String sql, Object... param) throws Exception;
    List<Map<String, Object>> queryListMap(String sql, Object... param) throws Exception;

    <TModel extends Model> TModel get(Class<TModel> clazz, Map<String, Object> param) throws Exception;
    <TModel extends Model> List<TModel> getList(Class<TModel> clazz, Map<String, Object> param) throws Exception;

    int add(String table, Map<String, Object> param)throws Exception;
    <TModel extends Model> int add(TModel m, String[] ignore)throws Exception;
    
    int update(String table, Map<String, Object> setParam, Map<String, Object> whereParam)throws Exception;
    <TModel extends Model> int update(Class<TModel> clazz, Map<String, Object> setParam, Map<String, Object> whereParam)throws Exception;
    
    int delete(String table, Map<String, Object> whereParam)throws Exception;
    <TModel extends Model> int delete(Class<TModel> clazz, Map<String, Object> whereParam)throws Exception;
    
}
