package br.com.aloi.shared;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import org.apache.commons.lang3.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.pmstation.shared.soap.client.AccountItem;
import com.pmstation.shared.soap.client.AccountItemArray;
import com.pmstation.shared.soap.client.ApiException;
import com.pmstation.shared.soap.client.DesktopAppJax2;
import com.pmstation.shared.soap.client.DesktopAppJax2Service;

import be.com.aloi.f4shared.ArquiveFile;

public class Admin4SharedCounts {

	public static final String PASSWORD = "qwer1234";
	public static final String[] LOGIN = new String[] { "alligatorlandrola@gmail.com",
			"alligator_landrola@gmail.com", "alligator.landrola@gmail.com",
			"a.lligatorlandrola@gmail.com", "al.ligatorlandrola@gmail.com",
			"all.igatorlandrola@gmail.com", "alli.gatorlandrola@gmail.com",
			"allig.atorlandrola@gmail.com", "alliga.torlandrola@gmail.com", "alligat.orlandrola@gmail.com"};
	protected static final boolean PROCESS = true;

	public static void main(String[] args) {
		
		new Admin4SharedCounts().inicializar();

	}

	protected Object sync = new Object();

	void inicializar() {
		long start = System.currentTimeMillis();
		final DesktopAppJax2 c;
		try {
			c = new DesktopAppJax2Service().getDesktopAppJax2Port();

			if (!c.hasRightUpload()) {
				System.out.println("Upload is currently disabled");
				return;
			}

			FileWriter saidaFileWriter = new FileWriter("saida.csv");
			final BufferedWriter saida = new BufferedWriter(saidaFileWriter);

			final HashSet<MaloiItem> allItens = new HashSet<MaloiItem>();
			
		      /*************************************************/
				BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
				int sizeParalems = 20;
				ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
						sizeParalems, sizeParalems + 5, 10, TimeUnit.MINUTES, workQuee);
		      /****************************************************/

			for (final String login : LOGIN) {

				final long freeSpace;
				try {
					String res = c.login(login, PASSWORD);
					System.out.println(res);
					if (StringUtils.isEmpty(res)) {
						System.out.println("Login succesfull");
					} else {
						System.out.println("Login failed: " + res);
					}
					c.emptyRecycleBin(login, PASSWORD);
					freeSpace = c.getFreeSpace(login, PASSWORD);

					
					System.out.printf("%s\t %s\n",login,ArquiveFile.formatMB(freeSpace));
					
					threadPoolExecutor.execute(new Runnable() {


						@Override
		  				public void run() {
							if(!PROCESS){
								return;
							}
							AccountItemArray itens;
							try {
								itens = c.getAllItems(login, PASSWORD);
								synchronized (sync ) {
									allItens.add(new MaloiItem(freeSpace, itens, login));	
								}
							} catch (ApiException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		
					
		  				}
					});
					

				} catch (ApiException e) {
					e.printStackTrace();
				}

			}
			
			threadPoolExecutor.shutdown();
			try {
				while (!threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
					System.out.printf("\rAwaiting  Admin completion of threads. %s",ArquiveFile.getTimeBySecond(System.currentTimeMillis()-start));
				}
				System.out.println();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			close(allItens, saida,saidaFileWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private static void close(HashSet<MaloiItem> allItens, BufferedWriter saida, FileWriter saidaFileWriter) throws IOException {
		writeDataInFile(allItens, saida);
		writeDataInBase(allItens);

		saida.flush();
		saidaFileWriter.flush();
		saida.close();
		saidaFileWriter.close();
		
	}

	private static void writeDataInBase(HashSet<MaloiItem> allItens) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient( "localhost" , 27017 );
		DB db = mongoClient.getDB( "4shared" );
		
		DBCollection coll = db.getCollection("lists");
		
		coll.drop();
		
		for (MaloiItem maloiItem : allItens) {
			for (AccountItem item : maloiItem.getItens().getItem()) {
				if(item.isDirectory()) continue;
				
				//BasicDBObject id = new BasicDBObject("_id", item.getId());
				
				BasicDBObject doc = new BasicDBObject("_id", item.getId())
				
				.append("login", maloiItem.getLogin())
				.append("name", item.getName())
				.append("downloadLink", item.getDownloadLink())
				.append("parentId", item.getParentId())
				.append("smallImageLink", "N_IMG")
				.append("extension", item.getName().toUpperCase().substring(item.getName().lastIndexOf(".")+1))
				.append("removed", item.isRemoved())

				;
				
				
				
				
				coll.insert(doc);
				
			}
		}
	}

	private static void writeDataInFile(HashSet<MaloiItem> allItens,
			BufferedWriter saida) {
		ArrayList<FileDel> files = new ArrayList<>();
		for (final MaloiItem itemMaloiItem : allItens) {
			for (AccountItem item : itemMaloiItem.getItens().getItem()) {
				if(item.isDirectory()) continue;

					if(F4SharedDeletador.INGEXT.contains(item.getName())){
						files.add(new FileDel(itemMaloiItem.getLogin(), new Long(item.getId()).toString()));
						continue;
					}
					
					try {

						saida.write(String.format(
								"%s|%-10.2f|%s|%s|%s|%s|%s|%s|%-10.2f\n",
								itemMaloiItem.getLogin(), itemMaloiItem
										.getFreeSpaceG(), item.getName(), item
										.getDownloadLink(), item.getId(), item
										.getParentId(), "N_IMG", item.isRemoved(),
								new Double(item.getSize() / 1024)));
						
						/*
							 0 Login
							 1 free space
							 2 file name
							 3 link download
							 4 id
							 5 parent folder
							 6 image
							 7 removed
							 8 file size			 
						 
						 
						 */

					} catch (IOException e) {
						e.printStackTrace();
					}
					;

				

			}
		}
		
		new F4SharedDeletador().deletador(files);

	}
	
	public static String extension(String name) {
		String extensionByName;
		if(name.lastIndexOf(".") > 0){
			extensionByName = name.substring(name.lastIndexOf(".")+1).toUpperCase();
		}else{
			extensionByName = "--";
		}
		return extensionByName;
	}

}

class MaloiItem {

	private double freeSpaceG;
	private AccountItemArray itens;
	private String login;

	public MaloiItem(double freeSpaceG, AccountItemArray itens2, String login) {
		super();
		this.freeSpaceG = freeSpaceG;
		this.itens = itens2;
		this.login = login;
	}

	public double getFreeSpaceG() {
		return freeSpaceG;
	}

	public void setFreeSpaceG(double freeSpaceG) {
		this.freeSpaceG = freeSpaceG;
	}

	public AccountItemArray getItens() {
		return itens;
	}

	public void setItens(AccountItemArray itens) {
		this.itens = itens;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

}
