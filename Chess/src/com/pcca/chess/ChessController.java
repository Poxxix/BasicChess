package com.pcca.chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

public class ChessController implements ChessDelegate, ActionListener {
	private ChessModel chessModel = new ChessModel();
	private ChessView mainBoardPanel;
	private JButton resetBtn;
	private JButton cleintBtn;
	private JButton serverBtn;
	private JButton themeBtn;
	private PrintWriter printWriter;
	private JFrame frame;
	private Socket socket;
	private int PORT = 50000;
	private Theme currentTheme = Theme.CLASSIC;

	ChessController() {
		chessModel.reset();
		frame = new JFrame("Chess");
		frame.setSize(900, 950);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());

		mainBoardPanel = new ChessView(this);
				
		frame.add(mainBoardPanel, BorderLayout.CENTER);
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
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				printWriter.close();
				try {
					socket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
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
		if (printWriter != null) {
			printWriter.println(fromCol + "," + fromRow + "," + toCol + "," + toRow);
		}

	}
	
	private void reciveMove(Scanner scanner)
	{
		while (scanner.hasNextLine()) {
			var moveString = scanner.nextLine();// "0,1,0,2"
			//System.out.println("chess move: " + moveString);//
			//test move
			var moveStringArr = moveString.split(",");// ["0","1","0","2"]
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
			});// Code luon luon duoc cap nhat chu k la sap
		}
	}
	
	private void runSocketServer() {
		Executors.newFixedThreadPool(1).execute(new Runnable() {
			@Override
			public void run() {
				try (var listener = new ServerSocket(PORT)) {
					System.out.println("server is listenting on port" + PORT);
								socket = listener.accept();
								printWriter = new PrintWriter(socket.getOutputStream(), true);
								var scanner = new Scanner(socket.getInputStream());
							
							reciveMove(scanner);
						}
						
							// taskkill /PID <PID> /F
							// netstat -ano | findstr :50000
							// nc localhost 50000
				catch (IOException e1) {

					e1.printStackTrace();
				}
				
			}
		});
	}
	private void runSocketClient() {
		try {
			socket = new Socket("localhost", PORT);
			System.out.println("client connected to port " + PORT);
			var scanner = new Scanner(socket.getInputStream());
			printWriter = new PrintWriter(socket.getOutputStream(),true);
		
		Executors.newFixedThreadPool(1).execute(new Runnable() {
			
			@Override
			public void run() {
			reciveMove(scanner);
				
			}
		});

	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == resetBtn) {
			chessModel.reset();
			mainBoardPanel.repaint();
		} else if (e.getSource() == serverBtn) {
			runSocketServer();
			serverBtn.setEnabled(false);
			frame.setTitle("Chess Server - KienDz");
		} else if (e.getSource() == cleintBtn) {
			frame.setTitle("Chess Client - KienDz");
			cleintBtn.setEnabled(false);
			runSocketClient();
		} else if (e.getSource() == themeBtn) {

			// System.out.println("theme changed");
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

	
}
