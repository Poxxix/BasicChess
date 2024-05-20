package com.pcca.chess;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ChessView extends JPanel {
	
	private ChessDelegate chessDelegate;

	private int originX = -1;
	private int originY = -1;
	private int cellSize = -1;
	private double scaleFactor = 1;

	Map<String, Image> keyNameValueImage = new HashMap<String, Image>();

	ChessView(ChessDelegate chessDelegate) 
	{
		this.chessDelegate = chessDelegate;
	}
        {
		String[] imageNames = { ChessConstants.wRook, ChessConstants.bRook, ChessConstants.wBishop, ChessConstants.bBishop, ChessConstants.bKing, ChessConstants.wKing,
				ChessConstants.wKnight, ChessConstants.bKnight, ChessConstants.bPawn, ChessConstants.wPawn, ChessConstants.bQueen,  ChessConstants.wQueen };

		try {
			for (String imgNm : imageNames) {
				Image img = LoadImage(imgNm + ".png");
				keyNameValueImage.put(imgNm, img);
			}
		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	@Override
	protected void paintChildren(Graphics g) {
		super.paintChildren(g);
		int size = Math.min(getSize().width, getSize().height);
		cellSize = (int) (((double) size) * scaleFactor /8) ;
		Graphics2D g2 = (Graphics2D) g;

	    originX = (getSize().width - 8 * cellSize) /2;
	    originY = (int) ((getSize().height - 7.8 * cellSize) /2); 	
		drawBoard(g2);
	    drawPieces(g2);
	
	}
	private void drawPieces(Graphics2D g2)
	{
		for(int row =0; row <8; row++)
		{
			for(int col = 0; col<8; col++)
			{
				ChessPiece p = chessDelegate.pieceAt(col, row);
				if(p!= null)
				{
					drawImage(g2, col, row, p.imgName);
				}
			}
		}
		
	}
	
	

	private void drawImage(Graphics2D g2, int col, int row, String imgName) {
		Image img = keyNameValueImage.get(imgName);
		g2.drawImage(img, originX + col * cellSize,originX + row * cellSize, cellSize, cellSize, null);
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
}
