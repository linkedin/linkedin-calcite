/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.sql;

import org.apache.calcite.sql.parser.SqlParserPos;

import java.util.Objects;


/**
 * Class to capture SQL map type specification.
 */
public class SqlMapTypeSpec extends SqlDataTypeSpec {

  public SqlMapTypeSpec(SqlDataTypeSpec keyType, SqlDataTypeSpec valType, SqlParserPos pos) {
    this(keyType, valType, null, pos);
  }

  public SqlMapTypeSpec(SqlDataTypeSpec keyType, SqlDataTypeSpec valType,
      Boolean nullable, SqlParserPos pos) {
    this(new SqlMapTypeNameSpec(pos, keyType, valType), nullable, pos);
  }

  private SqlMapTypeSpec(SqlMapTypeNameSpec mapTypeNameSpec, Boolean nullable, SqlParserPos pos) {
    super(mapTypeNameSpec, null, nullable, pos);
  }

  public SqlDataTypeSpec getKeyTypeSpec() {
    return ((SqlMapTypeNameSpec) getTypeNameSpec()).getKeyTypeSpec();
  }

  public SqlDataTypeSpec getValueTypeSpec() {
    return ((SqlMapTypeNameSpec) getTypeNameSpec()).getValTypeSpec();
  }

  @Override public SqlNode clone(SqlParserPos pos) {
    return new SqlMapTypeSpec((SqlMapTypeNameSpec) getTypeNameSpec(),
        getNullable(), getParserPosition());
  }

  @Override public SqlDataTypeSpec withNullable(Boolean nullable) {
    if (Objects.equals(getNullable(), nullable)) {
      return this;
    }
    return new SqlMapTypeSpec((SqlMapTypeNameSpec) getTypeNameSpec(), nullable,
        getParserPosition());
  }

  @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    getTypeNameSpec().unparse(writer, leftPrec, rightPrec);
  }
}

// End SqlMapTypeSpec.java
