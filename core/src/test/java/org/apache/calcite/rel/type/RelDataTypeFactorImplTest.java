package org.apache.calcite.rel.type;

import com.google.common.collect.ImmutableList;
import org.apache.calcite.runtime.Hook;
import org.apache.calcite.sql.type.SqlTypeFactoryImpl;
import org.junit.Assert;
import org.junit.Test;


public class RelDataTypeFactorImplTest {
  @Test
  public void testKey2TypeCache(){
    long maxSize = 0L;
    Hook.REL_DATA_TYPE_CACHE_SIZE.add(Hook.propertyJ(maxSize));
    RelDataTypeFactory typeFactory = new SqlTypeFactoryImpl(RelDataTypeSystem.DEFAULT);

    Assert.assertEquals(RelDataTypeFactoryImpl.getKey2typeCacheMaxSize(), maxSize);

    RelDataType relDataType = typeFactory.createStructType(
        StructKind.PEEK_FIELDS,
        ImmutableList.of(),
        ImmutableList.of());

    RelDataType relDataType2 = typeFactory.createStructType(
        StructKind.PEEK_FIELDS,
        ImmutableList.of(),
        ImmutableList.of());

    Assert.assertNotSame(relDataType, relDataType2);
    Hook.REL_DATA_TYPE_CACHE_SIZE.add(Hook.propertyJ(-1L));
  }
}
