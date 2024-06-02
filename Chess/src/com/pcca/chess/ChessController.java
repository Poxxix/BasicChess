package com.pcca.chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChessController implements ChessDelegate {
	private ChessModel chessModel = new ChessModel();
	private ChessView centerPanel;

	ChessController() {
		chessModel.reset();
		JFrame frame = new JFrame("Chess");
		frame.setSize(600,650);
	
		frame.setLayout(new BorderLayout());
		
		centerPanel = new ChessView(this);


		frame.add(centerPanel, BorderLayout.CENTER );
		JPanel southPandel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton resetBtn = new JButton("Reset");
		southPandel.add(resetBtn);
		JButton Delete = new JButton("Delete");
		southPandel.add(Delete);
		frame.add(southPandel, BorderLayout.PAGE_END);
		
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
		centerPanel.repaint();
		
	}

}
