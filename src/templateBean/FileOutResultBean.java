package templateBean;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import unicom.CFile;
import unicom.WordUnit;

public class FileOutResultBean {
	List<WordUnit> wordlist=null;
	public List<WordUnit> getWordlist() {
		return wordlist;
	}
	public void setWordlist(List<WordUnit> wordlist) {
		this.wordlist = wordlist;
	}
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
			//if(!controlField(tag)) continue;//
			if(tag==null) continue;
			if(tag.equals("0x9b")||tag.equals("0x83all")||tag.equals("0x84all")||tag.equals("0x86af"))
			{
//				sb.append(w.getID());
//				sb.append("-");
				sb.append(w.getValue());
				sb.append("|");
				if(tag.equals("0x9b")) sb.append("\r\n");
			}
			
		}
		
		try {
			String str=sb.toString();
			if(str!=null)
			{
				cf.writeOneRow(sb.toString());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordlist.clear();
		
		
	}
	public boolean controlField(String tag)
	{
		boolean flag=false;
		if((tag.equals("0x9b"))||(tag.equals("0x83all")||tag.equals("0x84all"))) flag=true;
		return flag;
	}

}
