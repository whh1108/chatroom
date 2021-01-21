package cn.itcast.chatroom.web.websocket;

import cn.itcast.chatroom.entity.Message;
import cn.itcast.chatroom.entity.User;
import cn.itcast.utils.GsonUtils;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.*;

@Component("chatWebSocketHandler")
public class ChatWebSocketHandler implements WebSocketHandler {

    //在线用户的SOCKETsession(存储了所有的通信通道)
    public static final Map<String, WebSocketSession> USER_SOCKETSESSION_MAP;

    //存储所有的在线用户
    static {
        USER_SOCKETSESSION_MAP = new HashMap<String, WebSocketSession>();
    }

    /**
     * webscoket建立好链接之后的处理函数--连接建立后的准备工作
     */

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        //此处是要将连接的用户放入MAP，key是用户的id
        //获取当前连接对象
        User user = (User) webSocketSession.getAttributes().get("user");
        USER_SOCKETSESSION_MAP.put(user.getId(),webSocketSession);

        //握手后第一件事：群发上线消息
        //如果是客户端发消息，那msg信息在客户端封装，现在是服务器发上线消息，所以要自己设置msg
        Message msg = new Message();
        msg.setText("用户"+user.getNickname()+"上线");
        msg.setDate(new Date());
        List<String> userOnlineList = new ArrayList<>();
        //获取所有在线的WebSocketSession对象集合
        Set<Map.Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
        //将最新的所有的在线人列表放入消息对象的list集合中，用于页面显示
        for (Map.Entry<String, WebSocketSession> entry : entrySet) {
//            System.out.println("测试"+entry.getValue().getAttributes().get("user"));
            User user1 = (User)entry.getValue().getAttributes().get("user");
            String userOnline = user1.getNickname();
            userOnlineList.add(userOnline);
//            System.out.println(user1);
//            msg.getUserList().add(user1);
//            List<User> userList = new ArrayList<>();
//            userList.add(user1);
//            msg.setUserList(userList);
            msg.getUserList().add((User)entry.getValue().getAttributes().get("user"));
//            for(User usertmpe:msg.getUserList()){
//                System.out.println("usertmpe"+usertmpe);
//            }

//            System.out.println(msg.getUserList().add((User)entry.getValue().getAttributes().get("user")));

        }
        msg.setOnLineName(userOnlineList);

        //将消息转换为json
//        String jsonTmp = GsonUtils.toJson(msg);
//        System.out.println(jsonTmp);
//        JSONArray obj= JSONArray.fromObject(msg);
////        JSONObject jUser = obj.getJSONObject(4).getJSONObject("userList");
//        System.out.println("jUser"+obj);
//        JSONArray obj1= JSONArray.fromObject(GsonUtils.toJson(msg));
//        System.out.println("obj1"+obj1);
        TextMessage message = new TextMessage(GsonUtils.toJson(msg));
        System.out.println(message.getPayload().toString());
        //群发消息
        sendMessageToAll(message);


    }

    /**
     * 客户端发送服务器的消息时的处理函数，在这里收到消息之后可以分发消息
     */
    @Override
    //处理消息：当一个新的WebSocket到达的时候，会被调用（在客户端通过Websocket API发送的消息会经过这里，然后进行相应的处理）
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> message) throws Exception {
        //如果消息没有任何内容，则直接返回
        if(message.getPayloadLength()==0)return;
        //反序列化服务端收到的json消息
        Message msg = GsonUtils.fromJson(message.getPayload().toString(), Message.class);
        msg.setDate(new Date());
        //处理html的字符，转义：
        String text = msg.getText();
        //转换为HTML转义字符表示
        String htmlEscapeText = HtmlUtils.htmlEscape(text);
        msg.setText(htmlEscapeText);
        System.out.println("消息（可存数据库作为历史记录）:"+message.getPayload().toString());
        //判断是群发还是单发
        if(msg.getTo()==null||msg.getTo().equals("-1")){
            //群发
            sendMessageToAll(new TextMessage(GsonUtils.toJson(msg)));
        }else{
            //单发
            sendMessageToUser(msg.getTo(), new TextMessage(GsonUtils.toJson(msg)));
        }
    }

    /**
     * 消息传输过程中出现的异常处理函数
     * 处理传输错误：处理由底层WebSocket消息传输过程中发生的异常
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        // 记录日志，准备关闭连接
        System.out.println("Websocket异常断开:" + webSocketSession.getId() + "已经关闭");
        //一旦发生异常，强制用户下线，关闭session
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }

        //群发消息告知大家
        Message msg = new Message();
        msg.setDate(new Date());

        //获取异常的用户的会话中的用户编号
        User user=(User)webSocketSession.getAttributes().get("user");
        //获取所有的用户的会话
        Set<Map.Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
        //并查找出在线用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
        for (Map.Entry<String, WebSocketSession> entry : entrySet) {
            if(entry.getKey().equals(user.getId())){
                msg.setText("用户【"+user.getNickname()+"】已经下线");
                //清除在线会话
                USER_SOCKETSESSION_MAP.remove(entry.getKey());
                //记录日志：
                System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
                break;
            }
        }

        //并查找出在线用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
        for (Map.Entry<String, WebSocketSession> entry : entrySet) {
            msg.getUserList().add((User)entry.getValue().getAttributes().get("user"));
        }

        TextMessage message = new TextMessage(GsonUtils.toJson(msg));
        sendMessageToAll(message);


    }
    /**
     * websocket链接关闭的回调
     * 连接关闭后：一般是回收资源等
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        // 记录日志，准备关闭连接
        System.out.println("Websocket正常断开:" + webSocketSession.getId() + "已经关闭");

        //群发消息告知大家
        Message msg = new Message();
        msg.setDate(new Date());

        //获取异常的用户的会话中的用户编号
        User user=(User)webSocketSession.getAttributes().get("user");
        Set<Map.Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
        //并查找出在线用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
        for (Map.Entry<String, WebSocketSession> entry : entrySet) {
            if(entry.getKey().equals(user.getId())){
                //群发消息告知大家
                msg.setText("用户【"+user.getNickname()+"】已经下线");
                //清除在线会话
                USER_SOCKETSESSION_MAP.remove(entry.getKey());
                //记录日志：
                System.out.println("Socket会话已经移除:用户ID" + entry.getKey());
                break;
            }
        }

        //并查找出在线用户的WebSocketSession（会话），将其移除（不再对其发消息了。。）
        for (Map.Entry<String, WebSocketSession> entry : entrySet) {
            msg.getUserList().add((User)entry.getValue().getAttributes().get("user"));
        }

        TextMessage message = new TextMessage(GsonUtils.toJson(msg));
        sendMessageToAll(message);

    }

    /**
     * 是否支持处理拆分消息，返回true返回拆分消息
     */
    //是否支持部分消息：如果设置为true，那么一个大的或未知尺寸的消息将会被分割，并会收到多次消息（会通过多次调用方法handleMessage(WebSocketSession, WebSocketMessage). ）
    //如果分为多条消息，那么可以通过一个api：org.springframework.web.socket.WebSocketMessage.isLast() 是否是某条消息的最后一部分。
    //默认一般为false，消息不分割
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     *
     * 说明：给某个人发信息
     * @param id
     * @param message
     */
    private void sendMessageToUser(String id, TextMessage message) throws IOException{
        //获取到要接收消息的用户的session
        WebSocketSession webSocketSession = USER_SOCKETSESSION_MAP.get(id);
        if (webSocketSession != null && webSocketSession.isOpen()) {
            //发送消息
            webSocketSession.sendMessage(message);
        }
    }

    /**
     *
     * 说明：群发信息：给所有在线用户发送消息
     */
    private void sendMessageToAll(final TextMessage message){
        //对用户发送的消息内容进行转义

        //获取到所有在线用户的SocketSession对象
        Set<Map.Entry<String, WebSocketSession>> entrySet = USER_SOCKETSESSION_MAP.entrySet();
        for (Map.Entry<String, WebSocketSession> entry : entrySet) {
            //某用户的WebSocketSession
            final WebSocketSession webSocketSession = entry.getValue();
            //判断连接是否仍然打开的
            if(webSocketSession.isOpen()){
                //开启多线程发送消息（效率高）
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            if (webSocketSession.isOpen()) {
                                webSocketSession.sendMessage(message);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();

            }
        }
    }
}
