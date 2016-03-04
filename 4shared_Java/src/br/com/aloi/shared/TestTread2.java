package br.com.aloi.shared;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class TestTread2 {

	public static void main(String[] args) {

		
		XStream xstream  = new XStream(new DomDriver());

		xstream.processAnnotations( GameList.class);
		
		GameList gameList = null;
		try {
		gameList =	(GameList) xstream.fromXML(new FileInputStream("/media/big/gamelist.xml"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Integer> games = new TreeMap<>();
		
		for (Game game : gameList.getGame()) {
			String gameName = game.getName();
			
			if(games.containsKey(gameName)){
				Integer countG = games.get(gameName);
				games.put(gameName, countG+1);
			}else{
				games.put(gameName, 1);
			}
		}
		
		for (Game game : gameList.getGame()) {
			String gameName = game.getName();
			if(games.get(gameName) > 1)
				game.setName(gameName += "_" +game.getPath().substring(2));;
		}
		
		//System.out.println(xstream.toXML(gameList));
		//System.out.println(games);
		try {
			xstream.toXML(gameList,new FileWriter(new File("/media/big/gamelist.xml_2")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
@XStreamAlias("gameList")
class GameList{
	@XStreamImplicit(itemFieldName="game")
	private	ArrayList<Game> game;

	public ArrayList<Game> getGame() {
		return game;
	}

	public void setGame(ArrayList<Game> game) {
		this.game = game;
	}
	

}
@XStreamAlias("game")
class Game{

	
	@XStreamImplicit(itemFieldName="genres")
	private	ArrayList<Genre> genres;


	public ArrayList<Genre> getGenres() {
		return genres;
	}

	public void setGenres(ArrayList<Genre> genres) {
		this.genres = genres;
	}
	
	
	@XStreamAlias("path")
	private String path;

	@XStreamAlias("name")
	private String name;

	@XStreamAlias("desc")
	private String desc;

	@XStreamAlias("image")
	private String image;

	@XStreamAlias("rating")
	private String rating;

	@XStreamAlias("releasedate")
	private String releasedate;

	@XStreamAlias("developer")
	private String developer;

	@XStreamAlias("publisher")
	private String publisher;

	@XStreamAlias("genre")
	private String genre;

	@XStreamAlias("players")
	private String players;

	@XStreamAlias("playcount")
	private String playcount;

	@XStreamAlias("lastplayed")
	private String lastplayed;


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getRating() {
		return rating;
	}

	public void setRating(String rating) {
		this.rating = rating;
	}

	public String getReleasedate() {
		return releasedate;
	}

	public void setReleasedate(String releasedate) {
		this.releasedate = releasedate;
	}

	public String getDeveloper() {
		return developer;
	}

	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getPlayers() {
		return players;
	}

	public void setPlayers(String players) {
		this.players = players;
	}

	public String getPlaycount() {
		return playcount;
	}

	public void setPlaycount(String playcount) {
		this.playcount = playcount;
	}

	public String getLastplayed() {
		return lastplayed;
	}

	public void setLastplayed(String lastplayed) {
		this.lastplayed = lastplayed;
	}
}

@XStreamAlias("genre")
class Genre{

	@XStreamAlias("genre")
	private String genre;

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}
}