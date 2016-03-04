package br.com.aloi.shared;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Scanner;

import javax.xml.rpc.ServiceException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.pmstation.shared.soap.client.DesktopAppJax2;
import com.pmstation.shared.soap.client.DesktopAppJax2Service;

import be.com.aloi.f4shared.ArquiveFile;


public class XVideo24Shared {

	private static final String MESSAGE_GETPATH = "Give me the URL : ";

	public static void main(String[] args) throws Throwable {
		new XVideo24Shared().initiallize(args);
	}

	private DesktopAppJax2 desktopAppJax2;

	public XVideo24Shared() throws ServiceException, RemoteException {
		desktopAppJax2 = new DesktopAppJax2Service()
				.getDesktopAppJax2Port();

		if (!desktopAppJax2.hasRightUpload()) {
			System.out.println("Upload is currently disabled");
			return;
		}
	}

	private void initiallize(String[] args) throws IOException,
			ServiceException, InterruptedException {

		Console c = System.console();

		String urlPath = null;

		if (args.length > 0) {
			urlPath = args[0];

		} else {
			if (c != null)
				urlPath = c.readLine(MESSAGE_GETPATH);
			else {
				Scanner in = new Scanner(System.in);
				System.out.print(MESSAGE_GETPATH);
				urlPath = in.nextLine();
				in.close();
			}
		}

		for (String path : urlPath.split("\\|")) {
			sendPathGet(path);
		}
		

	}

	private void sendPathGet(String urlPath) throws IOException, InterruptedException {
		File video = getFileVideo(urlPath);

		ArquiveFile af = new ArquiveFile(video, desktopAppJax2);

		af.start();
		Thread.sleep(1000);
		Long start = System.currentTimeMillis();
		String sizeFormated = ArquiveFile.formatMB(video.length());
		while (af.isAlive()) {
			System.out.printf("\rUploading %1$s [%2$s] %3$s ", video.getName(),
					sizeFormated, 
					ArquiveFile.getTimeBySecond(((System.currentTimeMillis())-start)));


			Thread.sleep(1000);
		}

		System.out.println();

		video.deleteOnExit();
		
	}

	private File getFileVideo(String urlPath) throws IOException {
		ProviderMovie provider = geturlFlv(urlPath);
		String urlFlv = provider.getUrlMovie(urlPath+"");
		
		File result = new File(provider.getUnifiedName(urlFlv));
		makeTheFile(result, urlFlv);
		return result;
	}

	private void makeTheFile(final File result, String urlFlv) throws IOException {

		final Stream st = getRequestStream(urlFlv);

		if (result.exists() && result.length() == st.size)
			return;

		if (result.exists() )
		result.delete();
		
		final InputStream is = st.InputStream;
		Long start = System.currentTimeMillis();
		Thread t = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Files.copy(is, result.toPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				
			}
		});
		
		t.start();
		
		final String sizeFormated = ArquiveFile.formatMB(st.size);
		while (t.isAlive()) {
			System.out.printf("\r%1$s seconds still running - %2$s size file ",
					ArquiveFile.getTimeBySecond(System.currentTimeMillis()-start), 
					sizeFormated);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println();
		System.out.println("Speed download: "+(((float)st.size)/1024)/((new Date().getTime()-start)/1000));
		

	}

	private Stream getRequestStream(String urlUpload)
			throws ClientProtocolException, IOException {
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(urlUpload);

		HttpResponse resp = client.execute(httpGet);

		HttpEntity resEntity = resp.getEntity();

		return new Stream(resp.getFirstHeader("Content-Length").getValue(),
				resEntity.getContent());
	}

	class Stream {
		long size;
		InputStream InputStream;

		public Stream(String size, java.io.InputStream inputStream) {
			super();
			this.size = Long.parseLong(size);
			InputStream = inputStream;
		}
	}

	private ProviderMovie geturlFlv(String urlPath) throws IOException {
		URL url = new URL(urlPath);
		
		ProviderMovie movie;
		if(url.getHost().contains("xvideo"))
			movie = new XVideoProvider();
		else if(url.getHost().contains("porn69"))
			movie = new Porn69Provider();
		else
			return null;

		return movie;
		
		
	}

}



/*
 * //URL url = new URL(
 * "http://www.xvideos.com/video3181731/casadas_putas_dando_o_cu_pra_seus_amantes_-_compilacao"
 * ); URL url = new URL("http://upload.xvideos.com/account"); Connection
 * connection = Jsoup.connect(url.toString());
 * 
 * Map<String, String> data = new HashMap<>(); data.put("login",
 * "alligatorlandrola@gmail.com"); data.put("password", "qwer1234");
 * data.put("referer",
 * "http://www.xvideos.com/video3181731/casadas_putas_dando_o_cu_pra_seus_amantes_-_compilacao"
 * ); connection.data(data ); cookiePopulate(connection,url); Document doc =
 * connection.post();
 * 
 * System.out.println(doc.outerHtml());
 * 
 * private void cookiePopulate(Connection connection, URL url) {
 * connection.cookie("SEARCHPREF","relevance%7Call%7Callduration");
 * connection.cookie(" TAGPREF","rating%7Call%7Callduration");
 * connection.cookie(" __utmt","1"); connection.cookie(" __utma",
 * "263005381.933038105.1411944145.1419210147.1419696828.7");
 * connection.cookie(" __utmb","263005381.1.10.1419696828");
 * connection.cookie(" __utmc","263005381"); connection.cookie(" __utmz",
 * "263005381.1411944145.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)");
 * 
 * }
 */
