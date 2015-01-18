package templateBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import all.DicTion;

import unicom.WordUnit;

public class RowBean {
	WordUnit wordlist[]=new WordUnit[listsize];
	public static int listsize=DicTion.fieldsize;//默认使用scdr的大小
	public static void setListSize(int size)
	{
		listsize=size;
	}
	public void add(WordUnit word,int pos)
	{
		try {
			if(pos<listsize)
			{
				wordlist[pos]=word;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public WordUnit [] getWordlist() {
		return wordlist;
	}

	public void setWordlist(WordUnit [] wordlist) {
		this.wordlist = wordlist;
	}


}
