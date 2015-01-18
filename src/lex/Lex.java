package lex;

import java.util.List;

import unicom.WordUnit;

public interface Lex {
	public void pasepacket();
	public void setData(byte[] data);
	public List<WordUnit> getWordlist();
	public void setPos(int pos);
	

}
