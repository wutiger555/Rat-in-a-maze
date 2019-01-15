package rate_in_a_maze;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class rate_in_a_maze {
	static int size;
	static int maze[][];

	public static void main(String[] args) throws IOException {
		try {
			// 讀檔
			FileReader fileReader = new FileReader("/Users/zhihuiwu/Documents/maze.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			// 先取第一行出來看size
			String firstline = bufferedReader.readLine();
			String firstsplitLine[] = firstline.split(" ");
			size = firstsplitLine.length;
			maze = new int[size][size];
			for (int i = 0; i < size; i++) {
				maze[0][i] = Integer.parseInt(firstsplitLine[i]);
			}
			// 開始做後續input
			int count = 1;
			while (bufferedReader.ready()) {
				String line = bufferedReader.readLine();
				String splitLine[] = line.split(" ");

				for (int i = 0; i < size; i++) {
					maze[count][i] = Integer.parseInt(splitLine[i]);
				}
				count++;
			}
			fileReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < size; i++) {
			for (int n = 0; n < size; n++) {
				System.out.print(maze[i][n] + " ");
			}
			System.out.println();
		}

		boolean result = findPath();
		System.out.println(result);

	}

	private static boolean findPath() {
		Stack<Position> path = new Stack<Position>();

		/*
		 * 這邊藉由實作一個Position class的offset陣列 來達到後面作為四個方位移動測試的目的
		 */
		Position[] offset = new Position[4];
		offset[0] = new Position(0, 1);// (列,行)
		offset[1] = new Position(1, 0);
		offset[2] = new Position(0, -1);
		offset[3] = new Position(-1, 0);

		int[][] newmaze = new int[size + 2][size + 2];
		int newsize = newmaze.length;
		// 用一個新的maze先根據原maze擺上圍牆
		for (int i = 0; i <= size + 1; i++) {
			newmaze[0][i] = newmaze[size + 1][i] = 1;
			newmaze[i][0] = newmaze[i][size + 1] = 1;
		}
		// 將原本的maze放入包含圍牆的新maze中
		int length = 0;
		for (int i = 1; i < newsize - 1; i++) {
			int width = 0;
			for (int n = 1; n < newsize - 1; n++) {
				newmaze[i][n] = maze[length][width++];
			}
			length++;
		}

		// 從位置11開始跑 因為要扣掉圍牆
		Position here = new Position(1, 1);
		// 初始位置先設圍牆 防止往後跑
		newmaze[1][1] = 1;
		int option = 0;
		int lastOption = 3;
		// 迴圈設置條件在老鼠碰到迷宮出口前會持續跑迴圈
		while (here.row != size || here.col != size) {
			int r = 0;// row
			int c = 0;// column
			// 嘗試 右>下>左>上 四種組合
			while (option <= lastOption) {
				/*
				 * 根據前面設定的offset 0開始往後跑的先後順序是 右>下>左>上 所以當跑到任一方向 且非1(沒有障礙物)就會跳開迴圈
				 * 如果所有方位都試走過沒有辦法前進 就以option=4結束
				 */
				r = here.row + offset[option].row;
				c = here.col + offset[option].col;
				if (newmaze[r][c] == 0)// 如果發現這個方向是有路可走就跳出迴圈
					break;
				option++;
			}
			// 這邊是option小於等於3的情況 也就是找到路能走的情況
			if (option <= lastOption) {
				path.push(here);// 將目前位置先push進stack 為防止往後繼續走沒路要回到前面
				here = new Position(r, c);
				newmaze[r][c] = 1;
				option = 0;
			} else {
				if (path.empty())// 如果前面沒有任何堆疊 表示沒路可走了 return false
					return false;
				Position next = path.pop();// pop掉前一層 回到剛剛位置
				/*
				 * 這邊主要是先篩前一步是同欄還是同列 進一步去掉前面已經用過的選項 讓下一個迴圈重新開始跑時 可以不用再重新判斷
				 */
				if (next.row == here.row) // 前一個是在同一個row上的時候
					option = 2 + next.col - here.col;// 剛好option會變成1 下一個迴圈就去掉0的選項(往右)
				else
					option = 3 + next.row - here.row;// 剛好option會變成2 下一個迴圈就去掉0,1的選項(往右 往下)
				here = next;
			}
		}

		return true;
	}

}

/*
 * 實作一個class 作為位置存取用
 */
class Position {
	int row = 0;
	int col = 0;

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

}
