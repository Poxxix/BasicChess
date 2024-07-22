package com.pcca.chess;

import java.util.HashSet;
import java.util.Set;

public class ChessModel {
    private Set<ChessPiece> piecesBox = new HashSet<ChessPiece>();
    private Player playerInTurn = Player.WHITE;
    void reset() {
        piecesBox.removeAll(piecesBox);
        for (int i = 0; i < 2; i++) {
            piecesBox.add(new ChessPiece(0 + i * 7, 7, Player.BLACK, Rank.ROOK, ChessConstants.bRook));
            piecesBox.add(new ChessPiece(0 + i * 7, 0, Player.WHITE, Rank.ROOK, ChessConstants.wRook));
            piecesBox.add(new ChessPiece(1 + i * 5, 7, Player.BLACK, Rank.KNIGHT, ChessConstants.bKnight));
            piecesBox.add(new ChessPiece(1 + i * 5, 0, Player.WHITE, Rank.KNIGHT, ChessConstants.wKnight));
            piecesBox.add(new ChessPiece(2 + i * 3, 0, Player.WHITE, Rank.BISHOP, ChessConstants.wBishop));
            piecesBox.add(new ChessPiece(2 + i * 3, 7, Player.BLACK, Rank.BISHOP, ChessConstants.bBishop));
        }
        for (int i = 0; i < 8; i++) {
            piecesBox.add(new ChessPiece(i, 1, Player.WHITE, Rank.PAWN, ChessConstants.wPawn));
            piecesBox.add(new ChessPiece(i, 6, Player.BLACK, Rank.PAWN, ChessConstants.bPawn));
        }
        piecesBox.add(new ChessPiece(3, 0, Player.WHITE, Rank.KING, ChessConstants.wKing));
        piecesBox.add(new ChessPiece(3, 7, Player.BLACK, Rank.QUEEN, ChessConstants.bQueen));
        piecesBox.add(new ChessPiece(4, 7, Player.BLACK, Rank.KING, ChessConstants.bKing));
        piecesBox.add(new ChessPiece(4, 0, Player.WHITE, Rank.QUEEN, ChessConstants.wQueen));

        playerInTurn = Player.WHITE;
    }

    public ChessPiece movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        ChessPiece candidate = pieceAt(fromCol, fromRow);
        if (candidate == null || candidate.player != playerInTurn || !isValidMove(candidate, fromCol, fromRow, toCol, toRow)) {
            return null;
        }

        ChessPiece capturedPiece = pieceAt(toCol, toRow);
        if (capturedPiece != null) {
            if (capturedPiece.player == candidate.player) {
                return null;
            } else {
                piecesBox.remove(capturedPiece);
            }
        }

        if (candidate.rank == Rank.KING && Math.abs(fromCol - toCol) == 2) {
            if (isValidCastlingMove(candidate, fromCol, fromRow, toCol, toRow)) {
                int rookCol = toCol > fromCol ? 7 : 0;
                int rookNewCol = toCol > fromCol ? toCol - 1 : toCol + 1;
                ChessPiece rook = pieceAt(rookCol, fromRow);
                if (rook != null) {
                    rook.col = rookNewCol;
                }
            } else {
                return null;
            }
        }

        candidate.col = toCol;
        candidate.row = toRow;

        if (candidate.rank == Rank.PAWN && (toRow == 7 || toRow == 0)) {
            candidate.rank = Rank.QUEEN;
            candidate.imgName = candidate.player == Player.WHITE ? ChessConstants.wQueen : ChessConstants.bQueen;
        }

        playerInTurn = playerInTurn == Player.WHITE ? Player.BLACK : Player.WHITE;

        return capturedPiece;
    }


    public boolean isValidMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
        switch (piece.rank) {
            case PAWN:
                return isValidPawnMove(piece, fromCol, fromRow, toCol, toRow);
            case ROOK:
                return isValidRookMove(fromCol, fromRow, toCol, toRow);
            case KNIGHT:
                return isValidKnightMove(fromCol, fromRow, toCol, toRow);
            case BISHOP:
                return isValidBishopMove(fromCol, fromRow, toCol, toRow);
            case QUEEN:
                return isValidQueenMove(fromCol, fromRow, toCol, toRow);
            case KING:
                return isValidKingMove(fromCol, fromRow, toCol, toRow) || isValidCastlingMove(piece, fromCol, fromRow, toCol, toRow);
        }
        return false;
    }

    private boolean isValidPawnMove(ChessPiece piece, int fromCol, int fromRow, int toCol, int toRow) {
        int direction = piece.player == Player.WHITE ? 1 : -1;
        if (fromCol == toCol && pieceAt(toCol, toRow) == null) {
            if (toRow - fromRow == direction) {
                return true;
            } else if (fromRow == (piece.player == Player.WHITE ? 1 : 6) && toRow - fromRow == 2 * direction) {
                return true;
            }
        } else if (Math.abs(fromCol - toCol) == 1 && toRow - fromRow == direction && pieceAt(toCol, toRow) != null) {
            return true;
        }
        return false;
    }

    private boolean isValidRookMove(int fromCol, int fromRow, int toCol, int toRow) {
        if (fromCol != toCol && fromRow != toRow) {
            return false;
        }
        int colIncrement = fromCol == toCol ? 0 : (toCol > fromCol ? 1 : -1);
        int rowIncrement = fromRow == toRow ? 0 : (toRow > fromRow ? 1 : -1);
        int col = fromCol + colIncrement;
        int row = fromRow + rowIncrement;
        while (col != toCol || row != toRow) {
            if (pieceAt(col, row) != null) {
                return false;
            }
            col += colIncrement;
            row += rowIncrement;
        }
        return true;
    }

    private boolean isValidKnightMove(int fromCol, int fromRow, int toCol, int toRow) {
        int dCol = Math.abs(fromCol - toCol);
        int dRow = Math.abs(fromRow - toRow);
        return (dCol == 1 && dRow == 2) || (dCol == 2 && dRow == 1);
    }

    private boolean isValidBishopMove(int fromCol, int fromRow, int toCol, int toRow) {
        if (Math.abs(fromCol - toCol) != Math.abs(fromRow - toRow)) {
            return false;
        }
        int colIncrement = toCol > fromCol ? 1 : -1;
        int rowIncrement = toRow > fromRow ? 1 : -1;
        int col = fromCol + colIncrement;
        int row = fromRow + rowIncrement;
        while (col != toCol || row != toRow) {
            if (pieceAt(col, row) != null) {
                return false;
            }
            col += colIncrement;
            row += rowIncrement;
        }
        return true;
    }

    private boolean isValidQueenMove(int fromCol, int fromRow, int toCol, int toRow) {
        return isValidRookMove(fromCol, fromRow, toCol, toRow) || isValidBishopMove(fromCol, fromRow, toCol, toRow);
    }

    private boolean isValidKingMove(int fromCol, int fromRow, int toCol, int toRow) {
        int dCol = Math.abs(fromCol - toCol);
        int dRow = Math.abs(fromRow - toRow);
        return dCol <= 1 && dRow <= 1;
    }

    private boolean isValidCastlingMove(ChessPiece king, int fromCol, int fromRow, int toCol, int toRow) {
        if (king.rank != Rank.KING || Math.abs(fromCol - toCol) != 2 || fromRow != toRow) {
            return false;
        }
        int rookCol = toCol > fromCol ? 7 : 0;
        ChessPiece rook = pieceAt(rookCol, fromRow);
        if (rook == null || rook.rank != Rank.ROOK || rook.player != king.player) {
            return false;
        }
        int colIncrement = toCol > fromCol ? 1 : -1;
        for (int col = fromCol + colIncrement; col != rookCol; col += colIncrement) {
            if (pieceAt(col, fromRow) != null || isCheck(king.player)) {
                return false;
            }
        }
        return true;
    }

    ChessPiece pieceAt(int col, int row) {
        for (ChessPiece chessPiece : piecesBox) {
            if (chessPiece.col == col && chessPiece.row == row) {
                return chessPiece;
            }
        }
        return null;
    }

    boolean isCheck(Player player) {
        ChessPiece king = findKing(player);
        if (king == null) return false;
        for (ChessPiece piece : piecesBox) {
            if (piece.player != player && isValidMove(piece, piece.col, piece.row, king.col, king.row)) {
                return true;
            }
        }
        return false;
    }

    private ChessPiece findKing(Player player) {
        for (ChessPiece piece : piecesBox) {
            if (piece.rank == Rank.KING && piece.player == player) {
                return piece;
            }
        }
        return null;
    }

    public boolean isCheckMate(Player player) {
        if (!isCheck(player)) return false;
        for (ChessPiece piece : piecesBox) {
            if (piece.player == player) {
                int fromCol = piece.col;
                int fromRow = piece.row;
                for (int toCol = 0; toCol < 8; toCol++) {
                    for (int toRow = 0; toRow < 8; toRow++) {
                        if (isValidMove(piece, fromCol, fromRow, toCol, toRow)) {
                            ChessPiece target = pieceAt(toCol, toRow);
                            piece.col = toCol;
                            piece.row = toRow;
                            piecesBox.remove(target);
                            boolean check = isCheck(player);
                            piece.col = fromCol;
                            piece.row = fromRow;
                            if (target != null) piecesBox.add(target);
                            if (!check) return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public Player getPlayerInTurn() {
        return playerInTurn;
    }
}
