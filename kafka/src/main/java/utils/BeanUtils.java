package utils;

import java.io.*;

public class BeanUtils {
    public static byte[] serialize(Object obj) throws IOException {
        byte[] bytes=null;
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);

        bytes=byteArrayOutputStream.toByteArray();

        objectOutputStream.close();
        byteArrayOutputStream.close();

        return bytes;
    }

    public static Object deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj=null;
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream=new ObjectInputStream(byteArrayInputStream);
        obj=objectInputStream.readObject();

        objectInputStream.close();
        byteArrayInputStream.close();
        return obj;
    }
}
