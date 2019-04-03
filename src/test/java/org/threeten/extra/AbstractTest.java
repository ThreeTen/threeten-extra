/*
 * Copyright (c) 2007-present, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.threeten.extra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamConstants;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Base test class.
 */
public abstract class AbstractTest {

    private static final String SERIALISATION_DATA_FOLDER = "src/test/resources/";

    protected static boolean isIsoLeap(long year) {
        if (year % 4 != 0) {
            return false;
        }
        if (year % 100 == 0 && year % 400 != 0) {
            return false;
        }
        return true;
    }

    protected static void assertSerializable(Object o) throws IOException, ClassNotFoundException {
        Object deserialisedObject = writeThenRead(o);
        assertEquals(o, deserialisedObject);
    }

    protected static void assertSerializableAndSame(Object o) throws IOException, ClassNotFoundException {
        Object deserialisedObject = writeThenRead(o);
        assertSame(o, deserialisedObject);
    }

    protected static Object writeThenRead(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }

        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return ois.readObject();
        } finally {
            if (ois != null) {
                ois.close();
            }
        }
    }

    protected static void assertEqualsSerialisedForm(Object objectSerialised) throws IOException, ClassNotFoundException {
        assertEqualsSerialisedForm(objectSerialised.getClass(), objectSerialised);
    }

    protected static void assertEqualsSerialisedForm(Class<?> cls, Object objectSerialised) throws IOException, ClassNotFoundException {
        String className = cls.getSimpleName();
//        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SERIALISATION_DATA_FOLDER + className + ".bin"))) {
//            out.writeObject(objectSerialised);
//        }

        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(new FileInputStream(SERIALISATION_DATA_FOLDER + className + ".bin"));
            Object objectFromFile = in.readObject();
            assertEquals(objectSerialised, objectFromFile);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    protected static void assertImmutable(Class<?> cls) {
        assertTrue(Modifier.isPublic(cls.getModifiers()));
        assertTrue(Modifier.isFinal(cls.getModifiers()));
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.getName().contains("$") == false) {
                if (Modifier.isStatic(field.getModifiers())) {
                    assertTrue("Field:" + field.getName(), Modifier.isFinal(field.getModifiers()));
                } else {
                    assertTrue("Field:" + field.getName(), Modifier.isPrivate(field.getModifiers()));
                    assertTrue("Field:" + field.getName(), Modifier.isFinal(field.getModifiers()));
                }
            }
        }
        Constructor<?>[] cons = cls.getDeclaredConstructors();
        for (Constructor<?> con : cons) {
            assertTrue(Modifier.isPrivate(con.getModifiers()));
        }
    }

    protected static void assertSerializedBySer(byte[] expectedBytes, Object object, byte[]... matches) throws Exception {
        String serClass = object.getClass().getPackage().getName() + ".Ser";
        Class<?> serCls = Class.forName(serClass);
        Field field = serCls.getDeclaredField("serialVersionUID");
        field.setAccessible(true);
        long serVer = (Long) field.get(null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
        } finally {
            if (oos != null) {
                oos.close();
            }
        }
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(bais);
            assertEquals(ObjectStreamConstants.STREAM_MAGIC, dis.readShort());
            assertEquals(ObjectStreamConstants.STREAM_VERSION, dis.readShort());
            assertEquals(ObjectStreamConstants.TC_OBJECT, dis.readByte());
            assertEquals(ObjectStreamConstants.TC_CLASSDESC, dis.readByte());
            assertEquals(serClass, dis.readUTF());
            assertEquals(serVer, dis.readLong());
            assertEquals(ObjectStreamConstants.SC_EXTERNALIZABLE | ObjectStreamConstants.SC_BLOCK_DATA, dis.readByte());
            assertEquals(0, dis.readShort());  // number of fields
            assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, dis.readByte());  // end of classdesc
            assertEquals(ObjectStreamConstants.TC_NULL, dis.readByte());  // no superclasses
            if (expectedBytes.length < 256) {
                assertEquals(ObjectStreamConstants.TC_BLOCKDATA, dis.readByte());
                assertEquals(expectedBytes.length, dis.readUnsignedByte());
            } else {
                assertEquals(ObjectStreamConstants.TC_BLOCKDATALONG, dis.readByte());
                assertEquals(expectedBytes.length, dis.readInt());
            }
            byte[] input = new byte[expectedBytes.length];
            dis.readFully(input);
            assertEquals(expectedBytes, input);
            if (matches.length > 0) {
                for (byte[] match : matches) {
                    boolean matched = false;
                    while (matched == false) {
                        try {
                            dis.mark(1000);
                            byte[] possible = new byte[match.length];
                            dis.readFully(possible);
                            assertEquals(match, possible);
                            matched = true;
                        } catch (AssertionError ex) {
                            dis.reset();
                            dis.readByte();  // ignore
                        }
                    }
                }
            } else {
                assertEquals(ObjectStreamConstants.TC_ENDBLOCKDATA, dis.readByte());  // end of blockdata
                assertEquals(-1, dis.read());
            }
        } finally {
            if (dis != null) {
                dis.close();
            }
        }
    }

}

