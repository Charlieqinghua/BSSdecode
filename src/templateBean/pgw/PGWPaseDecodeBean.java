package templateBean.pgw;

import java.util.LinkedList;
import java.util.List;

import all.DicTion;
import all.HexString;

import templateBean.FileOutResultAllFieldBean;
import templateBean.FileOutResultAllFieldBeanTag;
import templateBean.RowBean;
import templateBean.TmplatePaseDecodeBean;
import templateBean.TrafficDataDecodeMergeBean;
import templateBean.TrafficDataDecodeMergeBeanTV;
import templateallfield.DefineForest;
import templateallfield.Node;
import unicom.WordUnit;

/**
 * @param args
 */
public class PGWPaseDecodeBean extends templateBean.TmplatePaseDecodeBean {
	String startflag="0xbf4f";//开始标签
	String trafficflag="0xac";//内嵌上下行流量的标签
	TrafficDataDecodeMergeBeanTV bf22decode=new TrafficDataDecodeMergeBeanTV();
	public static DefineForest pgwbf22sdic=new DefineForest();
	static 
	{
		initpgwbf22sdic(pgwbf22sdic);
	}
	public static void initpgwbf22sdic(DefineForest dic)
	{
		DicTion.readTagDistinctTLVTV("pgwbf22/pgwbf22",dic);
	}
	
		
	
	public PGWPaseDecodeBean(List<RowBean> rowBeanlist,
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
					afdecode.match(DicTion.pgwtrafficdatavolumesdic,rowbean);
					//对于ac中的具体内容只在调试时使用//正式使用时注掉
//					bytecont=HexString.bytesToHexString(b,pos+2,end);//ac 的具体内容
//					word.setValue(bytecont);
//					word.setID(tag);
//					rowbean.add(word,arrnum);
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
				word.setValue("pgw");
				word.setID(tag);
				if(tag.equals(startflag))
				{		
			
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
					bf22decode.match(pgwbf22sdic,rowbean);
					
					
					//用来校验输出的bf22标签的
//					WordUnit word=new WordUnit();
//					bytecont=HexString.bytesToHexString(b,pos+1,end);//bf22 的具体内容
//					word.setValue(bytecont);
//					word.setID(tag);
//					rowbean.add(word,arrnum);
					break;//返回end
				}
			}
		}
		return end;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fname="D:\\BSSdecode\\zjfee\\pgw\\test\\141022155937.l2sf02.zj2014102200690912.dat.gz";
		//String fname="D:\\BSSdecode\\zjfee\\pgw\\split\\pgw0.dat";
		String fout="D:\\BSSdecode\\zjfee\\pgw\\move\\test1.csv";
		String fhexString=null;
		RowBean.setListSize(61);
		List<RowBean> RowBeanlist = new LinkedList<RowBean>();
		TrafficDataDecodeMergeBean afdecode=new TrafficDataDecodeMergeBean();
		TmplatePaseDecodeBean pase=new PGWPaseDecodeBean(RowBeanlist,afdecode);

		pase.readGzTobytes(fname);
		pase.match(DicTion.pgwdic);
		List<RowBean> wordlist=pase.getWordlist();
		FileOutResultAllFieldBean file=new FileOutResultAllFieldBeanTag(DicTion.pgwhead);
		FileOutResultAllFieldBean.setListSize(61);
		file.setWordlist(wordlist);
		file.FileOutput(fout);

	}

}
