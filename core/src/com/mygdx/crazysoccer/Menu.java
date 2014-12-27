package com.mygdx.crazysoccer;

import java.util.ArrayList;

import com.badlogic.gdx.Input.Keys;

public class Menu {

	public static class Item {
		private int id = 0;
		private int columnId = 0;
		private boolean isActive = false;
		private int x = 0;
		private int y = 0;
		private String name = "";
		
		public Item(int x, int y, String caption) {
			this.x = x;
			this.y = y;
			this.name = caption;
		}
		
		public Item(int x, int y, String caption, int columndId) {
			this.x = x;
			this.y = y;
			this.name = caption;
			this.columnId = columndId;
		}
		
		public int getId() {
			return id;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
	}
	
	private ArrayList<Item> items;
	
	public Menu() {
		items = new ArrayList<Item>();
		
		Sounds.load("nav_ok", "sound/sfx/nav_ok.ogg");
	}
	
	public void cursorMove(int key) {
		Item item = getActive();
		
		switch (key) {
			case Keys.LEFT:
				if (item.columnId > getMinColumnId()) {
					item.isActive = false;
					int pos = getPositionInColumn(item);
					for (int i = pos; i >= 0; i--) {
						if (getItemInColumn(i,item.columnId - 1) != null) {
							getItemInColumn(i,item.columnId - 1).isActive = true;
							break;
						}
					}
				} 
			break;
			
			case Keys.RIGHT:
				if (item.columnId < getMaxColumnId()) {
					item.isActive = false;
					int pos = getPositionInColumn(item);
					for (int i = pos; i >= 0; i--) {
						if (getItemInColumn(i,item.columnId + 1) != null) {
							getItemInColumn(i,item.columnId + 1).isActive = true;
							break;
						}
					}
				} 
			break;
			
			case Keys.UP:
				if (item.id != getFirstForColumn(item.columnId).id) {
					item.isActive = false;
					getItem(item.id - 1).isActive = true;
				} 
			break;
			
			case Keys.DOWN:
				if (item.id != getLastForColumn(item.columnId).id) {
					item.isActive = false;
					getItem(item.id + 1).isActive = true;
				} 
			break;
		}
		
		Sounds.play("nav_ok");
	}
	
	private int getPositionInColumn(Item item) {
		int position = 0;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).columnId == item.columnId) {
				if (items.get(i).id != item.id) {
					position++;
				}
				else {
					return position;
				}
			}
		}
		return position;		
	}
	
	private Item getItemInColumn(int position, int columnId) {
		int curPos = 0;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).columnId == columnId) {
				if (curPos == position) {
					return items.get(i);
				}
				else {
					curPos++;
				}
					
			}
		}
		return null;
	}
	
	private Item getFirstForColumn(int columnId) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).columnId == columnId) {
				return items.get(i);
			}
		}
		return null;
	}
	
	private Item getLastForColumn(int columnId) {
		Item item = null;
		
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).columnId == columnId) {
				item = items.get(i);
			}
		}
		return item;
	}
	
	private int getMinColumnId() {
		int min = 999;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).columnId < min) {
				min = items.get(i).columnId; 
			}
		}
		return min;
	}
	
	private int getMaxColumnId() {
		int max = 0;
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).columnId > max) {
				max = items.get(i).columnId; 
			}
		}
		return max;
	}
	
	public ArrayList<Item> getItems() {
		return items;
	}
	
	public void addItem(Item item) {
		if (items.size() == 0) 
			item.isActive = true;
		
		item.id = items.size();
		
		items.add(item);
	}
	
	public Item getItem(int index) {
		return items.get(index);
	}
	
	public Item getActive() {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isActive) {
				return items.get(i);
			}
		}
		return null;
	}
	
	public void draw() {
		for (int i = 0; i < items.size(); i ++) {
			
			CrazySoccer.batch.begin();
			CrazySoccer.font.draw(CrazySoccer.batch, items.get(i).name, items.get(i).x, items.get(i).y);
			CrazySoccer.batch.end();
			
//			if (items.get(i).isActive) 
//				System.out.print(">");
//			
//			System.out.println(items.get(i).name);
		}
//		System.out.println();
	}
}
