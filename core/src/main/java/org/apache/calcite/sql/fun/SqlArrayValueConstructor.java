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
package org.apache.calcite.sql.fun;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCallBinding;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlOperatorBinding;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.type.SqlTypeUtil;

import java.util.List;

/**
 * Definition of the SQL:2003 standard ARRAY constructor, <code>ARRAY
 * [&lt;expr&gt;, ...]</code>.
 */
public class SqlArrayValueConstructor extends SqlMultisetValueConstructor {
  public SqlArrayValueConstructor() {
    super("ARRAY", SqlKind.ARRAY_VALUE_CONSTRUCTOR);
  }

  @Override public RelDataType inferReturnType(SqlOperatorBinding opBinding) {
    RelDataType type =
        getComponentType(
            opBinding.getTypeFactory(),
            opBinding.collectOperandTypes());
    if (null == type) {
      return null;
    }
    return SqlTypeUtil.createArrayType(
        opBinding.getTypeFactory(), type, false);
  }

  /**
   * Allow users to select an empty array
   */
  public static class SqlArrayValueConstructorAllowingEmpty
      extends SqlArrayValueConstructor {
    @Override protected RelDataType getComponentType(
        RelDataTypeFactory typeFactory, List<RelDataType> argTypes) {
      if (argTypes.isEmpty()) {
        return typeFactory.createSqlType(SqlTypeName.NULL);
      }
      return super.getComponentType(typeFactory, argTypes);
    }

    @Override public boolean checkOperandTypes(SqlCallBinding callBinding, boolean throwOnFailure) {
      if (callBinding.operands().isEmpty()) {
        return true;
      }
      return super.checkOperandTypes(callBinding, throwOnFailure);
    }
  }
}

// End SqlArrayValueConstructor.java
