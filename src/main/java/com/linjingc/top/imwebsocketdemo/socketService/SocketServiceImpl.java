package com.linjingc.top.imwebsocketdemo.socketService;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Administrator
 */
public class SocketServiceImpl {


	public static void main(String[] args) {
		try {
			//最多容纳100个客户端聊天
			ExecutorService executorService = Executors.newFixedThreadPool(100);
			//监听6655号端口
			ServerSocket serverSocket = new ServerSocket(6655);
			for (int i = 0; i < 100; i++) {
				Socket client = serverSocket.accept();
				System.out.println("有新的用户连接 " + client.getInetAddress() + client.getPort());
				executorService.execute(new ExecuteClientThread(client));
			}
			executorService.shutdown();
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
