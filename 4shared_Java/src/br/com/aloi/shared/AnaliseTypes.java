package br.com.aloi.shared;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import br.com.aloi.shared.MaloiItem2;


public class AnaliseTypes {

	public static void main(String[] args) throws IOException {
		FileReader reader = new FileReader("saida.csv");
		BufferedReader readerb = new BufferedReader(reader);

		String line;
		//ArrayList<MaloiItem2> items = new ArrayList<MaloiItem2>();
		HashMap<String,Integer> ext = new HashMap<String,Integer>();
		while((line=readerb.readLine())!=null){
			String[] coluns = line.split("\\|");
			//items.add(new MaloiItem2(coluns[2],coluns[3],coluns[6],coluns[8],coluns[0], coluns[4]));
			String name = coluns[2];
			if(name.lastIndexOf(".") > 0){
				name = name.substring(name.lastIndexOf(".")+1).toUpperCase();
			}else{
				name = "--";
			}
			
			
				Integer i = ext.containsKey(name) ? ext.get(name) : 1;
				ext.put(name, ++i);
			
			
		}
		
		
		
		for (String key :  new TreeSet<String>(ext.keySet())) {
			System.out.printf("%s %s\n",key,ext.get(key));
		}
	}

}
