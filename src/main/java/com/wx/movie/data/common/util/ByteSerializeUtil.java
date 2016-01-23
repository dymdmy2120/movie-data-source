/**
 * 
 */
package com.wepiao.pricing.redis;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 二进制序列化工具
 * @author Chen Gang
 *
 */
public class ByteSerializeUtil {
	/**
	 * 序列化
	 * 
	 * @param object
	 *            实体对象
	 * @return 二进制流
	 * @throws IOException
	 */
	public static byte[] serialize(Object object) throws IOException {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;

		// 序列化
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		oos.writeObject(object);

		return baos.toByteArray();
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes 二进制流
	 * @return 实体对象
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static Object unserialize(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = null;

		// 反序列化
		bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		return ois.readObject();

	}

	/**
	 * 序列化
	 * 
	 * @param object
	 *            实体对象
	 * @return 二进制流
	 * @throws IOException
	 */
	public static <T> byte[] serializeList(List<T> list) throws IOException {
		ObjectOutputStream oos = null;
		ByteArrayOutputStream baos = null;

		// 序列化
		baos = new ByteArrayOutputStream();
		oos = new ObjectOutputStream(baos);
		for (T t : list)
			oos.writeObject(t);
		byte[] bytes = baos.toByteArray();
		return bytes;

	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 *            二进制流
	 * @return 实体对象
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
  public static <T> List<T> unserializeList(Class<T> c, byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = null;

		List<T> list = new ArrayList<T>();

		// 反序列化
		bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		while (true) {
			try {
				T t = (T) ois.readObject();
				list.add(t);
			} catch (EOFException eof) {
				break;
			}
		}

		return list;
	}
}
