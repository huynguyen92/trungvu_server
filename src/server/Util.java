package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Util {

	public static int transmogrify(int data) {
		if (Character.isLetter(data))
			return data ^ ' ';
		else
			return data;
	}

	public static void process(Socket s) {
		System.out.println("connection from " + s);
		try (InputStream in = s.getInputStream();
				OutputStream out = s.getOutputStream();) {

			int data;
			while ((data = in.read()) != -1) {
				data = Util.transmogrify(data);
				out.write(data);
			}

		} catch (IOException e) {
			System.err.println("connection problem ->" + e);
		}
	}

	public static void process(SocketChannel sc) {

		System.out.println("connection from " + sc);
		try {
			ByteBuffer buf = ByteBuffer.allocate(1024);
			while (sc.read(buf) != -1) {
				// byte array with some additional information
				// Position
				// length
				// limit
				// capacity
				buf.flip();

				for (int i = 0; i < buf.limit(); i++) {
					buf.put(i, (byte) transmogrify(buf.get(i)));
				}
				sc.write(buf);

				buf.clear();

			}
		} catch (IOException e) {
			System.err.println("connection problem ->" + e);
		}
	}

	public static StringBuffer transmogrify(StringBuffer stringInputBuffer) {
		// TODO Auto-generated method stub
		StringBuffer ch = new StringBuffer(stringInputBuffer.toString().toUpperCase());
		
		//MongodbBrigde.createMongoConnection("localhost", 27017);

		//MongodbBrigde.pushDB("mydb", "myCollection", th5);

		// MongodbBrigde.pushDB("mydb", "baihat", baihat);

		// MongodbBrigde.update("mydb", "myCollection", "nickName", "FIVE",
		// "0351648123");

		// MongodbBrigde.delete("mydb", "myCollection", "0351648123");

//		 Document data = MongodbBrigde.getDocumentByPhone("mydb",
//		 "myCollection", stringInputBuffer.toString());
		 
		// System.out.println(data.toJson());
		// Member reTH = new Gson().fromJson(data.toJson(), Member.class);

		//MongodbBrigde.closeMongoConnection();
		
		
		
		return ch;
	}
}
