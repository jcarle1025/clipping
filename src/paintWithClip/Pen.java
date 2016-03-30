/*
   This file contains the drawing pens used by the Painter
   application.

   All pens are subclasses of the abstract class Pen.

   The code defines how each type of pen responds to mouse events.
*/
package paintWithClip;

import com.jogamp.opengl.*;
import java.awt.event.*;
import java.util.ArrayList;

abstract class Pen {
    protected static final int FIRST=1, CONTINUE=2;
    protected int state;
    protected float r,g,b;
    protected GL2 gl;
    protected ArrayList<Edge> penBounds = new ArrayList<Edge>();
    
    Pen(GL2 gl){
    	this.gl = gl;
    	state = FIRST;
    }
    
    Pen(GL2 gl, ArrayList<Edge> boundaries){
		this.gl = gl;
		this.penBounds = boundaries;
		state = FIRST;
    }
    
    public void mouseDown(MouseEvent e){}
    
    public void mouseUp(MouseEvent e){}
    
    public void mouseDragged(MouseEvent e){}
    
    public void setColor(float r, float g, float b){
		this.r = r; this.g = g; this.b = b;
    }
}

class ClipRectanglePen extends Pen {
	Point p1, p2;
	ClipRectangle cRec;

    ClipRectanglePen(GL2 gl){
    	super(gl);
    }
    
    public void mouseDown(MouseEvent e){
    	int currentX = e.getX();
    	int currentY = e.getY();
    	p1 = new Point(currentX, currentY);
    	p2 = new Point(currentX, currentY);
    	cRec = new ClipRectangle(p1, p2);
    	gl.glColor3f(1-r, 1-g, 1-b);
    	cRec.draw(gl,  GL2.GL_XOR);
    }
    
    public void mouseUp(MouseEvent e){
    	cRec.draw(gl, GL2.GL_XOR);
    	int currentX = e.getX();
    	int currentY = e.getY();
    	p2= new Point(currentX, currentY);
    	cRec = new ClipRectangle(p1, p2);
    	this.penBounds = cRec.getBounds();
    	gl.glColor3f(r, g, b);
    	cRec.draw(gl,  GL2.GL_COPY);
    }
    
    public void mouseDragged(MouseEvent e){
		cRec.draw(gl, GL2.GL_XOR);
		int currX = e.getX();
		int currY = e.getY();
		p2 = new Point(currX,currY);
		cRec = new ClipRectangle(p1,p2);
		cRec.draw(gl, GL2.GL_XOR);
    }   
}

class RectanglePen extends Pen {
	Point p1, p2;
	Rectangle rec;

    RectanglePen(GL2 gl, ArrayList<Edge> myBounds){
    	super(gl, myBounds);
    }
    
    public void mouseDown(MouseEvent e){
    	int xnow = e.getX();
    	int ynow = e.getY();
    	p1 = new Point(xnow, ynow);
    	p2 = new Point(xnow, ynow);
    	rec = new Rectangle(p1, p2);
    	gl.glColor3f(1-r, 1-g, 1-b);
    	rec.draw(gl,  GL2.GL_XOR);
    }
    public void mouseUp(MouseEvent e){
    	rec.draw(gl, GL2.GL_XOR);
    	int currentX = e.getX();
    	int currentY = e.getY();
    	p2= new Point(currentX, currentY);
    	rec = new Rectangle(p1, p2);
    	gl.glColor3f(r, g, b);
    	rec.clipARect(penBounds);
    	rec.draw(gl,  GL2.GL_COPY);
    }
    public void mouseDragged(MouseEvent e){
		rec.draw(gl, GL2.GL_XOR);
		int currX = e.getX();
		int currY = e.getY();
		p2 = new Point(currX,currY);
		rec = new Rectangle(p1,p2);
		rec.draw(gl, GL2.GL_XOR);
    }
}

class FilledRectanglePen extends Pen {
    Point p1,p2;
    FilledRectangle rct;
    
    FilledRectanglePen(GL2 gl, ArrayList<Edge> myBounds){
		super(gl,myBounds);
    }

    public void mouseDown(MouseEvent e){
		int xnow = e.getX();
		int ynow = e.getY();
		p1 = p2 = new Point(xnow,ynow);
		rct = new FilledRectangle(p1,p2);
		gl.glColor3f(1-r,1-g,1-b);
		rct.draw(gl, GL2.GL_XOR);
    }
    
    public void mouseUp(MouseEvent e){
		rct.draw(gl, GL2.GL_XOR);
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow,ynow);
		rct = new FilledRectangle(p1,p2);
		gl.glColor3f(r,g,b);
		rct.clipARect(penBounds);
		rct.draw(gl, GL2.GL_COPY);
    }
    
    public void mouseDragged(MouseEvent e){
		rct.draw(gl, GL2.GL_XOR);
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow,ynow);
		rct = new FilledRectangle(p1,p2);
		rct.draw(gl, GL2.GL_XOR);
    }
}

class LinePen extends Pen {
	Point p1, p2;
	Line l;
	
    LinePen(GL2 gl, ArrayList<Edge> myBounds){
		super(gl, myBounds);
    }
    
    public void mouseDown(MouseEvent e){
		int xnow = e.getX();
		int ynow = e.getY();
		p1 = new Point(xnow,ynow);
		p2 = new Point (xnow, ynow);
		l = new Line(p1,p2);
		gl.glColor3f(1-r,1-g,1-b);
		l.draw(gl, GL2.GL_XOR);
    }
    
    public void mouseUp(MouseEvent e){
		l.draw(gl, GL2.GL_XOR);
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow,ynow);
		l = new Line(p1,p2);
		System.out.println("Line = P1("+p1.x+", "+p1.y+")\nP2("+p2.x+", "+p2.y+")");
		gl.glColor3f(r,g,b);
		
		//no clip rectangle
		if(penBounds.isEmpty())
			l.draw(gl, GL2.GL_COPY);
		//clip rectangle there
		else{
			l.clipLine(penBounds);
			l.draw(gl, GL2.GL_COPY);
		}
    }
    
    public void mouseDragged(MouseEvent e){
		l.draw(gl, GL2.GL_XOR);
		int xnow = e.getX();
		int ynow = e.getY();
		p2 = new Point(xnow,ynow);
		l = new Line(p1,p2);
		l.draw(gl, GL2.GL_XOR);
    }
}

class LineLoopPen extends Pen { 
	ArrayList<Point> verts = new ArrayList<Point>();
	LineLoop loop;
	
    LineLoopPen(GL2 gl, ArrayList<Edge> myBounds){
    	super(gl, myBounds);
    }
    
    public void mouseDown(MouseEvent e){
    	int currentX = e.getX();
    	int currentY = e.getY();
    	Point p = new Point(currentX, currentY);
    	verts.add(p);
    	
    	//user click on same vertex twice closes the loop
    	if(verts.size()>2 && verts.get(verts.size()-2).x == p.x && verts.get(verts.size()-2).y == p.y){
    		verts.add(new Point(verts.get(0).x,verts.get(0).y));
    		loop = new LineLoop(verts);
    		loop.clipLineLoop(penBounds);
    		
    		loop.draw(gl, GL2.GL_COPY);
    		return;
    	}
    	
    	loop = new LineLoop(verts);
    	gl.glColor3f(1-r, 1-g, 1-b);
    	loop.draw(gl,  GL2.GL_XOR);
    }
    
    public void mouseUp(MouseEvent e){
    	loop.draw(gl, GL2.GL_XOR);
    	loop = new LineLoop(verts);
    	gl.glColor3f(r, g, b);
    	int last=verts.size()-1;
    	
    	System.out.print("Before clipping got verts --- ");
		for(Point a : loop.getVerts()){
			System.out.print("("+a.x+","+a.y+") ");
		}
		System.out.print("\n");

    	loop.draw(gl, GL2.GL_XOR);
    	
    	//allows for new line loop to start
    	if(verts.size()>3 && verts.get(0).x==verts.get(last).x && verts.get(0).y==verts.get(last).y)
    		verts.clear();
    }
}

class FilledPolygonPen extends Pen { 
	ArrayList<Point> verts = new ArrayList<Point>();
	LineLoop l;
	Polygon poly;
	
    FilledPolygonPen(GL2 gl, ArrayList<Edge> myBounds){
    	super(gl,myBounds);
    }
    
    public void mouseDown(MouseEvent e){
    	int currentX = e.getX();
    	int currentY = e.getY();
    	Point p = new Point(currentX, currentY);
    	verts.add(p);
    	
    	//user click on same vertex twice closes the loop
    	if(verts.size()>2 && verts.get(verts.size()-2).x == p.x && verts.get(verts.size()-2).y == p.y){
    		verts.add(new Point(verts.get(0).x,verts.get(0).y));
    		
    		poly = new Polygon(verts);
    		poly.clipLineLoop(penBounds);
    		System.out.println("Got out of clipping");
    		poly.draw(gl, GL2.GL_COPY);
    		return;
    	}
    	
    	l = new LineLoop(verts);
    	gl.glColor3f(1-r, 1-g, 1-b);
    	l.draw(gl,  GL2.GL_XOR);
    }
    
    public void mouseUp(MouseEvent e){
    	l.draw(gl, GL2.GL_XOR);
    	l = new LineLoop(verts);
    	gl.glColor3f(r, g, b);
    	int last=verts.size()-1;
    	
    	System.out.print("Before clipping got verts --- ");
		for(Point a : l.getVerts()){
			System.out.print("("+a.x+","+a.y+") ");
		}
		System.out.print("\n");

    	l.draw(gl, GL2.GL_XOR);
    	
    	//allows for new line loop to start
    	if(verts.size()>3 && verts.get(0).x==verts.get(last).x && verts.get(0).y==verts.get(last).y)
    		verts.clear();
    }
}