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
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Types;

/**
 * Convert <code>String</code> to/from <code>SQLXML</code>.
 *
 * @since 3.5.0
 * @author Iwao AVE!
 */
public class SqlxmlTypeHandler extends BaseTypeHandler<String> {

  @Override
  public void setParameter(PreparedStatement ps, int i, Object parameter)
      throws SQLException {
    if(parameter == null)  ps.setNull(i, Types.SQLXML);
    else {
      SQLXML sqlxml = ps.getConnection().createSQLXML();
      try {
        sqlxml.setString((String)parameter);
        ps.setSQLXML(i, sqlxml);
      } finally {
        sqlxml.free();
      }
    }
  }

  @Override
  public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
    return sqlxmlToString(rs.getSQLXML(columnName));
  }

  @Override
  public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    return sqlxmlToString(rs.getSQLXML(columnIndex));
  }

  @Override
  public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    return sqlxmlToString(cs.getSQLXML(columnIndex));
  }

  protected String sqlxmlToString(SQLXML sqlxml) throws SQLException {
    if (sqlxml == null) {
      return null;
    }
    try {
      return sqlxml.getString();
    } finally {
      sqlxml.free();
    }
  }

}
