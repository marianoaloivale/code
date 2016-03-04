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

public class MakeJSONFile {

	public static void main(String[] args) {

		new MakeJSONFile().inicializar();

	}

	void inicializar() {
		try {
			FileReader reader = new FileReader("saida.csv");
			BufferedReader readerb = new BufferedReader(reader);

			FileWriter saidaFileWriter = new FileWriter("saida2.html");
			final BufferedWriter saida = new BufferedWriter(saidaFileWriter);

			saida.write("<html><head><title>tudo</title></head><body>\n");
			String line;
			while ((line = readerb.readLine()) != null) {
				String[] coluns = line.split("\\|");
					String conteudo = coluns[6];
					saida.write(String.format("\t\t<img src='%1$s'>\n", 
							conteudo));
				
			}

			saida.write("</body></html>");

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
