package nl.ru.ai.draw_interface;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

public class ToolPanel extends JPanel {
	private static final Color BUTTONCOLOR_DEFAULT = new Color( 0xFFE0E0E0, true );
	private static final Color BUTTONCOLOR_SELECTED = new Color( 0xFFE0FFFF, true );
	private static final int STROKEWIDTH_MIN = 0;
	private static final int STROKEWIDTH_MAX = 50;
	private static final int STROKEWIDTH_INIT = 5;

	private static final String[] TOOLTIPTEXT = { "Select an object in order to edit it", "Open an image", "Create text", "Draw a line", "Draw a triangle", "Draw a rectangle", "Draw an ellipse", "Recognizable drawing", "Freehand drawing", "Undo last action (Ctrl + Z)", "Clear all", "Change line width" };

	private JButton selectButton;
	private JButton imageButton;
	private JButton textButton;
	private JButton lineButton;
	private JButton triangleButton;
	private JButton rectangleButton;
	private JButton ellipseButton;
	private JButton recognizeButton;
	private JButton freedrawButton;
	private JButton clearAllButton;
	private JButton undoButton;
	private JLabel strokeWidthLabel;
	private JSlider strokeWidthSlider;

	private JButton currentButton;

	private DrawPanel drawPanel;

	private InputHandler inputHandler = new InputHandler() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			switch ( e.getActionCommand() ) {
			case "Select":
				setButtonSelection( Tool_t.SELECTION );
				drawPanel.setTool( Tool_t.SELECTION );
				break;
			case "Image":
				setButtonSelection( Tool_t.IMAGE );
				drawPanel.setTool( Tool_t.IMAGE );
				break;
			case "Text":
				setButtonSelection( Tool_t.TEXT );
				drawPanel.setTool( Tool_t.TEXT );
				break;
			case "Line":
				setButtonSelection( Tool_t.LINE );
				drawPanel.setTool( Tool_t.LINE );
				break;
			case "Triangle":
				setButtonSelection( Tool_t.TRIANGLE );
				drawPanel.setTool( Tool_t.TRIANGLE );
				break;
			case "Rectangle":
				setButtonSelection( Tool_t.RECTANGLE );
				drawPanel.setTool( Tool_t.RECTANGLE );
				break;
			case "Ellipse":
				setButtonSelection( Tool_t.ELLIPSE );
				drawPanel.setTool( Tool_t.ELLIPSE );
				break;
			case "Recognize":
				setButtonSelection( Tool_t.RECOGNIZE );
				drawPanel.setTool( Tool_t.RECOGNIZE );
				break;
			case "FreeDraw":
				setButtonSelection( Tool_t.FREEDRAW );
				drawPanel.setTool( Tool_t.FREEDRAW );
				break;
			case "ClearAll":
				currentButton.setBackground( BUTTONCOLOR_DEFAULT );
				drawPanel.clearShapes();
				drawPanel.setTool( Tool_t.NONE );
				break;
			case "Undo":
				drawPanel.undoLastAction();
				break;
			}

			drawPanel.setButtonSelection( null );
		}

		@Override
		public void stateChanged( ChangeEvent e ) {
			drawPanel.setStrokeSize( strokeWidthSlider.getValue() );
		}
	};

	public ToolPanel() {
		super();

		selectButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/selectIcon.png" ) ) );
		selectButton.setBackground( BUTTONCOLOR_DEFAULT );
		selectButton.setToolTipText( TOOLTIPTEXT[0] );
		selectButton.setActionCommand( "Select" );
		selectButton.addActionListener( inputHandler );

		imageButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/imageIcon.png" ) ) );
		imageButton.setBackground( BUTTONCOLOR_DEFAULT );
		imageButton.setToolTipText( TOOLTIPTEXT[1] );
		imageButton.setActionCommand( "Image" );
		imageButton.addActionListener( inputHandler );

		textButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/textIcon.png" ) ) );
		textButton.setBackground( BUTTONCOLOR_DEFAULT );
		textButton.setToolTipText( TOOLTIPTEXT[2] );
		textButton.setActionCommand( "Text" );
		textButton.addActionListener( inputHandler );

		lineButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/lineIcon.png" ) ) );
		lineButton.setBackground( BUTTONCOLOR_DEFAULT );
		lineButton.setToolTipText( TOOLTIPTEXT[3] );
		lineButton.setActionCommand( "Line" );
		lineButton.addActionListener( inputHandler );

		triangleButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/triangleIcon.png" ) ) );
		triangleButton.setBackground( BUTTONCOLOR_DEFAULT );
		triangleButton.setToolTipText( TOOLTIPTEXT[4] );
		triangleButton.setActionCommand( "Triangle" );
		triangleButton.addActionListener( inputHandler );

		rectangleButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/rectangleIcon.png" ) ) );
		rectangleButton.setBackground( BUTTONCOLOR_DEFAULT );
		rectangleButton.setToolTipText( TOOLTIPTEXT[5] );
		rectangleButton.setActionCommand( "Rectangle" );
		rectangleButton.addActionListener( inputHandler );

		ellipseButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/ellipseIcon.png" ) ) );
		ellipseButton.setBackground( BUTTONCOLOR_DEFAULT );
		ellipseButton.setToolTipText( TOOLTIPTEXT[6] );
		ellipseButton.setActionCommand( "Ellipse" );
		ellipseButton.addActionListener( inputHandler );

		recognizeButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/recognizeIcon.png" ) ) );
		recognizeButton.setBackground( BUTTONCOLOR_DEFAULT );
		recognizeButton.setToolTipText( TOOLTIPTEXT[7] );
		recognizeButton.setActionCommand( "Recognize" );
		recognizeButton.addActionListener( inputHandler );

		freedrawButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/freedrawIcon.png" ) ) );
		freedrawButton.setBackground( BUTTONCOLOR_DEFAULT );
		freedrawButton.setToolTipText( TOOLTIPTEXT[8] );
		freedrawButton.setActionCommand( "FreeDraw" );
		freedrawButton.addActionListener( inputHandler );

		undoButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/undoIcon.png" ) ) );
		undoButton.setBackground( BUTTONCOLOR_DEFAULT );
		undoButton.setToolTipText( TOOLTIPTEXT[9] );
		undoButton.setActionCommand( "Undo" );
		undoButton.addActionListener( inputHandler );

		clearAllButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/clearAllIcon.png" ) ) );
		clearAllButton.setBackground( BUTTONCOLOR_DEFAULT );
		clearAllButton.setToolTipText( TOOLTIPTEXT[10] );
		clearAllButton.setActionCommand( "ClearAll" );
		clearAllButton.addActionListener( inputHandler );

		strokeWidthLabel = new JLabel( "Line Width:" );
		selectButton.setToolTipText( TOOLTIPTEXT[11] );

		strokeWidthSlider = new JSlider( SwingConstants.VERTICAL, STROKEWIDTH_MIN, STROKEWIDTH_MAX, STROKEWIDTH_INIT );
		strokeWidthSlider.setMajorTickSpacing( 10 );
		strokeWidthSlider.setPaintTicks( true );
		strokeWidthSlider.setPaintLabels( true );
		strokeWidthSlider.setBackground( this.getBackground() );
		strokeWidthSlider.setToolTipText( TOOLTIPTEXT[11] );
		strokeWidthSlider.addChangeListener( inputHandler );

		GroupLayout layout = new GroupLayout( this );
		setLayout( layout );
		layout.setAutoCreateGaps( true );
		layout.setAutoCreateContainerGaps( true );

		layout.setHorizontalGroup( 
				layout.createParallelGroup( GroupLayout.Alignment.CENTER )
					.addComponent( selectButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( imageButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( textButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( lineButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( triangleButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( rectangleButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( ellipseButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( recognizeButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( freedrawButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( undoButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( clearAllButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( strokeWidthLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( strokeWidthSlider, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) );
			
			layout.setVerticalGroup( 
				layout.createSequentialGroup()
					.addComponent( selectButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( imageButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( textButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( lineButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( triangleButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( rectangleButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( ellipseButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( recognizeButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( freedrawButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( undoButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( clearAllButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent( strokeWidthLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
					.addComponent( strokeWidthSlider, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) );

		currentButton = recognizeButton;
		currentButton.setBackground( BUTTONCOLOR_SELECTED );
	}

	public void setDrawPanel( DrawPanel dp ) {
		this.drawPanel = dp;
	}

	public void setButtonSelection( Tool_t tool ) {
		if ( tool == null ) {
			currentButton.setBackground( BUTTONCOLOR_DEFAULT );
		} else {
			JButton button = null;

			switch ( tool ) {
			case SELECTION:
				button = selectButton;
				break;
			case IMAGE:
				button = imageButton;
				break;
			case TEXT:
				button = textButton;
				break;
			case LINE:
				button = lineButton;
				break;
			case TRIANGLE:
				button = triangleButton;
				break;
			case RECTANGLE:
				button = rectangleButton;
				break;
			case ELLIPSE:
				button = ellipseButton;
				break;
			case RECOGNIZE:
				button = recognizeButton;
				break;
			case FREEDRAW:
				button = freedrawButton;
				break;
			default:
				button = currentButton;
				break;
			}

			currentButton.setBackground( BUTTONCOLOR_DEFAULT );
			button.setBackground( BUTTONCOLOR_SELECTED );
			currentButton = button;
		}
	}

	public void setStrokeWidthSlider( int value ) {
		strokeWidthSlider.setValue( value );
	}
}
