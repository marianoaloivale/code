package br.aloi.getHomePage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetBookListMovies {

	private static final String URLSearch = "http://search.4shared.com/network/searchXml.jsp?searchExtention=category:2&sortType=2&sortOrder=1&start=";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		XStream xStream = getXstream();
		Search s = new Search();
		StringBuilder xml = null;
		Collection<FileMovie> fileMovies = new ArrayList<FileMovie>(100);
		for (int i = 0; i <= 200; i += 10) {
			try {
				xml = getRequestXML(i < 10 ? 1 : i);
				s = (Search) xStream.fromXML(xml.toString());
				fileMovies.addAll(s.getResult_files().getFile());
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		writeHTML(fileMovies);

	}

	private static void writeHTML(Collection<FileMovie> fileMovies) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileWriter writer = new FileWriter("4shared.html");
		sb.append("<html><head/><body><table><tr>");
		int i =0;
		for (FileMovie fileMovie : fileMovies) {
			if((i++)%10==0){
				sb.append("</tr><tr>");
			}
			sb.append("<td><a href='");

			sb.append(fileMovie.getUrl());

			sb.append("'><img src='");
			sb.append(fileMovie.getPreview_ur());
			sb.append("' alt='");
			sb.append(fileMovie.getName());
			sb.append("'/></a></td>");

		}

		sb.append("</tr></table></body>");
		
		writer.write(sb.toString());
		
	}

	private static StringBuilder getRequestXML(int i)
			throws ClientProtocolException, IOException {
		final HttpClient httpclient = new DefaultHttpClient();
		final HttpGet httppost = new HttpGet(URLSearch + i);
		HttpResponse response;
		response = httpclient.execute(httppost);

		HttpEntity resEntity = response.getEntity();
StringBuilder result = new StringBuilder();
		if (resEntity != null) {

			BufferedReader in = new BufferedReader(new InputStreamReader(
					resEntity.getContent()));

			String s2 = null;
			while ((s2 = in.readLine()) != null) {
				result.append(s2);
			}

		}

		return result;
	}

	private static XStream getXstream() {
		XStream xstream = new XStream(new DomDriver());

		xstream.processAnnotations( Search.class);
		xstream.processAnnotations( Result.class);
		xstream.processAnnotations( FileMovie.class);
		
		return xstream;
	}

}
@XStreamAlias("search-result")
class Search {
	@XStreamAlias("result-files")
	private Result result_files;

	public Result getResult_files() {
		return result_files;
	}
	public void setResult_files(Result result_files) {
		this.result_files = result_files;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getTotal_files() {
		return total_files;
	}
	public void setTotal_files(String total_files) {
		this.total_files = total_files;
	}
	public String getPage_number() {
		return page_number;
	}
	public void setPage_number(String page_number) {
		this.page_number = page_number;
	}
	public String getPages_total() {
		return pages_total;
	}
	public void setPages_total(String pages_total) {
		this.pages_total = pages_total;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getFiles_per_page() {
		return files_per_page;
	}
	public void setFiles_per_page(String files_per_page) {
		this.files_per_page = files_per_page;
	}
	public String getFiles_approx_count() {
		return files_approx_count;
	}
	public void setFiles_approx_count(String files_approx_count) {
		this.files_approx_count = files_approx_count;
	}
	@XStreamAlias("query")
	private String query ;
		@XStreamAlias("total-files")
	private String total_files ;
		@XStreamAlias("page-number")
	private String page_number;
		@XStreamAlias("pages-total")
	private String pages_total;
		@XStreamAlias("start")
	private String  start;
		@XStreamAlias("files-per-page")
	private String files_per_page;
		@XStreamAlias("files-approx-count")
	private String files_approx_count;
}

@XStreamAlias("result-files")
class Result {
	@XStreamImplicit(itemFieldName="file")
	private	ArrayList<FileMovie> file;

	public ArrayList<FileMovie> getFile() {
		return file;
	}

	public void setFile(ArrayList<FileMovie> file) {
		this.file = file;
	}
}

@XStreamAlias("file")
class FileMovie {
	
	@XStreamAlias("name")
	private String name;
	@XStreamAlias("description")
	private String description;
	@XStreamAlias("downloads-count")
	private String downloads_count;
	@XStreamAlias("upload-date-format")
	private String upload_date_format;
	@XStreamAlias("upload-date")
	private String upload_date;
	@XStreamAlias("url")
	private String url;
	@XStreamAlias("preview-url")
	private String preview_ur;
	@XStreamAlias("flash-preview-url")
	private String flash_preview_url;
	@XStreamAlias("user")

	private String user;
	@XStreamAlias("size")

	private String size;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUpload_date_format() {
		return upload_date_format;
	}

	public void setUpload_date_format(String upload_date_format) {
		this.upload_date_format = upload_date_format;
	}

	public String getUpload_date() {
		return upload_date;
	}

	public void setUpload_date(String upload_date) {
		this.upload_date = upload_date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPreview_ur() {
		return preview_ur;
	}

	public void setPreview_ur(String preview_ur) {
		this.preview_ur = preview_ur;
	}

	public String getFlash_preview_url() {
		return flash_preview_url;
	}

	public void setFlash_preview_url(String flash_preview_url) {
		this.flash_preview_url = flash_preview_url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getDownloads_count() {
		return downloads_count;
	}

	public void setDownloads_count(String downloads_count) {
		this.downloads_count = downloads_count;
	}
}
