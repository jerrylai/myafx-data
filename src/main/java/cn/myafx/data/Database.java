package cn.myafx.data;

import java.util.List;
import java.util.Map;

/**
 * database
 */
public interface Database extends AutoCloseable {

    /**
     * isClose
     * @return Boolean
     */
    Boolean isClose();
    /**
     * isTransaction
     * @return Boolean
     */
    Boolean isTransaction();
    /**
     * open
     * @throws Exception Exception
     */
    void open() throws Exception;
    /**
     * beginTransaction
     * @throws Exception Exception
     */
    void beginTransaction() throws Exception;
    /**
     * beginTransaction
     * @param level IsolationLevel
     * @throws Exception Exception
     */
    void beginTransaction(IsolationLevel level) throws Exception;
    /**
     * commit
     * @throws Exception Exception
     */
    void commit() throws Exception;
    /**
     * rollback
     * @throws Exception Exception
     */
    void rollback() throws Exception;
    /**
     * execute update/delete/insert
     * @param sql sql
     * @param param model or object[]
     * @return int
     * @throws Exception Exception
     */
    int execute(String sql, Object... param) throws Exception;
    /**
     * queryOne
     * @param <T> model type
     * @param sql sql
     * @param clazz model class
     * @param param model or object[]
     * @return moodel
     * @throws Exception Exception
     */
    <T> T queryOne(String sql, Class<T> clazz, Object... param) throws Exception;
    /**
     * queryList
     * @param <T> model type
     * @param sql sql
     * @param clazz model class
     * @param param model or object[]
     * @return moodel list
     * @throws Exception Exception
     */
    <T> List<T> queryList(String sql, Class<T> clazz, Object... param) throws Exception;
    /**
     * queryOneMap
     * @param sql sql
     * @param param model or object[]
     * @return Map String, Object
     * @throws Exception Exception
     */
    Map<String, Object> queryOneMap(String sql, Object... param) throws Exception;
    /**
     * queryListMap
     * @param sql sql
     * @param param model or object[]
     * @return List Map String, Object
     * @throws Exception Exception
     */
    List<Map<String, Object>> queryListMap(String sql, Object... param) throws Exception;
    /**
     * get
     * @param <TModel> model type
     * @param clazz model type
     * @param param  Map String, Object
     * @return  Model
     * @throws Exception Exception
     */
    <TModel extends Model> TModel get(Class<TModel> clazz, Map<String, Object> param) throws Exception;
    /**
     * getList
     * @param <TModel> model type
     * @param clazz model type
     * @param param Map String, Object
     * @return  List TModel
     * @throws Exception Exception
     */
    <TModel extends Model> List<TModel> getList(Class<TModel> clazz, Map<String, Object> param) throws Exception;
    /**
     * add
     * @param table table
     * @param param Map String, Object 
     * @return int
     * @throws Exception Exception
     */
    int add(String table, Map<String, Object> param)throws Exception;
    /**
     * add model 
     * @param <TModel> TModel
     * @param m TModel
     * @param ignore ignore col
     * @return int
     * @throws Exception Exception
     */
    <TModel extends Model> int add(TModel m, String[] ignore)throws Exception;
    /**
     * update
     * @param table table
     * @param setParam update param
     * @param whereParam where param
     * @return int
     * @throws Exception Exception
     */
    int update(String table, Map<String, Object> setParam, Map<String, Object> whereParam)throws Exception;
    /**
     * update
     * @param <TModel>  TModel
     * @param clazz TModel class
     * @param setParam update param
     * @param whereParam where param
     * @return int
     * @throws Exception Exception
     */
    <TModel extends Model> int update(Class<TModel> clazz, Map<String, Object> setParam, Map<String, Object> whereParam)throws Exception;
    /**
     * delete
     * @param table table
     * @param whereParam where param
     * @return int
     * @throws Exception  Exception
     */
    int delete(String table, Map<String, Object> whereParam)throws Exception;
    /**
     * delete
     * @param <TModel> TModel
     * @param clazz TModel class
     * @param whereParam where param
     * @return int
     * @throws Exception Exception
     */
    <TModel extends Model> int delete(Class<TModel> clazz, Map<String, Object> whereParam)throws Exception;
    
}
