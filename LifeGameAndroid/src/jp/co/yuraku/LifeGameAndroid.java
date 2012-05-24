package jp.co.yuraku;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

public class LifeGameAndroid extends Activity {

	/**
	 * スリープ間隔。
	 */
	private int sleep_int = 10;

	/**
	 * 描画フィールド。
	 */
	private LifeSurfaceView view = null;

	/**
	 * セルのサイズ。
	 */
	private int cell_size = 10;

	/**
	 * 横幅。
	 */
	private int max_col   = 20;

	/**
	 * 縦幅。
	 */
	private int max_row   = 20;

	/**
	 * 生存条件。
	 */
	private int [] condition_remain = { 2, 3 };

	/**
	 * 誕生条件。
	 */
	private int [] condition_birth  = { 3 };

	/**
	 * 世代数。
	 */
	private int generation = 0;

	/**
	 * ライフ数。
	 */
	private int population = 0;

	/**
	 * 使用方法。
	 */
	private String useage1 = "ENTER : Start / DEL : Clear / SPACE : Manual Step";
	private String useage2 = "ENTER : Stop / DEL : Clear / SPACE : Manual Step";

	/**
	 * 描画フィールド。
	 */
	class LifeSurfaceView extends SurfaceView
		implements SurfaceHolder.Callback, Runnable {

		/**
		 * 描画スレッド。
		 */
		private Thread running = null;

		/**
		 * サーフェイスホルダ。
		 */
		private SurfaceHolder holder = null;

		/**
		 * ダブルバッファ。
		 */
		private Bitmap offScreen = null;
		private Canvas offCanvas = null;

		/**
		 * コンストラクタ。
		 * @param context
		 */
		public LifeSurfaceView(Context context) {
			super(context);

			//setBackgroundColor(Color.WHITE);

			// SurfaceHolder の取得
			holder = getHolder();
			holder.addCallback(this);

			setFocusable(true);
		}

		/**
		 * ビューが生成されたときにコールされる。
		 */
		public void surfaceCreated(SurfaceHolder holder) {
			Log.v("LifeGame","w="+Integer.valueOf(getWidth())+"/h="+Integer.valueOf(getHeight()));

			//holder.setFixedSize(getWidth(), getHeight());

			// 画面サイズいっぱいにフィールドを広げる。
			int cols = (int)Math.floor(getWidth() / cell_size);
			int rows = (int)Math.floor(getHeight() / cell_size);

			if (cols > 5) max_col = cols;
			if (rows > 5) max_row = rows;

			// フィールドを初期化。
			initField(max_col, max_row);
		}

		/**
		 * ビューが破棄されるときにコールされる。
		 */
		public void surfaceDestroyed(SurfaceHolder holder) {
			stepStop();
		}

		/**
		 * ビューのサイズなどが変更されたときにコールされる。
		 */
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			//Log.v("LifeGame","w="+Integer.valueOf(width)+"/h="+Integer.valueOf(height));
		}

		/**
		 * キーイベントを拾う。
		 */
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {

			if (keyCode == KeyEvent.KEYCODE_SPACE) {
				// 次世代へ。
				next();
				drawViewLock();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DEL
					|| keyCode == KeyEvent.KEYCODE_CLEAR) {
				// クリア。
				stepStop();
				clearAll();
				drawViewLock();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
					|| keyCode == KeyEvent.KEYCODE_ENTER) {
				// 開始/停止
				if (running != null) {
					stepStop();
				} else {
					stepStart();
				}
				return true;
			}

			return super.onKeyDown(keyCode, event);
		}

	    /**
	     * タッチイベントを拾う。
	     */
		@Override
	    public boolean onTouchEvent(MotionEvent event) {
			Point p = getCell((int)event.getX(), (int)event.getY());
			if (p.x >= 0 && p.y >= 0) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (field[p.x][p.y] == 0) {
						putLife(p.x, p.y);
						drawLifeLock(p.x, p.y, field[p.x][p.y]);
						return true;
					}
				} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
					putLife(p.x, p.y);
					drawLifeLock(p.x, p.y, field[p.x][p.y]);
					return true;
				}
			}
	        return super.onTouchEvent(event);
	    }

		/**
		 * スレッドを開始する。
		 */
		public void stepStart() {
			stepStop();
			try {
				running = new Thread(this);
				running.start();
			} catch (Exception e) {
				running = null;
			}
		}

		/**
		 * スレッドを停止する。
		 */
		public void stepStop() {
			try {
				if (running != null) {
					running = null;
					running.join();
				}
			} catch (Exception e) {
			}
		}

		/**
		 * スレッドの処理。
		 */
		public void run() {
			while(running != null) {
				try{
					next();
					drawViewLock();
					Thread.sleep(sleep_int);
				} catch (Exception e) {

				}
			}
		}

		/**
		 * グリッドを描画する。
		 */
		private void drawView(Canvas canvas) {

			// 背景を白くする。
			canvas.drawColor(Color.WHITE);

			// ライフを描画する。
			for (int y = 0; y < max_row; y++) {
				for (int x = 0; x < max_col; x++) {
					drawLife(canvas, x, y, field [x][y]);
				}
			}

			// グリッドを描画する。
			drawGrid(canvas);

			// ガイドを描画する。
			drawGuide(canvas);
		}

		/**
		 * 使い方とかの文字列を描画する。
		 * @param canvas
		 */
		private void drawGuide(Canvas canvas) {

			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.BLUE);
			paint.setStyle(Paint.Style.FILL);
			paint.setTextSize(12);
			canvas.drawText("Generation : " + String.valueOf(generation), 12, 18, paint);
			canvas.drawText("Population : " + String.valueOf(population), 12, 32, paint);

			paint.setColor(Color.DKGRAY);
			if (running == null) {
				canvas.drawText(useage1, 12, cell_size * max_row - 8, paint);
			} else {
				canvas.drawText(useage2, 12, cell_size * max_row - 8, paint);
			}

		}

		/**
		 * ダブルバッファ用のオフスクリーンを作成する。
		 * @param canvas
		 */
		private void createOffScreen(Canvas canvas) {
			if (offScreen == null) {
				offScreen = Bitmap.createBitmap(
						canvas.getWidth(),
						canvas.getHeight(),
						Bitmap.Config.ARGB_8888);
			}
			offCanvas = new Canvas(offScreen);
		}

		/**
		 * ライフを描画する。
		 * @param canvas
		 * @param x
		 * @param y
		 * @param val
		 */
		private void drawLife(Canvas canvas, int x, int y, int val, boolean drawFrame) {

			if (canvas == null) return;
			if (x < 0 || x >= max_col) return;
			if (y < 0 || y >= max_row) return;

			Paint paint = new Paint();

			float left   = x * cell_size;
			float top    = y * cell_size;
			float right  = left + cell_size;
			float bottom = top + cell_size;

			// ライフを描画する。
			if (val == 1) {
				paint.setColor(Color.DKGRAY);
				paint.setStrokeWidth(1);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(left, top, right, bottom, paint);
			} else {
				paint.setColor(Color.WHITE);
				paint.setStrokeWidth(1);
				paint.setStyle(Paint.Style.FILL);
				canvas.drawRect(left, top, right, bottom, paint);
			}

			// 枠線を描画する。
			if (drawFrame) {
				paint.setColor(Color.LTGRAY);
				paint.setStrokeWidth(1);
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawRect(left, top, right, bottom, paint);
			}

		}

		/**
		 * ライフを描画する。
		 * @param canvas
		 * @param x
		 * @param y
		 * @param val
		 */
		private void drawLife(Canvas canvas, int x, int y, int val) {
			drawLife(canvas, x, y, val, false);
		}

		/**
		 * グリッドを描画する。
		 * @param canvas
		 */
		private void drawGrid(Canvas canvas) {

			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			paint.setStrokeWidth(1);
			paint.setStyle(Paint.Style.STROKE);

			for (int x = 0; x <= max_col; x++) {
				float start_x = x * cell_size;
				canvas.drawLine(start_x, 0, start_x, cell_size * max_row, paint);
			}
			for (int y = 0; y <= max_row; y++) {
				float start_y = y * cell_size;
				canvas.drawLine(0, start_y, cell_size * max_col, start_y, paint);
			}

		}

		/**
		 * グリッドを描画する。
		 */
		public void drawViewLock() {
			Canvas canvas = holder.lockCanvas();
			try {
				synchronized (holder) {

					// オフスクリーンを作成する。
					createOffScreen(canvas);

					// オフスクリーンに描画。
					drawView(offCanvas);

					// オフスクリーンを画面に描画。
					canvas.drawBitmap(offScreen, 0, 0, null);

				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
		}

		/**
		 * ライフを描画する。
		 */
		public void drawLifeLock(int x, int y, int val) {
			Canvas canvas = holder.lockCanvas();
			try {
				synchronized (holder) {

					// オフスクリーンを作成する。
					createOffScreen(canvas);

					// オフスクリーンにライフを描画。
					drawLife(offCanvas, x, y, val, true);

					// オフスクリーンを画面に描画。
					canvas.drawBitmap(offScreen, 0, 0, null);

				}
			} finally {
				if (canvas != null)
					holder.unlockCanvasAndPost(canvas);
			}
		}



//		@Override
//	    protected void onDraw(Canvas canvas) {
//
//		}


//		@Override
//	    public void draw(Canvas canvas) {
//			super.draw(canvas);
//		}
	}

	/**
	 * アプリが起動したときにコールされる。
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = new LifeSurfaceView(this);
		setContentView(view);

		generation = 0;
	}

	/**
	 * アプリが終わるときにコールされる。
	 */
	@Override
	protected void onDestroy() {
		view.stepStop();
		super.onDestroy();
	}


	/**
	 * ライフをクリアする。
	 */
	public void clearAll() {
		initField(max_col, max_row);
		generation = 0;
		population = 0;
	}


	/**
	 * ライフを表す配列。
	 */
	private int [][] field;

	/**
	 * ライフフィールドのグリッドを初期化する。
	 * @param col
	 * @param row
	 */
	public void initField(int col, int row) {
		max_col = col;
		max_row = row;
		field = new int [col][row];
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < col; x++) {
				field [x][y] = 0;
			}
		}
		view.drawViewLock();
	}

	/**
	 * フィールドの位置からセルの座標を取得する。
	 * @param mouse_x
	 * @param mouse_y
	 * @return
	 */
	public Point getCell(int mouse_x, int mouse_y) {
		Point p = new Point(-1, -1);
		for (int x = 0; x < max_col; x++) {
			if ( (mouse_x > x * cell_size)
				&& (mouse_x < x * cell_size + cell_size) ) {
				p.x = x;
				break;
			}
		}
		for (int y = 0; y < max_row; y++) {
			if ( (mouse_y > y * cell_size)
				&& (mouse_y < y * cell_size + cell_size) ) {
				p.y = y;
				break;
			}
		}
		return p;
	}

	/**
	 * ライフを配置する。
	 * @param x
	 * @param y
	 */
	public void putLife(int x, int y) {
		if (x < 0 || x >= max_col) return;
		if (y < 0 || y >= max_row) return;
		if (field [x][y] == 0) {
			field [x][y] = 1;
			population++;
		} else {
			field [x][y] = 0;
			population--;
		}
	}

	/**
	 * セルに対応する周囲のライフ数を数える。
	 * @param x
	 * @param y
	 * @return
	 */
	private int countLife(int x, int y) {
		int count = 0;
		if (x < 0 || y < 0 || x > max_col - 1 || y > max_row - 1) return 0;
		if (x > 0 && y > 0 && field[x-1][y-1] == 1) count++;
		if (y > 0 && field[x][y-1] == 1) count++;
		if (x < max_col - 1 && y > 0 && field[x+1][y-1] == 1) count++;
		if (x > 0 && field[x-1][y] == 1) count++;
		if (x < max_col - 1 && field[x+1][y] == 1) count++;
		if (x > 0 && y < max_row - 1 && field[x-1][y+1] == 1) count++;
		if (y < max_row - 1 && field[x][y+1] == 1) count++;
		if (x < max_col - 1 && y < max_row - 1 && field[x+1][y+1] == 1) count++;
		return count;
	}

	/**
	 * 生存できるか否か。
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isRemain(int x, int y) {
		for (int i = 0; i < condition_remain.length; i++) {
			if (condition_remain[i] == countLife(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 誕生するか否か。
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean isBirth(int x, int y) {
		for (int i = 0; i < condition_birth.length; i++) {
			if (condition_birth[i] == countLife(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 次の世代へ進む。
	 */
	private void next() {
		int [][] nextField = new int [max_col][max_row];
		population = 0;
		for (int y = 0; y < max_row; y++) {
			for (int x = 0; x < max_col; x++) {
				if (field[x][y] == 1 && isRemain(x, y)) {
					nextField[x][y] = 1;
					population++;
				} else if (field[x][y] == 0 && isBirth(x, y)) {
					nextField[x][y] = 1;
					population++;
				} else {
					nextField[x][y] = 0;
				}
			}
		}
		field = nextField;
		generation++;
	}

}