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
package org.apache.calcite.sql.validate;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlJoin;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;

/**
 * Namespace representing the row type produced by joining two relations.
 */
class JoinNamespace extends AbstractNamespace {
  //~ Instance fields --------------------------------------------------------

  private final SqlJoin join;

  //~ Constructors -----------------------------------------------------------

  JoinNamespace(SqlValidatorImpl validator, SqlJoin join) {
    super(validator, null);
    this.join = join;
  }

  //~ Methods ----------------------------------------------------------------

  protected RelDataType validateImpl(RelDataType targetRowType) {
    RelDataType leftType =
        validator.getNamespace(join.getLeft()).getRowType();

    // Since LATERALS are used preceding subqueries in FROM to allow the subquery to
    // reference columns provided by preceding FROM items, LATERALS will only appear
    // on the right side of a join.
    SqlNode right;
    if (join.getRight().getKind() == SqlKind.LATERAL) {
      // Skip over fetching for LATERAL's name space since they are not registered
      right = ((SqlCall) join.getRight()).operand(0);
    } else {
      right = join.getRight();
    }

    RelDataType rightType =
        validator.getNamespace(right).getRowType();
    final RelDataTypeFactory typeFactory = validator.getTypeFactory();
    switch (join.getJoinType()) {
    case LEFT:
      rightType = typeFactory.createTypeWithNullability(rightType, true);
      break;
    case RIGHT:
      leftType = typeFactory.createTypeWithNullability(leftType, true);
      break;
    case FULL:
      leftType = typeFactory.createTypeWithNullability(leftType, true);
      rightType = typeFactory.createTypeWithNullability(rightType, true);
      break;
    }
    return typeFactory.createJoinType(leftType, rightType);
  }

  public SqlNode getNode() {
    return join;
  }
}

// End JoinNamespace.java
