package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerAp {
	private static Map<SocketChannel, Queue<ByteBuffer>> pendingData = new HashMap<SocketChannel, Queue<ByteBuffer>>();

	// static Selector selector;
	private final static String HOSTNAME = "192.168.3.100";
	private final static int PORT_THSERVER = 1901;

	public static void main(String[] args) throws IOException {
		Selector selector = Selector.open();
		System.out.println("NonblockingSingleThreadedPollingServer open");
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.bind(new InetSocketAddress(HOSTNAME, PORT_THSERVER));

		ssc.configureBlocking(false);
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		stringInputBuffer = new StringBuffer();

		while (true) {
			selector.select();
			for (Iterator<SelectionKey> itKeys = selector.selectedKeys()
					.iterator(); itKeys.hasNext();) {
				SelectionKey key = itKeys.next();

				if (key.isValid()) {
					if (key.isAcceptable()) { // some one coneected to our
												// server coketchannel
						accept(key);
					} else if (key.isReadable()) {
						read(key);
					} else if (key.isWritable()) {
						write(key);
					}
				}
			}

		}
	}

	private static StringBuffer stringInputBuffer;
	private static StringBuffer stringOutputBuffer;

	private static void write(SelectionKey key) throws IOException {
		SocketChannel socket = (SocketChannel) key.channel();
		Queue<ByteBuffer> queue = pendingData.get(socket);
		ByteBuffer buf = ByteBuffer.allocate(4096);
		while ((buf = queue.peek()) != null) {
			stringInputBuffer.setLength(0);
			for (int i = 0; i < buf.limit(); i++) {
				// buf.put(i, (byte) Util.transmogrify(buf.get(i)));
				buf.put(i, (byte) buf.get(i));
			}
			stringInputBuffer.append(new String(buf.array(), "UTF-8").trim());
			System.out.println("write function: " + stringInputBuffer);

			stringOutputBuffer = Util.transmogrify(stringInputBuffer);

			buf.clear();
			buf.put(stringOutputBuffer.toString().getBytes(
					Charset.forName("UTF-8")));
			buf.flip();
			socket.write(buf);
			
			
			queue.poll();

			if (!buf.hasRemaining()) {
				queue.poll();
			} else {
				return;
			}
		}
		socket.register(key.selector(), SelectionKey.OP_READ);
	}

	private static void read(SelectionKey key) throws IOException {
		SocketChannel socket = (SocketChannel) key.channel();
		ByteBuffer buf = ByteBuffer.allocate(1024);
		int read = socket.read(buf);
		if (read == -1) {
			pendingData.remove(socket);
			return;
		}
		buf.rewind();
		buf.flip();
		for (int i = 0; i < buf.limit(); i++) {

			// buf.put(i, (byte) Util.transmogrify(buf.get(i)));
			buf.put(i, (byte) buf.get(i));
		}
		pendingData.get(socket).add(buf);

		buf.clear();
		socket.register(key.selector(), SelectionKey.OP_WRITE);
	}

	private static void accept(SelectionKey key) throws IOException {
		ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
		SocketChannel sc = ssc.accept();
		if (sc == null) {
			return;
		}

		sc.configureBlocking(false);

		sc.register(key.selector(), SelectionKey.OP_READ);

		pendingData.put(sc, new ConcurrentLinkedQueue<ByteBuffer>());

		// add pending data entry ... Dont foget

	}
}
