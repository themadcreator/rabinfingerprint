package org.rabinfingerprint.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileListing {

	public static List<File> getFileListing( File directory ) throws FileNotFoundException {
		// get files
		File[] files = directory.listFiles();
		ArrayList<File> result = new ArrayList<File>();
		if( files != null ){		
			result.addAll( Arrays.<File>asList( files ) );

			// recurse
			for ( File file : files ) {
				if ( !file.isDirectory() ) continue;
				result.addAll( getFileListing( file ) );
			}
		}

		// return
		return result;
	}

}
