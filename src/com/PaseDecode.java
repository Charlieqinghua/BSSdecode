package com;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import output.SGWFileOut;

import all.AllQueue;
import all.HexString;

import unicom.CFile;
import unicom.WordUnit;

import lex.Lex;
import lex.SGWCDR;
import lex.SGWLex;


/**
 * @param <T>
 * @param args
 *  param fname 输入的文件名 
 *  param file  输入的文件
 *  param b     文件对应的二进制数组
 *  parm  hexString 文件对应的16进制String
 */
public class PaseDecode extends Thread {
	String fname;
	File file;
	byte b[];
	String hexString;
	String fout;
	
	public String getFname() {
		return fname;
	}
	public File getFile() {
		
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
//	public void run()
//	{
//		readPacket();
//	}
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
		
		pasePacket(b);
		
	}
	public void pasePacket(byte []b)
	{
		int i=0;
		int len=b.length;
		boolean ismake=false;
		SGWCDR sgwnew=null;
		SGWCDR sgwrec=null;
		int count=0;
		String flag=null;
		String flagnext=null;
		int beginpos=0;//sgw开始分析的位置
		while(i<len)
		{
			flag=HexString.byteToHex(b[i]);
			if((i+1)>=len)
			{
				break;
			}	
			flagnext=HexString.byteToHex(b[i+1]);
			if(flag!=null)
			{
				if(flag.equals("bf")&&flagnext.equals("4e"))//进入 sgw分析
				{
					sgwnew=new SGWCDR();
					if(sgwrec!=null)
					{
						AllQueue.sgwque.add(sgwrec);
					}
					sgwrec=sgwnew;
					sgwnew=null;
				}
			if(sgwrec!=null)	sgwrec.addByte(b[i]);
			}
			i++;
		}
		
		
	}
	public void getSGWFromQueue()
	{
		int i=0;
		SGWCDR sgw=null;
		sgw=AllQueue.sgwque.poll();
		Lex lex=new SGWLex();
		SGWLexObject lexobj=new SGWLexObject();
		CFile cf=new CFile("d:\\data\\hexmore.txt");
		while(AllQueue.sgwque.size()>0)
		{
			sgw=AllQueue.sgwque.poll();
			SGWFileOut sgwout=new SGWFileOut();
			String fhex="d:\\data\\dat\\shex"+i+".dat";
			sgwout.setParament(sgw,fhex);
			sgwout.outputHexFile();
			i++;
			
			
			lexobj.setLex(lex);
			lexobj.setSgw(sgw);
			lexobj.decodeSGW(cf);
			sgw=null;
		}
	}
	
	

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fname="D:\\解码\\zjfee\\sgw\\140914120235.l1sf02.zj2014091400016021.dat";
		String fhexString=null;
		PaseDecode pase=new PaseDecode();
		pase.setFname(fname);
		pase.getFileFromName();
		pase.readPacket();
		pase.getSGWFromQueue();
		//pase.FileOutPut();
		

	}

}
