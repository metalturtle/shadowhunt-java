package render.basic;

import basic.resource.static_resource.StaticWorldResourceObject;

public class TextureObject extends StaticWorldResourceObject
{
	public int texid;
	public float u1,v1,u2,v2;
	public boolean lightBlock;
	
	public TextureObject() {
		super(0,0,0,0);
	}
	
	public TextureObject(int texid,float x, float y, float w, float h,float u1,float v1,float u2,float v2,float angle,boolean lightBlock) {
		super(x, y, w, h);
		this.texid = texid;
		this.u1 = u1;
		this.v1 = v1;
		this.u2 = u2;
		this.v2 = v2;
		this.lightBlock = lightBlock;
	}
	
	@Override
	public String toString() {
		return super.toString()+" u1,v1: "+u1+","+v1+" u2,v2: "+u2+","+v2+" ";
//		return this+"";
	}
	
}