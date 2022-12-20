package cn.myafx.data;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.myafx.data.factory.DefaultObjectFactory;
import cn.myafx.data.factory.ObjectFactory;
import cn.myafx.data.type.TypeHandler;
import cn.myafx.data.type.TypeHandlerRegistry;

public abstract class Database implements IDatabase {

    private Connection connection = null;
    private Boolean is_tran = false;
    private Boolean is_close = true;

    private final static int NOT_MODEL = Modifier.ABSTRACT | Modifier.STATIC | Modifier.FINAL | Modifier.STRICT;
    private final static int CLASS_FIELD = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
    protected final static TypeHandlerRegistry typeHandlerRegistry;
    private final static ObjectFactory objectFactory;
    private final static List<Class<?>> baseTypeList;
    static {
        typeHandlerRegistry = new TypeHandlerRegistry();
        objectFactory = new DefaultObjectFactory();
        baseTypeList = new ArrayList<>(50);

        baseTypeList.add(boolean.class);
        baseTypeList.add(byte.class);
        baseTypeList.add(short.class);
        baseTypeList.add(int.class);
        baseTypeList.add(long.class);
        baseTypeList.add(float.class);
        baseTypeList.add(double.class);
        baseTypeList.add(char.class);

        baseTypeList.add(java.lang.Boolean.class);
        baseTypeList.add(java.lang.Byte.class);
        baseTypeList.add(java.lang.Short.class);
        baseTypeList.add(java.lang.Integer.class);
        baseTypeList.add(java.lang.Long.class);
        baseTypeList.add(java.lang.Float.class);
        baseTypeList.add(java.lang.Double.class);
        baseTypeList.add(java.lang.Character.class);
        baseTypeList.add(java.lang.String.class);

        baseTypeList.add(java.math.BigDecimal.class);
        baseTypeList.add(java.math.BigInteger.class);

        baseTypeList.add(java.util.Date.class);

        baseTypeList.add(java.time.ZonedDateTime.class);
        baseTypeList.add(java.time.Year.class);
        baseTypeList.add(java.time.YearMonth.class);
        baseTypeList.add(java.time.OffsetTime.class);

        baseTypeList.add(java.sql.Date.class);
        baseTypeList.add(java.sql.Timestamp.class);
        baseTypeList.add(java.sql.Time.class);
        baseTypeList.add(java.sql.Blob.class);
        baseTypeList.add(java.sql.Clob.class);
        baseTypeList.add(java.sql.NClob.class);
    }

    /**
     * is close
     * 
     * @return boolean
     */
    @Override
    public boolean isClose() {
        return this.is_close;
    }

    /**
     * is transaction
     * 
     * @return boolean
     */
    @Override
    public boolean isTransaction() {
        return this.is_tran;
    }

    /**
     * getConnection
     * 
     * @return Connection
     * @throws Exception
     */
    protected abstract Connection getConnection() throws Exception;

    /**
     * encodeColumn
     * 
     * @param column name
     * @return mysql: `column`, ms sqlserver: [column]
     */
    protected abstract String encodeColumn(String column);

    /**
     * open
     * 
     * @throws Exception
     */
    @Override
    public void open() throws Exception {
        if (this.is_close) {
            this.connection = this.getConnection();
            is_close = false;
        }
    }

    /**
     * begin transaction
     * 
     * @throws Exception
     */
    @Override
    public void beginTransaction() throws Exception {
        this.beginTransaction(IsolationLevel.None);
    }

    /**
     * begin transaction
     * 
     * @param level IsolationLevel
     * @throws Exception
     */
    @Override
    public void beginTransaction(IsolationLevel level) throws Exception {
        if (this.is_tran)
            throw new Exception("repeat beginTransaction!");
        this.open();
        if (this.connection == null)
            throw new Exception("not open!");
        this.connection.setAutoCommit(false);
        switch (level) {
            case ReadUncommitted:
                this.connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
                break;
            case None:
            case ReadCommitted:
                this.connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                break;
            case RepeatableRead:
                this.connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                break;
            case Serializable:
                this.connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                break;
            default:
                break;
        }
        this.is_tran = true;
    }

    /**
     * commit
     * 
     * @throws Exception
     */
    @Override
    public void commit() throws Exception {
        if (this.is_tran) {
            this.connection.commit();
            this.is_tran = false;
        }
    }

    /**
     * rollback
     * 
     * @throws Exception
     */
    @Override
    public void rollback() throws Exception {
        if (this.is_tran) {
            this.connection.rollback();
            this.is_tran = false;
        }
    }

    /**
     * close
     */
    @Override
    public void close() throws Exception {
        if (!is_close) {
            try {
                this.rollback();
            } catch (Exception ex) {
            }
            try {
                this.connection.close();
            } catch (Exception ex) {
            }
            this.connection = null;
            this.is_close = true;
        }
    }

    /**
     * get T Default
     * 
     * @param clazz T
     * @return default value
     */
    private Object getDefault(Class<?> clazz) {
        Object obj = null;

        if (clazz.isPrimitive()) {
            if (clazz == boolean.class)
                obj = false;
            else if (clazz == char.class)
                obj = '\0';
            else if (clazz == byte.class)
                obj = (byte) 0;
            else if (clazz == short.class)
                obj = (short) 0;
            else if (clazz == int.class)
                obj = 0;
            else if (clazz == long.class)
                obj = 0l;
            else if (clazz == float.class)
                obj = 0f;
            else if (clazz == double.class)
                obj = 0d;
        }

        return obj;
    }

    /**
     * checkModel
     * 
     * @param clazz model.class
     * @throws Exception
     */
    private void checkModel(Class<?> clazz) throws Exception {
        if (clazz == null)
            throw new Exception("T class is null!");

        if (baseTypeList.contains(clazz))
            return;

        if (clazz.isArray() || clazz.isEnum() || clazz.isInterface() || clazz.isAnonymousClass() || clazz.isAnnotation()
                || (clazz.getModifiers() & NOT_MODEL) > 0)
            throw new Exception("T(" + clazz.getSimpleName() + ") class is error!");
    }

    /**
     * getFieldMap
     * 
     * @param clazz model.class
     * @return Map&lt;String, Field&gt;
     * @throws Exception
     */
    private Map<String, Field> getFieldMap(Class<?> clazz) throws Exception {
        Map<String, Field> fieldMap = new LinkedHashMap<>();
        var t = clazz;
        while (!Object.class.equals(t)) {
            var arr = t.getDeclaredFields();
            for (Field f : arr) {
                var modifiers = f.getModifiers();
                if (modifiers == 0 || (modifiers & CLASS_FIELD) > 0) {
                    fieldMap.put(f.getName(), f);
                    if (!Modifier.isPublic(modifiers))
                        f.setAccessible(true);
                }
            }
            t = t.getSuperclass();
        }

        if (fieldMap.size() == 0)
            throw new Exception("clazz(" + clazz.getName() + ") is error!");

        return fieldMap;
    }

    /**
     * setValue
     * 
     * @param m         model
     * @param fieldMap  model field
     * @param resultSet ResultSet
     * @param metaData  ResultSetMetaData
     * @param handerMap get column value Map
     * @throws Exception
     */
    private void setValue(Object m, Map<String, Field> fieldMap, ResultSet resultSet, ResultSetMetaData metaData,
            Map<String, TypeHandler<?>> handerMap) throws Exception {
        var count = metaData.getColumnCount();
        for (var i = 0; i < count; i++) {
            var name = metaData.getColumnLabel(i + 1);
            if (fieldMap.containsKey(name)) {
                var field = fieldMap.get(name);
                Object value = null;
                TypeHandler<?> handler = null;
                if (handerMap.containsKey(name)) {
                    handler = handerMap.get(name);
                } else {
                    var ft = field.getType();
                    handler = typeHandlerRegistry.getTypeHandler(ft);
                    handerMap.put(name, handler);
                }
                if (handler != null)
                    value = handler.getResult(resultSet, i + 1);
                if (value != null)
                    field.set(m, value);
            }
        }
    }

    /**
     * toListModel
     * 
     * @param <T>       Model
     * @param resultSet ResultSet
     * @param clazz     Model.class
     * @return List
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private <T> List<T> toListModel(ResultSet resultSet, Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<T>();

        var isBaseType = baseTypeList.contains(clazz);
        Map<String, Field> fieldMap = null;
        Map<String, TypeHandler<?>> handerMap = null;
        ResultSetMetaData metaData = null;
        TypeHandler<?> handler = null;
        if (isBaseType) {
            handler = typeHandlerRegistry.getTypeHandler(clazz);
        } else {
            fieldMap = getFieldMap(clazz);
            handerMap = new HashMap<>();
            metaData = resultSet.getMetaData();
        }

        while (resultSet.next()) {
            if (isBaseType) {
                var m = (T) handler.getResult(resultSet, 1);
                list.add(m);
            } else {
                // var m = constructor.newInstance();
                var m = objectFactory.create(clazz);
                this.setValue(m, fieldMap, resultSet, metaData, handerMap);
                list.add(m);
            }
        }

        return list;
    }

    /**
     * getParamInfo
     * 
     * @param sql   sql text
     * @param param
     * @return SqlParamInfo
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private SqlParamInfo getParamInfo(String sql, Object[] param) throws Exception {
        if (sql == null || sql.isEmpty()) {
            throw new Exception("sql is null!");
        }

        SqlParamInfo result = new SqlParamInfo();
        result.sql = sql;
        if (sql.indexOf('?') > 0) {
            result.param = param;
        } else {
            String openToken = "${";
            String closeToken = "}";
            var start = sql.indexOf(openToken);
            if (start < 0) {
                openToken = "#{";
                start = sql.indexOf(openToken);
            }

            if (start > 0) {
                if (param == null || param.length != 1)
                    throw new Exception("parameter is error!");
                var o = param[0];
                Integer paramtype = 0;
                Map<String, Object> map = null;
                Map<String, Field> fieldMap = null;
                if (o instanceof Map<?, ?> omap) {
                    map = (Map<String, Object>) omap;
                    if (map != null && map.size() > 0) {
                        paramtype = 1;
                    }
                } else {
                    var t = o.getClass();
                    fieldMap = getFieldMap(t);
                    if (fieldMap.size() > 0)
                        paramtype = 2;
                }

                if (paramtype == 0)
                    throw new Exception("parameter type is error!");

                StringBuilder builder = new StringBuilder();
                Integer appendStart = 0;
                Integer offset = start + openToken.length();
                Integer end = sql.indexOf(closeToken, offset);
                List<Object> olist = new ArrayList<>();
                while (start > 0 && end > start) {
                    var name = sql.substring(offset, end);
                    builder.append(sql.substring(appendStart, start));
                    builder.append("?");
                    if (paramtype == 1) {
                        if (map.containsKey(name)) {
                            olist.add(map.get(name));
                        } else {
                            throw new Exception("not find " + openToken + name + closeToken + " parameter!");
                        }
                    } else {
                        Object v = null;
                        if (fieldMap.containsKey(name)) {
                            var field = fieldMap.get(name);
                            v = field.get(o);
                        } else {
                            throw new Exception("not find " + openToken + name + closeToken + " parameter!");
                        }
                        olist.add(v);
                    }
                    appendStart = end + closeToken.length();
                    start = sql.indexOf(openToken, appendStart);
                    if (start < 0)
                        break;
                    offset = start + openToken.length();
                    end = sql.indexOf(closeToken, offset);
                }
                if (appendStart < sql.length()) {
                    builder.append(sql.substring(appendStart));
                }
                result.sql = builder.toString();
                result.param = olist.toArray();

            }
        }

        return result;
    }

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
    @Override
    public int execute(String sql, Object... param) throws Exception {
        int result = 0;
        this.open();
        if (param == null || param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                result = statement.executeUpdate(sql);
            }
        } else {
            var sqlparam = this.getParamInfo(sql, param);
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                result = statement.executeUpdate();
            }
        }

        return result;
    }

    /**
     * toModel
     * 
     * @param <T>       Model
     * @param resultSet ResultSet
     * @param clazz     Model.class
     * @return Model
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private <T> T toModel(ResultSet resultSet, Class<T> clazz) throws Exception {
        T m;
        var isBaseType = baseTypeList.contains(clazz);
        Map<String, Field> fieldMap = null;
        Map<String, TypeHandler<?>> handerMap = null;
        ResultSetMetaData metaData = null;
        TypeHandler<?> handler = null;
        if (isBaseType) {
            handler = typeHandlerRegistry.getTypeHandler(clazz);
        } else {
            fieldMap = getFieldMap(clazz);
            handerMap = new HashMap<>();
            metaData = resultSet.getMetaData();
        }

        if (resultSet.next()) {
            if (isBaseType) {
                m = (T) handler.getResult(resultSet, 1);
            } else {
                m = objectFactory.create(clazz);
                this.setValue(m, fieldMap, resultSet, metaData, handerMap);
            }
        } else {
            m = (T) getDefault(clazz);
        }

        return m;
    }

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
    @Override
    public <T> T queryOne(String sql, Class<T> clazz, Object... param) throws Exception {
        this.checkModel(clazz);
        this.open();
        if (param == null || param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                try (var resultSet = statement.executeQuery(sql)) {
                    var m = this.toModel(resultSet, clazz);
                    return m;
                }
            }
        } else {
            var sqlparam = this.getParamInfo(sql, param);
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                try (var resultSet = statement.executeQuery()) {
                    var m = this.toModel(resultSet, clazz);
                    return m;
                }
            }
        }
    }

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
    @Override
    public <T> List<T> queryList(String sql, Class<T> clazz, Object... param) throws Exception {
        List<T> list = null;
        this.checkModel(clazz);
        this.open();
        if (param == null || param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                try (var resultSet = statement.executeQuery(sql)) {
                    list = toListModel(resultSet, clazz);
                }
            }
        } else {
            var sqlparam = this.getParamInfo(sql, param);
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                try (var resultSet = statement.executeQuery()) {
                    list = toListModel(resultSet, clazz);
                }
            }
        }

        return list;
    }

    /**
     * toMap
     * 
     * @param resultSet ResultSet
     * @return Map
     * @throws Exception
     */
    private Map<String, Object> toMap(ResultSet resultSet) throws Exception {
        Map<String, Object> map = null;
        if (resultSet.next()) {
            var metaData = resultSet.getMetaData();
            var count = metaData.getColumnCount();
            map = new HashMap<String, Object>(count);
            for (var i = 0; i < count; i++) {
                var name = metaData.getColumnLabel(i + 1);
                var value = resultSet.getObject(i + 1);
                map.put(name, value);
            }
        }

        return map;
    }

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
    @Override
    public Map<String, Object> queryOneMap(String sql, Object... param) throws Exception {
        Map<String, Object> map = null;
        this.open();
        if (param == null || param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                try (var resultSet = statement.executeQuery(sql)) {
                    map = this.toMap(resultSet);
                }
            }
        } else {
            var sqlparam = this.getParamInfo(sql, param);
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                try (var resultSet = statement.executeQuery()) {
                    map = this.toMap(resultSet);
                }
            }
        }

        return map;
    }

    /**
     * toListMap
     * 
     * @param resultSet ResultSet
     * @return List Map
     * @throws Exception
     */
    private List<Map<String, Object>> toListMap(ResultSet resultSet) throws Exception {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        var metaData = resultSet.getMetaData();
        var count = metaData.getColumnCount();
        while (resultSet.next()) {
            Map<String, Object> map = new HashMap<>(count);
            for (var i = 0; i < count; i++) {
                var name = metaData.getColumnLabel(i + 1);
                var value = resultSet.getObject(i + 1);
                map.put(name, value);
            }
            list.add(map);
        }

        return list;
    }

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
    @Override
    public List<Map<String, Object>> queryListMap(String sql, Object... param) throws Exception {
        List<Map<String, Object>> list = null;
        this.open();
        if (param == null || param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                try (var resultSet = statement.executeQuery(sql)) {
                    list = this.toListMap(resultSet);
                }
            }
        } else {
            var sqlparam = this.getParamInfo(sql, param);
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                try (var resultSet = statement.executeQuery()) {
                    list = this.toListMap(resultSet);
                }
            }
        }

        return list;
    }

    /**
     * getSelectSql
     * 
     * @param clazz model.class
     * @param param where param
     * @return SqlParamInfo
     * @throws Exception
     */
    private SqlParamInfo getSelectSql(Class<?> clazz, Map<String, Object> param) throws Exception {
        var fieldMap = this.getFieldMap(clazz);
        SqlParamInfo result = new SqlParamInfo();
        result.sql = "SELECT ";
        fieldMap.forEach((k, v) -> {
            result.sql = result.sql + this.encodeColumn(k) + ", ";
        });
        result.sql = result.sql.substring(0, result.sql.length() - 2);
        result.sql = result.sql + " FROM " + this.encodeColumn(clazz.getSimpleName());
        if (param != null && param.size() > 0) {
            List<Object> arr = new ArrayList<>(param.size());
            result.sql = result.sql + " WHERE 1=1";
            param.forEach((k, v) -> {
                result.sql = result.sql + " AND " + this.encodeColumn(k) + " = ?";
                arr.add(v);
            });
            result.param = arr.toArray();
        }

        return result;
    }

    /**
     * get first model
     * 
     * @param <TModel> Model
     * @param clazz    TModel.class
     * @param param    where param
     * @return first Model
     * @throws Exception
     */
    @Override
    public <TModel> TModel get(Class<TModel> clazz, Map<String, Object> param) throws Exception {
        this.checkModel(clazz);
        this.open();
        var sqlparam = this.getSelectSql(clazz, param);
        if (sqlparam.param == null || sqlparam.param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                try (var resultSet = statement.executeQuery(sqlparam.sql)) {
                    var m = this.toModel(resultSet, clazz);
                    return m;
                }
            }
        } else {
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                try (var resultSet = statement.executeQuery()) {
                    var m = this.toModel(resultSet, clazz);
                    return m;
                }
            }
        }
    }

    /**
     * get List model
     * 
     * @param <TModel> Model
     * @param clazz    TModel.class
     * @param param    where param
     * @return List Model
     * @throws Exception
     */
    @Override
    public <TModel> List<TModel> getList(Class<TModel> clazz, Map<String, Object> param)
            throws Exception {
        List<TModel> list = null;
        this.checkModel(clazz);
        this.open();
        var sqlparam = this.getSelectSql(clazz, param);
        if (sqlparam.param == null || sqlparam.param.length == 0) {
            try (var statement = this.connection.createStatement()) {
                try (var resultSet = statement.executeQuery(sqlparam.sql)) {
                    list = this.toListModel(resultSet, clazz);
                }
            }
        } else {
            try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
                for (var i = 0; i < sqlparam.param.length; i++) {
                    var o = sqlparam.param[i];
                    if (o != null) {
                        var t = o.getClass();
                        var handler = typeHandlerRegistry.getTypeHandler(t);
                        handler.setParameter(statement, i + 1, o);
                    } else {
                        statement.setNull(i + 1, Types.VARCHAR);
                    }
                }
                try (var resultSet = statement.executeQuery()) {
                    list = toListModel(resultSet, clazz);
                }
            }
        }

        return list;
    }

    /**
     * getInsertSql
     * 
     * @param table table name
     * @param param insert param
     * @return SqlParamInfo
     * @throws Exception
     */
    private SqlParamInfo getInsertSql(String table, Map<String, Object> param) throws Exception {
        if (table == null || table.isEmpty())
            throw new Exception("table is null!");
        SqlParamInfo m = new SqlParamInfo();
        m.sql = "INSERT INTO " + this.encodeColumn(table) + "(";
        List<Object> plist = new ArrayList<>(param.size());
        String vsql = "VALUES(";
        for (Map.Entry<String, Object> kv : param.entrySet()) {
            m.sql = m.sql + this.encodeColumn(kv.getKey()) + ", ";
            plist.add(kv.getValue());
            vsql = vsql + "?, ";
        }
        m.sql = m.sql.substring(0, m.sql.length() - 2) + ") " + vsql.substring(0, vsql.length() - 2) + ");";
        m.param = plist.toArray();

        return m;
    }

    /**
     * add row
     * 
     * @param table table name
     * @param param insert param
     * @return int
     * @throws Exception
     */
    @Override
    public int add(String table, Map<String, Object> param) throws Exception {
        if (param == null || param.size() == 0)
            throw new Exception("param is null!");
        var sqlparam = this.getInsertSql(table, param);
        int result = 0;
        this.open();
        try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
            for (var i = 0; i < sqlparam.param.length; i++) {
                var o = sqlparam.param[i];
                if (o != null) {
                    var t = o.getClass();
                    var handler = typeHandlerRegistry.getTypeHandler(t);
                    handler.setParameter(statement, i + 1, o);
                } else {
                    statement.setNull(i + 1, Types.VARCHAR);
                }
            }
            result = statement.executeUpdate();
        }

        return result;
    }

    /**
     * add model
     * 
     * @param <TModel> Model
     * @param m        model
     * @param ignore   ignore model property name
     * @return int
     * @throws Exception
     */
    @Override
    public <TModel> int add(TModel m, String[] ignore) throws Exception {
        if (m == null)
            throw new Exception("m is null!");
        var clazz = m.getClass();
        var fieldMap = this.getFieldMap(clazz);
        if (ignore != null && ignore.length > 0) {
            for (var i = 0; i < ignore.length; i++) {
                fieldMap.remove(ignore[i]);
            }
        }
        Map<String, Object> param = new LinkedHashMap<>(fieldMap.size());
        for (Map.Entry<String, Field> kv : fieldMap.entrySet()) {
            param.put(kv.getKey(), kv.getValue().get(m));
        }

        return this.add(clazz.getSimpleName(), param);
    }

    /**
     * getUpdateSql
     * 
     * @param table      table name
     * @param setParam   set param
     * @param whereParam where param
     * @return SqlParamInfo
     * @throws Exception
     */
    private SqlParamInfo getUpdateSql(String table, Map<String, Object> setParam, Map<String, Object> whereParam)
            throws Exception {
        if (table == null || table.isEmpty())
            throw new Exception("table is null!");
        SqlParamInfo m = new SqlParamInfo();
        m.sql = "UPDATE " + this.encodeColumn(table) + " SET ";
        List<Object> plist = new ArrayList<>(setParam.size() + (whereParam != null ? whereParam.size() : 0));
        for (Map.Entry<String, Object> kv : setParam.entrySet()) {
            m.sql = m.sql + this.encodeColumn(kv.getKey()) + " = ?, ";
            plist.add(kv.getValue());
        }
        m.sql = m.sql.substring(0, m.sql.length() - 2) + " WHERE 1=1";
        if (whereParam != null && whereParam.size() > 0) {
            for (Map.Entry<String, Object> kv : whereParam.entrySet()) {
                m.sql = m.sql + " AND " + this.encodeColumn(kv.getKey()) + " = ?";
                plist.add(kv.getValue());
            }
        }
        m.param = plist.toArray();

        return m;
    }

    /**
     * update
     * 
     * @param table      table name
     * @param setParam   update param
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    @Override
    public int update(String table, Map<String, Object> setParam, Map<String, Object> whereParam) throws Exception {
        if (setParam == null || setParam.size() == 0)
            throw new Exception("param is null!");
        var sqlparam = this.getUpdateSql(table, setParam, whereParam);
        int result = 0;
        this.open();
        try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
            for (var i = 0; i < sqlparam.param.length; i++) {
                var o = sqlparam.param[i];
                if (o != null) {
                    var t = o.getClass();
                    var handler = typeHandlerRegistry.getTypeHandler(t);
                    handler.setParameter(statement, i + 1, o);
                } else {
                    statement.setNull(i + 1, Types.VARCHAR);
                }
            }
            result = statement.executeUpdate();
        }

        return result;
    }

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
    @Override
    public <TModel> int update(Class<TModel> clazz, Map<String, Object> setParam,
            Map<String, Object> whereParam) throws Exception {
        if (clazz == null)
            throw new Exception("T class is null!");
        var table = clazz.getSimpleName();

        return this.update(table, setParam, whereParam);
    }

    /**
     * getDeleteSql
     * 
     * @param table      table name
     * @param whereParam where param
     * @return SqlParamInfo
     * @throws Exception
     */
    private SqlParamInfo getDeleteSql(String table, Map<String, Object> whereParam) throws Exception {
        if (table == null || table.isEmpty())
            throw new Exception("table is null!");
        SqlParamInfo m = new SqlParamInfo();
        m.sql = "DELETE FROM " + this.encodeColumn(table) + " WHERE 1=1";
        if (whereParam != null && whereParam.size() > 0) {
            List<Object> plist = new ArrayList<>(whereParam.size());
            if (whereParam != null && whereParam.size() > 0) {
                for (Map.Entry<String, Object> kv : whereParam.entrySet()) {
                    m.sql = m.sql + " AND " + this.encodeColumn(kv.getKey()) + " = ?";
                    plist.add(kv.getValue());
                }
            }
            m.param = plist.toArray();
        }

        return m;
    }

    /**
     * delete
     * 
     * @param table      table name
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    @Override
    public int delete(String table, Map<String, Object> whereParam) throws Exception {
        if (table == null || table.isEmpty())
            throw new Exception("table is null!");
        var sqlparam = this.getDeleteSql(table, whereParam);
        int result = 0;
        this.open();
        try (var statement = this.connection.prepareStatement(sqlparam.sql)) {
            for (var i = 0; i < sqlparam.param.length; i++) {
                var o = sqlparam.param[i];
                if (o != null) {
                    var t = o.getClass();
                    var handler = typeHandlerRegistry.getTypeHandler(t);
                    handler.setParameter(statement, i + 1, o);
                } else {
                    statement.setNull(i + 1, Types.VARCHAR);
                }
            }
            result = statement.executeUpdate();
        }

        return result;
    }

    /**
     * delete
     * 
     * @param <TModel>   Model
     * @param clazz      Model.class
     * @param whereParam where param
     * @return int
     * @throws Exception
     */
    @Override
    public <TModel> int delete(Class<TModel> clazz, Map<String, Object> whereParam) throws Exception {
        if (clazz == null)
            throw new Exception("T class is null!");
        var table = clazz.getSimpleName();

        return this.delete(table, whereParam);
    }

}
