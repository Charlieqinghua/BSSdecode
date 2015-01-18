package templateBean.scdr;

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

import templateBean.FileOutResultAllFieldBean;
import templateBean.FileOutResultAllFieldBeanTag;
import templateBean.RowBean;
import templateBean.TmplatePaseDecodeBean;
import templateBean.TrafficDataDecodeMergeBean;
import templateallfield.DefineForest;
import templateallfield.GzFileToByte;
import templateallfield.GzNOFileToByte;
import templateallfield.Node;
import templateallfield.TrafficDataDecode;
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

/**
 * @author Administrator
 *
 */
public class SCDRPaseDecodeBean extends templateBean.TmplatePaseDecodeBean{
	String trafficflag="0xaf";


	public SCDRPaseDecodeBean(List<RowBean> rowBeanlist,
			TrafficDataDecodeMergeBean afdecode) {
		super(rowBeanlist, afdecode);
		// TODO Auto-generated constructor stub
	}

	public void match(DefineForest forest)
	{
		if(b==null) return;
		
		int len=filesize;
		int i=0;
		int p=0;//移动指针
		int state=0;
		int way=0;
		int tlv=0;//为1按照tv格式，为0按照tlv格式
		int starttag=-1;//记录tag标签的初始位置
		int arrnum=0;
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
						if(starttag==-1) //说明这个位置是一次匹配上来的
						{
							starttag=p;
						}
						arrnum=branchnext.getPos();//该字段在一行中的位置
						//i=dealTLV(starttag,p,way,arrnum);
						tlv=branchnext.getTlv();
						if(tlv==0)
						{
							i=dealTLV(starttag,p,way,arrnum);
						}
						else if(tlv==1)
						{
							i=dealTV(starttag,p,way,arrnum);
						}
						branch=root;
						starttag=-1;
						break;
					}
					case 0://匹配失败
					{
						branch=branchnext;
						starttag=-1;
						break;
					}
				}
				
				
				
			}
			else//没有匹配上
			{
				starttag=-1;
				branch=root;
			}
			
			i++;//没有匹配上直接后跳一个未知
			
		}
	}
	
	public int dealTLV(int starttag,int pos,int way,int arrnum)//处理完 tlv 返回 最后的位置
	{
		WordUnit word=new WordUnit();
		String bytecont=null;
		int endpos=0;
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		if(taglen<0) 
		{
			int b81=-127;//0x81
			int b82=-126;//0x82
			int b83=-125;//0x83
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
			//添加判断的
			else if(taglen==b83)//0x82018a后面两个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+4);//0x81c3模式
				pos=pos+3;//跳过length
			}
			//新添加的带代码
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
			case 10://squence 的处理方式
			{
				if(tag.equals(trafficflag))
				{


					afdecode.setParament(b,pos+2,end);
					afdecode.match(DicTion.trafficdatavolumesdic,rowbean);
					//对于ac中的具体内容只在调试时使用//正式使用时注掉
					//bytecont=HexString.bytesToHexString(b,pos+2,end);//ac 的具体内容
					//bytecont=HexString.bytesToHexString(b,starttag,end);//ac 的具体内容
					//word.setValue(bytecont);
					//word.setID(tag);
					rowbean.add(word,arrnum);
					return end;
				}

				
			}
			case 13:
			{
				bytecont=HexString.bytesToIntergerString(b,pos+2,end);
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
		if(rowbean!=null) rowbean.add(word,arrnum);
		
		return end;
	}
	/**
	 * @author Administrator
	 * 函数的返回值是 继续处理的标签位置
	 *
	 */
	public int dealTV(int starttag,int pos,int way,int arrnum)//处理完 tlv 返回 最后的位置
	{
		//b4zxscdr标签判断   b481xx80 b482xxxx80是 zx的开始标签
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
//		log.info(tag);
//		log.info(this.rt.totalMemory());
//		log.info(this.rt.freeMemory());
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		int b80=-128;
		int b81=-127;//0x81
		int b82=-126;//0x82
		int b83=-125;//0x82
		
		if(taglen<0) 
		{
			
			if(taglen==b81)//后面一个是长度 //0x81c3模式
			{
				taglen=HexString.ComputeTagLengh(b,pos+2);//0x81c3模式
				taglen=256+taglen;
				int bis=HexString.ComputeTagLengh(b,pos+3);//0x81c3模式
				if(bis==b80)
				{
					rowbean=new RowBean();
					RowBeanlist.add(rowbean);
				}
				pos=pos+2;//后面是value
			}
			else if(taglen==b82)//0x82018a后面两个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+3);//0x81c3模式
				int bis=HexString.ComputeTagLengh(b,pos+4);//0x81c3模式
				if(bis==b80)
				{
					rowbean=new RowBean();
					RowBeanlist.add(rowbean);
				}
				pos=pos+3;//跳过length
			}
			//
			else if(taglen==b83)//0x8301238a后面三个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+4);//0x81c3模式
				int bis=HexString.ComputeTagLengh(b,pos+5);//0x81c3模式
				if(bis==b80)
				{
					rowbean=new RowBean();
					RowBeanlist.add(rowbean);
				}
				pos=pos+3;//跳过length
			}
			//新加的判断
			else  //taglen<0同时又不是 0xb481(82)模式的直接返回
			{
				return pos;
			}
		}
		
		else //说明lengh 只占一位  0xb479800112 
		{
			int bis=HexString.ComputeTagLengh(b,pos+2);//0x81c3模式
			if(bis==b80)
			{
				
				//rowbean=new RowBean();
				String flag=HexString.bytesToHexString(b, pos+4,pos+5);
				if(flag.equals("21")) //0xb479800112也是scdr话单开始标签
				{
					RowBean rowbean=new RowBean();
				}
				else //说明是b4的tlv标签 连接释放更详细的原因  0xb403800124
				{
					String b4content=HexString.bytesToHexString(b,pos,pos+1+taglen);
					WordUnit word=new WordUnit();
					word.setValue(b4content);
					word.setID(tag);
					rowbean.add(word, arrnum);
					pos=pos+1+taglen;//相当于返回end
					return pos;
					
				}
				RowBeanlist.add(rowbean);
			}
			pos=pos+1;
		}
		WordUnit word=new WordUnit();
		
		word.setValue(HexString.bytesToHexString(b,starttag,pos));
		//word.setValue("scdr");
		word.setID(tag);
		

		if(rowbean!=null )rowbean.add(word, arrnum);
		return pos;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fname="D:\\BSSdecode\\zjfee\\scdr\\test\\141022155953.r1sf04.b07134408.dat.gz";
		//String fname="D:\\data\\dat\\shex0.dat";
		String fout="D:\\BSSdecode\\zjfee\\scdr\\move\\scdr.dat.字典序-af6.csv";
		String fhexString=null;
		List<RowBean> RowBeanlist = new LinkedList<RowBean>();
		TrafficDataDecodeMergeBean afdecode=new TrafficDataDecodeMergeBean();
		TmplatePaseDecodeBean pase=new SCDRPaseDecodeBean(RowBeanlist,afdecode);
		RowBean.setListSize(32);
		FileOutResultAllFieldBeanTag.setListSize(32);
		pase.readGzTobytes(fname);
		pase.match(DicTion.scdrdic);
		List<RowBean> wordlist=pase.getWordlist();
		FileOutResultAllFieldBean file=new FileOutResultAllFieldBeanTag(DicTion.scdrhead);
		file.setWordlist(wordlist);
		file.FileOutput(fout);
		System.out.println("end");
		//byte []tag1={0x00,0x1f,0x4e,0x55,0x01,0x44,0x1f,0x56,0x00,0x55};
		
		

	}

}
