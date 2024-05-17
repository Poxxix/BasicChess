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

public class ChessPanel extends JPanel {

	int originX = 70;
	int originY = 70;
	int cellSize = 80;

	Map<String, Image> keyNameValueImage = new HashMap<String, Image>();

	public ChessPanel() {
		String[] imageNames = { "Rook-white", "Rook-black", "Bishop-white", "Bishop-black", "King-black", "King-white",
				"Knight-white", "Knight-Black", "Pawn-black", "Pawn-white", "Queen-black", "Queen-white" };

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

		Graphics2D g2 = (Graphics2D) g;

		drawBoard(g2);
		drawImage(g2, 0,0,"Rook-black");
	
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
