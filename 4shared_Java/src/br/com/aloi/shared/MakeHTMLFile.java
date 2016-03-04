package br.com.aloi.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

public class MakeHTMLFile {

	public static void main(String[] args) {
		
		new MakeHTMLFile().inicializar();

	}

	void inicializar() {
		try {
			FileReader reader = new FileReader("saida.csv");
			BufferedReader readerb = new BufferedReader(reader);
			
			FileWriter saidaFileWriter = new FileWriter("saida.html");
			final BufferedWriter saida = new BufferedWriter(saidaFileWriter);

			saida.write("<html>"
					+ "<head> 	"
					+ "<title>Biblioteca 4Shared</title> 	"
					+ "<meta content='text/html;charset=utf-8' http-equiv='Content-Type'> "
					+ "<meta name='viewport' content='width=device-width, initial-scale=1.0'>	"
					+ "<meta content='utf-8' http-equiv='encoding'> 	"
					+ "<link rel='stylesheet' type='text/css' href='css.css'> 	  "
					+
					 "<script type='text/javascript' src='jquery-2.1.0.min.js'></script>"

					+ "<script type='text/javascript' src='jquery-ui.js'></script>"
					+ "<link rel='stylesheet' href='jquery-ui.css'>" // http://code.jquery.com/ui/1.11.2/themes/smoothness/
					+ "<style type='text/css'>"
					
					+ "</style> "
					+ "</head> "
					+ "<body> <div id='tabs'> "
					//+ "<table id='bigTable'><tr>"
					);
			
			ArrayList<MaloiItem2> items = getItems(readerb);
			
			//HashSet<MaloiItem2> items = new HashSet<>(getItems(readerb));
			
			HashMap<String,Integer> anchorExtensionAux= new HashMap<>();
			for (MaloiItem2 maloiItem2 : items) {
				String name = maloiItem2.extension();
				if(anchorExtensionAux.containsKey(name)){
					Integer val = anchorExtensionAux.get(name);
					anchorExtensionAux.put(name,val+1);
				}
				else{
					anchorExtensionAux.put(name,1);
				}
			}
			ArrayList<String> anchorExtension = new ArrayList<>(anchorExtensionAux.keySet());
			Collections.sort(anchorExtension);
			saida.write("<ul>");
			for (String loString : anchorExtension){
				saida.write(String.format("<li><a href='#%1$s'>%1$s(%2$s)</a></li>", loString,anchorExtensionAux.get(loString)));
			}
			saida.write("</ul>");
			
			
			
			String typeReuse = null;
			for (MaloiItem2 maloiItem : items) {
				boolean sameType = maloiItem.extension().equals(typeReuse);
				if(!sameType && typeReuse!=null){
					saida.write("</div>");
				}
				if(!sameType ){
					saida.write("<div id='"+maloiItem.extension()+ "'>");
				}
				
				saida.write(String.format(
						 "<div id='photo'>"
						 + "<input type=\"checkbox\" id='ccii_%4$s' codeId='%4$s' loginId='%3$s'  class='check_i' name=\"%3$s|%4$s\" value=\"%3$s|%4$s\">"
						 + "<input type=\"checkbox\" id='cckk_%4$s' codeId='%4$s' loginId='%3$s'  class='check_c' name=\"%3$s|%4$s\" value=\"%3$s|%4$s\">"
						 + "<input type=\"checkbox\" id='ccpp_%4$s' codeId='%4$s' loginId='%3$s'  class='check_p' name=\"%3$s|%4$s\" value=\"%3$s|%4$s\">"
						 + "<a href=\"%2$s\" target=\"_blank\" onclick=\"callLink(this,'%3$s')\" title=\"%1$s\">"
							+ "<img data-original=\"/opt/maloi/photos/%3$s_%4$s\" id='img_%4$s' class=\"imgSize\" alt=\"%1$s %5$s\" title=\"%1$s [%5$s] [%3$s]\" width=\"200\" height=\"200\">"							
						+ "</a>"
						+ "</div>"
					+"   \n",maloiItem.getName(),maloiItem.getPreview(),maloiItem.getLogin(),maloiItem.getId(),maloiItem.getMbSize()));


				typeReuse = maloiItem.extension();
				
			}
			saida.write("</div>");
			
			saida.write(
					//"</tr></table>"
					 "<script type='text/javascript' src='jquery.lazyload.min.js'></script>"
					+ "<script type=\"text/javascript\" charset=\"utf-8\">$(function() {$(\"img.imgSize\").lazyload();});</script>"
					+ "<script type='text/javascript' src='js.js'></script> "
					+ "<script type='text/javascript' >initDatabase();</script> "
					+ "</div></body></html>");
			
			
			saida.flush();
			saida.close();
			saidaFileWriter.close();
			reader.close();
			readerb.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("HTML MADE");
		
	}

	private ArrayList<MaloiItem2> getItems(BufferedReader readerb) throws IOException {
		String line;
		ArrayList<MaloiItem2> items = new ArrayList<MaloiItem2>();
		while((line=readerb.readLine())!=null){
			String[] coluns = line.split("\\|");
			//if(extension(coluns))
			items.add(new MaloiItem2(coluns[2],coluns[3],coluns[6],coluns[8],coluns[0], coluns[4]));
			
			
			//if(items.size()>=100)break;
		}
		Collections.sort(items,new Comparator<MaloiItem2>() {

			@Override
			public int compare(MaloiItem2 o1, MaloiItem2 o2) {
				int r =  o2.extension().compareTo(o1.extension()); 

				if (r != 0)
					return r;

				r = o2.getSize().compareTo(o1.getSize());

				if (r != 0)
					return r;

				r = o2.getName().compareTo(o1.getName());

				if (r != 0)
					return r;
				

				
				return r;
			}
		});
		return items;
	}

	

}

class MaloiItem2{

	private String name;
	private String preview;
	private String image;
	private Double size;
	private String login;
	private String id;
	public MaloiItem2(String name, String preview, String image, Double size,
			String login, String id) {
		super();
		this.name = name;
		this.preview = preview;
		this.image = image;
		this.size = size;
		this.login = login;
		this.id = id;
	}
	public String extension() {	
		
		String ext = Admin4SharedCounts.extension(name);
		
		//return ext;
		
		switch (ext) {
		case "GIF":
			return "GIF";
		case "JPEG":
		case "JPG":
		case "PNG":
			
			return "AIMG";

		default:
			return "VIDEO";
		}
	}
	public Object getMbSize() {
		return formatMB(size.longValue()*1024);
	}
	public MaloiItem2(String name2, String preview2, String image2,
			String string, String login2, String id2) {
		this(name2,preview2,image2,Double.parseDouble(string),login2,id2);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPreview() {
		return preview;
	}
	public void setPreview(String preview) {
		this.preview = preview;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Double getSize() {
		return size;
	}
	public void setSize(Double size) {
		this.size = size;
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
	
	private String formatMB(Long sizeFile) {
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
	
	
	
}
