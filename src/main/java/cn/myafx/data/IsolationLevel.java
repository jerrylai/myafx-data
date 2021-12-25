package cn.myafx.data;

/**
 * 事务隔离级别
 */
public enum IsolationLevel {
    /**
     * 默认
     */
    None,
    /** 
     * ReadUncommitted
    */
    ReadUncommitted,
    /** 
     * ReadCommitted
    */
    ReadCommitted,
    /** 
     * RepeatableRead
    */
    RepeatableRead,
    /** 
     * Serializable
    */
    Serializable,
}
