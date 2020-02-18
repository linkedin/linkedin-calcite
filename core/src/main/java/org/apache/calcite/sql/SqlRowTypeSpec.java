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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Class to capture SQL row type specification.
 */
public class SqlRowTypeSpec extends SqlDataTypeSpec {

  public SqlRowTypeSpec(List<String> fieldNames, List<SqlDataTypeSpec> fieldTypeSpecs,
      SqlParserPos pos) {
    this(new SqlRowTypeNameSpec(pos, toIdentifierList(fieldNames, pos), fieldTypeSpecs),
        null, pos);
  }

  public SqlRowTypeSpec(List<String> fieldNames, List<SqlDataTypeSpec> fieldTypeSpecs,
      Boolean nullable, SqlParserPos pos) {
    this(new SqlRowTypeNameSpec(pos, toIdentifierList(fieldNames, pos), fieldTypeSpecs),
        nullable, pos);
  }

  private SqlRowTypeSpec(SqlRowTypeNameSpec rowTypeNameSpec, Boolean nullable, SqlParserPos pos) {
    super(rowTypeNameSpec, null, nullable, pos);
  }

  private static List<SqlIdentifier> toIdentifierList(List<String> fieldNames, SqlParserPos pos) {
    final List<SqlIdentifier> results = new ArrayList<>();
    for (String fieldName : fieldNames) {
      results.add(new SqlIdentifier(fieldName, pos));
    }
    return results;
  }

  public List<String> getFieldNames() {
    return toStringList(((SqlRowTypeNameSpec) getTypeNameSpec()).getFieldNames());
  }

  private List<String> toStringList(final List<SqlIdentifier> fieldNames) {
    final List<String> results = new ArrayList<>();
    for (SqlIdentifier fieldName : fieldNames) {
      results.add(fieldName.toString());
    }
    return results;
  }

  public List<SqlDataTypeSpec> getFieldTypeSpecs() {
    return ((SqlRowTypeNameSpec) getTypeNameSpec()).getFieldTypes();
  }

  @Override public SqlNode clone(SqlParserPos pos) {
    return new SqlRowTypeSpec((SqlRowTypeNameSpec) getTypeNameSpec(), getNullable(), pos);
  }

  @Override public SqlDataTypeSpec withNullable(Boolean nullable) {
    if (Objects.equals(getNullable(), nullable)) {
      return this;
    }
    return new SqlRowTypeSpec((SqlRowTypeNameSpec) getTypeNameSpec(), nullable,
        getParserPosition());
  }

  @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    getTypeNameSpec().unparse(writer, leftPrec, rightPrec);
  }
}

// End SqlRowTypeSpec.java
