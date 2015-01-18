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

import templateallfield.GzFileToByte;
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
public class SplitFileDecode extends Thread {
	String fname;
	File file;
	byte b[];
	String hexString;
	String fout;
	GzFileToByte gzfile=new GzFileToByte();
	protected int filesize=0;
	public void readGzTobytes(String fname)
	{
		gzfile.setFname(fname);
		gzfile.getFileFromName();
		gzfile.readPacket();
		this.b=gzfile.getB();
		this.filesize=gzfile.getFilesize();
	}
	public void pasePacket()
	{
		int i=0;
		int len=filesize;
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
				if(flag.equals("bf")&&flagnext.equals("4f"))//进入 sgw分析
				{
					sgwnew=new SGWCDR();
					if(sgwnew!=null)
					{
						AllQueue.sgwque.add(sgwnew);
					}
//					sgwrec=sgwnew;
//					sgwnew=null;
				}
			if(sgwnew!=null)	sgwnew.addByte(b[i]);
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
			String fhex="D:\\解码\\zjfee\\pgw\\split\\pgw"+i+".dat";
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
		String fname="D:\\解码\\zjfee\\pgw\\test\\140917111552.l2sf02.zj2014091700539700.dat.gz";
		String fhexString=null;
		SplitFileDecode pase=new SplitFileDecode();
		pase.readGzTobytes(fname);
		pase.pasePacket();
		pase.getSGWFromQueue();
		//pase.FileOutPut();
		

	}

}
