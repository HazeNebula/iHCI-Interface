package nl.ru.ai.draw_interface;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

//TODO: add radio buttons for color changing
public class ColorPanel extends JPanel {
	private static final String[] TOOLTIPTEXT = { "Leftclick to change color", "Rightclick to change color" };

	private DrawPanel drawPanel;

	private boolean changeLineColor;

	private JButton blackButton;
	private JButton darkGrayButton;
	private JButton grayButton;
	private JButton lightGrayButton;
	private JButton whiteButton;
	private JButton pinkButton;
	private JButton redButton;
	private JButton orangeButton;
	private JButton yellowButton;
	private JButton greenButton;
	private JButton blueButton;
	private JButton magentaButton;
	private JButton cyanButton;
	private JButton noColorButton;

	private JLabel lineColorTextLabel;
	private JRadioButton lineColorButton;
	private JLabel lineColorLabel;
	private JLabel fillColorTextLabel;
	private JRadioButton fillColorButton;
	private JLabel fillColorLabel;

	InputHandler inputHandler = new InputHandler() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			switch ( e.getActionCommand() ) {
			case "LineColor":
				changeLineColor = true;

				break;
			case "FillColor":
				changeLineColor = false;

				break;
			default:
				Color color = null;
				ImageIcon icon = null;

				color = stringToColor( e.getActionCommand() );
				icon = colorToIcon( color );

				if ( changeLineColor ) {
					lineColorLabel.setIcon( icon );
					drawPanel.setLineColor( color );
				} else {
					fillColorLabel.setIcon( icon );
					drawPanel.setFillColor( color );
				}

				break;
			}
		}
	};

	public ColorPanel() {
		changeLineColor = true;

		blackButton = new JButton();
		blackButton.setActionCommand( "Black" );
		blackButton.setBackground( Color.BLACK );
		blackButton.addActionListener( inputHandler );

		darkGrayButton = new JButton();
		darkGrayButton.setActionCommand( "DarkGray" );
		darkGrayButton.setBackground( Color.DARK_GRAY );
		darkGrayButton.addActionListener( inputHandler );

		grayButton = new JButton();
		grayButton.setActionCommand( "Gray" );
		grayButton.setBackground( Color.GRAY );
		grayButton.addActionListener( inputHandler );

		lightGrayButton = new JButton();
		lightGrayButton.setActionCommand( "LightGray" );
		lightGrayButton.setBackground( Color.LIGHT_GRAY );
		lightGrayButton.addActionListener( inputHandler );

		whiteButton = new JButton();
		whiteButton.setActionCommand( "White" );
		whiteButton.setBackground( Color.WHITE );
		whiteButton.addActionListener( inputHandler );

		pinkButton = new JButton();
		pinkButton.setActionCommand( "Pink" );
		pinkButton.setBackground( Color.PINK );
		pinkButton.addActionListener( inputHandler );

		redButton = new JButton();
		redButton.setActionCommand( "Red" );
		redButton.setBackground( Color.RED );
		redButton.addActionListener( inputHandler );

		orangeButton = new JButton();
		orangeButton.setActionCommand( "Orange" );
		orangeButton.setBackground( Color.ORANGE );
		orangeButton.addActionListener( inputHandler );

		yellowButton = new JButton();
		yellowButton.setActionCommand( "Yellow" );
		yellowButton.setBackground( Color.YELLOW );
		yellowButton.addActionListener( inputHandler );

		greenButton = new JButton();
		greenButton.setActionCommand( "Green" );
		greenButton.setBackground( Color.GREEN );
		greenButton.addActionListener( inputHandler );

		blueButton = new JButton();
		blueButton.setActionCommand( "Blue" );
		blueButton.setBackground( Color.BLUE );
		blueButton.addActionListener( inputHandler );

		magentaButton = new JButton();
		magentaButton.setActionCommand( "Magenta" );
		magentaButton.setBackground( Color.MAGENTA );
		magentaButton.addActionListener( inputHandler );

		cyanButton = new JButton();
		cyanButton.setActionCommand( "Cyan" );
		cyanButton.setBackground( Color.CYAN );
		cyanButton.addActionListener( inputHandler );

		noColorButton = new JButton( new ImageIcon( "images\\colors\\noColorIcon.png" ) );
		noColorButton.setActionCommand( "NoColor" );
		noColorButton.setBackground( Color.WHITE );
		noColorButton.addActionListener( inputHandler );

		lineColorTextLabel = new JLabel( "Line color:" );
		lineColorTextLabel.setToolTipText( TOOLTIPTEXT[0] );

		lineColorButton = new JRadioButton();
		lineColorButton.setActionCommand( "LineColor" );
		lineColorButton.setToolTipText( TOOLTIPTEXT[0] );
		lineColorButton.setSelected( true );
		lineColorButton.addActionListener( inputHandler );

		lineColorLabel = new JLabel( new ImageIcon( "images\\colors\\blackIcon.png" ) );
		lineColorLabel.setToolTipText( TOOLTIPTEXT[0] );

		fillColorTextLabel = new JLabel( "Fill color:" );
		fillColorTextLabel.setToolTipText( TOOLTIPTEXT[1] );

		fillColorButton = new JRadioButton();
		fillColorButton.setActionCommand( "FillColor" );
		fillColorButton.setToolTipText( TOOLTIPTEXT[1] );
		fillColorButton.addActionListener( inputHandler );

		fillColorLabel = new JLabel( new ImageIcon( "images\\colors\\noColorIcon.png" ) );
		fillColorLabel.setToolTipText( TOOLTIPTEXT[1] );

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add( lineColorButton );
		buttonGroup.add( fillColorButton );

		GroupLayout layout = new GroupLayout( this );
		setLayout( layout );
		layout.setAutoCreateGaps( true );
		layout.setAutoCreateContainerGaps( true );
		
		layout.setHorizontalGroup( 
			layout.createSequentialGroup()
				.addComponent( blackButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( darkGrayButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( grayButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( lightGrayButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( whiteButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( pinkButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( redButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( orangeButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( yellowButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( greenButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( blueButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( magentaButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( cyanButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( noColorButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addGroup( 
					layout.createParallelGroup( GroupLayout.Alignment.LEADING )
						.addComponent( lineColorTextLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( fillColorTextLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
				.addGroup( 
					layout.createParallelGroup( GroupLayout.Alignment.LEADING )
						.addComponent( lineColorButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( fillColorButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
				.addGroup( 
					layout.createParallelGroup( GroupLayout.Alignment.LEADING )
						.addComponent( lineColorLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( fillColorLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) );
		
		layout.setVerticalGroup( 
			layout.createParallelGroup( GroupLayout.Alignment.CENTER )
				.addComponent( blackButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( darkGrayButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( grayButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( lightGrayButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( whiteButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( pinkButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( redButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( orangeButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( yellowButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( greenButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( blueButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( magentaButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( cyanButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addComponent( noColorButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
				.addGroup( 
					layout.createSequentialGroup()
						.addComponent( lineColorTextLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( fillColorTextLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
				.addGroup( 
					layout.createSequentialGroup()
						.addComponent( lineColorButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( fillColorButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) )
				.addGroup( 
					layout.createSequentialGroup()
						.addComponent( lineColorLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE )
						.addComponent( fillColorLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE ) ) );
	}

	public void setDrawPanel( DrawPanel drawPanel ) {
		this.drawPanel = drawPanel;
	}

	private Color stringToColor( String s ) {
		Color color;

		switch ( s ) {
		case "Black":
			color = Color.BLACK;
			break;
		case "DarkGray":
			color = Color.DARK_GRAY;
			break;
		case "Gray":
			color = Color.GRAY;
			break;
		case "LightGray":
			color = Color.LIGHT_GRAY;
			break;
		case "White":
			color = Color.WHITE;
			break;
		case "Pink":
			color = Color.PINK;
			break;
		case "Red":
			color = Color.RED;
			break;
		case "Orange":
			color = Color.ORANGE;
			break;
		case "Yellow":
			color = Color.YELLOW;
			break;
		case "Green":
			color = Color.GREEN;
			break;
		case "Blue":
			color = Color.BLUE;
			break;
		case "Magenta":
			color = Color.MAGENTA;
			break;
		case "Cyan":
			color = Color.CYAN;
			break;
		case "NoColor":
			color = new Color( 0x00FFFFFF, true );
		default:
			color = new Color( 0x00FFFFFF, true );
			break;
		}

		return color;
	}

	private ImageIcon colorToIcon( Color color ) {
		ImageIcon icon;

		if ( color.equals( Color.BLACK ) ) {
			icon = new ImageIcon( "images\\colors\\blackIcon.png" );
		} else if ( color.equals( Color.DARK_GRAY ) ) {
			icon = new ImageIcon( "images\\colors\\darkGrayIcon.png" );
		} else if ( color.equals( Color.GRAY ) ) {
			icon = new ImageIcon( "images\\colors\\grayIcon.png" );
		} else if ( color.equals( Color.LIGHT_GRAY ) ) {
			icon = new ImageIcon( "images\\colors\\lightGrayIcon.png" );
		} else if ( color.equals( Color.WHITE ) ) {
			icon = new ImageIcon( "images\\colors\\whiteIcon.png" );
		} else if ( color.equals( Color.PINK ) ) {
			icon = new ImageIcon( "images\\colors\\pinkIcon.png" );
		} else if ( color.equals( Color.RED ) ) {
			icon = new ImageIcon( "images\\colors\\redIcon.png" );
		} else if ( color.equals( Color.ORANGE ) ) {
			icon = new ImageIcon( "images\\colors\\orangeIcon.png" );
		} else if ( color.equals( Color.YELLOW ) ) {
			icon = new ImageIcon( "images\\colors\\yellowIcon.png" );
		} else if ( color.equals( Color.GREEN ) ) {
			icon = new ImageIcon( "images\\colors\\greenIcon.png" );
		} else if ( color.equals( Color.BLUE ) ) {
			icon = new ImageIcon( "images\\colors\\blueIcon.png" );
		} else if ( color.equals( Color.MAGENTA ) ) {
			icon = new ImageIcon( "images\\colors\\magentaIcon.png" );
		} else if ( color.equals( Color.CYAN ) ) {
			icon = new ImageIcon( "images\\colors\\cyanIcon.png" );
		} else {
			icon = new ImageIcon( "images\\colors\\noColorIcon.png" );
		}

		return icon;
	}

	public void setLineColor( String s ) {
		ImageIcon icon = colorToIcon( stringToColor( s ) );
		lineColorLabel.setIcon( icon );
	}

	public void setFillColor( String s ) {
		ImageIcon icon = colorToIcon( stringToColor( s ) );
		fillColorLabel.setIcon( icon );
	}
}
