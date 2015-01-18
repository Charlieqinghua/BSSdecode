package lex;

import java.util.ArrayList;
import java.util.List;

import unicom.WordUnit;
import all.HexString;



public class SGWLex implements Lex{
	
	List<WordUnit> wordlist = new ArrayList<WordUnit>();
	byte []data;
	int pos=0;
	int datalen=0;
	public List<WordUnit> getWordlist() {
		return wordlist;
	}
	public void setWordlist(List<WordUnit> wordlist) {
		this.wordlist = wordlist;
	}
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
		datalen=0;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	boolean isTagPre(String c)
	{
       
         return true;
	}
	boolean isEqual(String byteString,String in)
	{
		boolean flag=false;
		if(byteString.equals(in)) flag=true;
		return flag;
	}
	/*
	 * byte	[]	data  
	 * i 	是tag后面的length字段  
	 * v 	value是String类型解析
	 */
	//way=3按照 ascii方式解码
	int yetRecgnizeAsString(byte []data,int i)
	{
		
		String tag=HexString.byteToHex(data[i-1]);
		int taglen=HexString.ComputeTagLengh(data,i);
		int end=i+taglen;
		if(taglen<0) return ++i;
		WordUnit word=new WordUnit();
		
		String bytecont=HexString.ExplainStartEndToString(data, ++i, end);
		word.setValue(bytecont);
		
		word.setID("0x"+tag);
		wordlist.add(word);
		return end;
	}
	//将 byte数组 转为 OctetString way=4
	int yetRecgnizeAsOctetString(byte []data,int i)
	{
		
		String tag=HexString.byteToHex(data[i-1]);
		int taglen=HexString.ComputeTagLengh(data,i);
		int end=i+taglen;
		if(taglen<0) return ++i;
		//if(end>=datalen) return datalen;
		WordUnit word=new WordUnit();
		
		String bytecont=HexString.byteToOctetString(data, ++i, end);
		word.setValue(bytecont);
		
		word.setID("0x"+tag);
		wordlist.add(word);
		return end;
	}
	
	//way=6处理方式
	int yetRecgnizeAsInteger(byte []data,int i)
	{
		String tag=HexString.byteToHex(data[i-1]);
		int taglen=HexString.ComputeTagLengh(data,i);
		if(taglen<0) return ++i;
		int end=i+taglen;
		WordUnit word=new WordUnit();
		
		String bytecont=HexString.ExplainStartEndToString(data, ++i, end);
		word.setValue(bytecont);
		
		word.setID(tag);
		wordlist.add(word);
		return end;
	}
	//way=5处理方式
	int yetRecgnizeAsBoolean(byte[] data,int i )
	{
		String tag=HexString.byteToHex(data[i-1]);
		int taglen=HexString.ComputeTagLengh(data,i);
		if(taglen<0) return ++i;
		int end=i+taglen;
		if(end>=datalen) return end;
		int bytecont=HexString.byteToInteger(data[end]);
		WordUnit word=new WordUnit();
		String result="NO";
		if(bytecont!=0) result="yes";
		word.setValue(result);
		
		word.setID(tag);
		wordlist.add(word);
		return end;
	}
	//way=12处理方式 按照hexSting返回
		int yetRecgnizeAsHexString(byte []data,int i)
		{
			String tag=HexString.byteToHex(data[i-1]);
			int taglen=HexString.ComputeTagLengh(data,i);
			if(taglen<0) return ++i;
			int end=i+taglen;
			WordUnit word=new WordUnit();
			
			String bytecont=HexString.bytesToHexString(data, ++i, end);
			word.setValue(bytecont);
			
			word.setID(tag);
			wordlist.add(word);
			return end;
		}
	
	/*
	 * 
	 * @see lex.Lex#pasepacket()
	 * state 0是状态开始
	 * state 2 choices
	 * state 4是value按照 octetString类型解析
	 * state 5是按照boolean类型解析
	 * state 6是按照integer类型解析
	 * state 7是按照ENUMERATED
	 * state 8是ContentInfo
	 * state 9是Null
	 * state 10是SEQUENCE
	 * state 11是 ia5 String
	 * state 12是 hexString输出
	 */
	@Override
	public void pasepacket() {
		// TODO Auto-generated method stub
		pos=0;
		int datalen=data.length;
		// 对所有字符进行检测
		int row = 1;// 数排列,横坐标
		
		int state = 0;// 状态标志
		// charAt(i) 读取i相对于当前位置的给定索引处的字符
		StringBuffer content=new StringBuffer();
		for (int i = pos; i < datalen; i++) 
		{
		
			byte now =data[i] ;
			String c=null;
			c=HexString.byteToHex(now);
			switch (state) {
			case 0:

			
			if (c.equals("bf"))
				state = 1;
			else if (c.equals("81"))
				state = 5;
			else if (c.equals("80"))
				state = 6;
			else if (c.equals("9f"))
				state = 11;
			else if (c.equals("83"))
				state = 4;
			else if (isEqual(c,"a4"))
				state = 4;
			else if (isEqual(c,"ac"))
				state = 4;
			else if (isEqual(c,"85"))
				state = 6;
			else if (isEqual(c,"a6"))
				state = 4;
			else if (isEqual(c,"87"))
				state = 3;
			else if (isEqual(c,"88"))
				state = 4;
			else if (isEqual(c,"a9"))
				state = 2;// 输入为回车(\n)
		

			else if (isEqual(c,"8b"))
				state = 5;
			else if (isEqual(c,"8d"))
				state = 4;
			else if (isEqual(c,"8e"))
				state = 6;
			else if (isEqual(c,"8f"))
				state = 6;

			else if (isEqual(c,"ac")) 
				state =12 ;
			
			else if (isEqual(c,"91")) state=6;
			else if ( isEqual(c,"92"))state=3;
			else if (isEqual(c,"94")) state=5;
			else if (isEqual(c,"95")) state=5;
			else if (isEqual(c,"96"))
			{ 
				state=4;
			}
			else if (isEqual(c,"97")) state=4;
			else if (isEqual(c,"98")) state=6;
			else if (isEqual(c,"99")) state=5;
			
			else 
			{
				WordUnit word = new WordUnit();//
				word.setValue(""+c);
				word.setID("未知");
				state=0;
				
			}
			break;
		case 1:// 标志符是 bf
				
			if (c.equals("4e")) //bf4e
			{
				state = 0;
				WordUnit word=new WordUnit();
				word.setValue("sgwcdr");
				
				word.setID("bf4e");
				wordlist.add(word);
			} 
			else if(c.equals("23"))
			{
				int end=yetRecgnizeAsOctetString(data,i);
				i=end;
			}
			else if(c.equals("24"))
			{
				int end=yetRecgnizeAsOctetString(data,i);
				i=end;
			}
			else if(c.equals("2a"))
			{
				state = 0;
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				String bytecont=HexString.ExplainStartEndToString(data, i, end);
				word.setValue(bytecont);
				word.setID("0xbf2a");
				wordlist.add(word);
			}
			else if(c.equals("2b"))
			{
				state = 0;
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				
				String bytecont=HexString.ExplainStartEndToString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0xbf2b");
				wordlist.add(word);
			}
			else 
			{
				state = 0;
				WordUnit word=new WordUnit();
				word.setValue("bf");
				
				wordlist.add(word);
				i--;
				row--;
			}
			break;
		case 2:// 标志符是 0x81
			
			{
				WordUnit word=new WordUnit();
				word.setValue("IP CAN承载");
				
				word.setID("0x81");
				wordlist.add(word);
				//i--;
				row--;
			}
			state = 0;
			break;
		case 3:// 标志符是 0x92
		{
			int end=yetRecgnizeAsString(data,i);
			i=end;
				
		}
			state = 0;
		break;
		case 11:// 标志符是 9f1f  后续length2
			if (c.equals("1f"))
				
			{
				state = 0;
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				
				String bytecont=HexString.byteToOctetString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f1f");
				wordlist.add(word);
				}
			else if(c.equals("20"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				//content.delete(0, content.length());
				//HexString.bytesToHexString(src)
				String bytecont=HexString.byteToOctetString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f20");
				wordlist.add(word);
			}
			else if(c.equals("21"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				//content.delete(0, content.length());
				//HexString.bytesToHexString(src)
				String bytecont=HexString.ExplainStartEndToString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f21");
				wordlist.add(word);
			}
			else if(c.equals("22"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				//content.delete(0, content.length());
				//HexString.bytesToHexString(src)
				String bytecont=HexString.ExplainStartEndToString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f22");
				wordlist.add(word);
			}
			else if(c.equals("23"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				//content.delete(0, content.length());
				//HexString.bytesToHexString(src)
				String bytecont=HexString.ExplainStartEndToString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f23");
				wordlist.add(word);
			}
			else if(c.equals("24"))
			{
				int end=yetRecgnizeAsOctetString(data,i);
				i=end;
			}
			else if(c.equals("25"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				//content.delete(0, content.length());
				//HexString.bytesToHexString(src)
				String bytecont=HexString.byteToOctetString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f25");
				wordlist.add(word);
			}
			else if(c.equals("26"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				
				String bytecont=HexString.byteToOctetString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f26");
				wordlist.add(word);
			}
			else if(c.equals("27"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				String bytecont=HexString.byteToOctetString(data, i, end);
				word.setValue(bytecont);
				word.setID("0x9f27");
				wordlist.add(word);
			}
			else if(c.equals("28"))
			{
				int taglen=HexString.ComputeTagLengh(data,++i);
				int end=i+taglen;
				WordUnit word=new WordUnit();
				String bytecont=HexString.ExplainStartEndToString(data, i, end);
				word.setValue(bytecont);
				
				word.setID("0x9f28");
				wordlist.add(word);
			}
			else 
			{
				WordUnit word=new WordUnit();
				word.setValue("*");
				
				wordlist.add(word);
				i--;
				row--;
			}
			state = 0;
			break;
		case 4:// 标志符是 0x83
		{
			int end=yetRecgnizeAsOctetString(data,i);
			i=end;
				
		}
			state = 0;
		break;
		case 5:// 标志符是 0xa4
		{
				
			int end=yetRecgnizeAsBoolean(data,i);
			 
			i=end;
		}
			state = 0;
			break;
		case 6:// 标志符是 #
		{
			int end=yetRecgnizeAsInteger(data,i);
			i=end;
			state = 0;
		}
		break;
		case 7:// 标志符是 0x83
		{
			int end=yetRecgnizeAsString(data,i);
			i=end;
				
		}
			state = 0;
		break;
		case 8:// 标志符是 0x83
		{
			int end=yetRecgnizeAsString(data,i);
			i=end;
				
		}
			state = 0;
		break;
		case 9:// 标志符是 0x83
		{
			int end=yetRecgnizeAsString(data,i);
			i=end;
				
		}
			state = 0;
		break;
		
		case 12:// 标志符是 0x83
		{
			int end=yetRecgnizeAsOctetString(data,i);
			i=end;
				
		}
			state = 0;
		break;
	}
		}
		}
	}

		
		
		
	
		

		
	
	



