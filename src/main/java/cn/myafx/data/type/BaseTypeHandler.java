/*
 *    Copyright 2009-2021 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package cn.myafx.data.type;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The base {@link TypeHandler} for references a generic type.
 * <p>
 * Important: Since 3.5.0, This class never call the {@link ResultSet#wasNull()} and
 * {@link CallableStatement#wasNull()} method for handling the SQL {@code NULL} value.
 * In other words, {@code null} value handling should be performed on subclass.
 * </p>
 *
 * @author Clinton Begin
 * @author Simone Tripodi
 * @author Kzuki Shimizu
 */
public abstract class BaseTypeHandler<T> extends TypeReference<T> implements TypeHandler<T> {

  @Override
  public T getResult(ResultSet rs, String columnName) throws Exception {
    try {
      return getNullableResult(rs, columnName);
    } catch (Exception e) {
      throw new Exception("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e);
    }
  }

  @Override
  public T getResult(ResultSet rs, int columnIndex) throws Exception {
    try {
      return getNullableResult(rs, columnIndex);
    } catch (Exception e) {
      throw new Exception("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e);
    }
  }

  @Override
  public T getResult(CallableStatement cs, int columnIndex) throws Exception {
    try {
      return getNullableResult(cs, columnIndex);
    } catch (Exception e) {
      throw new Exception("Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + e);
    }
  }
  /**
   * Gets the nullable result.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
   * @return the nullable result
   * @throws Exception
   *           the SQL exception
   */
  public abstract T getNullableResult(ResultSet rs, String columnName) throws Exception;
/**
 * 
 * @param rs the rs
 * @param columnIndex  Column ndex 
 * @return the nullable result
 * @throws Exception the exception
 */
  public abstract T getNullableResult(ResultSet rs, int columnIndex) throws Exception;
/**
 * 
 * @param cs the rs
 * @param columnIndex Column ndex 
 * @return the nullable result
 * @throws Exception the exception
 */
  public abstract T getNullableResult(CallableStatement cs, int columnIndex) throws Exception;
  /**
   * 
   */
  public abstract void setParameter(PreparedStatement ps, int i, Object parameter) throws Exception;
}
