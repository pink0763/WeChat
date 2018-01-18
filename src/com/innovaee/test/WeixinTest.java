package com.innovaee.test;

import com.innovaee.po.AccessToken;
import com.innovaee.util.MessageUtil;
import com.innovaee.util.WeixinUtil;

import net.sf.json.JSONObject;

public class WeixinTest {
	public static void main(String[] args) {
		try {
			AccessToken token = WeixinUtil.getAccessToken();
			
			//test
				
//			MusicMessage musicMessage = JSONObject.fromObject(MessageUtil.initMusicMessage(toUserName, fromUserName));
//			int result = MessageUtil.musicMessageToXml(musicMessage);
			
			//创建音乐消息
//			System.out.println("票据"+token.getToken());
//			System.out.println("有效时间"+token.getExpiresIn());
//			
//			String path = "D:/1.png";
//			String mediaId = WeixinUtil.upload(path, token.getToken(), "thumb");
//			System.out.println(mediaId);
			
			//创建图片消息
//			System.out.println("票据"+token.getToken());
//			System.out.println("有效时间"+token.getExpiresIn());
//			
//			String path = "D:/Windows.png";
//			String mediaId = WeixinUtil.upload(path, token.getToken(), "image");
//			System.out.println(mediaId);
			
			//创建菜单
			String menu = JSONObject.fromObject(WeixinUtil.initMenu()).toString();
			int result1 = WeixinUtil.deleteMenu(token.getToken());
			if(result1==0) {
				System.out.println("删除菜单成功");
			}else {
				System.out.println("错误码："+result1);
			}
			int result = WeixinUtil.createMenu(token.getToken(), menu);
			if(result==0) {
				System.out.println("创建菜单成功");
			}else {
				System.out.println("错误码："+result);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
