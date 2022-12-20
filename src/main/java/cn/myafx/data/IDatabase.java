package cn.myafx.data;

import java.util.List;
import java.util.Map;

/**
 * database
 */
public interface IDatabase extends AutoCloseable {

    /**
     * is close
     * 
     * @return boolean
     */
    boolean isClose();

    /**
     * is transaction
     * 
     * @return boolean
     */
    boolean isTransaction();

    /**
     * open
     * 
     * @throws Exception
     */
    void open() throws Exception;

    /**
     * begin transaction
     * 
     * @throws Exception
     */
    void beginTransaction() throws Exception;

    /**
     * begin transaction
     * 
     * @param level IsolationLevel
     * @throws Exception
     */
    void beginTransaction(IsolationLevel level) throws Exception;

    /**
     * commit
     * 
     * @throws Exception
     */
    void commit() throws Exception;

    /**
     * rollback
     * 
     * @throws Exception
     */
    void rollback() throws Exception;

    /**
     * execute sql
     * 
     * @param sql   update/delete/insert; param: ? or ${name} or #{name}
     * @param param update tb set name = ? where id = ? param is Object[];
     *              update tb set name = ${mame} where id = ${id} param is model or
     *              Map&lt;String, Object&gt;
     * @return int
     * @throws Exception
     */
    int execute(String sql, Object... param) throws Exception;

    /**
     * query first model
     * 
     * @param <T>   model
     * @param sql   select sql; param: ? or ${name} or #{name}
     * @param clazz T.class
     * @param param select id, name from tb where id = ? param is Object[];
     *              select id, name from tb where id = ${id} param is model or
     *              Map&lt;String, Object&gt;
     * @return first model
     * @throws Exception
     */
    <T> T queryOne(String sql, Class<T> clazz, Object... param) throws Exception;

    /**
     * query List
     * 
     * @param <T>   model
     * @param sql   select sql; param: ? or ${name} or #{name}
     * @param clazz T.class
     * @param param select id, name from tb where id = ? param is Object[];
     *              select id, name from tb where id = ${id} param is model or
     *              Map&lt;String, Object&gt;
     * @return List
     * @throws Exception
     */
    <T> List<T> queryList(String sql, Class<T> clazz, Object... param) throws Exception;

    /**
     * query first Map&lt;String, Object&gt;
     * 
     * @param sql   select sql; param: ? or ${name} or #{name}
     * @param param select id, name from tb where id = ? param is Object[];
     *              select id, name from tb where id = ${id} param is model or
     *              Map&lt;String, Object&gt;
     * @return first Map&lt;String, Object&gt;
     * @throws Exception
     */
    Map<String, Object> queryOneMap(String sql, Object... param) throws Exception;

    /**
     * query List Map&lt;String, Object&gt;
     * 
     * @param sql   select sql; param: ? or ${name} or #{name}
     * @param param select id, name from tb where id = ? param is Object[];
     *              select id, name from tb where id = ${id} param is model or
     *              Map&lt;String, Object&gt;
     * @return List Map String, Object
     * @throws Exception
     */
    List<Map<String, Object>> queryListMap(String sql, Object... param) throws Exception;

    /**
     * get first model
     * 
     * @param <TModel> TModel
     * @param clazz    TModel.class
     * @param param    where param
     * @return first Model
     * @throws Exception
     */
    <TModel> TModel get(Class<TModel> clazz, Map<String, Object> param) throws Exception;

    /**
     * get List model
     * 
     * @param <TModel> Model
     * @param clazz    TModel.class
     * @param param    where param
     * @return List Model
     * @throws Exception
     */
    <TModel> List<TModel> getList(Class<TModel> clazz, Map<String, Object> param) throws Exception;

    /**
     * add row
     * 
     * @param table table name
     * @param param insert param
     * @return int
     * @throws Exception
     */
    int add(String table, Map<String, Object> param) throws Exception;

    /**
     * add model
     * 
     * @param <TModel> Model
     * @param m        model
     * @param ignore   ignore model property name
     * @return int
     * @throws Exception
     */
    <TModel> int add(TModel m, String[] ignore) throws Exception;

    /**
     * update
     * 
     * @param table      table name
     * @param setParam   update param
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    int update(String table, Map<String, Object> setParam, Map<String, Object> whereParam) throws Exception;

    /**
     * update
     * 
     * @param <TModel>   Model
     * @param clazz      Model.class
     * @param setParam   update param
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    <TModel> int update(Class<TModel> clazz, Map<String, Object> setParam, Map<String, Object> whereParam)
            throws Exception;

    /**
     * delete
     * 
     * @param table      table name
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    int delete(String table, Map<String, Object> whereParam) throws Exception;

    /**
     * delete
     * 
     * @param <TModel>   Model
     * @param clazz      Model.class
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    <TModel> int delete(Class<TModel> clazz, Map<String, Object> whereParam) throws Exception;

}
