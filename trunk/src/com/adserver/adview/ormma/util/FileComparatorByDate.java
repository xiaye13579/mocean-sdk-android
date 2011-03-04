/*© 2010-2011 mOcean Mobile. A subsidiary of Mojiva, Inc. All Rights Reserved.*/
package com.adserver.adview.ormma.util;

import java.io.File;
import java.util.Comparator;

public class FileComparatorByDate implements Comparator<File> {
	@Override
	public int compare(File object1, File object2) {
		Long object1Date = object1.lastModified();
		Long object2Date = object2.lastModified();
		return object1Date.compareTo(object2Date);
	}
}