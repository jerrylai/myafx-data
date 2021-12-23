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
 * @author Clinton Begin
 */
public interface TypeHandler<T> {

  void setParameter(PreparedStatement ps, int i, Object param) throws Exception;

  /**
   * Gets the result.
   *
   * @param rs
   *          the rs
   * @param columnName
   *          Column name, when configuration <code>useColumnLabel</code> is <code>false</code>
   * @return the result
   * @throws Exception
   *           the SQL exception
   */
  T getResult(ResultSet rs, String columnName) throws Exception;

  T getResult(ResultSet rs, int columnIndex) throws Exception;

  T getResult(CallableStatement cs, int columnIndex) throws Exception;

}
