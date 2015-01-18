package com;

import java.io.IOException;
import java.util.List;

import all.AllQueue;

import unicom.CFile;
import unicom.WordUnit;
import lex.Lex;
import lex.SGWCDR;

public class SGWLexObject extends Thread {
	SGWCDR sgw;
	Lex lex;
	List<WordUnit> wordlist;
	public SGWCDR getSgw() {
		return sgw;
	}
	public void setSgw(SGWCDR sgw) {
		this.sgw = sgw;
	}
	public Lex getLex() {
		return lex;
	}
	public void setLex(Lex lex) {
		this.lex = lex;
	}

	public void decodeSGW(CFile cf)
	{
		List<Byte> blist=sgw.getSegment();
		int size=blist.size();
		byte []bout=new byte[size];
		int i=0;
		for(Byte b:blist)
		{
			bout[i]=b;
			i++;
		}
		lex.setData(bout);
		lex.pasepacket();
		wordlist=lex.getWordlist();
		StringBuffer sb=new StringBuffer();
		for(WordUnit w:wordlist)
		{
			String start=null;
			start=w.getID();
			sb.append(w.getID());
			sb.append("-");
			sb.append(w.getValue());
			sb.append("|");
		}
		sb.append("\r\n");
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		wordlist=null;
		
	}

}
