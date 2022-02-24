package mapa;

import java.util.concurrent.locks.ReentrantLock;

public class Mapa {
	
	private Object[][] mapaObjekata; // 15 do 30 velicina odredjuje se slucajno pri kreiranju
	public static ReentrantLock mapLock = new ReentrantLock();
	
	public Mapa(int mapSize) {
		mapaObjekata = new Object[mapSize][mapSize];
	}
	
	public void lockMap() {
		mapLock.lock();
	}
	
	public void unlockMap() {
		if(mapLock.isHeldByCurrentThread()) {
			mapLock.unlock();
		}
	}
	
	public int getMapSize() {
		return mapaObjekata.length;
	}
	
	public Object[][] getMapa(){
		return mapaObjekata;
	}
	
	public void setMapaXY(int x, int y, Object obj) {
		if(x < 0 || y < 0 || x >= getMapSize() || y >= getMapSize()) {
			return;
		}
		mapaObjekata[x][y] = obj;
	}
	
	public Object getMapaXY(int x, int y) {
		if(x < 0 || y < 0 || x >= getMapSize() || y >= getMapSize()) {
			return null;
		}
		return mapaObjekata[x][y];
	}
	
	public void ispisiMapu() {
		for(int i = 0; i < getMapSize(); i++,System.out.println()) {
			for(int j=0; j<mapaObjekata[i].length; j++) {
				System.out.print(mapaObjekata[i][j] + "    ");
			}
		}
	}

}
