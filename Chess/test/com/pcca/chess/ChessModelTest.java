package com.pcca.chess;

import org.junit.jupiter.api.Test;

class ChessModelTest {

	@Test
	void testToString() {
		ChessModel chessModel = new ChessModel();
		chessModel.reset();
	    System.out.println(chessModel);
		
	}

}
