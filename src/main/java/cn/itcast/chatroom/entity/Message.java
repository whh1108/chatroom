package cn.itcast.chatroom.entity;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DTO类，用来存放聊天的消息
 * @author BoBo
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

	//发送者
	@Expose
	public String from;
	//发送者名称
	@Expose
	public String fromName;
	//接收者
	@Expose
	public String to;
	//发送的文本
	@Expose
	public String text;

	@Override
	public String toString() {
		return "Message{" +
				"userList=" + userList +
				'}';
	}

	//发送日期
	@Expose
	public Date date;
	
	//在线用户列表
	@Expose
	List<User> userList = new ArrayList<>();

	@Expose
	List<String> onLineName = new ArrayList<>();
	
	public List<User> getUserList() {
		return userList;
	}
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getFromName() {
		return fromName;
	}
	public void setFromName(String fromName) {
		this.fromName = fromName;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	

}
