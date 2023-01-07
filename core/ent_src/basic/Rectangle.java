package basic;

import basic.resource.WorldResourceHandler;
import basic.resource.static_resource.StaticWorldResourceObject;
import render.GameCamera;

public class Rectangle {
	protected float x,y,w,h;
	static int precision = 1000;
	static int creatednum;
	Rectangle rect;
	public Rectangle() {
		x=y=w=h=0;
	}
	public Rectangle (float x,float y,float w,float h){
		float x1,y1,w1,h1;
		x1 = Math.min(x, x+w);
		y1 = Math.min(y, y+h);
		w1 = Math.abs(w);
		h1 = Math.abs(h);
		this.x=x1;
		this.y=y1;
		this.w=w1;
		this.h=h1;
		set_precise();

	}
	
	public Rectangle(Rectangle rect) {
		set(rect);
	}
	

	void set_precise() {
//		float fp = precision;
//		int intx = (int)(this.x*fp);
//		int inty = (int)(this.y*fp);
//		int intw = (int)(this.w*fp);
//		int inth = (int)(this.h*fp);
//		this.x = ((float)intx)/fp;
//		this.y = ((float)inty)/fp;
//		this.w = ((float)intw)/fp;
//		this.h = ((float)inth)/fp;
	}
	
	public float x(){ return x;	}
	public float y(){return y;}
	public float w(){return w;}
	public float h(){return h;}
	
	public void x(float x){ 
		this.x = x;
		set_precise();
	}
	public void y(float y){
		this.y = y;
		set_precise();
	}
	public void w(float w){
		this.w = w;
		set_precise();
	}
	public void h(float h){
		this.h = h;
		set_precise();
	}
	
	public void set (float x,float y,float w,float h){
		float x1,y1,w1,h1;
		x1 = Math.min(x, x+w);
		y1 = Math.min(y, y+h);
		w1 = Math.abs(w);
		h1 = Math.abs(h);
		this.x=x1;
		this.y=y1;
		this.w=w1;
		this.h=h1;
		set_precise();
	}
	
	public float center_x() {
		return x+w/2;
	}
	
	public float center_y() {
		return y+h/2;
	}
	
	public void set(Rectangle rect)
	{
		if(rect == null)
			return;
		set(rect.x,rect.y,rect.w,rect.h);
	}
	
	public static boolean check_rect_intersection(Rectangle a,Rectangle b) {
//		float epsilon = 0.0001f;
		//if((a.x>b.x+b.w+epsilon)||(a.x+a.w+epsilon<b.x)||(a.y>b.y+b.h+epsilon)||(a.y+a.h+epsilon<b.y) )
		if(Math.abs(a.x+a.w/2-(b.x+b.w/2))<(a.w+b.w)/2 &&(Math.abs(a.y+a.h/2-(b.y+b.h/2))<(a.h+b.h)/2))
		{
			return true;
		}
		return false;
	}
	
	
	public static boolean check_point_intersection(Rectangle r,Vector v)
	{
		if((v.x() >=r.x && v.x() <=r.x+r.w) &&(v.y()>=r.y && v.y()<= r.y+r.h))
		{
			return true;
		}
		return false;
	}
	
	public static void intersection(Rectangle a,Rectangle b,Rectangle intersect) {
		if(!check_rect_intersection(a,b)) {
//			return new Rectangle(0,0,0,0);
			intersect.set(0,0,0,0);;
		}
		Rectangle dest = intersect;
		dest.x(Math.max(a.x, b.x));
		dest.y(Math.max(a.y, b.y));
		dest.w(Math.min(a.x+a.w, b.x+b.w)-dest.x);
		dest.h(Math.min(a.y+a.h, b.y+b.h)-dest.y);
//		return dest;
	}
	
	public static void union(Rectangle a,Rectangle b, Rectangle intersect) {
		Rectangle dest = intersect;
		dest.x(Math.min(a.x, b.x));
		dest.y(Math.min(a.y, b.y));
		dest.w(Math.max(a.x+a.w, b.x+b.w)-dest.x);
		dest.h(Math.max(a.y+a.h, b.y+b.h)-dest.y);
	}
	
	@Override
	public String toString() {
		return x+" "+y+" "+w+" "+h;
	}
	
	
	public void get_float(float fval[]) {
		fval[0]=x;fval[1]=y;fval[2]=w;fval[3]=h;
	}
	
	
	public void set_float(float fval[]) {
		set(fval[0],fval[1],fval[2],fval[3]);
	}
}
