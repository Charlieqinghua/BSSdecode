package templateBean;

import templateallfield.DefineForest;
import templateallfield.Node;
import unicom.WordUnit;
import all.HexString;

public class TrafficDataDecodeMergeBeanEgcdrTV extends
		TrafficDataDecodeMergeBeanTV 
		{
	public void match(DefineForest forest,RowBean rowbean)
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
		rowbean.add(uplink,62);
		
		WordUnit downlink=new WordUnit();
		String down=""+a[1];
		
		downlink.setValue(down);
		downlink.setID("0xbf22-8d");
		rowbean.add(downlink,63);
		
		sb=null;
		interBeanlist.clear();
		
		
//		WordUnit up=new WordUnit();
//		String uplinksumstr=String.format("%d", uplinksum);
//		up.setValue(uplinksumstr);
//		up.setID("0x83all");
//		rowbean.add(up,13);
//		
//		WordUnit down=new WordUnit();
//		String downlinksumstr=String.format("%d", downlinksum);
//		down.setValue(downlinksumstr);
//		down.setID("0x84all");
//		rowbean.add(down,14);
//		
//		WordUnit changeCondition=new WordUnit();
//		changeCondition.setValue(this.change);
//		changeCondition.setID("0x85af");
//		rowbean.add(changeCondition,15);
//		
//		WordUnit time=new WordUnit();
//		time.setValue(timestemp);
//		time.setID("0x86af");
//		rowbean.add(time,16);
		
		
		//clause
		
	}//里面是 t(tlv)结构

}
