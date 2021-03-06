package com.fetherbrik.mongodb;

import com.fetherbrik.core.persistence.id.IdGenerator;
import org.bson.types.ObjectId;

import javax.annotation.Nonnull;
import java.math.BigInteger;

/**
 * Generate a MongoDb compatible ObjectId as a Hex String or BigInteger. BigInteger has a slight memory and significant
 * comparison advantage over a Hexadecimal String, at the cost of some complexity. The complexity arises from the
 * fact that BigInteger#toString(16) will truncate any leading zero bytes originally used to create the value.
 *
 * Which is to say that, if a BigInteger is created with 12 bytes, but the first byte value of that 12 bytes is zero,
 * then BigInteger.toString(16).getBytes().length will be 11.
 *
 * @author ggranum
 */
public class MongoIdGenerator implements IdGenerator {
  public  static final int objectIdByteCount = 12;


  @Override
  @Nonnull
  public String nextHex() {
    return new ObjectId().toHexString();
  }

  @Override public int idByteLength() {
    return objectIdByteCount;
  }

  @Override
  @Nonnull
  public BigInteger next() {
    return new BigInteger(new ObjectId().toHexString(), 16);
  }


}
