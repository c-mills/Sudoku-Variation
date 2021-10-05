
import java.util.*;
import java.lang.*;
import java.io.*;
 
 
public class Game {
	
	Board sudoku;
	
	private boolean done = false;
	private int count = 0;
	private int current = 0;
	private boolean countdone = false;
	int[] list;
	
	
	public class Cell{
		private int row = 0;
		private int column = 0;
		
		public Cell(int row, int column) {
			this.row = row;
			this.column = column;
		}
		public int getRow() {
			return row;
		}
		public int getColumn() {
			return column;
		}
	}
	
	public class Region{
		private Cell[] matrix;
		private int num_cells;
		
		public Region(int num_cells) {
			this.matrix = new Cell[num_cells];
			this.num_cells = num_cells;
		}
		public Cell[] getCells() {
			return matrix;
		}
		public void setCell(int pos, Cell element){
			matrix[pos] = element;
		}
		
	}
	
	public class Board{
		private int[][] board_values;
		private Region[] board_regions;
		private int num_rows;
		private int num_columns;
		private int num_regions;
		
		//private int total_cells;
		
		public Board(int num_rows,int num_columns, int num_regions){
			this.board_values = new int[num_rows][num_columns];
			this.board_regions = new Region[num_regions];
			this.num_rows = num_rows;
			this.num_columns = num_columns;
			this.num_regions = num_regions;
			
			//this.total_cells=0;
			
		}
		
		public int[][] getValues(){
			return board_values;
		}
		public int getValue(int row, int column) {
			return board_values[row][column];
		}
		public Region getRegion(int index) {
			return board_regions[index];
		}
		public Region[] getRegions(){
			return board_regions;
		}
		public void setValue(int row, int column, int value){
			board_values[row][column] = value;
		}
		public void setRegion(int index, Region initial_region) {
			board_regions[index] = initial_region;
		}	
		public void setValues(int[][] values) {
			board_values = values;
		}
 
	}
	
	
	
	public boolean inregion(int num, Cell[] r) {
		for (int i=0;i<r.length;i++) {
			
			if (this.sudoku.board_values[r[i].getRow()][r[i].getColumn()] == num) {
				return true;
			}
		}
		return false;
	}
	
	public boolean suroundings(int row,int col, int num) {
		
		boolean rowmin=false;
		boolean rowmax=false;
		boolean colmin=false;
		boolean colmax=false;
		
		if (row-1>-1) {
			rowmin=true;
		}
		if (col-1>-1) {
			colmin=true;
		}
		if (row+1<this.sudoku.num_rows) {
			rowmax=true;
		}
		if (col+1<this.sudoku.num_columns) {
			colmax=true;
		}
		
		if (rowmax) {
			if (this.sudoku.board_values[row+1][col]==num ) {//(x+1,y)
				return true;
			}
				
			if (colmax && this.sudoku.board_values[row+1][col+1]==num) {//(x+1,y+1)
				return true;
			}
			
			if(colmin && this.sudoku.board_values[row+1][col-1]==num) {//(x+1,y-1)
				return true;
			}
			
		}
		if (rowmin) {
			if (this.sudoku.board_values[row-1][col]==num ) {//(x-1,y)
				return true;
			}
			if (colmax && this.sudoku.board_values[row-1][col+1]==num) { //(x-1,y+1)
				return true;
			}
			if(colmin && this.sudoku.board_values[row-1][col-1]==num) {//(x-1,y-1)
				return true;
			}
		}
			
		if(colmin && this.sudoku.board_values[row][col-1]==num) {//(x,y-1)
			return true;
		}
		if (colmax && this.sudoku.board_values[row][col+1]==num) {//(x,y+1)
			return true;	
		}
		return false;
		
		
	}
	
	
	
	public boolean regionfilled(Region r) {
		for (Cell c: r.getCells()) {
			if (this.sudoku.board_values[c.getRow()][c.getColumn()] == -1) {
				return false;
			}
		}
		return true;
		
	}
	
	//coordinate
	//make recursive call 
	
	public void recursive( int cellnum, int regionnum){
		int row=this.sudoku.getRegion(regionnum).getCells()[cellnum].getRow();
		int col=this.sudoku.getRegion(regionnum).getCells()[cellnum].getColumn();
		int len_region = this.sudoku.getRegion(regionnum).num_cells;
		
			
		if (this.sudoku.board_values[row][col] == -1) {
			for (int a=1; a<=len_region;a++) {
					
					//check if num a is in the region already
					if(! inregion(a,this.sudoku.getRegion(regionnum).getCells())) {
						if (! suroundings(row,col,a)) {
							this.sudoku.setValue(row,col,a);
							current++;
							if(count==current) {
								this.done = true;
								break;
								
							}
							this.list[regionnum]--;
							if(list[regionnum]==0) {
								recursive(0,regionnum+1);
							}
							else {
								recursive(cellnum+1,regionnum);
							}
							this.list[regionnum]++;
							this.current--;
							if (this.done) {
								break;
							}
						}
						
						
							
					}
					
			}
			if (! this.done) {
				this.sudoku.setValue(row, col,-1);
			}
			
			
		}
		else {
			recursive(cellnum+1,regionnum);
		}
			
	}
	public int filled(Board b) {
		int count = 0;
		for (int r = 0; r < b.num_rows; r++) {
			for (int c = 0; c < b.num_columns; c++) {
 
				if (b.board_values[r][c] == -1) { // means cell is empty
					count++;
				}
			}
		}
		return count; //returns num of cells to be filled
	}
	public int[][] solver() {
		
		
		if (! this.countdone) {
			this.count=filled(this.sudoku);
			this.countdone=true;
		}
		
		this.list = new int[this.sudoku.num_regions];
		
		for (int regi=0;regi<this.sudoku.num_regions;regi++) {
		
			int len_region=this.sudoku.getRegion(regi).num_cells; //size of region
			
			int row;
			int col;
			
			for (Cell c:this.sudoku.getRegion(regi).getCells()) {//traverse the cells of this region
				row=c.getRow();
				col=c.getColumn();
				
				if (this.sudoku.board_values[row][col] != -1) {
					len_region--;
					
				}
				
 
				
			}
			list[regi]=len_region;
			
		}
		recursive(0,0);
		
		return sudoku.getValues();
	}
 
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int rows = sc.nextInt();
		int columns = sc.nextInt();
		int[][] board = new int[rows][columns];
		//Reading the board
		for (int i=0; i<rows; i++){
			for (int j=0; j<columns; j++){
				String value = sc.next();
				if (value.equals("-")) {
					board[i][j] = -1;
				}else {
					try {
						board[i][j] = Integer.valueOf(value);
					}catch(Exception e) {
						System.out.println("Ups, something went wrong");
					}
				}	
			}
		}
		int regions = sc.nextInt();
		Game game = new Game();
	    game.sudoku = game.new Board(rows, columns, regions);
		game.sudoku.setValues(board);
		for (int i=0; i< regions;i++) {
			int num_cells = sc.nextInt();
			Game.Region new_region = game.new Region(num_cells);
			for (int j=0; j< num_cells; j++) {
				String cell = sc.next();
				String value1 = cell.substring(cell.indexOf("(") + 1, cell.indexOf(","));
				String value2 = cell.substring(cell.indexOf(",") + 1, cell.indexOf(")"));
				Game.Cell new_cell = game.new Cell(Integer.valueOf(value1)-1,Integer.valueOf(value2)-1);
				new_region.setCell(j, new_cell);
			}
			game.sudoku.setRegion(i, new_region);
		}
		int[][] answer = game.solver();
		for (int i=0; i<answer.length;i++) {
			for (int j=0; j<answer[0].length; j++) {
				System.out.print(answer[i][j]);
				if (j<answer[0].length -1) {
					System.out.print(" ");
				}
			}
			System.out.println();
		}
	}
	
 
 
}