package com.example.catchcrazycat;

public class Dot {
	
	private int X;
	private int Y;
	public static final int STATUS_ON = 1;
	public static final int STATUS_OFF = 2;
	public static final int STATUS_IN = 3;
	private int status;
	
	public Dot(int x,int y){
		X = x;
		Y = y;
		status = STATUS_OFF;
	}
	
	public void setXY(int x,int y){
		X = x;
		Y = y;
	}

	public int getX() {
		return X;
	}

	public void setX(int x) {
		X = x;
	}

	public int getY() {
		return Y;
	}

	public void setY(int y) {
		Y = y;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
}
