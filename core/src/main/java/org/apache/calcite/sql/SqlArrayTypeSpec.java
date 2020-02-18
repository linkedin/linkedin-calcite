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
import org.apache.calcite.sql.type.SqlTypeName;

import java.util.Objects;


/**
 * SQL array type specification
 */
public class SqlArrayTypeSpec extends SqlDataTypeSpec {

  private final SqlDataTypeSpec typeSpec;

  public SqlArrayTypeSpec(SqlDataTypeSpec type, SqlParserPos pos) {
    this(type, null, pos);
  }

  public SqlArrayTypeSpec(SqlDataTypeSpec type, Boolean nullable, SqlParserPos pos) {
    super(new SqlCollectionTypeNameSpec(type.getTypeNameSpec(), SqlTypeName.ARRAY, pos),
        null, nullable, pos);
    this.typeSpec = type;
  }

  public SqlDataTypeSpec getElementTypeSpec() {
    return typeSpec;
  }

  @Override public SqlNode clone(SqlParserPos pos) {
    return new SqlArrayTypeSpec(this.typeSpec, getNullable(), pos);
  }

  @Override public SqlDataTypeSpec withNullable(Boolean nullable) {
    if (Objects.equals(getNullable(), nullable)) {
      return this;
    }
    return new SqlArrayTypeSpec(typeSpec, getNullable(), getParserPosition());
  }

  @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    writer.keyword(getTypeName().getSimple());
    SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "<", ">");
    writer.setNeedWhitespace(false);
    typeSpec.unparse(writer, leftPrec, rightPrec);
    writer.setNeedWhitespace(false);
    writer.endList(frame);
  }
}

// End SqlArrayTypeSpec.java
