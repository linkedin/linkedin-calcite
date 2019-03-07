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
package org.apache.calcite.adapter.spark;

import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.schema.Function;
import org.apache.calcite.sql.SqlFunctionCategory;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.parser.SqlParserPos;
import org.apache.calcite.sql.type.OperandTypes;
import org.apache.calcite.sql.type.SqlOperandTypeChecker;
import org.apache.calcite.sql.type.SqlOperandTypeInference;
import org.apache.calcite.sql.type.SqlReturnTypeInference;
import org.apache.calcite.sql.validate.SqlUserDefinedFunction;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Custom UDF representing a Spark Dataset lambda function.
 */
public class SparkDatasetLambdaFunction extends SqlUserDefinedFunction {
  private String className;
  private String methodName;
  private boolean isExternalVariable;
  private String inputClassName;
  private String outputClassName;
  private boolean isEdgeNode;

  private SparkDatasetLambdaFunction(SqlIdentifier opName,
      SqlReturnTypeInference returnTypeInference,
      SqlOperandTypeInference operandTypeInference,
      SqlOperandTypeChecker operandTypeChecker,
      List<RelDataType> paramTypes,
      Function function,
      String className,
      String methodName,
      boolean isExternalVariable,
      String inputClassName,
      String outputClassName,
      boolean isEdgeNode) {
    super(opName, returnTypeInference, operandTypeInference, operandTypeChecker,
        paramTypes, function, SqlFunctionCategory.USER_DEFINED_CONSTRUCTOR);
    this.className = className;
    this.methodName = methodName;
    this.isExternalVariable = isExternalVariable;
    this.inputClassName = inputClassName;
    this.outputClassName = outputClassName;
    this.isEdgeNode = isEdgeNode;
  }

  public SparkDatasetLambdaFunction(String name,
      SqlReturnTypeInference returnTypeInference,
      List<RelDataType> paramTypes,
      Function function,
      String className,
      String methodName,
      boolean isExternalVariable,
      String inputClassName,
      String outputClassName,
      boolean isEdgeNode) {
    this(new SqlIdentifier(ImmutableList.of(name), SqlParserPos.ZERO),
        returnTypeInference,
        null,
        OperandTypes.ANY,
        paramTypes,
        function,
        className,
        methodName,
        isExternalVariable,
        inputClassName,
        outputClassName,
        isEdgeNode);
  }

  public String getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  public boolean isExternalVariable() {
    return isExternalVariable;
  }

  public String getInputClassName() {
    return inputClassName;
  }

  public String getOutputClassName() {
    return outputClassName;
  }

  public boolean isEdgeNode() {
    return isEdgeNode;
  }
}

// End SparkDatasetLambdaFunction.java
