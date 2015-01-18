package output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import lex.SGWCDR;

public class SGWFileOut {
	
	SGWCDR sgwcdr;
	String fname;
	public void setParament(SGWCDR cdr,String f)
	{
		sgwcdr=cdr;
		fname=f;
	}
	public void outputHexFile()
	{
		
		File file=new File(fname);
		OutputStream writer=null;
		try {
			writer=new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Byte> blist=sgwcdr.getSegment();
		int size=blist.size();
		byte []bout=new byte[size];
		int i=0;
		for(Byte b:blist)
		{
			bout[i]=b;
			i++;
		}
		try {
			writer.write(bout);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
