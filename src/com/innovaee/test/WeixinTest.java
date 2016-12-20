package com.innovaee.test;

import com.innovaee.po.AccessToken;
import com.innovaee.util.MessageUtil;
import com.innovaee.util.WeixinUtil;

import net.sf.json.JSONObject;

public class WeixinTest {
	public static void main(String[] args) {
		try {
			AccessToken token = WeixinUtil.getAccessToken();
				
//			MusicMessage musicMessage = JSONObject.fromObject(MessageUtil.initMusicMessage(toUserName, fromUserName));
//			int result = MessageUtil.musicMessageToXml(musicMessage);
			
			//����������Ϣ
//			System.out.println("Ʊ��"+token.getToken());
//			System.out.println("��Чʱ��"+token.getExpiresIn());
//			
//			String path = "D:/1.png";
//			String mediaId = WeixinUtil.upload(path, token.getToken(), "thumb");
//			System.out.println(mediaId);
			
			//����ͼƬ��Ϣ
//			System.out.println("Ʊ��"+token.getToken());
//			System.out.println("��Чʱ��"+token.getExpiresIn());
//			
//			String path = "D:/Windows.png";
//			String mediaId = WeixinUtil.upload(path, token.getToken(), "image");
//			System.out.println(mediaId);
			
			//�����˵�
			String menu = JSONObject.fromObject(WeixinUtil.initMenu()).toString();
			int result1 = WeixinUtil.deleteMenu(token.getToken());
			if(result1==0) {
				System.out.println("ɾ���˵��ɹ�");
			}else {
				System.out.println("�����룺"+result1);
			}
			int result = WeixinUtil.createMenu(token.getToken(), menu);
			if(result==0) {
				System.out.println("�����˵��ɹ�");
			}else {
				System.out.println("�����룺"+result);
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
