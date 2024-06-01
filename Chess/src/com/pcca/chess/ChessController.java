package com.pcca.chess;

import javax.swing.JFrame;

public class ChessController implements ChessDelegate {
	private ChessModel chessModel = new ChessModel();
	private ChessView panel;

	ChessController() {
		chessModel.reset();
		JFrame frame = new JFrame("Chess");
		frame.setSize(900,900);

		panel = new ChessView(this);
;

		frame.add(panel);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		new ChessController();
	}

	@Override
	public ChessPiece pieceAt(int col, int row) {
		return chessModel.pieceAt(col, row);

	}

	@Override
	public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
		// TODO Auto-generated method stub
		chessModel.movePiece(fromCol, fromRow, toCol, toRow);
		panel.repaint();
		
	}

}
