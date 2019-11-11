package com.linjingc.top.imwebsocketdemo.socketService;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * (服务端推送消息)线程
 */
public class ExecuteClientThread implements Runnable {

    private static Map<String, Socket> clientMap = new ConcurrentHashMap<>();//存储所有的用户信息
    private Socket client;//每一个服务器线程对应一个客户端线程


    ExecuteClientThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //防止一个客户端多次注册所做的标记位置
        boolean Flag = true;
        try {
            //服务器向用户输出一些提示信息
            PrintStream PrintToCilent = new PrintStream(client.getOutputStream());


            Scanner scanner = new Scanner(client.getInputStream());
            //用户外部的输入信息
            String str = null;
            while (true) {
                if (scanner.hasNext()) {
                    //外部的用户输出
                    str = scanner.next();
                    //排除特殊符号
                    Pattern pattern = Pattern.compile("\r");
                    Matcher matcher = pattern.matcher(str);
                    str = matcher.replaceAll("");

                    if (str.startsWith("userName")) {
                        String userName = str.split(":")[1];
                        userRegist(userName, client, Flag);
                        Flag = false;
                    }
                    // 群聊流程
                    else if (str.startsWith("G:")) {
                        PrintToCilent.println("已进入群聊模式！");
                        groupChat(scanner, client);
                    }
                    // 私聊流程
                    else if (str.startsWith("P")) {//模式
                        String userName = str.split("-")[1];
                        PrintToCilent.println("已经进入与" + userName + "的私聊");

                        privateChat(scanner, userName);
                    }
                    // 用户退出
                    else if (str.contains("byebye")) {
                        String userName = null;
                        for (String getKey : clientMap.keySet()) {
                            if (clientMap.get(getKey).equals(client)) {
                                userName = getKey;
                            }
                        }

                        System.out.println("用户" + userName + "下线了..");
                        clientMap.remove(userName);//将此实例从map中移除
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册用户
     * @param userName
     * @param client
     * @param Flag
     * @throws IOException
     */
    private void userRegist(String userName, Socket client, boolean Flag) throws IOException {
        //服务器向用户输出一些提示信息
        PrintStream PrintToCilent = new PrintStream(client.getOutputStream());
        if (Flag) {
            System.out.println("用户" + userName + "上线了！");

            //把用户加入储存map
            clientMap.put(userName, client);
            System.out.println("当前群聊人数为" + (clientMap.size()) + "人");
            PrintToCilent.println("注册成功！");
        } else {
            PrintToCilent.println("警告:一个客户端只能注册一个用户！");
        }
    }


    /**
     * 聊天室消息
     * @param scanner
     * @param client
     * @throws IOException
     */
    private void groupChat(Scanner scanner, Socket client) throws IOException {
        // 取出clientMap中所有客户端Socket，然后遍历一遍
        // 分别取得每个Socket的输出流向每个客户端输出
        //在群聊的时候服务器向客户端发送数据
        PrintStream PrintToClient = new PrintStream(client.getOutputStream());
        boolean ExitFlag = false;

        Set<Map.Entry<String, Socket>> entrySet =
                clientMap.entrySet();

        String userName = null;
        //获得:是哪个用户说的话
        for (Map.Entry<String, Socket> socketEntry : entrySet) {
            if (socketEntry.getValue() == client) {
                //发出信息的用户
                userName = socketEntry.getKey();
            }
        }
        String msg = null;

        while (true) {
            if (scanner.hasNext()) {
                msg = scanner.next();
                //如果用户退出了
                if ("exit".equals(msg)) {
                    for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                        new PrintStream(stringSocketEntry.getValue().getOutputStream(), true).println("用户" + userName + "刚刚退出了群聊！！");//给所有人发退出群聊的消息
                    }
                    return;
                }

//遍历用户的map，获取所有用户的Socket
                for (Map.Entry<String, Socket> stringSocketEntry : entrySet) {
                    try {
                        Socket socket = stringSocketEntry.getValue();
                        PrintStream ps = new PrintStream(socket.getOutputStream(), true);
                        //给每个用户发消息
                        ps.println("群聊:用户" + userName + "说: " + msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }

    /**
     * 私聊消息
     * @param scanner
     * @param privatepeopleName
     * @throws IOException
     */
    private void privateChat(Scanner scanner, String privatepeopleName) throws IOException {

        Socket privateUser = clientMap.get(privatepeopleName);
        //拿到私聊对象的输出流
        PrintStream ps = new PrintStream(privateUser.getOutputStream());
        //拿到当前客户端的输出流
        PrintStream PrintToClient = new PrintStream(client.getOutputStream());
        String Message = null;
        String MyName = null;
        Set<Map.Entry<String, Socket>> set = clientMap.entrySet();
        for (Map.Entry<String, Socket> value : set) {
            if (value.getValue() == client) {
                MyName = value.getKey();
                break;
            }
        }

        while (true) {
            if (scanner.hasNext()) {
                Message = scanner.next();
                //如果用户输入了退出
                if ("exit".equals(Message)) {
                    PrintToClient.println("已退出和" + privatepeopleName + "的私聊");
                    ps.println("对方已经退出了私聊");
                    break;
                }
                //如果用户没有退出，向私聊对象发送消息
                ps.println(MyName + "说" + Message);
            }
        }
    }
}
