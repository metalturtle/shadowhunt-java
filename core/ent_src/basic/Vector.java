package basic;

import util.GameInfo;

//import java.io.Serializable;
public strictfp class Vector {
  

//private static final long serialVersionUID = 1L;
public static final float EPSILON = 0.0001f;
private float x;
private float y;

  public Vector() {}
  public Vector(float x, float y) {
    this.x = x;
    this.y = y;
    set_precise();
  }
  
  public Vector(Vector v)
  {
	  this.x = v.x;
	  this.y = v.y;
	  set_precise();
  }
	
  void set_precise() {
//	 x = GameInfo.f2deg(x);
//	 y = GameInfo.f2deg(y);
  }
  
  public float x() {
    return x;
  }
  
  public float y() {
    return y;
  }
  
  public void x(float x) {
	  this.x = x;
	  set_precise();
  }
  
  public void y(float y) {
	  this.y = y;
	  set_precise();
  }
  
  public void set(float x, float y) {
    this.x = x;
    this.y = y;
    set_precise();
  }
  
  public void set(Vector vec)
  {
	  this.x = vec.x;
	  this.y = vec.y;
	  set_precise();
  }
  
  public float length() {
    return (float)Math.sqrt(lengthSquared()); 
  }
  
  public float lengthSquared() {
    return this.dot(this);
  }
  
  public float dot(Vector v2) {
    return x * v2.x + y * v2.y;
  }
  
  public void add(Vector v2) {
    set(x + v2.x, y + v2.y);
  }
  
  public void substract(Vector v2) {
    set(x - v2.x, y - v2.y);
  }
  
  public void multiply(float constant) {
    set(x * constant, y * constant);
  }
  
  public void unitVector() {
    float length = length();
    if(length==0) return;
    multiply(1/length);
  }
  
//  public void normal() {
//    setXY(-y, x);
//  }
  
  public float bearing() {
    return (float) Math.atan2(y, x);
  }
  
  public static Vector copyOf(Vector vector) {
    return new Vector(vector.x, vector.y);
  }
  
  public void SetAngle(float angle) {
	  set((float)Math.cos(Math.toRadians(angle)),(float) Math.sin(Math.toRadians(angle)));
  }
  
  public void SetRadian(float radian) {
	  set((float)Math.cos(-radian),(float) Math.sin(-radian));
  }
  
  public static float GetAngle(Vector a)
  {
	  float angle;
	  angle = (float)Math.atan2(a.y(),a.x());
	  angle = (float)Math.toDegrees(angle);
	  return angle;
	  
  }
  public static float distance(Vector a,Vector b)
  {
	  float x = a.x()-b.x();
	  float y = a.y()-b.y();
	  return (float)Math.sqrt(x*x+y*y);
  }
  
  public boolean equals(Vector p) {
	  return Math.abs(p.x- x) < 0.01f && Math.abs(p.y- y)<0.01f;
  }
  
  
  @Override
  public String toString() {
	  return "x,y:"+x+" "+y; 
  }
}
