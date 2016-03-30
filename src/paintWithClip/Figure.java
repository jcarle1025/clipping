/*
  This file contains classes representing geometric objects
*/
package paintWithClip;

import com.jogamp.opengl.*;
import java.util.*;

class Point {
    public float x,y;
    
    Point(float x,float y){
    	this.x=x; this.y=y;
    }
}

class Polygon {
	ArrayList<Point> verts = new ArrayList<Point>();
	ArrayList<Point> input = new ArrayList<Point>();
	float xl,xr,yb,yt;
	
	public Polygon (ArrayList<Point> verts){
		this.verts = verts;
		this.input = verts;
	}
	
	public void draw(GL2 gl, int how){
		gl.glLogicOp(how);
		gl.glBegin(GL2.GL_POLYGON);
		for (Point p : verts){
			gl.glVertex2f(p.x, p.y);
		}
		gl.glEnd();
		gl.glFlush();
	}
	
	public void clipLineLoop(ArrayList<Edge> bs){
		Point pNow, pNext;
		
		for(Edge e : bs){
			ArrayList<Point> output = new ArrayList<Point>();
					
												System.out.print("\n"+e.name.toUpperCase()+"\nInput: [ ");
												for(Point a : input){
													System.out.print("("+a.x+","+a.y+") ");
												}
												System.out.println(" ]");
			
			//see if you can expand this, and say if its out of bounds at the end, then
			//go to the beginning again
			for (int i=0; i>=0; i++){
				if (i>=input.size()-1){
					i=0;
				}
				pNow = input.get(i);
				pNext = input.get(i+1);
				
				if(inside(pNow,e)){
					if(inside(pNext,e)){
						if(output.contains(pNext)){
							output.add(output.get(0));
							break;
						}
						output.add(pNext);
						System.out.println("in-in");
					}
					else{
						System.out.println("in-out");
						if(output.contains(myIntersect(pNow,pNext,e))){
							output.add(output.get(0));
							break;
						}
						output.add(myIntersect(pNow,pNext,e));
					}
				}
				else{
					if (inside(pNext,e)){
						System.out.println("out-in");
						if(output.contains(myIntersect(pNow,pNext,e))){
							output.add(output.get(0));
							break;
						}
						output.add(myIntersect(pNow,pNext,e));
						if(output.contains(pNext)){
							output.add(output.get(0));
							break;
						}
						output.add(pNext);
					}
				}
			}
												System.out.print("Output at the end: ");
												for(Point a : output){
													System.out.print("("+a.x+","+a.y+") ");
												}
												System.out.print("\n");
			input = output;
		}
		verts = input;
	}
	
	//get intersection of the line p0-p1 and e
	private Point myIntersect(Point pa, Point pb, Edge e){
		double m = (pb.y-pa.y)/(pb.x-pa.x);
		double t;
		//x clip edge (y=e.val)
		if(e.name.contains("y")){
			double temp = e.val-pa.y;
			t = (temp/m)+pa.x;
			return new Point((float) t,e.val);
		}
		//dealing with a y clip edge (x=e.val)
		else{
			double temp = m*(e.val-pa.x);
			t = temp+pa.y;
			return new Point(e.val, (float) t);
		}
	}
	
	//determine whether p is "inside" or "outside" the edge
	private boolean inside(Point p, Edge e){
		if(e.name.equals("xLeft") && p.x > e.val)
			return true;
		else if (e.name.equals("xRight") && p.x < e.val)
			return true;
		else if (e.name.equals("yBot") && p.y > e.val)
			return true;
		else if (e.name.equals("yTop")&& p.y < e.val)
			return true;
		return false;
	}
}

class Line {
	Point p1, p2;
	double tmin,tmax,dx,dy;
	
	
	Line(Point p1, Point p2){
		this.tmin=0;
		this.tmax=1;
		this.p1=p1;
		this.p2=p2;
		this.dx=p2.x-p1.x;
		this.dy=p2.y-p1.y;
	}
	
	public void draw(GL2 gl, int how){
		gl.glLogicOp(how);
		gl.glBegin(GL2.GL_LINE_STRIP);
		gl.glVertex2f(p1.x, p1.y);
		gl.glVertex2f(p2.x, p2.y);
		gl.glEnd();
		gl.glFlush();
	}
	
	public void clipLine(ArrayList<Edge> bounds){
		double dir=0,t=0,chg=0;
		double x1,y1,x2,y2;
		
		for(Edge e : bounds){
			if(e.name.equals("xLeft")){
				dir = -1*dx;
				t=-1*(e.val-p1.x);
			}
			else if (e.name.equals("xRight")){
				dir = dx;
				t = e.val-p1.x;
			}
			else if (e.name.equals("yBot")){
				dir=-1*dy;
				t=-1*(e.val-p1.y);
			}
			else if (e.name.equals("yTop")){
				dir=dy;
				t=e.val-p1.y;
			}
			chg=t/dir;
			
			//erase
			if (dir==0 && t<0){
				tmin=0;
				tmax=0;
			}
			
			else if (dir<0){
				//erase
				if(chg>tmax){
					tmax=0;
					tmin=0;
				}
				else if (chg>tmin)
					tmin=chg;
			}
			else if (dir>0){
				//erase
				if(chg<tmin){
					tmin=0;
					tmax=0;
				}
				else if (chg<tmax)
					tmax=chg;
			}
		}
		x1=p1.x+tmin*dx;
		y1=p1.y+tmin*dy;
		x2=p1.x+tmax*dx;
		y2=p1.y+tmax*dy;
		p1=new Point((float) x1,(float) y1);
		p2=new Point((float) x2,(float) y2);
	}
}
	

class LineLoop {
	ArrayList<Point> verts = new ArrayList<Point>();
	ArrayList<Point> input = new ArrayList<Point>();
	
	public LineLoop (ArrayList<Point> verts){
		this.verts = verts;
		this.input = this.verts;
	}
	
	public void draw(GL2 gl, int how){
		gl.glLogicOp(how);
		gl.glBegin(GL2.GL_LINE_STRIP);
		for (Point p : verts){
			gl.glVertex2f(p.x, p.y);
		}
		gl.glEnd();
		gl.glFlush();
	}
	
	public ArrayList<Point> getVerts(){
		return verts;
	}
	
	public void clipLineLoop(ArrayList<Edge> bs){
		Point pNow, pNext;
		
		for(Edge e : bs){
			ArrayList<Point> output = new ArrayList<Point>();

			for (int i=0; i>=0; i++){
				if (i>=input.size()-1){
					i=0;
				}
				pNow = input.get(i);
				pNext = input.get(i+1);
				
				if(inside(pNow,e)){
					if(inside(pNext,e)){
						if(output.contains(pNext)){
							output.add(output.get(0));
							break;
						}
						output.add(pNext);
						System.out.println("in-in");
					}
					else{
						System.out.println("in-out");
						if(output.contains(intersect(pNow,pNext,e))){
							output.add(output.get(0));
							break;
						}
						output.add(intersect(pNow,pNext,e));
					}
				}
				else{
					if (inside(pNext,e)){
						System.out.println("out-in");
						if(output.contains(intersect(pNow,pNext,e))){
							output.add(output.get(0));
							break;
						}
						output.add(intersect(pNow,pNext,e));
						if(output.contains(pNext)){
							output.add(output.get(0));
							break;
						}
						output.add(pNext);
					}
				}
			}
			input = output;
		}
		verts = input;
	}
	
	//get intersection of the line p0-p1 and e
	private Point intersect(Point pa, Point pb, Edge e){
		double m = (pb.y-pa.y)/(pb.x-pa.x);
		double t;
		//x clip edge (y=e.val)
		if(e.name.contains("y")){
			double temp = e.val-pa.y;
			t = (temp/m)+pa.x;
			return new Point((float) t,e.val);
		}
		//dealing with a y clip edge (x=e.val)
		else{
			double temp = m*(e.val-pa.x);
			t = temp+pa.y;
			return new Point(e.val, (float) t);
		}
	}
	
	//determine whether p is "inside" or "outside" the edge
	private boolean inside(Point p, Edge e){
		if(e.name.equals("xLeft") && p.x > e.val)
			return true;
		else if (e.name.equals("xRight") && p.x < e.val)
			return true;
		else if (e.name.equals("yBot") && p.y > e.val)
			return true;
		else if (e.name.equals("yTop")&& p.y < e.val)
			return true;
		return false;
	}
}

class FilledRectangle {
    Point p1,p2;
    
    FilledRectangle(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
    }
    
    public void draw(GL2 gl, int how){
		gl.glLogicOp(how);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2f(p1.x,p1.y);
		gl.glVertex2f(p1.x,p2.y);
		gl.glVertex2f(p2.x,p2.y);
		gl.glVertex2f(p2.x,p1.y);
		gl.glEnd();
		gl.glFlush();
    }
public void clipARect(ArrayList<Edge> bs){
		
		for(Edge e : bs){
			if(e.name.equals("xLeft")){
				if (e.val > p1.x && !isBackwards())
					p1.x = e.val;
				else if (e.val > p2.x)
					p2.x = e.val;
			}
			else if (e.name.equals("xRight")){
				if (e.val < p2.x && !isBackwards())
					p2.x = e.val;
				else if (e.val < p1.x)
					p1.x = e.val;
			}
			else if (e.name.equals("yBot")){
				if (e.val > p1.y && !isBackwards())
					p1.y = e.val;
				else if (e.val > p2.y)
					p2.y = e.val;
			}
			else if (e.name.equals("yTop")){
				if (!isBackwards() && e.val < p2.y)
					p2.y = e.val;
				else if (e.val < p1.y)
					p1.y = e.val;
			}
		}
	}
	
	private boolean isBackwards(){
		if (p1.x > p2.x)
			return true;
		return false;
	}
}

class Rectangle {
	Point p1,p2;
	
	Rectangle(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
	}
	
	public void draw(GL2 gl, int how){
		gl.glLogicOp(how);
		
		gl.glBegin(gl.GL_LINE_STRIP);
		gl.glVertex2f(p1.x,p1.y);
		gl.glVertex2f(p1.x,p2.y);
		gl.glVertex2f(p2.x,p2.y);
		gl.glVertex2f(p2.x,p1.y);
		gl.glVertex2f(p1.x, p1.y);
		gl.glEnd();
		gl.glFlush();
	}
	
	public void clipARect(ArrayList<Edge> bs){
		
		for(Edge e : bs){
			if(e.name.equals("xLeft")){
				if (e.val > p1.x && !isBackwards())
					p1.x = e.val;
				else if (e.val > p2.x)
					p2.x = e.val;
			}
			else if (e.name.equals("xRight")){
				if (e.val < p2.x && !isBackwards())
					p2.x = e.val;
				else if (e.val < p1.x)
					p1.x = e.val;
			}
			else if (e.name.equals("yBot")){
				if (e.val > p1.y && !isBackwards())
					p1.y = e.val;
				else if (e.val > p2.y)
					p2.y = e.val;
			}
			else if (e.name.equals("yTop")){
				if (!isBackwards() && e.val < p2.y)
					p2.y = e.val;
				else if (e.val < p1.y)
					p1.y = e.val;
			}
		}
	}
	
	private boolean isBackwards(){
		if (p1.x > p2.x)
			return true;
		return false;
	}
}

class Edge{
	boolean enter,exit;
	float val;
	String name;
	Point p1,p2;
	Edge (String name, float val, Point p1, Point p2){
		this.enter = false;
		this.exit = false;
		this.val = val;
		this.name = name;
		this.p1 = p1;
		this.p2=p2;
	}
}

class ClipRectangle{ //normal rectangle -- but sets boundaries
	Point p1,p2;
	ArrayList<Edge> bounds = new ArrayList<Edge>();
	Edge xLeft, xRight, yBot, yTop;
	float dx, dy;
	
	ClipRectangle(Point p1, Point p2){
		this.p1 = p1;
		this.p2 = p2;
		this.xLeft = new Edge("xLeft",min(p1.x, p2.x), p1, new Point(p1.x,p2.y));
		this.bounds.add(xLeft);
		this.yTop = new Edge("yTop",max(p1.y,p2.y), p2, new Point(p1.x,p2.y));
		this.bounds.add(yTop);
		this.xRight = new Edge("xRight",max(p1.x,p2.x),p2, new Point(p2.x,p1.y));
		this.bounds.add(xRight);
		this.yBot = new Edge("yBot",min(p1.y,p2.y), p1, new Point(p2.x,p1.y));
		this.bounds.add(yBot);

		this.dx = p1.x-p2.x;
		this.dy = p1.y-p2.y;
	}
	
	public void draw(GL2 gl, int how){
		gl.glLogicOp(how);	
		gl.glBegin(gl.GL_LINE_STRIP);
		gl.glVertex2f(p1.x,p1.y);
		gl.glVertex2f(p1.x,p2.y);
		gl.glVertex2f(p2.x,p2.y);
		gl.glVertex2f(p2.x,p1.y);
		gl.glVertex2f(p1.x, p1.y);
		gl.glEnd();
		gl.glFlush();
	}
	
	public ArrayList<Edge> getBounds (){return bounds;}
	private float min(float a, float b){return Math.min(a,b);}	
	private float max(float a, float b){return Math.max(a,b);}
}