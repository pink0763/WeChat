package com.innovaee.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.innovaee.menu.Button;
import com.innovaee.menu.ClickButton;
import com.innovaee.menu.Menu;
import com.innovaee.menu.ViewButton;
import com.innovaee.po.AccessToken;
import com.innovaee.trans.Data;
import com.innovaee.trans.Parts;
import com.innovaee.trans.Symbols;
import com.innovaee.trans.TransResult;

/**
 * ΢�Ź�����
 * @author Stephen
 *
 */
public class WeixinUtil {
	//����΢�Ź��ں��ṩ��appID��appsecret
	private static final String APPID = "wx67e949ca7108ad7a";
	private static final String APPSECRET = "9c2951214a775291e9b60fbc0e3e2224";
	
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	
	private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";
	
	private static final String CREATE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	
	private static final String QUERY_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=ACCESS_TOKEN";
	
	private static final String DELETE_MENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
	/**
	 * get����
	 * @param url
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject doGetStr(String url) throws ParseException, IOException{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		JSONObject jsonObject = null;
		HttpResponse httpResponse = client.execute(httpGet);
		HttpEntity entity = httpResponse.getEntity();
		if(entity != null){
			String result = EntityUtils.toString(entity,"UTF-8");
			jsonObject = JSONObject.fromObject(result);
		}
		return jsonObject;
	}
	
	/**
	 * POST����
	 * @param url
	 * @param outStr
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static JSONObject doPostStr(String url,String outStr) throws ParseException, IOException{
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(url);
		JSONObject jsonObject = null;
		httpost.setEntity(new StringEntity(outStr,"UTF-8"));
		HttpResponse response = client.execute(httpost);
		String result = EntityUtils.toString(response.getEntity(),"UTF-8");
		jsonObject = JSONObject.fromObject(result);
		return jsonObject;
	}
	
	/**
	 * �ļ��ϴ�
	 * @param filePath
	 * @param accessToken
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws KeyManagementException
	 */
	public static String upload(String filePath, String accessToken,String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("�ļ�������");
		}

		String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE",type);
		
		URL urlObj = new URL(url);
		//����
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		con.setRequestMethod("POST"); 
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); 

		//��������ͷ��Ϣ
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		//���ñ߽�
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		//��������
		OutputStream out = new DataOutputStream(con.getOutputStream());
		//�����ͷ
		out.write(head);

		//�ļ����Ĳ���
		//���ļ������ļ��ķ�ʽ ���뵽url��
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		//��β����
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//����������ݷָ���

		out.write(foot);

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			//����BufferedReader����������ȡURL����Ӧ
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
		System.out.println(jsonObj);
		String typeName = "media_id";
		if(!"image".equals(type)){
			typeName = type + "_media_id";
		}
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}
	
	/**
	 * ��ȡaccessToken
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static AccessToken getAccessToken() throws ParseException, IOException{
		AccessToken token = new AccessToken();
		String url = ACCESS_TOKEN_URL.replace("APPID", APPID).replace("APPSECRET", APPSECRET);
		JSONObject jsonObject = doGetStr(url);
		if(jsonObject!=null){
			token.setToken(jsonObject.getString("access_token"));
			token.setExpiresIn(jsonObject.getInt("expires_in"));
		}
		return token;
	}
	
	/**
	 * ��װ�˵�
	 * @return
	 */
	public static Menu initMenu(){
		Menu menu = new Menu();
		
		ViewButton button11 = new ViewButton();
		button11.setName("ѧԱ��ѯ");
		button11.setType("view");
		button11.setUrl("http://192.168.3.48:8080/hpeu/mobile/train/info");
		
		ViewButton button12 = new ViewButton();
		button12.setName("֤���ѯ");
		button12.setType("view");
		button12.setUrl("http://192.168.3.48:8080/hpeu/mobile/certificate/index?init=%2Fcertificate%2Fsearch");
		
		Button button1 = new Button();
		button1.setName("��ѯ");
		button1.setSub_button(new Button[]{button11,button12});
		
		ViewButton button21 = new ViewButton();
		button21.setName("��ʦ����");
		button21.setType("view");
		button21.setUrl("http://192.168.3.48:8080/hpeu/mobile/lecturer/info/0");
		
		ViewButton button22 = new ViewButton();
		button22.setName("�γ̽���");
		button22.setType("view");
		button22.setUrl("http://192.168.3.48:8080/hpeu/mobile/certificate/index?init=%2Fcourse%2Fintroduce");
		
		Button button2 = new Button();
		button2.setName("��ѵ��֤");
		button2.setSub_button(new Button[]{button21,button22});
		
		ViewButton button31 = new ViewButton();
		button31.setName("���ⷴ��");
		button31.setType("view");
		button31.setUrl("http://192.168.3.48:8080/hpeu/mobile/certificate/index?init=%2Ffeedback");
		
		ViewButton button32 = new ViewButton();
		button32.setName("�γ̱���");
		button32.setType("view");
		button32.setUrl("http://192.168.3.48:8080/hpeu/mobile/entry/index");
		
		ViewButton button33 = new ViewButton();
		button33.setName("��������");
		button33.setType("view");
		button33.setUrl("http://mp.weixin.qq.com/s?__biz=MzIyNzM3OTE5OQ==&mid=2247483814&idx=1&sn=1cc36d24c9830ba28f9f8448a86a169c&chksm=e8635336df14da200785f0b0a8ee12e8f2b349dd8da07d34a23e2ec6f44b7405ba3ffec954ad&mpshare=1&scene=1&srcid=1026EWKPNyRw279rAPwWezao&from=singlemessage&isappinstalled=0#wechat_redirect");
		
//		ViewButton button34 = new ViewButton();
//		button34.setName("��������");
//		button34.setType("view");
//		button34.setUrl("http://www.innovaee.com");
		
		Button button3 = new Button();
		button3.setName("��ϵ����");
		button3.setSub_button(new Button[]{button31,button32,button33});
		
		menu.setButton(new Button[]{button1,button2,button3});
		return menu;
	}
	
	public static int createMenu(String token,String menu) throws ParseException, IOException{
		int result = 0;
		String url = CREATE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doPostStr(url, menu);
		if(jsonObject != null){
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
	
	public static JSONObject queryMenu(String token) throws ParseException, IOException{
		String url = QUERY_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		return jsonObject;
	}
	
	public static int deleteMenu(String token) throws ParseException, IOException{
		String url = DELETE_MENU_URL.replace("ACCESS_TOKEN", token);
		JSONObject jsonObject = doGetStr(url);
		int result = 0;
		if(jsonObject != null){
			result = jsonObject.getInt("errcode");
		}
		return result;
	}
	
//	public static String translate(String source) throws ParseException, IOException{
//		String url = "http://openapi.baidu.com/public/2.0/translate/dict/simple?client_id=jNg0LPSBe691Il0CG5MwDupw&q=KEYWORD&from=auto&to=auto";
//		url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
//		JSONObject jsonObject = doGetStr(url);
//		String errno = jsonObject.getString("errno");
//		Object obj = jsonObject.get("data");
//		StringBuffer dst = new StringBuffer();
//		if("0".equals(errno) && !"[]".equals(obj.toString())){
//			TransResult transResult = (TransResult) JSONObject.toBean(jsonObject, TransResult.class);
//			Data data = transResult.getData();
//			Symbols symbols = data.getSymbols()[0];
//			String phzh = symbols.getPh_zh()==null ? "" : "����ƴ����"+symbols.getPh_zh()+"\n";
//			String phen = symbols.getPh_en()==null ? "" : "ӢʽӢ�꣺"+symbols.getPh_en()+"\n";
//			String pham = symbols.getPh_am()==null ? "" : "��ʽӢ�꣺"+symbols.getPh_am()+"\n";
//			dst.append(phzh+phen+pham);
//			
//			Parts[] parts = symbols.getParts();
//			String pat = null;
//			for(Parts part : parts){
//				pat = (part.getPart()!=null && !"".equals(part.getPart())) ? "["+part.getPart()+"]" : "";
//				String[] means = part.getMeans();
//				dst.append(pat);
//				for(String mean : means){
//					dst.append(mean+";");
//				}
//			}
//		}else{
//			dst.append(translateFull(source));
//		}
//		return dst.toString();
//	}
//	
//	public static String translateFull(String source) throws ParseException, IOException{
//		String url = "http://openapi.baidu.com/public/2.0/bmt/translate?client_id=jNg0LPSBe691Il0CG5MwDupw&q=KEYWORD&from=auto&to=auto";
//		url = url.replace("KEYWORD", URLEncoder.encode(source, "UTF-8"));
//		JSONObject jsonObject = doGetStr(url);
//		StringBuffer dst = new StringBuffer();
//		List<Map> list = (List<Map>) jsonObject.get("trans_result");
//		for(Map map : list){
//			dst.append(map.get("dst"));
//		}
//		return dst.toString();
//	}
}
