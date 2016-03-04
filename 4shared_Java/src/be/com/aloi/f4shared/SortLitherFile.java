package be.com.aloi.f4shared;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class SortLitherFile {

	public static File[] sort(File folder) {
		File[] originalList = folder.listFiles();

		Arrays.sort(originalList, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				if(o1.length() > o2.length())
					return 1;
				else if(o1.length() < o2.length())
					return -1;
				return 0;
			}
		});

		return originalList;
	}

}
