package com.pcca.chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ChessController implements ChessDelegate {
	private ChessModel chessModel = new ChessModel();
	private ChessView mainBoardPanel;

	ChessController() {
		chessModel.reset();
		JFrame frame = new JFrame("Chess");
		frame.setSize(600,650);
	
		frame.setLayout(new BorderLayout());
		
		mainBoardPanel = new ChessView(this);


		frame.add(mainBoardPanel, BorderLayout.CENTER );
		JPanel underPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton resetBtn = new JButton("Reset");
		underPanel.add(resetBtn);
		resetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				chessModel.reset();
				mainBoardPanel.repaint();
			}
		});
		JButton Delete = new JButton("Delete");
		underPanel.add(Delete);
		frame.add(underPanel, BorderLayout.PAGE_END);
		
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
		mainBoardPanel.repaint();
		
	}

}
