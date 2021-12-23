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

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author Clinton Begin
 */
public class BlobByteObjectArrayTypeHandler extends BaseTypeHandler<Byte[]> {

  @Override
  public void setParameter(PreparedStatement ps, int i, Object parameter) throws SQLException {
    if(parameter == null){
      ps.setNull(i, Types.BLOB);
    } 
    else {
      var v = (Byte[])parameter;
      ByteArrayInputStream bis = new ByteArrayInputStream(ByteArrayUtils.convertToPrimitiveArray(v));
      ps.setBinaryStream(i, bis, v.length);
    }
  }

  @Override
  public Byte[] getNullableResult(ResultSet rs, String columnName)
      throws SQLException {
    Blob blob = rs.getBlob(columnName);
    return getBytes(blob);
  }

  @Override
  public Byte[] getNullableResult(ResultSet rs, int columnIndex)
      throws SQLException {
    Blob blob = rs.getBlob(columnIndex);
    return getBytes(blob);
  }

  @Override
  public Byte[] getNullableResult(CallableStatement cs, int columnIndex)
      throws SQLException {
    Blob blob = cs.getBlob(columnIndex);
    return getBytes(blob);
  }

  private Byte[] getBytes(Blob blob) throws SQLException {
    Byte[] returnValue = null;
    if (blob != null) {
      returnValue = ByteArrayUtils.convertToObjectArray(blob.getBytes(1, (int) blob.length()));
    }
    return returnValue;
  }
}
