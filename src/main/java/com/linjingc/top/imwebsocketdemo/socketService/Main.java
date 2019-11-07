package com.linjingc.top.imwebsocketdemo.socketService;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class ExcuteServerInPut implements Runnable{//接收服务器的数据
    private Socket ToServer;
 
    ExcuteServerInPut(Socket ToServer){
        this.ToServer = ToServer;
    }
 
    @Override
    public void run() {
        try {
            Scanner scanner = new Scanner(ToServer.getInputStream());
               while (scanner.hasNext()){
                System.out.println(scanner.nextLine());
            }
            scanner.close();
            ToServer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 
class ExcuteServerOutPut implements Runnable{//向服务器发送数据
 
    private Socket Socket;
    ExcuteServerOutPut(Socket Socket){
        this.Socket = Socket;
    }
 
    @Override
    public void run() {
        try {
            PrintStream printStream = new PrintStream(Socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter("\n");
            System.out.println("*****************************************");
            System.out.println("***用户注册:useerName:同户名(仅限一次)***");
            System.out.println("***进入群聊:G:           退出群聊:exit***");
            System.out.println("***私聊:P-用户名         退出私聊:exit***");
            System.out.println("***********退出聊天室:byebye*************");
            while (true){
                if(scanner.hasNext()) {
                    String string = scanner.next();
                    printStream.println(string);
                    if ("byebye".equals(string)) {
                        System.out.println("退出！");
                        printStream.close();
                        scanner.close();
                        break;
                    }
                }
 
            }
 
            Socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 
 
public class Main {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 6655);
        ExcuteServerInPut excuteServerInPut = new ExcuteServerInPut(socket);
        ExcuteServerOutPut excuteServerOutPut = new ExcuteServerOutPut(socket);
        new Thread(excuteServerInPut).start();
        new Thread(excuteServerOutPut).start();
        }
}