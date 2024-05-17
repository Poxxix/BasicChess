package com.pcca.chess;

import javax.swing.JFrame;

public class Chess {

	public static void main(String[] args) {
		JFrame frame = new JFrame("Chess");
		frame.setSize(800,800);
	
		ChessPanel panel = new ChessPanel();
		frame.add(panel);
		frame.setVisible(true);
		
	}

}
