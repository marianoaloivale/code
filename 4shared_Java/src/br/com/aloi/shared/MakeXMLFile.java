package br.com.aloi.shared;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MakeXMLFile {

	public static void main(String[] args) {

		new MakeXMLFile().inicializar();

	}

	void inicializar() {
		try {
			FileReader reader = new FileReader("saida.csv");
			BufferedReader readerb = new BufferedReader(reader);

			FileWriter saidaFileWriter = new FileWriter("saida.xml");
			final BufferedWriter saida = new BufferedWriter(saidaFileWriter);

			saida.write("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"saida.xsl\"?>\n"
					+ "<root> \n");
			String line;
			while ((line = readerb.readLine()) != null) {
				String[] coluns = line.split("\\|");
				// if(extension(coluns))
				saida.write("\t<img> \n");
				for (int i = 0; i < coluns.length; i++) {
					String conteudo = coluns[i];
					saida.write(String.format("\t\t<tag%1$s>%2$s</tag%1$s>\n", i,
							URLEncoder.encode(conteudo)));
				}
				saida.write("\t</img>\n ");
				// if(items.size()>=100)break;
			}

			saida.write("</root>");

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

}
