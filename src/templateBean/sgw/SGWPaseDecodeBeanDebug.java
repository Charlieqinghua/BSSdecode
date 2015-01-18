package templateBean.sgw;

import java.util.LinkedList;
import java.util.List;

import all.DicTion;
import all.HexString;

import templateBean.FileOutResultAllFieldBean;
import templateBean.RowBean;
import templateBean.TmplatePaseDecodeBean;
import templateBean.TrafficDataDecodeMergeBean;
import templateallfield.DefineForest;
import templateallfield.Node;
import unicom.WordUnit;

/**
 * @param args
 */
public class SGWPaseDecodeBeanDebug extends templateBean.TmplatePaseDecodeBean {
	String startflag="0xbf4e";
	String trafficflag="0xac";//内嵌上下行流量的标签
	public SGWPaseDecodeBeanDebug(List<RowBean> rowBeanlist,
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
		int starttag=-1;//记录tag标签的初始位置
		int arrnum=0;
		int tlv=0;//为1按照tv格式，为0按照tlv格式
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
		int endpos=0;
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		String bytecont=null;
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
			else if(taglen==b83)//0x82018a后面两个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+4);//0x81c3模式
				pos=pos+3;//跳过length
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
				if(tag.equals(startflag))
				{		
					rowbean=new RowBean();
					RowBeanlist.add(rowbean);
				}
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
					afdecode.match(DicTion.sgwtrafficdatavolumesdic,rowbean);
					bytecont=HexString.bytesToHexString(b,pos+2,end);//ac 的具体内容
					word.setValue(bytecont);
					word.setID(tag);
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
		if(rowbean!=null)
		{
			rowbean.add(word,arrnum);
		}
		
		return end;
	}
	public int dealTV(int starttag,int pos,int way,int arrnum)//处理完 tlv 返回 最后的位置
	{
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		WordUnit word=new WordUnit();
		word.setValue("sgw");
		word.setID(tag);
		if(tag.equals(startflag))
		{		
			rowbean=new RowBean();
			RowBeanlist.add(rowbean);
		}
		rowbean.add(word, arrnum);
		return pos;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String fname="D:\\解码\\zjfee\\sgw\\test\\140914120235.l1sf02.zj2014091400016021.dat.gz";
		String fname="D:\\解码\\zjfee\\sgw\\test\\140914120235.l1sf02.zj2014091400016021.dat.gz";
		String fout="D:\\解码\\zjfee\\sgw\\move\\140914120235.l1sf02.zj2014091400016021.dat.gz-decode.txt";
		String fhexString=null;
		List<RowBean> RowBeanlist = new LinkedList<RowBean>();
		TrafficDataDecodeMergeBean afdecode=new TrafficDataDecodeMergeBean();
		TmplatePaseDecodeBean pase=new SGWPaseDecodeBeanDebug(RowBeanlist,afdecode);
		pase.readGzTobytes(fname);
		pase.match(DicTion.sgwdic);
		List<RowBean> wordlist=pase.getWordlist();
		FileOutResultAllFieldBean file=new FileOutResultAllFieldBean(DicTion.sgwhead);
		file.setWordlist(wordlist);
		file.FileOutput(fout);

	}

}
