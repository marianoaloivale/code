package be.com.aloi.f4shared;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class SendTumblr {
	private static int sizeParalems = 10;

	public static void main(String[] args) throws ServiceException {

		if (args.length <= 1) {
			System.out
					.println("Envie alguma dessas opções:\n-photo \"Photo folder path\" \n-text \"Title\" \"message\"");
			System.exit(0);
		} else {
			File folder = new File(args[1]);
			if ("-photo".equalsIgnoreCase(args[0])) {
				sizeParalems = 10;
				sendFile(Type.photos, folder);
			} else if ("-video".equalsIgnoreCase(args[0])) {
				sizeParalems = 1;
				sendFile(Type.video, folder);
			}
		}

	}

	private static void sendFile(Type type, File folder)
			throws ServiceException {
		// TODO Auto-generated method stub

		BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				sizeParalems, sizeParalems + 5, 10, TimeUnit.HOURS, workQuee);
		for (File photo : folder.listFiles()) {
			if (photo.isDirectory()) {
				sendFile(type, photo);
			} else {

				threadPoolExecutor.execute(new sendPhoto(photo, type));

			}
		}
	}

}

enum Type {
	text, photos, video, link, conversation, audio, quote
}

class sendPhoto implements Runnable {

	private File photo;
	private Type type;

	public sendPhoto(File photo, Type type) {

		this.photo = photo;
		this.type = type;
	}

	private void sendForShared() throws ClientProtocolException, IOException {
		photo.delete();
	}

	@Override
	public void run() {
		try {

			if (sendPhotos(photo))
				sendForShared();

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean sendPhotos(File photo) throws IllegalStateException,
			IOException {
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpPost httppost = new HttpPost(
				"http://www.tumblr.com/api/write");
		System.out.println("Send File " + photo.getCanonicalPath());
		MultipartEntity reqEntity = null;
		switch (type) {
		case photos:
			reqEntity = getPhotoPost(photo);
			break;
		case video:
			reqEntity = getVideoPost(photo);
			break;
		}
		httppost.setEntity(reqEntity);

		System.out.println("executing request " + httppost.getRequestLine());
		HttpResponse response;
		response = httpclient.execute(httppost);

		HttpEntity resEntity = response.getEntity();

		System.out.println("----------------------------------------");
		System.out.print(response.getStatusLine());
		if (resEntity != null) {
			System.out.println(" | Response content length: "
					+ resEntity.getContentLength() + " Chunked?: "
					+ resEntity.isChunked());
			BufferedReader in = new BufferedReader(new InputStreamReader(
					resEntity.getContent()));

			String s2 = null;
			while ((s2 = in.readLine()) != null) {
				System.err.println(s2);
			}
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		return response.getStatusLine().getStatusCode() - 200 < 100;

	}

	private MultipartEntity getPhotoPost(File photo)
			throws UnsupportedEncodingException {
		MultipartEntity reqEntity = getDefaultFile(photo);
		reqEntity.addPart("type", new StringBody("photo"));
		return reqEntity;
	}

	private MultipartEntity getVideoPost(File photo)
			throws UnsupportedEncodingException {
		MultipartEntity reqEntity = getDefaultFile(photo);
		reqEntity.addPart("type", new StringBody("video"));
		return reqEntity;
	}

	private MultipartEntity getDefaultFile(File file)
			throws UnsupportedEncodingException {
		Map<String, String> s = new HashMap<String, String>(MapDefault());
		MultipartEntity reqEntity = new MultipartEntity();

		for (String key : s.keySet()) {
			reqEntity.addPart(key, new StringBody(s.get(key)));
		}
		reqEntity.addPart("data", new FileBody(file));
		return reqEntity;
	}

	private static Map<String, String> MapDefault() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("email", "alligatorlandrola@gmail.com");
		map.put("password", "qwer1234");
		map.put("generator", "API Suvivor");
		return map;
	}
}
