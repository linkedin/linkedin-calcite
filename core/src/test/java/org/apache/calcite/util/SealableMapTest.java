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
package org.apache.calcite.util;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for the seal/unseal functionalities of {@link SealableMap}
 */
public class SealableMapTest {
  //~ Constructors -----------------------------------------------------------

  public SealableMapTest() {
  }

  //~ Methods ----------------------------------------------------------------

  @Test public void testSealUnseal() {
    Map<String, String> backingMap = new HashMap<>();
    SealableMap<String, String> sealableMap = new SealableMap<>(backingMap);
    sealableMap.put("foo1", "bar1");
    sealableMap.put("foo2", "bar2");

    // start off unsealed
    ensureUnsealed(sealableMap, backingMap);

    // seal and verify
    sealableMap.seal();
    ensureSealed(sealableMap, backingMap);

    // unseal and verify
    sealableMap.unseal();
    ensureUnsealed(sealableMap, backingMap);
  }

  @Test public void testEqualsHashCode() {
    Map<String, String> backingMap = new HashMap<>();
    backingMap.put("foo", "bar");

    // will be equal
    SealableMap<String, String> map1 = new SealableMap<>(backingMap);
    SealableMap<String, String> map2 = new SealableMap<>(backingMap);

    // will be unequal because this map will be sealed
    SealableMap<String, String> map3 = new SealableMap<>(backingMap);
    map3.seal();

    // will be unequal because map entries are different
    SealableMap<String, String> map4 = new SealableMap<>(new HashMap<>());
    map4.put("foo", "baz");

    assertEquals(map1, map2);
    assertEquals(map1.hashCode(), map2.hashCode());

    assertNotEquals(map1, map3);
    assertNotEquals(map1, map4);
    assertNotEquals(map3, map4);
  }

  private void ensureSealed(SealableMap<String, String> map, Map<String, String> backingMap) {
    assertTrue(map.isSealed());
    ensureReadableAndDataMatchesDelegate(map, backingMap);

    ensureFailure(() -> {
      map.put("foo", "bar");
      return "put";
    });

    ensureFailure(() -> {
      map.remove("foo");
      return "remove(k)";
    });

    ensureFailure(() -> {
      map.putAll(backingMap);
      return "putAll";
    });

    ensureFailure(() -> {
      map.clear();
      return "clear";
    });

    ensureFailure(() -> {
      map.keySet().clear();
      return "keySet mutate";
    });

    ensureFailure(() -> {
      map.values().clear();
      return "values mutate";
    });

    ensureFailure(() -> {
      map.entrySet().clear();
      return "entrySet mutate";
    });

    ensureFailure(() -> {
      map.replaceAll((k, v) -> v);
      return "replaceAll";
    });

    ensureFailure(() -> {
      map.putIfAbsent("foo", "bar");
      return "putIfAbsent";
    });

    ensureFailure(() -> {
      map.remove("foo", "bar");
      return "remove(k,v)";
    });

    ensureFailure(() -> {
      map.replace("foo", "bar", "baz");
      return "replace(k,o_v,n_v)";
    });

    ensureFailure(() -> {
      map.replace("foo", "baz");
      return "replace(k,n_v)";
    });

    ensureFailure(() -> {
      map.computeIfAbsent("foo", ignored -> "bar");
      return "computeIfAbsent";
    });

    ensureFailure(() -> {
      map.computeIfPresent("foo", (ignored1, ignored2) -> "bar");
      return "computeIfPresent";
    });

    ensureFailure(() -> {
      map.compute("foo", (ignored1, ignored2) -> "bar");
      return "compute";
    });

    ensureFailure(() -> {
      map.merge("foo", "bar", (ignored1, ignored2) -> "baz");
      return "merge";
    });
  }

  private void ensureUnsealed(SealableMap<String, String> map, Map<String, String> backingMap) {
    assertFalse(map.isSealed());
    ensureReadableAndDataMatchesDelegate(map, backingMap);

    Map<String, String> backingMapCopy = new HashMap<>(backingMap);

    String key = "@@key@@";
    String value1 = "@@value1@@";
    String value2 = "@@value2@@";

    map.put(key, value1);
    assertEquals(map.get(key), value1);
    map.remove(key);
    assertFalse(map.containsKey(key));

    map.clear();
    map.putAll(backingMapCopy);
    assertEquals(map.entrySet(), backingMapCopy.entrySet());

    map.keySet().clear();
    map.putAll(backingMapCopy);

    map.values().clear();
    map.putAll(backingMapCopy);

    map.entrySet().clear();
    map.putAll(backingMapCopy);

    map.replaceAll((k, v) -> value1);
    map.values().forEach(value -> assertEquals(value, value1));
    map.clear();
    map.putAll(backingMapCopy);

    map.putIfAbsent(key, value1);
    assertEquals(map.get(key), value1);
    map.replace(key, value1, value2);
    assertEquals(map.get(key), value2);
    map.replace(key, value1);
    assertEquals(map.get(key), value1);
    map.remove(key, value1);
    assertFalse(map.containsKey(key));

    map.computeIfAbsent(key, k -> value1);
    assertEquals(map.get(key), value1);
    map.computeIfPresent(key, (k, v) -> value2);
    assertEquals(map.get(key), value2);
    map.compute(key, (k, v) -> value1);
    assertEquals(map.get(key), value1);
    map.merge(key, value1, (k, v) -> null);
    assertFalse(map.containsKey(key));

    // clean up
    map.clear();
    map.putAll(backingMapCopy);
  }

  private <K, V> void ensureReadableAndDataMatchesDelegate(Map<K, V> map, Map<K, V> backingMap) {
    assertEquals(map.size(), backingMap.size());
    assertEquals(map.isEmpty(), backingMap.isEmpty());
    assertEquals(map.entrySet(), backingMap.entrySet());

    backingMap.forEach((k, v) -> {
      assertTrue(map.containsKey(k));
      assertTrue(map.containsValue(v));
      assertEquals(map.get(k), v);
      assertEquals(map.getOrDefault(k, null), v);
    });
  }

  private void ensureFailure(Supplier<String> work) {
    try {
      String opName = work.get();
      fail(opName + " is expected to fail");
    } catch (IllegalStateException | UnsupportedOperationException e) {
      // expected. Nothing to do
    }
  }
}

// End SealableMapTest.java
