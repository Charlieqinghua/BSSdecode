package templateallfield;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import all.DicTion;

import templateBean.RowBean;
import unicom.CFile;
import unicom.WordUnit;

public class FileOutResultAllFieldOrader extends FileOutResultAllField {
	
	Queue<RowBean> rowbeanqueue=new LinkedList<RowBean>();
	static Map map=new HashMap();
	static
	{
		//map.put("\r\n0x80", 0);//使用rowBean控制输出去掉\r\n
		map.put("0x80", 0);
		map.put("0x83", 1);
		map.put("0x84", 2);
		map.put("0xa5", 3);
		map.put("0x86", 4);
		map.put("0x87", 5);
		map.put("0x88", 6);
		map.put("0x89", 7);
		map.put("0x8a", 8);
		map.put("0xab", 9);
		map.put("0x8c", 10);
		map.put("0x8d", 11);
		map.put("0xae", 12);
		map.put("0x83all", 13);
		map.put("0x84all", 14);
		map.put("0x85af", 15);
		map.put("0x86af", 16);
		map.put("0x90", 17);
		map.put("0x91", 18);
		map.put("0x93", 19);
		map.put("0xb5", 20);
		map.put("0x96", 21);
		map.put("0xb8", 22);
		map.put("0x99", 23);
		map.put("0x9a", 24);
		map.put("0x9b", 25);
		map.put("0x9c", 26);
		map.put("0x9d", 27);
		map.put("0x9f20", 28);
		map.put("0x9f21", 29);
		map.put("0x9f28", 30);
		map.put("0x9f32", 31);
		
		
	}
	public void FileOutput(String fout)
	{
		
		CFile cf=new CFile(fout);
		//String head="recordType,imsi,imei,sgsnip,msNetworkCapability,routingArea,lac,cellid,chargingID,ggsnip,apn,pdpType,pdpip,uplink,downlink,updownlinktime,recordExtensions,recordOpeningTime,duration,0x80,0x80,apnSelectionMode,accessPointName,msisdn,chargingCharacteristics,rATType,chChSelectionMode,dynamicAddressFlag,sgsnPLMNIdentifier";
		try {
			cf.writeHead(DicTion.scdrhead);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Iterator<WordUnit> it=null;
		RowBean rowbean=null;
		int pos=-1;
		
		for(it=wordlist.iterator();it.hasNext();)
		{
			WordUnit w=(WordUnit)it.next();
			String tag=null;
			tag=w.getID();
			String value=w.getValue();
			//if(tag.equals("\r\n0x80")&&value.equals("21"))//使用rowBean控制输出去掉\r\n
			if(tag.equals("0x80")&&value.equals("21"))
			{
				rowbean=new RowBean();
				rowbeanqueue.add(rowbean);			
			}
			if(rowbean!=null)
			{
				try {
					if(map.containsKey(tag)) 
					{
						pos=(Integer)map.get(tag);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(pos>=0) 
				{
					rowbean.add(w, pos);
					pos=-1;//恢复默认值
				}
			}

				
		}
		
		
		wordlist.clear();
		StringBuffer sb=new StringBuffer();
		int size=rowbeanqueue.size();
		while(size>0)
		{
			rowbean=rowbeanqueue.poll();
			if(rowbean!=null)
			{
				WordUnit []word=rowbean.getWordlist();
				int len=word.length;
				WordUnit wu=null;
				for(int i=0;i<DicTion.sgwrfieldsize;i++)
				{
					wu=word[i];
					if(wu==null)
					{
						sb.append("null");
						sb.append("-");
						sb.append("null");
						sb.append(",");
					}
					else
					{
						sb.append(wu.getID());
						sb.append("-");
						sb.append(wu.getValue());
						sb.append(",");
					}
					
					
					wu=null;
				}
				sb.append("\r\n");
			}
			size=rowbeanqueue.size();
		}
		try {
			cf.writeOneRow(sb.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
