package com.example.moucan.androidcoursesystem.imageloader.glide;

import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

public final class DataCacheKey implements Key {

  private final Key sourceKey;
  private final Key signature;

  DataCacheKey(Key sourceKey, Key signature) {
    this.sourceKey = sourceKey;
    this.signature = signature;
  }

  Key getSourceKey() {
    return sourceKey;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof DataCacheKey) {
      DataCacheKey other = (DataCacheKey) o;
      return sourceKey.equals(other.sourceKey) && signature.equals(other.signature);
    }
    return false;
  }

  @Override
  public int hashCode() {
    int result = sourceKey.hashCode();
    result = 31 * result + signature.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "DataCacheKey{"
        + "sourceKey=" + sourceKey
        + ", signature=" + signature
        + '}';
  }

  @Override
  public void updateDiskCacheKey(MessageDigest messageDigest) {
    sourceKey.updateDiskCacheKey(messageDigest);
    signature.updateDiskCacheKey(messageDigest);
  }
}