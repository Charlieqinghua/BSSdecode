package templateallfield;

import java.io.IOException;
import java.util.Iterator;

import unicom.CFile;
import unicom.WordUnit;

public class FileOutResultAllField extends FileOutResult {
	
	public void FileOutput(String fout)
	{
		
		CFile cf=new CFile(fout);
		StringBuffer sb=new StringBuffer();
		Iterator<WordUnit> it=null;
		
		for(it=wordlist.iterator();it.hasNext();)
		{
			WordUnit w=(WordUnit)it.next();
			String tag=null;
			tag=w.getID();
			sb.append(w.getID());
			sb.append("-");
			sb.append(w.getValue());
			sb.append("|");
				
		}
		
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordlist.clear();
		
		
	}

}
