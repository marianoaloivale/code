package br.com.aloi.shared;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;

import be.com.aloi.f4shared.ArquiveFile;

public class DownloadImgs {

	public static void main(String[] args) {

		new DownloadImgs().inicializar();

	}
//	
//	public static void main(String[] args) {
//		new DownloadImgs().defaultImg("4472937669|all.igatorlandrola@gmail.com",new File("oi.png"));
//	}

	void inicializar() {
		Long start = System.currentTimeMillis();
		FileReader reader = null;
		try {
			reader = new FileReader("saida.csv");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader readerb = new BufferedReader(reader);

		ArrayList<MaloiItem3> items = new ArrayList<MaloiItem3>();
		String line;
		try {
			while ((line = readerb.readLine()) != null) {
				String[] coluns = line.split("\\|");
				items.add(new MaloiItem3(coluns[0], coluns[4], coluns[6]));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
		int sizeParalems = 20;
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				sizeParalems, sizeParalems + 5, 5, TimeUnit.MINUTES, workQuee);

		for (final MaloiItem3 maloiItem3 : items) {
			final File fileName = new File(String.format(
					"/opt/maloi/photos/%1$s_%2$s",
					maloiItem3.getLogin(), maloiItem3.getId()));
			if(fileName.exists() || maloiItem3.getImage() == null){
				continue;
			}
			threadPoolExecutor.execute(new Runnable() {

				@Override
				public void run() {
					try {
							String targetImg = maloiItem3.getImage();
							if (targetImg.isEmpty()
									|| "null".equals(targetImg.toLowerCase()))
							{
								System.out.printf("Id %1$S not have img\n",maloiItem3.getId());
								defaultImg(maloiItem3,fileName);
							}
							
			
							else {
								byte[] dataBytes = new byte[1024];
			
								int nread = 0;
								InputStream i = null;
								
								i = getRequestStream(targetImg);
								
								if(i==null){
									defaultImg(maloiItem3,fileName);
								}else{							
									OutputStream o = new FileOutputStream(fileName);
									while ((nread = i.read(dataBytes)) != -1) {
										o.write(dataBytes, 0, nread);
									}
	
									o.close();
								}
								
								i.close();
							}
					} catch (Exception e) {
						System.err.println(String.format("%s|%s|%s|%s",
								e.getMessage(), maloiItem3.getImage(),maloiItem3.getId(),maloiItem3.getLogin()));
						defaultImg(maloiItem3,fileName);
					}

				}

				
			});
		}

		threadPoolExecutor.shutdown();
		try {
			while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
				long qtdFiles = new File("/opt/maloi/photos").listFiles().length;
				System.out.printf("\r%1$s/%3$s photos %2$s ", qtdFiles,ArquiveFile.getTimeBySecond(System.currentTimeMillis()-start),items.size());
			}
			System.out.println();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("IMG Downloaded");

	}
	
	private void defaultImg(MaloiItem3 maloiItem3, File fileName) {
		String text = String.format("%s|%s", maloiItem3.getId(),maloiItem3.getLogin());
		BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.PLAIN, 22);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
//        int width = fm.stringWidth(text);
//        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(200, 150, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(Color.BLACK);
        int i = 0;
        for (String s : text.split("\\|")) {
        	for (String n : s.split("@")) {
        		g2d.drawString(n, 0, fm.getAscent()+((i++)*50));
			}
		}
        
        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File(fileName.getAbsolutePath()));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
	}

	private InputStream getRequestStream(String urlUpload)
			throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		client.setCookieStore(cookieStore());
		HttpGet httpGet = new HttpGet(urlUpload);

		HttpResponse resp = client.execute(httpGet);

		HttpEntity resEntity = resp.getEntity();

		if(resEntity.getContentLength() < 1024){
			return null;
		}
		
		return resEntity.getContent();
	}

	private BasicCookieStore cookieStore() {
		BasicCookieStore cookieStore = new BasicCookieStore();
		cookieStore.addCookie(addCookie("__qca","P0-647603529-1418166893631", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" 4langcookie","en", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" oauth_session","", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" Login","501922771", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" Password","7a0977b097a7b70dc19c0036fdf775e6", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" ulin","true", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" dlpvc110439","N", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" __utma","210074320.981975735.1418166894.1418253238.1418253238.1", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" __utmc","210074320", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" __utmz","210074320.1418253238.1.1.utmcsr=4shared.com|utmccn=(referral)|utmcmd=referral|utmcct=/video/kGeRF67zce/csco_640_461_29.htm", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" sr.vw","1", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" viewMode121247015","2", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" day1host","h", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" cd1v","fmceqEbalccejZceD9balPbaJLceaYba3Mce0_baNycef9ceBcba_wce6AceWibamTcesVcePPce8Zce", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" WWW_JSESSIONID","CBC357899A6DE88022D078C8CAC30D28.dc571", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" _ga","GA1.2.981975735.1418166894", "dc227.4shared.com", "/"));
		cookieStore.addCookie(addCookie(" _gat","1", "dc227.4shared.com", "/"));
		 
		return cookieStore;
	}
	private Cookie addCookie(String nome, String valor, String url, String path) {
		BasicClientCookie cookie = new BasicClientCookie(nome, valor);
		//cookie.setDomain(url);
		//cookie.setPath(path);
		return cookie;
	}
}

class MaloiItem3 {

	private String login;
	private String id;
	private String image;

	public MaloiItem3(String login, String id, String image) {
		super();
		this.login = login;
		this.id = id;
		this.image = image;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

}
