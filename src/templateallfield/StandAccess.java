package templateallfield;

import java.util.List;

import unicom.WordUnit;

import all.DicTion;
/*state 0是状态开始
* state 2 choices
* state 3 ia5
* state 4是value按照 octetString类型解析
* state 5是按照boolean类型解析
* state 6是按照integer类型解析
* state 7是按照ENUMERATED
* state 8是ContentInfo
* state 9是Null
* state 10是SEQUENCE
* state 11是 ia5 String
* state 12是 hexString输出
* state 13是set
* state 14是 rat
*/
public class StandAccess {
	TmplatePaseDecode pase=null;
	List<WordUnit> wordlist=null;
	FileOutResult out=new FileOutResultAllField();
	public StandAccess(TmplatePaseDecode pase,FileOutResult out) {
		super();
		this.pase = pase;
		this.out=out;
	}

	public StandAccess() {
		super();
		pase=new TmplatePaseDecode();
		out=new FileOutResultAllField();
	}

	
	public void startDecode(DefineForest dic,String path)
	{
//		pase.setFname(path);
//		pase.getFileFromName();
//		pase.readPacket();
		pase.readGzTobytes(path);
		pase.match(dic);
	
		//pase.FileOutput(fout);
		this.wordlist=pase.getWordlist();
		
		out.setWordlist(wordlist);
		
	}
	public void outPutFile(String fout)
	{
		out.FileOutput(fout);
		wordlist.clear();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path="D:\\data\\dat\\shex0.dat";
		String fout="d:\\data\\hexsgw1.txt";
		DefineForest dic=DicTion.sgwdic;
		StandAccess start=new StandAccess();
		start.startDecode(dic, path);
		start.outPutFile(fout);

	}

}
