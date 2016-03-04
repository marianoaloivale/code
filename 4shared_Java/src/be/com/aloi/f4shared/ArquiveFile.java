package be.com.aloi.f4shared;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import com.pmstation.shared.soap.client.ApiException;
import com.pmstation.shared.soap.client.DesktopAppJax2;


public class ArquiveFile extends Thread implements Runnable {

	private static final String LOGIN = Send4Shared.getInstance().LOGIN;
	private static final String PASSWORD = Send4Shared.PASSWORD;

	File photo;
	DesktopAppJax2 da;

	public ArquiveFile(File photo, DesktopAppJax2 da) {
		super();
		this.photo = photo;
		this.da = da;
	}

	public void run() {
		try {

			requestSendFile(photo, da);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void requestSendFile(File photo, DesktopAppJax2 da)
			throws ClientProtocolException, IOException {

		Long sizeFree = 0l;
		try {
			sizeFree = da.getFreeSpace(LOGIN, PASSWORD);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Long sizeFile = photo.length();
		Send4Shared.setAllSize(Send4Shared.getAllSize() + sizeFile);
		if (sizeFree > sizeFile) {
			System.out.println("Size free: " + formatMB(sizeFree)
					+ " total uploaded: " + formatMB(Send4Shared.getAllSize())
					+ " file size " + formatMB(sizeFile));
		} else {
			System.out.println("No possible Upload file:" + photo.getName()
					+ " size free: " + formatMB(sizeFree) + " file size "
					+ formatMB(sizeFile) + " total uploaded: "
					+ formatMB(Send4Shared.getAllSize()));
			return;
		}
		System.out.println("Wait " + getTimeBySize(sizeFile) + " with net Speed "
				+ Send4Shared.getNetSpeed() + " KB/s");

		int dcId = 0;
		String sessKey = null;
		long newId = -1;
		String md5 = md5Digest(photo);

		try {
			dcId = (int) da.getNewFileDataCenter(LOGIN, PASSWORD);
			sessKey = da.createUploadSessionKey(LOGIN, PASSWORD, -1);
			String urlUpload = da.getUploadFormUrl(dcId, sessKey);
			String filename = genName(photo.getName());
			newId = da.uploadStartFile(LOGIN, PASSWORD, -1, filename,
					photo.length());
			if (newId < 0) {
				System.out.println("Unable to reserve file id for file");
				return;
			}
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(urlUpload);
		MultipartEntity me = getMultiPartoPost(photo, newId);
		post.setEntity(me);

		long startTime = System.currentTimeMillis();
		HttpResponse resp = client.execute(post);
		float Seconds = (System.currentTimeMillis() - startTime) / 1000;
		Send4Shared.setNetSpeed(sizeFile / 1024 / Seconds);

		responseWrite(resp);
		String res = da.uploadFinishFile(LOGIN, PASSWORD, newId, md5);
		if (res != null && !res.isEmpty()) {
			System.err.println("Failed: " + res);
		} else {
			System.out.println("File " + photo.getName() + " uploaded as "
					+ filename + " at count "+LOGIN);
			System.out.printf("Speed: %1$s in %2$s seconds.",Send4Shared.getNetSpeed(),Seconds);
			photo.delete();
		}
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getTimeBySize(long size) {
		float seconds = size / 1024 / Send4Shared.getNetSpeed();
		return getTimeBySecond((long) (seconds*1000));
	}

	public static String getTimeBySecond(long seconds) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		
		seconds += (TimeZone.getDefault().getRawOffset())*-1;
		
		return df.format(new Date(seconds));
	}

	public static String formatMB(Long sizeFile) {
		DecimalFormat format = new DecimalFormat("#0.00");
		if (sizeFile > 1073741824) {
			return format.format(new Double(sizeFile) / 1073741824) + " GB";
		} else if (sizeFile > 1048576) {
			return format.format(new Double(sizeFile) / 1048576) + " MB";
		} else if (sizeFile > 1024) {
			return format.format(new Double(sizeFile) / 1024) + " KB";
		}

		return format.format(sizeFile) + " BITS";
	}

	private String genName(String file) {
		Random r = new Random();
		char[] alphabet = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
				'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
				'u', 'v', 'w', 'x', 'y', 'z' };
		char[] res = new char[14];
		for (int i = 0; i < res.length; i++) {
			res[i] = alphabet[r.nextInt(26)];
		}
		int extensionpossition = file.lastIndexOf(".");
		return new String(res)
				+ (extensionpossition > 0 ? file.substring(extensionpossition)
						: ".outtype");
	}

	private void responseWrite(HttpResponse response) throws IOException {
		HttpEntity resEntity = response.getEntity();

		System.out.println("\n----------------------------------------");
		System.out.println(response.getStatusLine());
		if (resEntity != null) {
			System.out.println("Response content length: "
					+ resEntity.getContentLength());
			System.out.println("Chunked?: " + resEntity.isChunked());
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}
		System.out.println("----------------------------------------\n");
	}

	private String md5Digest(File photo) throws FileNotFoundException,
			IOException {

		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}

		BufferedInputStream br = new BufferedInputStream(new FileInputStream(
				photo));
		byte[] dataBytes = new byte[1024];

		int nread = 0;
		while ((nread = br.read(dataBytes)) != -1) {
			md5.update(dataBytes, 0, nread);
		}
		;
		byte[] mdbytes = md5.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
					.substring(1));
		}
		br.close();
		return sb.toString();
	}

	private MultipartEntity getMultiPartoPost(File photo, long newId)
			throws UnsupportedEncodingException {
		MultipartEntity me = new MultipartEntity();
		StringBody rfid = new StringBody("" + newId);
		StringBody rfb = new StringBody("" + 0);

		me.addPart("resumableFileId", rfid);
		me.addPart("resumableFirstByte", rfb);
		me.addPart("FilePart", new FileBody(photo));
		return me;
	}

}