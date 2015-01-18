package templateallfield;

import java.util.ArrayList;
import java.util.List;
import unicom.WordUnit;
import all.HexString;
//对于af 流量细节的解码不进行合并，输出每次up和down的结果和 TrafficDataDecodeMerge可以进行对比
/**
 * @author Administrator
 * @param b 解码输入的比特串
 * @param  start 比特串开始位置
 * @param  end 比特串结束位置
 */
public class TrafficDataDecode {
	public byte[] b;
	public int start;//指针开始的位置
	public int end;//指针结束的位置
	
	public void setParament(byte[]data,int s,int e)
	{
		b=data;
		start=s;
		end=e;
	}
	
	
	public void match(DefineForest forest,List<WordUnit> wordlist)
	{
		
		if(b==null) return ;
		//int len=b.length;
		int len=end-start;
		if(len<=0)return ;
		
		int i=start;//和以前的区别 i=0;
		int p=0;//移动指针
		int state=0;
		int way=0;//处理方式表示按照 ascii，octet，还是hexstring解析16进制串
		int tlv=0;//为1按照tv格式，为0按照tlv格式
		//int startpos=0;
		int starttag=0;//记录tag标签的初始位置
		Node branch=null;//得到可以匹配的枝
		Node branchnext=null;
		Node root=forest.getRoot();
		branch=root;//开始从根节点匹配
		while(i<=end)
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
						
						tlv=branchnext.getTlv();
						if(tlv==0)
						{
							i=dealTLV(starttag,p,way,wordlist);
						}
						else if(tlv==1)
						{
							i=dealTV(starttag,p,way,wordlist);
						}
						branch=root;
						starttag=0;
						break;
					}
					case 0://匹配失败
					{
						branch=branchnext;
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
		
	}//里面是 t(tlv)结构
	public int dealTLV(int starttag,int pos,int way,List <WordUnit> wordlist)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		//String tag=HexString.byteToHex(b[pos]);
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		String bytecont=null;
		//TrafficDataVolumes bean=null;
		if(taglen<0) 
		{
			
			return pos+1;
		}
		int end=pos+taglen+1;
		
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.ExplainStartEndToString(b,pos+2,end);
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
				
//				if(tag.equals("0x82")) 
//				{
//					bytecont=HexString.bytesToIntergerString(b,pos+2,end);
//									
//				}
				 if(tag.equals("0x83")) 
				{
					bytecont=HexString.bytesToIntergerString(b,pos+2,end);
					//bean.setDownlink(downlink);
				}
				else if(tag.equals("0x84"))
				{
					bytecont=HexString.bytesToIntergerString(b,pos+2,end);
					//bean.setClause(clause);
				}
				else if(tag.equals("0x85"))
				{
					bytecont=HexString.bytesToHexString(b,pos+2,end);
					//bean.setTime(time);
				}
				else if(tag.equals("0x86"))
				{
					bytecont=HexString.bytesToHexString(b,pos+2,end);
					//bean.setTime(time);
				}
				else bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
				
			}
			
		}
		word.setValue(bytecont);
		word.setID(tag);
		wordlist.add(word);
		
		return end;
	}
	/*有些标签是tv结构 例如 3032|0
	3036|0
	3038|0
	 */
	public int dealTV(int starttag,int pos,int way,List <WordUnit> wordlist)//处理完 tlv 返回 最后的位置
	{
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		WordUnit word=new WordUnit();
		word.setValue("");
		word.setID(tag);
		wordlist.add(word);
		return pos;
	}
}