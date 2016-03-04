package be.com.aloi.f4shared;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import org.apache.http.client.ClientProtocolException;

import com.pmstation.shared.soap.client.ApiException;
import com.pmstation.shared.soap.client.DesktopAppJax2;
import com.pmstation.shared.soap.client.DesktopAppJax2Service;

import br.com.aloi.shared.Admin4SharedCounts;

public class Send4Shared {



	static final String PASSWORD = "qwer1234";
	public String LOGIN = "alligator_landrola@gmail.com";

	private static float netSpeed = 300;
	private static long allSize = 0;
	private static int sizeParalems = 1;
	private static String startFolder = "/opt/maloi/d";
	private static boolean subfolderEntry;

	public synchronized static float getNetSpeed() {
		return netSpeed;
	}

	public synchronized static void setNetSpeed(float netSpeedi) {
		netSpeed = netSpeedi;
	}

	private DesktopAppJax2 c;
	
	private static Send4Shared instance = null;
	
	public static Send4Shared getInstance() {
		try {
			return instance != null ? instance : (instance = new Send4Shared());
		} catch (RemoteException | ServiceException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return instance;
	}

	private Send4Shared() throws ServiceException, RemoteException {
		c = new DesktopAppJax2Service().getDesktopAppJax2Port();
		if (!c.hasRightUpload()) {
			System.out.println("Upload is currently disabled");
			return;
		}
		LOGIN = getBetterLogin();
	}

	public String getBetterLogin() throws RemoteException {
		
		String result = Admin4SharedCounts.LOGIN[0];
		
	      /*************************************************/
			BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
			int sizeParalems = 20;
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
					sizeParalems, sizeParalems + 5, 10, TimeUnit.HOURS, workQuee);
	      /****************************************************/
		final Map<String, Long> resultM = new HashMap<>();
		for (final String login : Admin4SharedCounts.LOGIN) {
			
			threadPoolExecutor.execute(new Runnable() {
				
				@Override
				public void run() {
					try {
						resultM.put(login, (c.getFreeSpace(login,
						Admin4SharedCounts.PASSWORD)));
					} catch (ApiException e) {
						throw new RuntimeException(e);
					}
					
				}
			});
		}

		threadPoolExecutor.shutdown();
		try {
			while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
				System.out.printf("\rAwaiting completion of threads. ");
			}
			System.out.println();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		Long size = 0l;
		for (String login : resultM.keySet()) {
			if (size < resultM.get(login)){
				result = login;
				size = resultM.get(login);
			}
		}
		
		return result;
	}

	public static void main(String[] args) throws ServiceException,
			RemoteException {
		getInstance().initialize(args);
	}

	private void initialize(String[] args) {
		try {

			if (args.length > 0) {
				for (String string : args) {
					if (string.contains("-s")) {
						setNetSpeed(Float.parseFloat(string.substring(2)));
					} else if (string.contains("-p")) {
						sizeParalems = Integer.parseInt(string.substring(2));
					} else if (string.contains("-l")) {
						sizeParalems = Integer.parseInt(string.substring(2));
					} else if (string.contains("-f")) {
						subfolderEntry = false;
					} else if (string.contains("-h")) {
						System.out
								.println("Put:\n"
										+ "-p define how many paralles thread is executed a  while\n (1)"
										+ "-s define a estimative of your upload velocit in KB/S (100)\n"
										+ "-l local where need upload files or file will be uploaded(\"/opt/maloi/d/Delete\")\n"
										+ "-f entry in subfolder, only is apper for negative the subfolder (true)\n\n"
										+ "Example java -jar Send4Shared.jar -p2 -s100 \"-lc:\\path with space\folder with media will be uploaded\" -f");
						return;
					}
				}
			}
			sendPhoto(c);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendPhoto(DesktopAppJax2 c) throws ClientProtocolException,
			IOException {

		try {
			System.out.println("Permission Size "
					+ c.getMaxFileSize(LOGIN, PASSWORD));
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File folder = new File(startFolder);
		sendFolder(folder, c);
	}

	private static void sendFolder(File folder, DesktopAppJax2 c)
			throws ClientProtocolException, IOException {
		// List<Thread> threads = new ArrayList<Thread>();

		BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				sizeParalems, sizeParalems + 5, 10, TimeUnit.HOURS, workQuee);
		for (File photo : SortLitherFile.sort(folder)) {
			if (photo.isDirectory() && subfolderEntry) {
				sendFolder(photo, c);
			} else {
				System.out.println(photo.getName());
				try {
					// String res = /*new arquiveFile(photo, c).*/new
					// ExecuteClient().requestSendFile(photo, c);
					// new arquiveFile(photo, c).requestSendFile(photo, c);
					threadPoolExecutor.execute(new ArquiveFile(photo, c));

				} catch (Exception e) {

					e.printStackTrace();

				}

			}

		}
	}

	public static void setAllSize(long allSize) {
		Send4Shared.allSize = allSize;
	}

	public static long getAllSize() {
		return allSize;
	}
}


