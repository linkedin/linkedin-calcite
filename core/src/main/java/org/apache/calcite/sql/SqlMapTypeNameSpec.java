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

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.SqlTypeName;
import org.apache.calcite.sql.validate.SqlValidator;
import org.apache.calcite.util.Litmus;


/**
 * Class to capture SQL map type name specification.
 */
public class SqlMapTypeNameSpec extends SqlTypeNameSpec {
  private final SqlDataTypeSpec keyTypeSpec;
  private final SqlDataTypeSpec valTypeSpec;

  public SqlMapTypeNameSpec(
      SqlParserPos pos,
      SqlDataTypeSpec keyTypeSpec,
      SqlDataTypeSpec valTypeSpec) {
    super(new SqlIdentifier(SqlTypeName.MAP.getName(), pos), pos);
    this.keyTypeSpec = keyTypeSpec;
    this.valTypeSpec = valTypeSpec;
  }

  public SqlDataTypeSpec getKeyTypeSpec() {
    return keyTypeSpec;
  }

  public SqlDataTypeSpec getValTypeSpec() {
    return valTypeSpec;
  }

  @Override public RelDataType deriveType(SqlValidator validator) {
    final RelDataTypeFactory typeFactory = validator.getTypeFactory();
    return typeFactory.createMapType(
        keyTypeSpec.deriveType(validator),
        valTypeSpec.deriveType(validator));
  }

  @Override public void unparse(SqlWriter writer, int leftPrec, int rightPrec) {
    writer.keyword(getTypeName().getSimple());
    final SqlWriter.Frame frame = writer.startList(SqlWriter.FrameTypeEnum.FUN_CALL, "<", ">");
    writer.setNeedWhitespace(false);
    keyTypeSpec.unparse(writer, leftPrec, rightPrec);
    writer.sep(",", true);
    valTypeSpec.unparse(writer, leftPrec, rightPrec);
    writer.setNeedWhitespace(false);
    writer.endList(frame);
  }

  @Override public boolean equalsDeep(SqlTypeNameSpec node, Litmus litmus) {
    if (!(node instanceof SqlMapTypeNameSpec)) {
      return fail(node, litmus);
    }
    final SqlMapTypeNameSpec that = (SqlMapTypeNameSpec) node;
    if (!keyTypeSpec.equalsDeep(that.keyTypeSpec, litmus)) {
      return fail(node, litmus);
    }
    if (!valTypeSpec.equalsDeep(that.valTypeSpec, litmus)) {
      return fail(node, litmus);
    }
    return litmus.succeed();
  }

  private boolean fail(SqlTypeNameSpec node, Litmus litmus) {
    return litmus.fail("{} != {}", this, node);
  }
}

// End SqlMapTypeNameSpec.java
