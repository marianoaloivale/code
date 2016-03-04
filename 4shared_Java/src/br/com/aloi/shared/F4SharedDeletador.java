package br.com.aloi.shared;//br.com.aloi.shared.F4SharedDeletador

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.rpc.ServiceException;

import com.pmstation.shared.soap.client.ApiException;
import com.pmstation.shared.soap.client.DesktopAppJax2;
import com.pmstation.shared.soap.client.DesktopAppJax2Service;

public class F4SharedDeletador {

	static final String PASSWORD ="qwer1234";
	public static void main(String[] args) throws Throwable {
		F4SharedDeletador f = new F4SharedDeletador();
		f.deleteByInutilType();
		f.deleteByBase();
	}

	protected Object deletator = new Object();
	private Connection c;

	private void deleteByBase() throws ServiceException, SQLException, ClassNotFoundException  {

		ArrayList<FileDel> files = new ArrayList<>();
		final DesktopAppJax2 cc = new DesktopAppJax2Service().getDesktopAppJax2Port();
		
		Class.forName("org.sqlite.JDBC");
	       c = DriverManager.getConnection("jdbc:sqlite:/home/maloi/.config/chromium/Default/databases/file__0/2");
		
	      Statement stmt = c.createStatement();
	      ResultSet rs = stmt.executeQuery( "SELECT * FROM deleteT  ;" );
	      
	      /*************************************************/
			BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
			int sizeParalems = 20;
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
					sizeParalems, sizeParalems + 5, 10, TimeUnit.HOURS, workQuee);
	      /****************************************************/
	      
	      while ( rs.next() ) {
	    	  final Long id = rs.getLong("id");
	          final String  loggin = rs.getString("login");
	    	  if(id != 1)
					//files.add(new FileDel(loggin,id.toString()));
	    	  {

				threadPoolExecutor.execute(new Runnable() {

					@Override
					public void run() {
						FileDel fileDel = new FileDel(loggin, id.toString());
						boolean notFound = true;
						try {
							deletador(cc, fileDel);
							System.out.println(fileDel);
						} catch (NumberFormatException | RemoteException e) {
							notFound = e.getMessage().contains("File not found");
							
							if(!notFound)
								System.err.println(e.getMessage() + " | " + fileDel);
						}
						synchronized (deletator) {

							try {
								if(notFound)
								c.createStatement()
										.execute(
												"DELETE FROM deleteT  WHERE id = "
														+ id);
							} catch (SQLException e) {
								System.err.println(e.getMessage() + " | " + fileDel);
							}

						}
					}
				});
	    	  }
	      }
	      
			threadPoolExecutor.shutdown();
			try {
				while (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
					System.out.println("Awaiting completion of threads. ");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	      
	      //validar(files);
	      
		  //deletador(files  );
	      
	      
			

	      new Admin4SharedCounts().inicializar();
	      new DownloadImgs().      inicializar();
	      new MakeHTMLFile().      inicializar();
		
	}

	private void validar(final ArrayList<FileDel> files) throws IOException {
		FileReader reader = new FileReader("saida.csv");
		BufferedReader readerb = new BufferedReader(reader);
		ArrayList<FileDel> files2 = new ArrayList<>();
		String line;
		while((line=readerb.readLine())!=null){
			String[] coluns = line.split("\\|");
			//items.add(new MaloiItem2(coluns[2],coluns[3],coluns[6],coluns[8],coluns[0], coluns[4]));
			String name = coluns[2];
			if(name.lastIndexOf(".") > 0){
				name = name.substring(name.lastIndexOf(".")+1).toUpperCase();
			}else{
				name ="--";
			}
				
					files2.add(new FileDel(coluns[0], coluns[4]));
			
			
		}
		
		for (FileDel fileDel : files) {
			if(files2.contains(fileDel)){
				System.out.println(fileDel);
			}else{
				System.err.println(fileDel);
			}
		}
		readerb.close();
		reader.close();
		
	}
	
	public static final HashSet<String> INGEXT = new HashSet<>(Arrays.asList(new String[]{"BMP"
			,"JS"
			,"INI"
			,"DB"
			,"DOC"
			,"DOCX"
			,"EXE"
			,"B1"
			,"M4V"
			,"MP3"
			,"--"
			

			,"APK"
			,"AMR"
			,"LTL"
			,"WPL"
			,"WEBM"
			,"TXZ"
			,"PPS"
			,"NF2"
			,"M4A"
			,"HTML"
			,"AAC"
			
			,"3G2"
			,"3GA"
			,"ZIP"
			,"3GPP"
			,"PANDO"
			,"WAV"
			,"TXT"
			,"RAR"
			,"PDF"}));

	public  void deleteByInutilType() throws IOException {
		
		
		
		ArrayList<FileDel> files = new ArrayList<>();
		
		FileReader reader = new FileReader("saida.csv");
		BufferedReader readerb = new BufferedReader(reader);

		String line;
		while((line=readerb.readLine())!=null){
			String[] coluns = line.split("\\|");
			//items.add(new MaloiItem2(coluns[2],coluns[3],coluns[6],coluns[8],coluns[0], coluns[4]));
			String extensionFile = coluns[2];
			if(extensionFile.lastIndexOf(".") > 0){
				extensionFile = extensionFile.substring(extensionFile.lastIndexOf(".")+1).toUpperCase();
			}else{
				extensionFile ="--";
			}
				
				if(INGEXT.contains(extensionFile))
					files.add(new FileDel(coluns[0], coluns[4]));
			
			
		}
		
		readerb.close();
		reader.close();
		
		deletador(files );
		
	}

	public void deletador(ArrayList<FileDel> files){
		final DesktopAppJax2 c;
		c = new DesktopAppJax2Service().getDesktopAppJax2Port();

		if (!c.hasRightUpload()) {
			System.out.println("Upload is currently disabled");
			return;
		}
		  /*************************************************/
			BlockingQueue<Runnable> workQuee = new LinkedBlockingQueue<Runnable>();
			int sizeParalems = 20;
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
					sizeParalems, sizeParalems + 5, 10, TimeUnit.HOURS, workQuee);
		  /****************************************************/
		for (final FileDel fileDel : files) {
			try {
				threadPoolExecutor.execute(new Runnable() {

					@Override
					public void run() {
						try {
							deletador(c, fileDel);
						} catch (NumberFormatException | RemoteException e) {
							throw new RuntimeException(e);
						}
					}
				});
			} catch (Exception e) {
				System.err.println(e.getMessage() + " | " + fileDel);
			}
		}
		threadPoolExecutor.shutdown();
		try {
			while (!threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
				System.out.println("Awaiting completion of threads. ");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void deletador(DesktopAppJax2 c, FileDel fileDel) throws NumberFormatException, RemoteException {
		try {
			c.deleteFileFinal(fileDel.getLogin(), PASSWORD, Long.parseLong(fileDel.getArquivo()));
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(fileDel);
		
	}
	
}

class FileDel{
	private String login,arquivo;

	public FileDel(String login, String arquivo) {
		super();
		this.login = login;
		this.arquivo = arquivo;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getArquivo() {
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	@Override
	public String toString() {
		return"FileDel [login=" + login +", arquivo=" + arquivo +" ]";
	}
	
	@Override
	public boolean equals(Object obj) {
		FileDel fd = (FileDel) obj;
		return fd.getArquivo().equals(arquivo);
	}
	
}
