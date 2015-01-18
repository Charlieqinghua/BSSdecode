package templateBean;

import java.util.LinkedList;
import java.util.List;

import all.HexString;
import templateallfield.DefineForest;
import templateallfield.Node;
import templateallfield.TrafficDataDecode;
import unicom.WordUnit;

/**
 * @author Administrator
 * 这个bean是流量解码时处理TV结构的bean,while循环不能根据length退出。
 * 只能根据下一个标签退出
 *
 */
public class TrafficDataDecodeMergeBeanTV extends templateallfield.TrafficDataDecode {
	String quittag="0xbf23";
	int uplinksum=0;
	int downlinksum=0;
	String timestemp=null;
	String change=null;
	StringBuffer timechange=new StringBuffer();
	StringBuffer timedate=new StringBuffer();
	int tvend=0;//判断出的tv的长度。
	List<InterRowBean> interBeanlist = new LinkedList<InterRowBean>();//内部squenece解码
	public InterRowBean interrowbean=null;//必须是全局的变量
	public int getTvend() {
		return tvend;
	}
	public void setTvend(int tvend) {
		this.tvend = tvend;
	}
	public void match(DefineForest forest,RowBean rowbean)
	{
		uplinksum=0;//新的一个文件处理时要重新置 0；
		downlinksum=0;
		timestemp="";
		change="";
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
		int arrnum=0;
		Node branch=null;//得到可以匹配的枝
		Node branchnext=null;
		Node root=forest.getRoot();
		branch=root;//开始从根节点匹配
		String tag=null;
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
						//
							//匹配成功之后看 是否是退出标签
						
						
						tag=HexString.bytesToHexString(b,starttag,p);
						tag="0x"+tag;
						if(tag.equals(quittag))
						{
							tvend=p-2;
							break;
						}
						//
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
						starttag=0;
						tag=null;//上次的标签处理结束恢复值
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
			if(tag!=null)
			{
				if(tag.equals(quittag))
				{
					break;//说明bf22标签处理结束退出循环
				}
			}
			i++;//没有匹配上直接后跳一个未知
		}
		//到这里说明af标签的长度已经匹配完成，要往rowbean里填充字段。
		//在这里进行本层协议的合并
		WordUnit mergeword=new WordUnit();
		StringBuffer sb=new StringBuffer();
		int []a={0,0};//a[0] 0x8c得值  a[1] 0x8d 
		String id=null;
		String value=null;
		/*生成 
		0xmerge-{{0x306c-start}{0x81-01}{0x82-755f705f33676e6574}
		{0x84-null}{0x85-1410221518192b0800}{0x86-1410221518192b0800}
		{0x87-00}{0x88-0080}{0xa9-810107820100830100840100850100860158}
		{0xaa-8004dcceb45e}{0x8c-0}{0x8d-0}{0x8e-422250496}{0x91-00fe0affff}
		{0x94-0064f0103e8b11e6}}
		对于内部标签进行合成
		*/
		for(InterRowBean bean:interBeanlist)
		{
			sb.append("{");
			for(WordUnit interword:bean.getWordlist())
			{
				if(interword!=null)
				{
					sb.append("{");
					id=interword.getID();
					sb.append(id);
					
					//if((interword.getID()).equals("0x8c"))
					sb.append("-");
					value=interword.getValue();
					sb.append(value);
					//对上下行流量进行统计
					if(id.equals("0x8c"))
					{
						int vc=Integer.parseInt(value);
						a[0]+=vc;
					}
					else if(id.equals("0x8d"))
					{
						int vd=Integer.parseInt(value);
						a[1]+=vd;
					}
					//取得内部计费时间
					if(id.equals("0x85"))
					{
						//int vc=Integer.parseInt(value);
						//a[0]+=vc;
						
						if(value!=null) timestemp=value;
						
					}
					value=null;
					id=null;
					sb.append("}");
				}
			}
			sb.append("}");
			bean=null;
		}
		String mergecont=sb.toString();
		mergeword.setValue(mergecont);
		mergeword.setID("0xmerge");
		rowbean.add(mergeword,59 );
		
		WordUnit sumword=new WordUnit();
		String sum="{0x8c-"+a[0]+"}"+"{0x8d-"+a[1]+"}";
		sumword.setValue(sum);
		sumword.setID("0xsumword");
		rowbean.add(sumword,60 );
		
		WordUnit uplink=new WordUnit();
		String up=""+a[0];
		
		uplink.setValue(up);
		uplink.setID("0xbf22-8c");
		rowbean.add(uplink,13);
		
		WordUnit downlink=new WordUnit();
		String down=""+a[1];
		
		downlink.setValue(down);
		downlink.setID("0xbf22-8d");
		rowbean.add(downlink,14);
		
		//根据福东要求计算分区时间
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
		
		//
		
		sb=null;
		interBeanlist.clear();
		
		

		
		
		//clause
		
	}//里面是 t(tlv)结构
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
	public int dealTLV(int starttag,int pos,int way,int arrnum)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		//String bytecont=null;
		String bytecont=null;
		if(taglen<0) 
		{
			return pos+1;
		}
		int end=pos+taglen+1;
		if(tag.equals("0x8d")&&(taglen>4))
		{
			System.out.println("error");
		}
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.ExplainStartEndToString(b,pos+2,end);
				//tag="\r\n"+tag;
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
			//按照整形处理
			case 13:
			{
				bytecont=HexString.bytesToIntergerString(b,pos+2,end);
				break;
			}
			default:
			{
				
//				 if(tag.equals("0x83")) 
//				{
//					int uplink=HexString.bytesToInterger(b,pos+2,end);
//					uplinksum+=uplink;
//				}
//				else if(tag.equals("0x84"))
//				{
//					int downlink=HexString.bytesToInterger(b,pos+2,end);
//					downlinksum+=downlink;
//				}
//				else if(tag.equals("0x85"))
//				{
//					bytecont=HexString.bytesToHexString(b,pos+2,end);
//					//bean.setTime(time);
//					change=bytecont;
//				}
//				else if(tag.equals("0x86"))
//				{
//					bytecont=HexString.bytesToHexString(b,pos+2,end);
//					timestemp=bytecont;
//					//bean.setTime(time);
//				}
//				else bytecont=HexString.bytesToHexString(b,pos+2,end);
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
				
			}
			
		}
		word.setValue(bytecont);
		word.setID(tag);
		//注意这里往interbean中注入内容
		if(interrowbean!=null)
		{
			interrowbean.add(word,arrnum);
		}
		
		return end;
	}
	/*有些标签是tv结构 例如 3032|0
	3036|0
	3038|0
	egcdrbf22中 tv标签全部作为内部协议分析作为 interbean 的开始
	 */
	public int dealTV(int starttag,int pos,int way,int arrnums)//处理完 tlv 返回 最后的位置
	{
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		WordUnit word=new WordUnit();
		//String bytecont=null;
//		/String bytecont=null;
		word.setID(tag);
		word.setValue("start");
		interrowbean=new InterRowBean();
		interrowbean.add(word, arrnums);
		interBeanlist.add(interrowbean);
		return pos;
	}
}
