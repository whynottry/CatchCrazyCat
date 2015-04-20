package com.example.catchcrazycat;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class PlayGround extends SurfaceView implements Callback, OnTouchListener {
	
	private static int WIDTH = 10;
	private static final int ROW = 10;
	private static final int COL = 10;
	private static final int BLOCKS = 10;
	private Dot martix[][];
	private Dot cat;

	public PlayGround(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
		setOnTouchListener(this);
		//设置状态监听
		getHolder().addCallback(this);
		
		martix = new Dot[ROW][COL];
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j < COL; j++){
				martix[i][j] = new Dot(j,i);
			}
		}
		
		initGame();
	}
	
	private Dot getDot(int x,int y){
		return martix[y][x];
	}
	
	//实现界面的绘制
	private void reDraw(){
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.LTGRAY);
		
		Paint paint = new Paint();
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		
		for(int i = 0; i < ROW; i++){
			int offset = 0;
			if(i%2 != 0){
				offset = WIDTH/2;
			}
			for(int j = 0; j < COL; j++){
				Dot one = getDot(j,i);
				switch (one.getStatus()){
				case Dot.STATUS_OFF:
					paint.setColor(0xFFEEEEEE);
					break;
				case Dot.STATUS_ON:
					paint.setColor(0xFFFFAA00);
					break;
				case Dot.STATUS_IN:
					paint.setColor(0xFFFF0000);
					break;
				default:
					break;
				}
				canvas.drawOval(new RectF(one.getX()*WIDTH + offset,one.getY()*WIDTH,
						(one.getX()+1)*WIDTH + offset,(one.getY()+1)*WIDTH), paint);
			}
		}
		
		
		//取消Canvas的锁定，把绘制的内容更新到界面
		getHolder().unlockCanvasAndPost(canvas);
	}
	
	//Callback callback = new Callback(){

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			WIDTH = arg2/(COL+1);
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			int screenW = this.getWidth();
			WIDTH = screenW/(COL+1);
			Log.d("test1",Integer.toString(WIDTH));
			
			reDraw();
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
		}
	//};
		
	private boolean isAtEdge(Dot d){
		if(d.getX()*d.getY() == 0 || d.getX()+1 == COL || d.getY()+1 == ROW){
			return true;
		}
		return false;
	}

	//返回值为null时，说明在该节点在边界上
	private Dot getNeigh(Dot one, int dir){
		Dot neigh = null;
		switch(dir){
		case 1:
			if(one.getX()>=1){
				neigh = getDot(one.getX()-1,one.getY());
			}
			break;
		case 2:
			if(one.getY()%2 == 0){
				//奇数行
				if(!(one.getX()*one.getY() == 0)){
					neigh = getDot(one.getX()-1,one.getY()-1);
				}
			}else{
				if(one.getY()>=1){
					neigh = getDot(one.getX(),one.getY()-1);
				}
			}
			break;
		case 3:
			if(one.getY()%2 == 0){
				//奇数行
				if(one.getY()>=1){
					neigh = getDot(one.getX(),one.getY()-1);
				}
			}else{
				if(one.getY()>=1 && one.getX()<COL-1){
					neigh = getDot(one.getX()+1,one.getY()-1);
				}
			}
			break;
		case 4:
			if(one.getX()<COL-1){
				neigh = getDot(one.getX()+1,one.getY());
			}
			break;
		case 5:
			if(one.getY()%2 == 0){
				//奇数行
				if(one.getY() < ROW-1){
					neigh = getDot(one.getX(),one.getY()+1);
				}
			}else{
				if(one.getX() < COL-1 && one.getY() < ROW-1){
					neigh = getDot(one.getX()+1,one.getY()+1);
				}
			}
			break;
		case 6:
			if(one.getY()%2 == 0){
				//奇数行
				if(one.getX()>=1 && one.getY() < ROW-1){
					neigh = getDot(one.getX()-1,one.getY()+1);
				}
			}else{
				if(one.getY() < ROW-1){
					neigh = getDot(one.getX(),one.getY()+1);
				}
			}
			break;
		default:
		//	Log.e("PlayGround", "输入的方向的值在1~6之间");
		}
		return neigh;
	}
	
	//cat移动到某点
	private void moveTo(Dot one){
		one.setStatus(Dot.STATUS_IN);
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
		//cat.setStatus(Dot.STATUS_OFF);
		cat.setXY(one.getX(), one.getY());
		//cat.setStatus(Dot.STATUS_IN);
	}
	
	//返回某固定点沿着某一方向到达边缘或者路障的距离，到达路障的距离为负数
	private int getDistanse(Dot one, int dir){
		int dis = 0;
		Dot ori = one,next;
		while(true){
			next = getNeigh(ori,dir);
			if(next != null){
				if(next.getStatus() == Dot.STATUS_ON){
					//路障
					dis = dis*(-1);
					return dis;
				}
				dis++;
				ori = next;
			}else{
				return dis;
			}
		}
	}
	
	//游戏的初始化
	private void initGame(){
		for(int i = 0; i < ROW; i++){
			for(int j = 0; j < COL; j++){
				martix[i][j].setStatus(Dot.STATUS_OFF);
			}
		}
		cat = new Dot(4,5);
		getDot(4,5).setStatus(Dot.STATUS_IN);
		
		for(int i = 0; i < BLOCKS;){
			int x  = (int)((Math.random()*1000)%COL);
			int y  = (int)((Math.random()*1000)%ROW);
			if(getDot(x,y).getStatus() == Dot.STATUS_OFF){
				getDot(x,y).setStatus(Dot.STATUS_ON);
				i++;
			}
		}
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent e) {
		// TODO Auto-generated method stub
		if(e.getAction() == MotionEvent.ACTION_UP){
			int x = 0,y;
			y = (int) (e.getY()/WIDTH);
			if(y%2 == 0){
				//奇数行
				x = (int) (e.getX()/WIDTH);
			}else{
				//偶数行
				x = (int) ((e.getX() - WIDTH/2)/WIDTH);
			}
			//获得点击点的x,y坐标
			if((x+1)>COL || (y+1)>ROW){
				initGame();
			}else if(getDot(x,y).getStatus() == Dot.STATUS_OFF){
				martix[y][x].setStatus(Dot.STATUS_ON);
				move();
			}
			reDraw();
		}
		return true;
	}
	
	//选择到边缘距离近的方向，如果每个方向均有障碍物，选择到障碍物远的方向
	private void move(){
		if(isAtEdge(cat)){
			lose();
			return;
		}
		Vector<Dot> feasible = new Vector<Dot>();  //记录cat周围可行点
		Vector<Dot> positive = new Vector<Dot>();
		Vector<Dot> negative = new Vector<Dot>();
		HashMap<Dot,Integer> feasibleMap = new HashMap<Dot,Integer>();
		HashMap<Dot,Integer> positiveMap = new HashMap<Dot,Integer>();
		HashMap<Dot,Integer> negativeMap = new HashMap<Dot,Integer>();
		for(int i = 1; i < 7; i++){
			Dot neigh = getNeigh(cat,i);
			if(neigh!=null){
				if(neigh.getStatus() == Dot.STATUS_OFF){
					feasible.add(neigh);
					feasibleMap.put(neigh, i);
					//计算距离
					int dis = getDistanse(cat,i);
					if(dis > 0){
						positive.add(neigh);
						positiveMap.put(neigh, dis);
					}
					if(dis < 0){
						negative.add(neigh);
						negativeMap.put(neigh, dis);
					}
				} 
			}
		}
		if(feasible.size() == 0){
			win();
		}else{
			Dot best = null;
			if(positive.size() > 0){
				//寻找距离为正的最短方向
				int minDis = 999;
				for(int k = 0; k < positive.size(); k++){
					int tempDis = positiveMap.get(positive.get(k));
					if(tempDis < minDis){
						minDis = tempDis;
						best = positive.get(k);
					}
				}
			}else{
				//寻找距离为负的最小值
				int minDis = 999;
				for(int k = 0; k < negative.size(); k++){
					int tempDis = negativeMap.get(negative.get(k));
					if(tempDis < minDis){
						minDis = tempDis;
						best = negative.get(k);
					}
				}
			}
			getDot(cat.getX(),cat.getY()).setStatus(Dot.STATUS_OFF);
			getDot(best.getX(),best.getY()).setStatus(Dot.STATUS_IN);
			//cat.setStatus(Dot.STATUS_OFF);
			//best.setStatus(Dot.STATUS_IN);
			cat.setXY(best.getX(), best.getY());
			//cat.setStatus(Dot.STATUS_IN);
			//moveTo(best);
		}
	}
	
	private void lose(){
		Toast.makeText(getContext(), "lose", Toast.LENGTH_SHORT).show();
	}
	
	private void win(){
		Toast.makeText(getContext(), "win", Toast.LENGTH_SHORT).show();
	}
}

