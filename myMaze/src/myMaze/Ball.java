package myMaze;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
	private float xCoor=0f; // x coordinate
	private float yCoor=0f; // y coordinate
	private float xVel=0f;  // x velocity
	private float yVel=0f;	// y velocity
	private float xAcc=0f;	// acceleration of x direction
	private float yAcc=0f;	// acceleration of y direction
	private float radius=0f; //radius of the ball
	private float top=0f, bottom=0f, left=0f, right=0f; //different from maze;
	private float slowDown=10f;
	private int i=0;
	private int j=0;
		
 	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public float getxCoor() {
		return xCoor;
	}

	public void setxCoor(float xCoor) {
		this.xCoor = xCoor;
	}

	public float getyCoor() {
		return yCoor;
	}

	public void setyCoor(float yCoor) {
		this.yCoor = yCoor;
	}

	public float getxAcc() {
		return xAcc;
	}

	public void setxAcc(float xAcc) {
		this.xAcc = xAcc;
	}

	public float getyAcc() {
		return yAcc;
	}

	public void setyAcc(float yAcc) {
		this.yAcc = yAcc;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public void initialize(){
		xCoor=left;
		yCoor=top;
		xAcc=0f;
		yAcc=0f;
	}
	
	public void setCorner(float top, float bottom, float left, float right){
		this.top=top+radius;
		this.bottom=bottom-radius;
		this.left=left+radius;
		this.right=right-radius;
	}
	
	public void updatePosition(Maze maze){
		xVel+=xAcc;
		if (xVel>slowDown*radius)// x velocity limits, can't be more than the radius of the ball, or it may pass a wall in one frame.
			xVel=slowDown*radius;
		if (xVel<-slowDown*radius)
			xVel=-slowDown*radius;
//		System.out.println("Speed: " + xVel);
		xCoor+=xVel/slowDown;
//		System.out.println("Position: "+ xCoor);
		yVel+=yAcc;
		if (yVel>slowDown*radius)// y velocity limits, can't be more than the radius of the ball, or it may pass a wall in one frame.
			yVel=slowDown*radius;
		if (yVel<-slowDown*radius)
			yVel=-slowDown*radius;
		yCoor+=yVel/slowDown;
		checkCollision(maze);
	}
	
	public void checkCollision(Maze maze){
		if (xCoor<left){
			xCoor=left;
			xVel=0;
		}
		if (xCoor>right){
			xCoor=right;
			xVel=0;
		}
		if (yCoor<top){
			yCoor=top;
			yVel=0;
		}
		if (yCoor>bottom){
			yCoor=bottom;
			yVel=0;
		}
		
		i=(int) ((xCoor-maze.getLeft())/maze.getSize()); 
		j=(int) ((yCoor-maze.getTop())/maze.getSize());
		
		int x=maze.getSize()*i+maze.getLeft(); //left side of a rectangle
		int y=maze.getSize()*j+maze.getTop(); //top side of a rectangle
		
		if (maze.getwalls(i*maze.getRow()+j)/2==1){// exist left wall
			if (xCoor<x+radius){
			xCoor=x+radius;
			xVel=0;
			}
		}
		
		if (maze.getwalls(i*maze.getRow()+j)%2==1){ // exist top wall	
			if (yCoor<y+radius){
			yCoor=y+radius;
			yVel=0;
			}
		}
		
		if (i<maze.getRow()-1 && maze.getwalls((i+1)*maze.getRow()+j)/2==1){  //exist right wall
			if (xCoor>x+maze.getSize()-radius){
			xCoor=x+maze.getSize()-radius;
			xVel=0;
			}
		}
		
		if (j<maze.getColumn()-1 && maze.getwalls(i*maze.getRow()+j+1)%2==1){ //exist down wall
			if (yCoor>y+maze.getSize()-radius){
			yCoor=y+maze.getSize()-radius;
			yVel=0;
			}
		}
		
		//check corners, each corner has a 1/4 circle area that cannot be reached (for the center of the ball).
		//if distance to corner is less than radius, bounce it back to the margin of the 1/4 circle
		// x, y position zoomed according to their fraction x/y. 
		double lefttop=Math.sqrt((xCoor-x)*(xCoor-x)+(yCoor-y)*(yCoor-y));
		if (lefttop<radius){//check left top corner
			xCoor=(float) (radius/lefttop*(xCoor-x)+x);
			yCoor=(float) (radius/lefttop*(yCoor-y)+y);
		}
		
		double righttop=Math.sqrt((xCoor-x-maze.getSize())*(xCoor-x-maze.getSize())+(yCoor-y)*(yCoor-y));
		if (righttop<radius){//check right top corner
			xCoor=(float) (radius/righttop*(xCoor-x-maze.getSize())+x+maze.getSize());
			yCoor=(float) (radius/righttop*(yCoor-y)+y);
		}
		
		double leftbottom=Math.sqrt((xCoor-x)*(xCoor-x)+(yCoor-y-maze.getSize())*(yCoor-y-maze.getSize()));
		if (leftbottom<radius){//check left bottom corner
			xCoor=(float) (radius/leftbottom*(xCoor-x)+x);
			yCoor=(float) (radius/leftbottom*(yCoor-y-maze.getSize())+y+maze.getSize());
		}
		
		double rightbottom=Math.sqrt((xCoor-x-maze.getSize())*(xCoor-x-maze.getSize())+(yCoor-y-maze.getSize())*(yCoor-y-maze.getSize()));
		if (rightbottom<radius){//check left bottom corner
			xCoor=(float) (radius/rightbottom*(xCoor-x-maze.getSize())+x+maze.getSize());
			yCoor=(float) (radius/rightbottom*(yCoor-y-maze.getSize())+y+maze.getSize());
		}
	}
	
	public void drawBall(Canvas canvas, Paint mPaint){

		canvas.drawCircle(xCoor-left+top, yCoor-top+left, radius, mPaint);// coordinates totally lost. this works fine.
		canvas.save();
		canvas.restore();
	}
}
