package com.pcca.chess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ChessView extends JPanel implements MouseListener , MouseMotionListener{

	private ChessDelegate chessDelegate;

	private int originX = -1;
	private int originY = -1;
	private int cellSize = -1;
	private double scaleFactor = 1;

	Map<String, Image> keyNameValueImage = new HashMap<String, Image>();
	private int fromCol = -1;
	private int fromRow = -1;
	private ChessPiece movingPiece;	
	private Point movingPiecePoint;
	ChessView(ChessDelegate chessDelegate) {
		this.chessDelegate = chessDelegate;
	}

	{
		String[] imageNames = { ChessConstants.wRook, ChessConstants.bRook, ChessConstants.wBishop,
				ChessConstants.bBishop, ChessConstants.bKing, ChessConstants.wKing, ChessConstants.wKnight,
				ChessConstants.bKnight, ChessConstants.bPawn, ChessConstants.wPawn, ChessConstants.bQueen,
				ChessConstants.wQueen };

		try {
			for (String imgNm : imageNames) {
				Image img = LoadImage(imgNm + ".png");
				keyNameValueImage.put(imgNm, img);
			}
		} catch (Exception e) {

			e.printStackTrace();

		}
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		int size = Math.min(getSize().width, getSize().height);
		cellSize = (int) (((double) size) * scaleFactor / 8);
		Graphics2D g2 = (Graphics2D) g;

		originX = (getSize().width - 8 * cellSize) / 2;
		originY = (int) ((getSize().height - 7.8 * cellSize) / 2);
		drawBoard(g2);
		drawPieces(g2);

	}

	private void drawPieces(Graphics2D g2) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				ChessPiece p = chessDelegate.pieceAt(col, row);
				if (p != null && p != movingPiece) {
					drawImage(g2, col, row, p.imgName);
				}
			}
		}
		if(movingPiece != null && movingPiecePoint != null)
		{
			Image img = keyNameValueImage.get(movingPiece.imgName);
			g2.drawImage(img,movingPiecePoint.x - cellSize/2,movingPiecePoint.y - cellSize/2, cellSize, cellSize, null);
		}

	}

	private void drawImage(Graphics2D g2, int col, int row, String imgName) {
		Image img = keyNameValueImage.get(imgName);
		g2.drawImage(img, originX + col * cellSize, originX + row * cellSize, cellSize, cellSize, null);
	}

	private Image LoadImage(String imgFileName) throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resU = classLoader.getResource("img/" + imgFileName);

		if (resU == null) {
			return null;
		} else {
			File imgFile = new File(resU.toURI());

			return ImageIO.read(imgFile);

		}
	}

	private void drawBoard(Graphics2D g2) {
		for (int n = 0; n < 4; n++) {
			for (int i = 0; i < 4; i++) {
				Square(g2, 0 + 2 * i, 0 + 2 * n, true);
				Square(g2, 1 + 2 * i, 1 + 2 * n, true);
				Square(g2, 1 + 2 * i, 0 + 2 * n, false);
				Square(g2, 0 + 2 * i, 1 + 2 * n, false);

			}
		}

	}

	private void Square(Graphics2D g2, int col, int row, boolean light) {
		g2.setColor(light ? Color.white : Color.pink);
		g2.fillRect(originX + col * cellSize, originY + row * cellSize, cellSize, cellSize);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

		fromCol = (e.getPoint().x - originX) / cellSize;
		fromRow = (e.getPoint().y - originY) / cellSize;
		movingPiece = chessDelegate.pieceAt(fromCol, fromRow);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
	
		int col = (e.getPoint().x - originX) / cellSize;
		int row = (e.getPoint().y - originY) / cellSize;
		//System.out.println("from " + fromCol + " to" + col);
		//test vi tri 
		chessDelegate.movePiece(fromCol, fromRow, col, row);
		movingPiece = null;
		movingPiecePoint = null;

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	movingPiecePoint =  e.getPoint();
	repaint();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
