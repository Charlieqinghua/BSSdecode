package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import unicom.CFile;
import unicom.WordUnit;



import lex.Lex;
import lex.SGWLex;

public class testLex {

	/**
	 * @param args
	 */
	String fname;
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	File file;
	byte b[];
	public boolean getFileFromName()
	{
		boolean flag=false;
		file=new File(fname);
		if(file!=null)
		{
			if((file.exists())&&(file.isFile()))
			{
				flag=true;
			}
		}
		return flag;
	}
	public void  readPacket()
	{
		RandomAccessFile raf=null;
		try {
			raf=new RandomAccessFile(file, "r");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int size=0;
		try {
			size = (int)raf.length();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		b=new byte[size];
		try {
			raf.read(b, 0, size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Byte onebyte=new Byte(b[0]);
		
		
	}
	
	public byte[] getB() {
		return b;
	}
	public void setB(byte[] b) {
		this.b = b;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fname="D:\\data\\dat\\shex0.dat";
		String fhexString=null;
		testLex pase=new testLex();
		pase.setFname(fname);
		pase.getFileFromName();
		pase.readPacket();
		byte []b=pase.getB();
		Lex lex=new SGWLex();
		lex.setData(b);
		lex.setPos(0);
		lex.pasepacket();
		List<WordUnit> wordlist =lex.getWordlist();
		CFile cf=new CFile("d:\\data\\hex.txt");
		StringBuffer sb=new StringBuffer();
		for(WordUnit w:wordlist)
		{
			String start=null;
			start=w.getID();
			if(start!=null)
			{
				if(start.equals("bf4e")) sb.append("\r\n");
			}
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
		
		

	}

}
