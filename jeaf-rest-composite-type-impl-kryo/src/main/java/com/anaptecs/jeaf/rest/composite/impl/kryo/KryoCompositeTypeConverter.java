/**
 * Copyright 2004 - 2022 anaptecs GmbH, Burgstr. 96, 72764 Reutlingen, Germany
 *
 * All rights reserved.
 */
package com.anaptecs.jeaf.rest.composite.impl.kryo;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;

import com.anaptecs.jeaf.rest.composite.api.CompositeTypeConverter;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class KryoCompositeTypeConverter implements CompositeTypeConverter {

  @Override
  public String serializeObject( Object pObject, List<Class<?>> pSerializedClasses ) {
    // Prepare streams where content will be written to
    int lInitialSize;
    if (pSerializedClasses != null && pSerializedClasses.isEmpty() == false) {
      lInitialSize = 128;
    }
    else {
      lInitialSize = 256;
    }
    ByteArrayOutputStream lByteOutputStream = new ByteArrayOutputStream(lInitialSize);
    Output lOutput = new Output(lByteOutputStream);

    // Serialize the passed object
    Kryo lKryo = this.getKryoInstance(pSerializedClasses);
    lKryo.writeObject(lOutput, pObject);
    lOutput.close();

    // As it is expected we have to convert byte[] to base 64 encoded string.
    byte[] lBuffer = lByteOutputStream.toByteArray();
    return Base64.getEncoder().encodeToString(lBuffer);
  }

  @Override
  public <T> T deserializeObject( String pSerializedObject, Class<T> pResultType, List<Class<?>> pSerializedClasses ) {
    // Decode serialized object as we work with base 64 encoding
    byte[] lDecodedObject = Base64.getDecoder().decode(pSerializedObject);
    Kryo lKryo = this.getKryoInstance(pSerializedClasses);
    Input lInput = new Input(lDecodedObject);
    return lKryo.readObject(lInput, pResultType);
  }

  private Kryo getKryoInstance( List<Class<?>> pSerializedClasses ) {
    // Create new Kryo instance. As they are not thread-safe we have to created them over and over again.
    Kryo lKryo = new Kryo();

    // If provided then register all classes that are involved in the serialization process. This will reduce the size
    // of the serialized objects and will also increase portability as class names are not part of serialized content.
    if (pSerializedClasses != null && pSerializedClasses.isEmpty() == false) {
      lKryo.setRegistrationRequired(true);
      for (Class<?> lNextClass : pSerializedClasses) {
        lKryo.register(lNextClass);
      }
    }
    // We will use Kryo in mode without pre-registered classes. This is not optimal but it works.
    else {
      lKryo.setRegistrationRequired(false);
    }
    return lKryo;
  }
}
