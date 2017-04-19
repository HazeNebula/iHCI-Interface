package nl.ru.ai.draw_interface;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;

public class Window extends JFrame {
	public Window() {
		super();

		setSize( new Dimension( 1280, 960 ) );
		setTitle( "Paint" );
		setLocationRelativeTo( null );
		setDefaultCloseOperation( EXIT_ON_CLOSE );
		getContentPane().setLayout( new BorderLayout() );

		DrawPanel drawPanel = new DrawPanel();
		ToolPanel toolPanel = new ToolPanel();
		ColorPanel colorPanel = new ColorPanel();

		drawPanel.setToolPanel( toolPanel );
		drawPanel.setColorPanel( colorPanel );
		toolPanel.setDrawPanel( drawPanel );
		colorPanel.setDrawPanel( drawPanel );

		getContentPane().add( toolPanel, BorderLayout.WEST );
		getContentPane().add( drawPanel, BorderLayout.CENTER );
		getContentPane().add( colorPanel, BorderLayout.SOUTH );

		setVisible( true );
	}
}
