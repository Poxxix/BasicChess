package com.pcca.chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.pcca.chess.ChessView.Theme;

public class ChessController implements ChessDelegate, ActionListener, Runnable{
	private ChessModel chessModel = new ChessModel();
	private ChessView mainBoardPanel;
	private JButton resetBtn;
	private JButton cleintBtn;
	private JButton serverBtn;
	private JButton themeBtn;
    private Theme currentTheme = Theme.CLASSIC;
	ChessController() {
		chessModel.reset();
		var frame = new JFrame("Chess");
		frame.setSize(900,950);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		
		mainBoardPanel = new ChessView(this);


		frame.add(mainBoardPanel, BorderLayout.CENTER );
		var underPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		resetBtn = new JButton("Reset");
		underPanel.add(resetBtn);
		resetBtn.addActionListener(this);
		
		cleintBtn = new JButton("Connect");
		underPanel.add(cleintBtn);
		cleintBtn.addActionListener(this);
		
		serverBtn = new JButton("Listen");
		underPanel.add(serverBtn);
		serverBtn.addActionListener(this);
		
		themeBtn = new JButton("Theme");
		underPanel.add(themeBtn);
		themeBtn.addActionListener(this);
//		resetBtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				chessModel.reset();
//				mainBoardPanel.repaint();
//			}
//		});
//Anonymous class

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

	@Override
	public void actionPerformed(ActionEvent e) {
	
		if(e.getSource()==resetBtn)
		{
			chessModel.reset();
			mainBoardPanel.repaint();
		}
		else if(e.getSource() == serverBtn )
		{
			var pool = Executors.newFixedThreadPool(1);
			pool.execute(this);
		
			
		}
		else if(e.getSource() == cleintBtn)
		{
			System.out.println("connect(for socket client) click");
			try {
				var socket = new Socket("localhost",50000);
				var in = new Scanner(socket.getInputStream());
				var moveString = in.nextLine();//"0,1,0,2"
				System.out.println("from server: "+ moveString);
				var moveStringArr = moveString.split(",");//["0","1","0","2"]
				var fromCol = Integer.parseInt(moveStringArr[0]);
				var fromRow = Integer.parseInt(moveStringArr[1]);
				var toCol = Integer.parseInt(moveStringArr[2]);
				var toRow = Integer.parseInt(moveStringArr[3]);
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						chessModel.movePiece(fromCol, fromRow, toCol, toRow);
						mainBoardPanel.repaint();
					}
				});//Code luon luon duoc cap nhat chu k la sap 
		
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} 
		else if (e.getSource() == themeBtn) 
		{
	
			//System.out.println("theme changed");
			switch (currentTheme) {
			  case CLASSIC:
                  currentTheme = Theme.DARK;
                  break;
              case DARK:
                  currentTheme = Theme.BROWN;
                  break;
              case BROWN:
                  currentTheme = Theme.PURPLE;
                  break;
              case PURPLE:
                  currentTheme = Theme.CLASSIC;
                  break;
          }
        mainBoardPanel.changeTheme(currentTheme);

		}
    }

	@Override
	public void run() {
		try(var listener = new ServerSocket(50000)){
			System.out.println("server is listenting to port 50000");
			while (true)
			{
				try(var socket = listener.accept())
				{
					var out = new PrintWriter(socket.getOutputStream(),true);
					out.println("0,1,0,3");
					System.out.println("sending a move to cleint");
					//taskkill /PID <PID> /F
					//netstat -ano | findstr :50000
					//nc localhost 50000

				}
			}
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
	}

}
