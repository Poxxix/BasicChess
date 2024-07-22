package com.pcca.chess;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
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
import javax.swing.JOptionPane;
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
	private ChessCapturedPieces capturedPiecesPanel; // Thêm biến này
	private JFrame capturedFrame; // JFrame riêng cho ChessCapturedPieces

	ChessController() {
		chessModel.reset();
		frame = new JFrame("Chess");
		frame.setSize(900, 950); // Tăng chiều cao để có chỗ cho quân cờ bị bắt
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());
		mainBoardPanel = new ChessView(this);
		capturedPiecesPanel = new ChessCapturedPieces(); // Tạo instance cho ChessCapturedPieces
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(mainBoardPanel, BorderLayout.CENTER);
		mainPanel.add(capturedPiecesPanel, BorderLayout.SOUTH);
		frame.add(mainPanel, BorderLayout.CENTER);
		capturedFrame = new JFrame("Captured Pieces");
		capturedFrame.setSize(70, 200);
		capturedFrame.setLayout(new BorderLayout());
		capturedFrame.add(capturedPiecesPanel, BorderLayout.CENTER);
		capturedFrame.setVisible(true);
		capturedFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		capturedFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				capturedFrame.setVisible(false); // Chỉ ẩn cửa sổ khi đóng
			}
		});
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
		// resetBtn.addActionListener(new ActionListener() {
		//
		// @Override
		// public void actionPerformed(ActionEvent e) {
		// chessModel.reset();
		// mainBoardPanel.repaint();
		// }Z
		// });
		// Anonymous class

		frame.add(underPanel, BorderLayout.PAGE_END);

		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				if (printWriter != null) {
					printWriter.close();
				}
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
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
	    ChessPiece capturedPiece = chessModel.movePiece(fromCol, fromRow, toCol, toRow);

	    if (capturedPiece != null) {
	        Image capturedImage = mainBoardPanel.getImageForPiece(capturedPiece.imgName);
	        capturedPiecesPanel.addCapturedPiece(capturedImage);
	        
	        if (capturedPiece.imgName.equals(ChessConstants.bKing) || capturedPiece.imgName.equals(ChessConstants.wKing)) {
	            String winner = (capturedPiece.imgName.equals(ChessConstants.bKing)) ? "White" : "Black";
	            JOptionPane.showMessageDialog(frame, "Checkmate! " + winner + " wins!");
	            
	            if (printWriter != null) {
	                printWriter.println("CHECKMATE:" + winner);
	            }
	            
	            chessModel.reset();
	            capturedPiecesPanel.clearCapturedPieces();
	            mainBoardPanel.repaint();
	            return; // Kết thúc hàm nếu vua bị ăn
	        }

	        if (printWriter != null) {
	            printWriter.println("CAPTURED:" + capturedPiece.imgName);
	        }
	    }

	    mainBoardPanel.repaint();

	    if (printWriter != null) {
	        printWriter.println(fromCol + "," + fromRow + "," + toCol + "," + toRow);
	    }
	}

	private void receiveMove(Scanner scanner) {
	    while (scanner.hasNextLine()) {
	        var message = scanner.nextLine();
	        if (message.startsWith("CHECKMATE:")) {
	            var winner = message.substring("CHECKMATE:".length());
	            JOptionPane.showMessageDialog(frame, "Checkmate! " + winner + " wins!");
	            chessModel.reset();
	            capturedPiecesPanel.clearCapturedPieces();
	            mainBoardPanel.repaint();
	            return; // Kết thúc hàm nếu vua bị ăn
	        } else if (message.startsWith("CAPTURED:")) {
	            var imgName = message.substring("CAPTURED:".length());
	            Image capturedImage = mainBoardPanel.getImageForPiece(imgName);
	            capturedPiecesPanel.addCapturedPiece(capturedImage);
	        } else {
	            var moveStringArr = message.split(",");
	            var fromCol = Integer.parseInt(moveStringArr[0]);
	            var fromRow = Integer.parseInt(moveStringArr[1]);
	            var toCol = Integer.parseInt(moveStringArr[2]);
	            var toRow = Integer.parseInt(moveStringArr[3]);
	            SwingUtilities.invokeLater(() -> {
	                chessModel.movePiece(fromCol, fromRow, toCol, toRow);
	                mainBoardPanel.repaint();
	            });
	        }
	    }
	}


	private void runSocketServer() {
	    Executors.newFixedThreadPool(1).execute(() -> {
	        try (var listener = new ServerSocket(PORT)) {
	            System.out.println("Server is listening on port " + PORT);
	            socket = listener.accept();
	            printWriter = new PrintWriter(socket.getOutputStream(), true);
	            var scanner = new Scanner(socket.getInputStream());

	            receiveMove(scanner);
	        } catch (IOException e1) {
	            e1.printStackTrace();
	        }
	    });
	}

	private void runSocketClient() {
	    try {
	        socket = new Socket("localhost", PORT);
	        System.out.println("Client connected to port " + PORT);
	        var scanner = new Scanner(socket.getInputStream());
	        printWriter = new PrintWriter(socket.getOutputStream(), true);

	        Executors.newFixedThreadPool(1).execute(() -> {
	            receiveMove(scanner);
	        });
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    }
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == resetBtn) {
			chessModel.reset();
			capturedPiecesPanel.clearCapturedPieces(); // Xóa danh sách quân cờ bị bắt khi reset
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
