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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


/**
 * This implementation of {@link Map} can be sealed and unsealed at will. When
 * sealed, it does not accept any writes.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class SealableMap<K, V> implements Map<K, V> {
  //~ Instance fields --------------------------------------------------------

  private final Map<K, V> delegate;
  private final Map<K, V> readOnlyDelegate;
  private boolean sealed = false;

  //~ Constructors -----------------------------------------------------------

  /**
   * Creates a map that can be sealed and unsealed at will. When sealed, the
   * map will not accept any write operations
   * @param delegate the map that all calls will be delegated to. Will not
   *                 delegate write calls when sealed. Any changes
   *                 to the delegate will reflect in the {@link SealableMap}
   */
  public SealableMap(Map<K, V> delegate) {
    this.delegate = Objects.requireNonNull(delegate, "Delegate map cannot be null");
    this.readOnlyDelegate = Collections.unmodifiableMap(delegate);
  }

  //~ Methods ----------------------------------------------------------------

  @Override public int size() {
    return delegate.size();
  }

  @Override public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override public boolean containsKey(Object key) {
    return delegate.containsKey(key);
  }

  @Override public boolean containsValue(Object value) {
    return delegate.containsValue(value);
  }

  @Override public V get(Object key) {
    return delegate.get(key);
  }

  @Override public V put(K key, V value) {
    checkSealed();
    return delegate.put(key, value);
  }

  @Override public V remove(Object key) {
    checkSealed();
    return delegate.remove(key);
  }

  @Override public void putAll(Map<? extends K, ? extends V> m) {
    checkSealed();
    delegate.putAll(m);
  }

  @Override public void clear() {
    checkSealed();
    delegate.clear();
  }

  @Override public Set<K> keySet() {
    return sealed ? readOnlyDelegate.keySet() : delegate.keySet();
  }

  @Override public Collection<V> values() {
    return sealed ? readOnlyDelegate.values() : delegate.values();
  }

  @Override public Set<Entry<K, V>> entrySet() {
    return sealed ? readOnlyDelegate.entrySet() : delegate.entrySet();
  }

  @Override public V getOrDefault(Object key, V defaultValue) {
    return delegate.getOrDefault(key, defaultValue);
  }

  @Override public void forEach(BiConsumer<? super K, ? super V> action) {
    delegate.forEach(action);
  }

  @Override public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    checkSealed();
    delegate.replaceAll(function);
  }

  @Override public V putIfAbsent(K key, V value) {
    checkSealed();
    return delegate.putIfAbsent(key, value);
  }

  @Override public boolean remove(Object key, Object value) {
    checkSealed();
    return delegate.remove(key, value);
  }

  @Override public boolean replace(K key, V oldValue, V newValue) {
    checkSealed();
    return delegate.replace(key, oldValue, newValue);
  }

  @Override public V replace(K key, V value) {
    checkSealed();
    return delegate.replace(key, value);
  }

  @Override public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    checkSealed();
    return delegate.computeIfAbsent(key, mappingFunction);
  }

  @Override public V computeIfPresent(
      K key,
      BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    checkSealed();
    return delegate.computeIfPresent(key, remappingFunction);
  }

  @Override public V compute(
      K key,
      BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    checkSealed();
    return delegate.compute(key, remappingFunction);
  }

  @Override public V merge(
      K key,
      V value,
      BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    checkSealed();
    return delegate.merge(key, value, remappingFunction);
  }

  @Override public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SealableMap)) {
      return false;
    }
    SealableMap<?, ?> that = (SealableMap<?, ?>) o;
    return sealed == that.sealed && delegate.equals(that.delegate);
  }

  @Override public int hashCode() {
    return Objects.hash(delegate, sealed);
  }

  @Override public String toString() {
    return "SealableMap{" + "delegate=" + delegate + ", sealed=" + sealed + '}';
  }

  /**
   * Seals the map for writes. No effect if already sealed
   */
  public void seal() {
    sealed = true;
  }

  /**
   * Unseals the map for writes. No effect if already unsealed
   */
  public void unseal() {
    sealed = false;
  }

  /**
   * @return if the map is currently sealed
   */
  public boolean isSealed() {
    return sealed;
  }

  private void checkSealed() {
    if (sealed) {
      throw new IllegalStateException("Cannot write to this map when it is sealed");
    }
  }
}

// End SealableMap.java
