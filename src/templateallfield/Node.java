package templateallfield;

public class Node {
	byte data;
	Node []branch;
	int state=0;
	int way=0;
	int tlv=0;
	int pos=0;
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public Node(byte b)
	{
		this.data=b;
		branch=null;
	}
	public int getTlv() {
		return tlv;
	}
	public void setTlv(int tlv) {
		this.tlv = tlv;
	}
	public Node get(byte b)
	{
		//boolean flag=false;
		if(branch==null) return null;
		for(Node node:branch)
		{
			if(node.data==b) 
			{
				return node;
			} 
		}
		return null;
	}
	public int getExplainWay()
	{
		return way;
		
	}
	public void setWay(int way)
	{
		this.way=way;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public void  add(Node node)
	{
		
		int size=0;
		if(branch!=null)	size=branch.length;
		Node []newbranch=new Node[size+1];
		int i=0;
		if(branch!=null)
		{
			for(Node oldnode:branch)
			{
				newbranch[i]=oldnode;
				i++;
			}
		}	
		newbranch[i]=node;
		this.branch=null;//将原来的内存释放
		this.state=2;//后面插入一个节点证明这个节点状态时可以继续
		this.branch=newbranch;
		
	}
	}
