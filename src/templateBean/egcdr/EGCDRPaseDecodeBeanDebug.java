package templateBean.egcdr;

import java.util.LinkedList;
import java.util.List;

import all.DicTion;
import all.HexString;

import templateBean.FileOutResultAllFieldBean;
import templateBean.FileOutResultAllFieldBeanTag;
import templateBean.RowBean;
import templateBean.TmplatePaseDecodeBean;
import templateBean.TrafficDataDecodeMergeBean;
import templateBean.TrafficDataDecodeMergeBeanEgcdrTV;
import templateBean.TrafficDataDecodeMergeBeanTV;
import templateallfield.DefineForest;
import templateallfield.Node;
import unicom.WordUnit;

/**
 * @param args
 * 不同话单的afdecode List of Traffic Data Volumes 需要导入对应的词典
 *  egcdr DicTion.egcdrtrafficdatavolumesdic
 *  pgw   DicTion.pgwdrtrafficdatavolumesdic
 *  sgw   DicTion.sgwdrtrafficdatavolumesdic
 *   
 * 不同话单bf22decode的 List of Service Data 需要导入对应的词典
 */
public class EGCDRPaseDecodeBeanDebug extends templateBean.TmplatePaseDecodeBean {
	String startflag="0xbf46";//开始标签
	String trafficflag="0xac";//内嵌上下行流量的标签
	TrafficDataDecodeMergeBeanTV bf22decode=new TrafficDataDecodeMergeBeanEgcdrTV();
	public static DefineForest egcdrbf22sdic=new DefineForest();
	static 
	{
		initpgwbf22sdic(egcdrbf22sdic);
	}
	public static void initpgwbf22sdic(DefineForest dic)
	{
		DicTion.readTagDistinctTLVTV("egcdr/egcdrbf22",dic);
	}
	
		
	
	public EGCDRPaseDecodeBeanDebug(List<RowBean> rowBeanlist,
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
		//
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
		//
		
		int end=pos+taglen+1;
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				
				if(tag.equals(startflag))
				{		
					
					//
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
//				if(taglen<0&&((tag.equals("0xbf22"))||(tag.equals("0xbc")||(tag.equals("0xb4")))))//bf22情况
//				{
//					taglen=256+taglen; //af如果为负值说明携带符号位置了
//					end=pos+taglen+1;
//				}
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				
				break;
			}
			case 10://squence 的处理方式
			{
				if(tag.equals(trafficflag))
				{

					afdecode.setParament(b,pos+2,end);
					afdecode.match(DicTion.egcdrtrafficdatavolumesdic,rowbean);
					//对于ac中的具体内容只在调试时使用//正式使用时注掉
					bytecont=HexString.bytesToHexString(b,pos+2,end);//ac 的具体内容
					word.setValue(bytecont);
					word.setID(tag);
					rowbean.add(word,arrnum);
					return end;
				}
//				
				
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
	//pgw协议特有的部分
	public int dealTV(int starttag,int pos,int way,int arrnum)//处理完 tlv 返回 最后的位置
	{
		
		int end=0;//处理返回之后的指针位置
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		String bytecont=null;
		//处理pgw中的bf22标签
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		if(taglen<0) 
		{
			int b81=-127;//0x81
			int b82=-126;//0x82
			if(taglen==b81)//后面一个是长度 //0x81c3模式
			{
				taglen=HexString.ComputeTagLengh(b,pos+2);//0x81c3模式
				taglen=256+taglen;
				pos=pos+2;//后面是value
			}
			else if(taglen==b82)//0x82018a后面两个是长度
			{
				taglen=HexString.ComputeTwoTagLengh(b,pos+2,pos+3);//0x81c3模式
				pos=pos+3;//跳过length
			}
		}
		else //说明lengh 只占一位
		{
			pos=pos+1;
		}
		switch(way)
		{
					
			case 0://是ＴＶ也是开始ＲｏｗＢｅａｎ
			{
				WordUnit word=new WordUnit();
				word.setValue("egcdr");
				word.setID(tag);
				if(tag.equals(startflag))
				{		
					//创建新的之前先合并流量  uplink 13,62 downlink 14,63
					if(rowbean!=null)
					{
						mergeVolume(rowbean);
					}
					rowbean=new RowBean();
					RowBeanlist.add(rowbean);
				}
				rowbean.add(word, arrnum);
				end=pos;
				break;
			}
			case 10://内部tv标签
			{
				if(tag.equals("0xbf22"))
				{
					//bf22decode.setParament(b,pos+1,filesize);//根据下一个标签跳出
					//end=bf22decode.getTvend();根据下一个标签跳出时的结束判断
					end=pos+taglen;
					bf22decode.setParament(b,pos+1,end);//根据长度跳出
					bf22decode.match(egcdrbf22sdic,rowbean);
					
					
					//用来校验输出的bf22标签的
					WordUnit word=new WordUnit();
					bytecont=HexString.bytesToHexString(b,pos+1,end);//bf22 的具体内容
				
					word.setValue(bytecont);
					word.setID(tag);
					rowbean.add(word,arrnum);
					break;//返回end
				}
			}
		}
		return end;
	}
	//ac 和 bf22 同时有时 是否需要合并流量
	public void mergeVolume(RowBean rowbean)
	{
		//13和 62
		WordUnit []word=rowbean.getWordlist();
		int up1=0;
		int up2=0;
		int down1=0;
		int down2=0;
		if(word!=null)
		{
			if((word[13]!=null))
			{
				up1=Integer.parseInt(word[13].getValue());
				if(word[62]!=null)
				{
					up2=Integer.parseInt(word[62].getValue());
				}
			}
			if(word[14]!=null)
			{
				down1=Integer.parseInt(word[14].getValue());
				if(word[63]!=null)
				{
					down2=Integer.parseInt(word[63].getValue());
				}
			}
		}
		up1+=up2;
		down1+=down2;
		WordUnit up1word=new WordUnit();
		up1word.setValue(String.format("%d", up1));
		up1word.setID("0xupmerge");
		WordUnit down1word=new WordUnit();
		down1word.setValue(String.format("%d", down1));
		down1word.setID("0xdownmerge");
		rowbean.add(up1word, 64);
		rowbean.add(down1word, 65);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fname="D:\\BSSdecode\\zjfee\\egcdr\\test\\140917111426.r2sf11.b01144173.dat.gz";
		//String fname="D:\\BSSdecode\\zjfee\\pgw\\split\\pgw0.dat";
		String fout="D:\\BSSdecode\\zjfee\\egcdr\\move\\140917111426.r2sf11.b01144173.dat.gz.csv";
		String fhexString=null;
		RowBean.setListSize(66);
		List<RowBean> RowBeanlist = new LinkedList<RowBean>();
		TrafficDataDecodeMergeBean afdecode=new TrafficDataDecodeMergeBean();
		TmplatePaseDecodeBean pase=new EGCDRPaseDecodeBeanDebug(RowBeanlist,afdecode);
		pase.readGzTobytes(fname);
		pase.match(DicTion.egcdrdic);
		List<RowBean> wordlist=pase.getWordlist();
		FileOutResultAllFieldBean file=new FileOutResultAllFieldBeanTag(DicTion.egcdrhead);
		FileOutResultAllFieldBean.setListSize(66);
		file.setWordlist(wordlist);
		file.FileOutput(fout);

	}

}
