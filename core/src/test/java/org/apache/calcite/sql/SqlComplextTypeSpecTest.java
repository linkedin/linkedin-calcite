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

import org.apache.calcite.config.NullCollation;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rel.type.RelDataTypeField;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.pretty.SqlPrettyWriter;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.test.RexImplicationCheckerTest.Fixture;

import com.google.common.collect.ImmutableList;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.apache.calcite.sql.type.SqlTypeUtil.convertTypeToSpec;


/**
 * Tests for SqlComplextTypeSpec
 */
public class SqlComplextTypeSpecTest {
  private static final RelDataTypeFactory TYPE_FACTORY;
  private static final RelDataType INT_TYPE;
  private static final RelDataType DATE_TYPE;
  private static final RelDataType VARCHAR_TYPE;
  private static final SqlDialect SQL_DIALECT = new SqlDialect(
      SqlDialect.emptyContext()
          .withDatabaseProduct(SqlDialect.DatabaseProduct.UNKNOWN)
      .withDatabaseProductName("DUMMY")
      .withIdentifierQuoteString("\"")
      .withNullCollation(NullCollation.LAST)
  ) {
    public boolean supportsCharSet() {
      return false;
    }
  };


  static {
    Fixture f = new Fixture();
    TYPE_FACTORY = f.typeFactory;
    INT_TYPE = TYPE_FACTORY.createSqlType(SqlTypeName.INTEGER);
    DATE_TYPE = TYPE_FACTORY.createSqlType(SqlTypeName.DATE);
    VARCHAR_TYPE = TYPE_FACTORY.createSqlType(SqlTypeName.VARCHAR);
  }

  @Test
  public void testUnparseOneField() {
    RelDataTypeFactory typeFactory = createTypeFactory();
    RelDataType rowType = typeFactory.createStructType(
        ImmutableList.of(INT_TYPE),
        ImmutableList.of("colA"));
    String converted = toSqlString(rowType);
    Assert.assertEquals("ROW(\"colA\" INTEGER)", converted);
  }

  @Test
  public void testUnparseMultipleComplex() {
    RelDataTypeFactory typeFactory = createTypeFactory();
    RelDataType colArr = typeFactory.createArrayType(INT_TYPE, -1);
    RelDataType colArrNullable = typeFactory.createTypeWithNullability(colArr, true);
    RelDataType colMap = typeFactory.createMapType(VARCHAR_TYPE, DATE_TYPE);
    RelDataType colMapNullable = typeFactory.createTypeWithNullability(colMap, true);
    RelDataType colRow = typeFactory.createStructType(
        ImmutableList.of(DATE_TYPE, VARCHAR_TYPE),
        ImmutableList.of("s_dateCol", "s_stringCol"));
    RelDataType colRowNullable = typeFactory.createTypeWithNullability(colRow, true);
    RelDataType rowType = typeFactory.createStructType(
        ImmutableList.of(INT_TYPE, colArr, colArrNullable, colMap, colMapNullable, colRow,
            colRowNullable),
        ImmutableList.of("colA", "colArr", "colArrNullable", "colMap", "colMapNullable", "colRow",
            "colRowNullable"));
    String converted = toSqlString(rowType);
    String expected =
        "ROW("
            + "\"colA\" INTEGER, "
            + "\"colArr\" ARRAY<INTEGER>, "
            + "\"colArrNullable\" ARRAY<INTEGER>, "
            + "\"colMap\" MAP<VARCHAR, DATE>, "
            + "\"colMapNullable\" MAP<VARCHAR, DATE>, "
            + "\"colRow\" ROW(\"s_dateCol\" DATE, \"s_stringCol\" VARCHAR), "
            + "\"colRowNullable\" ROW(\"s_dateCol\" DATE, \"s_stringCol\" VARCHAR))";
    Assert.assertEquals(expected, converted);
  }

  private String toSqlString(RelDataType rowType) {
    List<String> names = rowType.getFieldNames();
    ImmutableList.Builder<SqlDataTypeSpec> typeListBuilder = ImmutableList.builder();
    for (RelDataTypeField field : rowType.getFieldList()) {
      SqlDataTypeSpec typeSpec = convertTypeToSpec(field.getType());
      typeListBuilder.add(typeSpec);
    }

    SqlRowTypeSpec spec = new SqlRowTypeSpec(names,
        typeListBuilder.build(),
        false,
        SqlParserPos.ZERO);
    SqlWriter writer = new SqlPrettyWriter(SQL_DIALECT);

    spec.unparse(writer, 1, 1);
    return writer.toSqlString().toString();
  }

  private RelDataTypeFactory createTypeFactory() {
    Fixture f = new Fixture();
    return f.typeFactory;
  }
}

// End SqlComplextTypeSpecTest.java
