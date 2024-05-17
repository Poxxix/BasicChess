package com.pcca.chess;

import java.util.HashSet;
import java.util.Set;

public class ChessModel {
	private Set<ChessPiece> piecesBox = new HashSet<ChessPiece>();

	void reset() {
		for (int i = 0; i < 2; i++)
		{
		piecesBox.add(new ChessPiece(0 + i * 7,  7, Player.BLACK, Rank.ROOK, "Rook-black"));
		piecesBox.add(new ChessPiece(0 + i * 7,  0, Player.WHITE, Rank.ROOK, "Rook-white"));
		piecesBox.add(new ChessPiece(1 + i * 5,  7, Player.BLACK, Rank.KNIGHT, "Knight-black"));
		piecesBox.add(new ChessPiece(1 + i * 5,  0, Player.WHITE, Rank.KNIGHT, "Knight-white"));
		piecesBox.add(new ChessPiece(2 + i * 3,  0, Player.WHITE, Rank.BISHOP, "Bishop-white"));
		piecesBox.add(new ChessPiece(2 + i * 3,  7, Player.BLACK, Rank.BISHOP, "Bishop-black"));

		}
		for (int i =0; i< 8;i++)
		{
			piecesBox.add(new ChessPiece( i ,  1, Player.WHITE, Rank.PAWN, "Pawn-white"));
			piecesBox.add(new ChessPiece( i ,  6, Player.BLACK, Rank.PAWN, "Pawn-black"));
		}
		piecesBox.add(new ChessPiece( 3 ,  0, Player.WHITE, Rank.KING, "King-white"));
		piecesBox.add(new ChessPiece( 3 ,  7, Player.BLACK, Rank.QUEEN, "Queen-black"));
		piecesBox.add(new ChessPiece( 4 ,  7, Player.BLACK, Rank.KING, "King-black"));
		piecesBox.add(new ChessPiece( 4 ,  0, Player.WHITE, Rank.QUEEN, "Queen-white"));
	}

	ChessPiece pieceAt(int col, int row) {
		for (ChessPiece chessPiece : piecesBox)

		{
			if (chessPiece.col == col && chessPiece.row == row) {
			return chessPiece;
			}

		}
		return null;
	}

	@Override
	public String toString() {
		String desc = "";
		for (int row = 7; row >= 0; row--) {
			desc += "" + row; // 7 = "7"
			for (int col = 0; col < 8; col++) {
				ChessPiece p = pieceAt(col, row);
				if(p==null)
				{
					desc += " .";
				}
				else {
					desc += " ";
					switch (p.rank) {
					case KING: 
						desc += p.player == Player.WHITE ? "k" : "K";
						break;
					case QUEEN: 
						desc += p.player == Player.WHITE ? "q" : "Q";
						break;
					case BISHOP: 
						desc += p.player == Player.WHITE ? "b" : "B";
						break;
					case KNIGHT: 
						desc += p.player == Player.WHITE ? "n" : "N";
						break;
					case ROOK: 
						desc += p.player == Player.WHITE ? "r" : "R";
						break;
					case PAWN: 
						desc += p.player == Player.WHITE ? "p" : "P";
						break;
					}
			
				}
			}
			desc += "\n";
		}
		desc += "  0 1 2 3 4 5 6 7";
		return desc;
	}

}
