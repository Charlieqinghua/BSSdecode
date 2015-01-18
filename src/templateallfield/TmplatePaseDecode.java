package templateallfield;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.GZIPInputStream;

import com.SGWLexObject;

import output.SGWFileOut;

import all.AllQueue;
import all.DicTion;
import all.HexString;

import unicom.CFile;
import unicom.WordUnit;

import lex.Lex;
import lex.SGWCDR;
import lex.SGWLex;


/**
 * @param <T>
 * @param args
 * @param fname 输入的文件名 
 * @param file  输入的文件
 * @param b     文件对应的二进制数组
 * @parm  hexString 文件对应的16进制String
 * 
 * 全部解码的基类，主要功能是按照顺序解码，结果全部放在List<WordUnit> wordlist
 * 与TmplatePaseDecodeBean的区别是解码时没有生成行对应的RowBean
 * 解码时一个解码器可以对应多个文件，解码的Tag保存在列表中，每个文件的进入解码结果输出之后
 * 清空列表.
 * 
 */
public class TmplatePaseDecode {
	
	GzFileToByte gzfile=new GzFileToByte();
	byte b[];
	static int maxlen=9000000;//最大支持文件是 9mb 
	int filesize=0;
	List<WordUnit> wordlist = new LinkedList<WordUnit>();//一个文件对应一个wordlist
	public byte[] getB() {
		return b;
	}
	public void setB(byte[] b) {
		this.b = b;
	}
	
	public List<WordUnit> getWordlist() {
		return wordlist;
	}
	public void setWordlist(List<WordUnit> wordlist) {
		this.wordlist = wordlist;
	}
	public void readGzTobytes(String fname)
	{
		gzfile.setFname(fname);
		gzfile.getFileFromName();
		gzfile.readPacket();
		this.b=gzfile.getB();
		this.filesize=gzfile.getFilesize();
	}
	
		
	public void match(DefineForest forest)
	{
		if(b==null) return;
		//int len=b.length;
		int len=filesize;
		int i=0;
		int p=0;//移动指针
		int state=0;
		int way=0;
		int starttag=0;//记录tag标签的初始位置
		Node branch=null;//得到可以匹配的枝
		Node branchnext=null;
		Node root=forest.getRoot();
		branch=root;//开始从根节点匹配
		StringBuffer tagsb=new StringBuffer();
		while(i<len)
		{
			p=i;
			branchnext=branch.get(b[p]);
			if(branchnext!=null)//有后续位置
			{
				state=branchnext.getState();
				switch (state)
				{
					case 1:
					{
						branch=branchnext;
						starttag=p;
						
						
						break;
					}
					case 2:
					{
						branch=branchnext;
						starttag=p;
						break;
					}
					case 3://tag匹配成功
					{
						way=branchnext.getExplainWay();
						if(starttag==0) //说明这个位置是一次匹配上来的
						{
							starttag=p;
						}
						i=dealTLV(starttag,p,way);
						branch=root;
						starttag=0;
						break;
					}
					case 0://匹配失败
					{
						branch=branchnext;
						starttag=0;
						break;
					}
				}
				
				
				
			}
			else//没有匹配上
			{
				starttag=0;
				branch=root;
			}
			
			i++;//没有匹配上直接后跳一个未知
			
		}
	}
	public int dealTLV(int pos,int way)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		
		String tag=HexString.byteToHex(b[pos]);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		String bytecont=null;
//		if(taglen<0) 
//		{
//			word.setValue(HexString.byteToHex(b[pos+1]));
//			word.setID(tag);
//			wordlist.add(word);
//			return pos+1;
//		}
		if(taglen<0) 
		{
			int b81=-127;//0x81
			int b82=-126;//0x82
			if(taglen==b81)//后面一个是长度 //0x81c3模式
			{
				taglen=HexString.ComputeTagLengh(b,pos+2);//0x81c3模式
				taglen=256+taglen;
				pos=pos+1;//后面是value
			}
			else if(taglen==b82)//0x82018a后面两个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+3);//0x81c3模式
				pos=pos+2;//跳过length
			}
			else 
			{
				return pos;
			}
		}
		else //说明lengh 只占一位
		{
			pos=pos;
		}
		int end=pos+taglen+1;
		
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				//bytecont="\r\n"+bytecont;
				tag="\r\n"+tag;
				break;
			}
			//choice解码
			case 2:
			{
				bytecont=HexString.IPString(b,pos+2,end);
				break;
			}
			case 3:
			{
				bytecont=HexString.ExplainStartEndToString(b,pos+2,end);
				break;
			}
			case 4:
			{
				bytecont=HexString.byteToOctetString(b, pos+2, end);
				break;
			}
			case 5:
			{
				int v=HexString.byteToInteger(b[end]);
				if(v!=0) bytecont="yes";
				else bytecont="no";
				break;
			}
			case 6:
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
			}
			default:
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
			}
			
		}
		word.setValue(bytecont);
		word.setID(tag);
		wordlist.add(word);
		return end;
	}
	// starttag是开始的tag 位置，pos是 结束的tag位置
	public int dealTLV(int starttag, int pos,int way)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		String bytecont=null;
//		if(taglen<0) 
//		{
//			word.setValue(HexString.byteToHex(b[pos+1]));
//			word.setID(tag);
//			wordlist.add(word);
//			return pos+1;
//		}
		if(taglen<0) 
		{
			int b81=-127;//0x81
			int b82=-126;//0x82
			if(taglen==b81)//后面一个是长度 //0x81c3模式
			{
				taglen=HexString.ComputeTagLengh(b,pos+2);//0x81c3模式
				taglen=256+taglen;
				pos=pos+1;//后面是value
			}
			else if(taglen==b82)//0x82018a后面两个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+3);//0x81c3模式
				pos=pos+2;//跳过length
			}
			else 
			{
				return pos;
			}
		}
		else //说明lengh 只占一位
		{
			pos=pos;
		}
		int end=pos+taglen+1;
		
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.ExplainStartEndToString(b,pos+2,end);
				//bytecont="\r\n"+bytecont;
				tag="\r\n"+tag;
				break;
			}
			//choice解码
			case 2:
			{
				bytecont=HexString.IPString(b,pos+2,end);
				break;
			}
			case 3:
			{
				bytecont=HexString.ExplainStartEndToString(b,pos+2,end);
				break;
			}
			case 4:
			{
				bytecont=HexString.byteToOctetString(b, pos+2, end);
				break;
			}
			case 5:
			{
				int v=HexString.byteToInteger(b[end]);
				if(v!=0) bytecont="yes";
				else bytecont="no";
				break;
			}
			case 6:
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
			}
			default:
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
			}
			
		}
		word.setValue(bytecont);
		word.setID(tag);
		wordlist.add(word);
		return end;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String fname="D:\\解码\\zjfee\\sgw\\140914120235.l1sf02.zj2014091400016021.dat";
		String fname="D:\\data\\dat\\shex0.dat";
		String fout="d:\\data\\hexsgwoo.txt";
		String fhexString=null;
		
		TmplatePaseDecode pase=new TmplatePaseDecode();
//		byte []tag1={0x00,0x1f,0x4e,0x55,0x01,0x44,0x1f,0x56,0x00,0x55};
//		pase.setB(tag1);
//		pase.match(all.DicTion.dic);
		pase.readGzTobytes(fname);
		pase.match(DicTion.sgwdic);
		//pase.FileOutput(fout);
//		pase.getSGWFromQueue();
		//pase.FileOutPut();
		

	}

}
