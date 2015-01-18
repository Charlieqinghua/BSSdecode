package templateallfield;

import java.util.List;


import unicom.WordUnit;
import all.DicTion;
import all.HexString;

/**
 * @author wwh
 * @param  afdecode scdr嵌入式标签 af的解析类
 * @category
 * 
 * scdr 的解码类继承自TmplatePaseDecode重写了
 * public int dealTLV(int starttag,int pos,int way) 函数
 * 对每个标签处理结果合成方式不一样
 * param TrafficDataDecode af标签处理的解码器
 * ScdrPaseDecode 解码之后和FileOutResultAllFieldOrader一起使用控制结果输出。
 * ScdrPaseDecodeAllField 解码之后，输出格式字自己控制的。
 */
public class ScdrPaseDecode extends TmplatePaseDecode {
	//TrafficDataDecode afdecode=new TrafficDataDecode();
	TrafficDataDecode afdecode;
	public ScdrPaseDecode(TrafficDataDecode afdecode) {
		super();
		this.afdecode = afdecode;
	}
	public ScdrPaseDecode() {
		super();
		afdecode=new TrafficDataDecodeMerge();
	}
	public int dealTLV(int pos,int way)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		
		String tag=HexString.byteToHex(b[pos]);
		tag="0x"+tag;
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		String bytecont=null;
		if(taglen<0&&(!tag.equals("0xaf"))) 
		{
			word.setValue(HexString.byteToHex(b[pos+1]));
			word.setID(tag);
			wordlist.add(word);
			return pos+1;
		}
		int end=pos+taglen+1;
		
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.ExplainStartEndToString(b,pos+2,end);
				//bytecont="\r\n"+bytecont;
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
			case 10://squence 的处理方式
			{
				if(tag.equals("0xaf"))
				{
					
//					word.setValue("{");
//					word.setID("0xaf");
//					wordlist.add(word);
					afdecode.setParament(b,pos+2,end);
					//List <TrafficDataVolumes> aflist=afdecode.match(DicTion.trafficdatavolumesdic);
					afdecode.match(DicTion.trafficdatavolumesdic,wordlist);
					if(taglen<0)
					{
						taglen=256+taglen; 
						end=pos+taglen+1;
					}
//					word=new WordUnit();
//					word.setValue("}");
//					word.setID("-0xaf");
//					wordlist.add(word);
					return end;
				}
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
		wordlist.add(word);
		return end;
	}
	public int dealTLV(int starttag,int pos,int way)//处理完 tlv 返回 最后的位置
	{
		int endpos=0;
		
		String tag=HexString.bytesToHexString(b,starttag,pos);
		tag="0x"+tag;
		
		int taglen=HexString.ComputeTagLengh(b,pos+1);
		WordUnit word=new WordUnit();
		String bytecont=null;
		if(taglen<0) 
		{
			word.setValue(HexString.byteToHex(b[pos+1]));
			word.setID(tag);
			wordlist.add(word);
			return pos+1;
		}
		int end=pos+taglen+1;
		
		switch(way)
		{
			
			case 0://开始
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				//bytecont="\r\n"+bytecont;
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
//				if(tag.equals("0x80")&&bytecont.equals("21")) 
//				{
//					tag="\r\n"+tag;
//				}
//				使用 rowbean之后输出在FileoutResult中控制
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
				if(tag.equals("0xaf"))
				{
					
//					word.setValue("{");
//					word.setID("0xaf");
//					wordlist.add(word);
					afdecode.setParament(b,pos+2,end);
					//List <TrafficDataVolumes> aflist=afdecode.match(DicTion.trafficdatavolumesdic);
					afdecode.match(DicTion.trafficdatavolumesdic,wordlist);
//					word=new WordUnit();
//					word.setValue("}");
//					word.setID("-0xaf");
//					wordlist.add(word);
					return end;
				}
				//break;
			}
			default:
			{
				bytecont=HexString.bytesToHexString(b,pos+2,end);
				break;
			}
			
		}
	
		word.setValue(bytecont);
		word.setID(tag);
		if(!tag.equals("\r\n0xb482"))wordlist.add(word);
		//wordlist.add(word);
		return end;
	}

}
