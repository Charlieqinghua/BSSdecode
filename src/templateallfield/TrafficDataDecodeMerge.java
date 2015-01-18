package templateallfield;

import java.util.List;

import unicom.WordUnit;
import all.HexString;

//出现0x83与0x84要进行合并 计算总的流量 才加入 wordlist 所以 dealTLV 不需要传 wordlist
/**
 *@author Administrator
* @param  uplinksum 合并的上行流量
* @param  downlinksum 合并的下行流量
* @param  timestemp af标签中的时间
*/
public class TrafficDataDecodeMerge extends TrafficDataDecode {
	int uplinksum=0;
	int downlinksum=0;
	String timestemp=null;
	String change=null;
	public void match(DefineForest forest,List<WordUnit> wordlist)
	{
		
		uplinksum=0;//新的一个文件处理时要重新置 0；
		downlinksum=0;
		timestemp="";
		change="";
		if(b==null) return ;
		int len=end-start;
		if(len<=0)return ;
		
		int i=start;//和以前的区别 i=0;
		int p=0;//移动指针
		int state=0;
		int way=0;
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
							i=dealTLV(starttag,p,way);
						}
						else if(tlv==1)
						{
							i=dealTV(starttag,p,way);
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
		WordUnit up=new WordUnit();
		String uplinksumstr=String.format("%d", uplinksum);
		up.setValue(uplinksumstr);
		up.setID("0x83all");
		wordlist.add(up);
		
		WordUnit down=new WordUnit();
		String downlinksumstr=String.format("%d", downlinksum);
		down.setValue(downlinksumstr);
		down.setID("0x84all");
		wordlist.add(down);
		
		WordUnit changeCondition=new WordUnit();
		changeCondition.setValue(this.change);
		changeCondition.setID("0x85af");
		wordlist.add(changeCondition);
		
		WordUnit time=new WordUnit();
		time.setValue(timestemp);
		time.setID("0x86af");
		wordlist.add(time);
		
	}//里面是 t(tlv)结构
	public int dealTLV(int starttag,int pos,int way)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		
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
				
				 if(tag.equals("0x83")) 
				{
					int uplink=HexString.bytesToInterger(b,pos+2,end);
					uplinksum+=uplink;
				}
				else if(tag.equals("0x84"))
				{
					int downlink=HexString.bytesToInterger(b,pos+2,end);
					downlinksum+=downlink;
				}
				else if(tag.equals("0x85"))
				{
					bytecont=HexString.bytesToHexString(b,pos+2,end);
					this.change=bytecont;
					//bean.setTime(time);
				}
				else if(tag.equals("0x86"))
				{
					bytecont=HexString.bytesToHexString(b,pos+2,end);
					timestemp=bytecont;
					//bean.setTime(time);
				}
				else bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
				
			}
			
		}
		
		
		return end;
	}
	/*有些标签是tv结构 例如 3032|0
	3036|0
	3038|0
	 */
	public int dealTV(int starttag,int pos,int way)//处理完 tlv 返回 最后的位置
	{
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		
		
		return pos;
	}
}
