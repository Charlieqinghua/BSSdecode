package templateBean;

import java.util.List;

import templateallfield.DefineForest;
import templateallfield.Node;
import templateallfield.TrafficDataDecode;
import unicom.WordUnit;
import all.HexString;

//出现0x83与0x84要进行合并 计算总的流量 才加入 wordlist 所以 dealTLV 不需要传 wordlist

/**
 * @author Administrator
 * 处理 af标签时，处理ＴＬＶ中的Ｖ解释。全部匹配ａｆ标签的ｌｅｎｇｈ之后将流量的汇总结果输出到＼
 * rowbean中
 *
 */
public class TrafficDataDecodeMergeBean extends TrafficDataDecode {
	int uplinksum=0;
	int downlinksum=0;
	String timestemp=null;
	String change=null;
	StringBuffer timechange=new StringBuffer();
	StringBuffer timedate=new StringBuffer();
	public void match(DefineForest forest,RowBean rowbean)
	{
		uplinksum=0;//新的一个文件处理时要重新置 0；
		downlinksum=0;//新的一个文件处理时要重新置 0；
		timestemp="";//新的一个文件处理时要重新置 0；
		change="";//新的一个文件处理时要重新置 0；
		timechange.delete(0, timechange.length());//新的一个文件处理时要重新置 0；
		timedate.delete(0,timedate.length());
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
		//到这里说明af标签的长度已经匹配完成，要往rowbean里填充字段。
		WordUnit up=new WordUnit();
		String uplinksumstr=String.format("%d", uplinksum);
		up.setValue(uplinksumstr);
		up.setID("0x83all");
		rowbean.add(up,13);
		
		WordUnit down=new WordUnit();
		String downlinksumstr=String.format("%d", downlinksum);
		down.setValue(downlinksumstr);
		down.setID("0x84all");
		rowbean.add(down,14);
		
//		WordUnit changeCondition=new WordUnit();
//		changeCondition.setValue(this.change);
//		changeCondition.setID("0x85af");
//		rowbean.add(changeCondition,15);
		
		WordUnit time=new WordUnit();
		timeChange(timestemp);//转换时间格式
		timedate.append(timechange);//
		timestemp=timechange.toString();
		time.setValue(timestemp );
		
		time.setID("0x86af");
		rowbean.add(time,16);
		
		//根据福东需求最后添加一个用于分区的字段；
		//格式yyyymmdd
		WordUnit date=new WordUnit();
		//timeChange();//转换时间格式
		//timestemp=timechange.toString();
		//timedate.substring(0,8);
		date.setValue(timedate.substring(0,8).toString());
		date.setID("0xdate");
		rowbean.add(date,15);
		
		//clause
		
	}//里面是 t(tlv)结构
	//按照 YYYYMMDDMMSS输出日期
	public void timeChange(String time)
	{
		timechange.append("20");
		try {
			this.timechange.append(time.substring(0,12));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
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
					//bean.setTime(time);
					change=bytecont;
				}
				else if(tag.equals("0x86"))
				{
					bytecont=HexString.bytesToHexString(b,pos+2,end);
					timestemp=bytecont;
					if(timestemp.length()<12)
					{
						System.out.println("error");
					}
					//bean.setTime(time);
				}
				else if(tag.equals("0xa9"))
				{
					bytecont=HexString.bytesToHexString(b,pos+2,end);
					//timestemp=bytecont;
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
