package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import nl.ru.ai.selforganisingmap.DataVector;
import nl.ru.ai.selforganisingmap.Polygon_t;
import nl.ru.ai.selforganisingmap.SelfOrganisingMap;

public class DrawPanel extends JPanel {
	private static final Color BACKGROUNDCOLOR = Color.WHITE;

	private static final int NUM_BUTTONS = 6;
	private static final int BUTTON_SIZE = 60;
	private static final int BUTTON_GAP_SIZE = 10;
	private static final Color BUTTONCOLOR_DEFAULT = new Color( 0xFFE0E0E0, true );
	private static final Color BUTTONCOLOR_SELECTED = new Color( 0xFFE0FFFF, true );

	private static final Color LINECOLOR_INIT = Color.BLACK;
	private static final Color FILLCOLOR_INIT = new Color( 0x00FFFFFF, true );
	private static final float STROKEWIDTH_INIT = 5.0f;
	private static final float RECOGNIZE_STROKEWIDTH = 5.0f;

	private static final String[] TOOLTIPTEXT = { "Move", "Resize", "Rotate", "Move to foreground", "Move to background", "Delete" };

	private ToolPanel toolPanel;
	private ColorPanel colorPanel;

	private SelfOrganisingMap map;

	private ArrayList<Drawable> shapes;
	private FreeShape recognizeShape;

	private Point2D lastCoords;
	private boolean dragging;
	private boolean shapeSet;
	private Tool_t tool;
	private Selection selection;
	private BasicStroke stroke;
	private BasicStroke backupStroke;
	private Color lineColor;
	private Color backupLineColor;
	private Color fillColor;
	private Color backupFillColor;

	private JButton moveButton;
	private JButton resizeButton;
	private JButton rotateButton;
	private JButton layerUpButton;
	private JButton layerDownButton;
	private JButton deleteButton;
	private JButton currentButton;

	private InputHandler inputHandler = new InputHandler() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			int shapeIndex = 0;
			int intersectIndex = 0;
			Drawable shape = null;
			Rectangle2D bounds = null;
			boolean shapeFound = false;
			switch ( e.getActionCommand() ) {
			case "Move":
				setButtonSelection( Tool_t.MOVESELECTED );
				toolPanel.setButtonSelection( null );

				tool = Tool_t.MOVESELECTED;
				break;
			case "Resize":
				setButtonSelection( Tool_t.RESIZESELECTED );
				toolPanel.setButtonSelection( null );

				tool = Tool_t.RESIZESELECTED;
				break;
			case "Rotate":
				setButtonSelection( Tool_t.ROTATESELECTED );
				toolPanel.setButtonSelection( null );

				tool = Tool_t.ROTATESELECTED;
				break;
			case "LayerUp":
				currentButton.setBackground( BUTTONCOLOR_DEFAULT );

				shape = selection.getShape();
				bounds = shape.getBounds();
				for ( int i = 0; i < shapes.size(); ++i ) {
					if ( shape == shapes.get( i ) ) {
						shapeIndex = i;
						break;
					}
				}
				
				for ( int i = shapeIndex + 1; i < shapes.size(); ++i ) {
					if ( shapes.get( i ).intersects( bounds ) ) {
						intersectIndex = i;
						shapeFound = true;
						break;
					}
				}

				if ( shapeFound ) {
					shapes.add( intersectIndex, shapes.remove( shapeIndex ) );
				}

				break;
			case "LayerDown":
				currentButton.setBackground( BUTTONCOLOR_DEFAULT );

				shape = selection.getShape();
				bounds = shape.getBounds();
				for ( int i = shapes.size() - 1; i >= 0; --i ) {
					if ( shape == shapes.get( i ) ) {
						shapeIndex = i;
						break;
					}
				}
				
				for ( int i = shapeIndex - 1; i >= 0; --i ) {
					if ( shapes.get( i ).intersects( bounds ) ) {
						intersectIndex = i;
						shapeFound = true;
						break;
					}
				}

				if ( shapeFound ) {
					shapes.add( intersectIndex, shapes.remove( shapeIndex ) );
				}

				break;
			case "Delete":
				tool = Tool_t.SELECTION;
				shape = selection.getShape();

				if ( shape != null ) {
					for ( int i = 0; i < shapes.size(); ++i ) {
						if ( shapes.get( i ) == shape ) {
							shapes.remove( i );
							removeSelection();

							setButtonSelection( null );
							toolPanel.setButtonSelection( Tool_t.SELECTION );
							tool = Tool_t.SELECTION;
							break;
						}
					}
				}

				break;
			default:
				break;
			}

			repaint();
		}

		@Override
		public void mousePressed( MouseEvent e ) {
			Point mouseCoords = new Point( e.getX(), e.getY() );

			if ( tool != Tool_t.SELECTION && tool != Tool_t.MOVESELECTED && tool != Tool_t.RESIZESELECTED && tool != Tool_t.ROTATESELECTED && selection != null ) {
				removeSelection();
			}

			switch ( tool ) {
			case SELECTION:
				int contained = -1;

				for ( int i = shapes.size() - 1; i >= 0; --i ) {
					if ( shapes.get( i ).contains( mouseCoords.x, mouseCoords.y ) ) {
						contained = i;
						break;
					}
				}

				if ( contained != -1 ) {
					createSelection( shapes.get( contained ) );
				} else {
					removeSelection();
				}

				break;
			case IMAGE:
				JFileChooser fc = new JFileChooser( Paths.get( "." ).toAbsolutePath().normalize().toString() );
				FileNameExtensionFilter filter = new FileNameExtensionFilter( "Image files", "png", "bmp", "jpg", "jpeg" );
				fc.setFileFilter( filter );
				fc.setAcceptAllFileFilterUsed( false );
				int returnVal = fc.showOpenDialog( (DrawPanel)e.getSource() );

				if ( returnVal == JFileChooser.APPROVE_OPTION ) {
					String filename = fc.getSelectedFile().getName();
					BufferedImage img = null;
					try {
						img = ImageIO.read( new File( filename ) );
					} catch ( IOException exception ) {
						System.err.println( "Could not open file\n " + exception.getMessage() );
						return;
					}

					shapes.add( new Image( mouseCoords.getX(), mouseCoords.getY(), mouseCoords.getX() + img.getWidth(), mouseCoords.getY() + img.getHeight(), img ) );
				}

				break;
			case TEXT:
				int changeTextIndex = -1;
				for ( int i = 0; i < shapes.size(); ++i ) {
					String className = shapes.get( i ).getClass().getName();
					boolean contains = shapes.get( i ).contains( mouseCoords.x, mouseCoords.y );

					if ( className.equals( "nl.ru.ai.draw_interface.Text" ) && contains ) {
						changeTextIndex = i;
						break;
					}
				}

				if ( changeTextIndex == -1 ) {
					String text = (String)JOptionPane.showInputDialog( (DrawPanel)e.getSource(), "Enter text:", "Text", JOptionPane.PLAIN_MESSAGE, null, null, "Text" );
					if ( text != null ) {
						shapes.add( new Text( mouseCoords.getX(), mouseCoords.getY(), text, lineColor ) );
					}
				} else {
					String newText = (String)JOptionPane.showInputDialog( (DrawPanel)e.getSource(), "Change text:", "Text", JOptionPane.PLAIN_MESSAGE, null, null, "Text" );
					if ( newText != null ) {
						Text textObject = (Text)shapes.get( changeTextIndex );
						textObject.setText( newText );
					}
				}

				break;
			default:
				break;
			}

			repaint();
		}

		@Override
		public void mouseReleased( MouseEvent e ) {
			switch ( tool ) {
			case RECOGNIZE:
				if ( dragging && shapeSet ) {
					if ( recognizeShape.isStraightLine() ) {
						Point2D[] coords = recognizeShape.getEndPoints();
						shapes.add( new Line( coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY(), lineColor, fillColor, stroke ) );
					} else {
						DataVector vector = recognizeShape.getDataVector();
						Rectangle2D bounds = recognizeShape.getBounds();
						Polygon_t type = map.getClassification( vector );

						switch ( type ) {
						case TRIANGLE:
							shapes.add( new Triangle( bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), lineColor, fillColor, stroke ) );
							break;
						case RECTANGLE:
							shapes.add( new Rectangle( bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), lineColor, fillColor, stroke ) );
							break;
						case ELLIPSE:
							shapes.add( new Ellipse( bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), lineColor, fillColor, stroke ) );
							break;
						default:
							break;
						}
					}

					recognizeShape.clear();
					repaint();
				}

				break;
			default:
				break;
			}

			dragging = false;
		}

		@Override
		public void mouseDragged( MouseEvent e ) {
			Point2D mouseCoords = new Point2D.Double( e.getX(), e.getY() );

			switch ( tool ) {
			case LINE:
				if ( dragging ) {
					shapes.remove( shapes.size() - 1 );
				} else {
					lastCoords = mouseCoords;
				}

				dragging = true;
				shapes.add( new Line( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY(), lineColor, fillColor, stroke ) );

				break;
			case TRIANGLE:
				if ( dragging ) {
					shapes.remove( shapes.size() - 1 );
				} else {
					lastCoords = mouseCoords;
				}

				dragging = true;
				shapes.add( new Triangle( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY(), lineColor, fillColor, stroke ) );

				break;
			case RECTANGLE:
				if ( dragging ) {
					shapes.remove( shapes.size() - 1 );
				} else {
					lastCoords = mouseCoords;
				}

				dragging = true;
				shapes.add( new Rectangle( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY(), lineColor, fillColor, stroke ) );

				break;
			case ELLIPSE:
				if ( dragging ) {
					shapes.remove( shapes.size() - 1 );
				} else {
					lastCoords = mouseCoords;
				}

				dragging = true;
				shapes.add( new Ellipse( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY(), lineColor, fillColor, stroke ) );

				break;
			case RECOGNIZE:
				if ( dragging ) {
					recognizeShape.add( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY() );
					shapeSet = true;
				}

				lastCoords = mouseCoords;
				dragging = true;

				break;
			case FREEDRAW:
				if ( dragging ) {
					FreeShape freeShape = (FreeShape)shapes.get( shapes.size() - 1 );
					freeShape.add( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY() );
				} else {
					shapes.add( new FreeShape( lineColor, fillColor, stroke ) );
				}

				lastCoords = mouseCoords;
				dragging = true;

				break;
			case MOVESELECTED:
				if ( dragging && selection != null ) {
					Drawable shape = selection.getShape();

					if ( shape != null ) {
						Point2D dCoords = new Point2D.Double( mouseCoords.getX() - lastCoords.getX(), mouseCoords.getY() - lastCoords.getY() );
						Point2D translation = shape.getTranslation();
						shape.setTranslation( new Point2D.Double( translation.getX() + dCoords.getX(), translation.getY() + dCoords.getY() ) );
					}
				}

				lastCoords = mouseCoords;
				dragging = true;

				break;
			case RESIZESELECTED:
				if ( dragging && selection != null ) {
					Drawable shape = selection.getShape();

					if ( shape != null ) {
						Point2D size = shape.getSize();
						Point2D dCoords = new Point2D.Double( mouseCoords.getX() - lastCoords.getX(), mouseCoords.getY() - lastCoords.getY() );
						Point2D dScale = new Point2D.Double( dCoords.getX() / size.getX(), dCoords.getY() / size.getY() );
						Point2D currentScale = shape.getScale();
						shape.setScale( new Point2D.Double( currentScale.getX() + dScale.getX(), currentScale.getY() + dScale.getY() ) );
					}
				}

				lastCoords = mouseCoords;
				dragging = true;

				break;
			case ROTATESELECTED:
				if ( dragging && selection != null ) {
					Drawable shape = selection.getShape();

					if ( shape != null ) {
						Point2D center = shape.getCenter();
						double oldAngle = Math.atan2( lastCoords.getY() - center.getY(), lastCoords.getX() - center.getX() );
						double currentAngle = Math.atan2( mouseCoords.getY() - center.getY(), mouseCoords.getX() - center.getX() );
						double dAngle = currentAngle - oldAngle;
						if ( dAngle > Math.PI ) {
							dAngle -= Math.PI * 2.0d;
						} else if ( dAngle < -Math.PI ) {
							dAngle += Math.PI * 2.0d;
						}
						shape.setAngle( shape.getAngle() + dAngle );
					}
				}

				lastCoords = mouseCoords;
				dragging = true;

				break;
			default:
				break;
			}

			repaint();
		}
	};

	public DrawPanel() {
		super();

		setLayout( null );

		shapes = new ArrayList<Drawable>();
		recognizeShape = new FreeShape( Color.BLACK, new Color( 0x00FFFFFF, true ), new BasicStroke( RECOGNIZE_STROKEWIDTH ) );
		tool = Tool_t.RECOGNIZE;
		map = new SelfOrganisingMap( "map\\vectors.txt" );
		lastCoords = new Point( 0, 0 );

		dragging = false;
		shapeSet = false;

		lineColor = LINECOLOR_INIT;
		backupLineColor = lineColor;
		fillColor = FILLCOLOR_INIT;
		backupFillColor = fillColor;
		stroke = new BasicStroke( STROKEWIDTH_INIT );
		backupStroke = stroke;

		addMouseListener( inputHandler );
		addMouseMotionListener( inputHandler );
		selection = null;

		this.setBackground( BACKGROUNDCOLOR );

		moveButton = new JButton( new ImageIcon( "images\\icons\\MoveIcon.png" ) );
		moveButton.setBackground( BUTTONCOLOR_DEFAULT );
		moveButton.setToolTipText( TOOLTIPTEXT[0] );
		moveButton.setActionCommand( "Move" );
		moveButton.addActionListener( inputHandler );
		add( moveButton );
		moveButton.setVisible( false );

		resizeButton = new JButton( new ImageIcon( "images\\icons\\ResizeIcon.png" ) );
		resizeButton.setBackground( BUTTONCOLOR_DEFAULT );
		resizeButton.setToolTipText( TOOLTIPTEXT[1] );
		resizeButton.setActionCommand( "Resize" );
		resizeButton.addActionListener( inputHandler );
		add( resizeButton );
		resizeButton.setVisible( false );

		rotateButton = new JButton( new ImageIcon( "images\\icons\\RotateIcon.png" ) );
		rotateButton.setBackground( BUTTONCOLOR_DEFAULT );
		rotateButton.setToolTipText( TOOLTIPTEXT[2] );
		rotateButton.setActionCommand( "Rotate" );
		rotateButton.addActionListener( inputHandler );
		add( rotateButton );
		rotateButton.setVisible( false );

		layerUpButton = new JButton( new ImageIcon( "images\\icons\\layerUpIcon.png" ) );
		layerUpButton.setBackground( BUTTONCOLOR_DEFAULT );
		layerUpButton.setToolTipText( TOOLTIPTEXT[3] );
		layerUpButton.setActionCommand( "LayerUp" );
		layerUpButton.addActionListener( inputHandler );
		add( layerUpButton );
		layerUpButton.setVisible( false );

		layerDownButton = new JButton( new ImageIcon( "images\\icons\\layerDownIcon.png" ) );
		layerDownButton.setBackground( BUTTONCOLOR_DEFAULT );
		layerDownButton.setToolTipText( TOOLTIPTEXT[4] );
		layerDownButton.setActionCommand( "LayerDown" );
		layerDownButton.addActionListener( inputHandler );
		add( layerDownButton );
		layerDownButton.setVisible( false );

		deleteButton = new JButton( new ImageIcon( "images\\icons\\DeleteIcon.png" ) );
		deleteButton.setBackground( BUTTONCOLOR_DEFAULT );
		deleteButton.setToolTipText( TOOLTIPTEXT[5] );
		deleteButton.setActionCommand( "Delete" );
		deleteButton.addActionListener( inputHandler );
		add( deleteButton );
		deleteButton.setVisible( false );

		currentButton = moveButton;
	}

	public void setToolPanel( ToolPanel buttonPanel ) {
		this.toolPanel = buttonPanel;
	}

	public void setColorPanel( ColorPanel colorPanel ) {
		this.colorPanel = colorPanel;
	}

	public void setTool( Tool_t tool ) {
		this.tool = tool;
	}

	private void createSelection( Drawable shape ) {
		selection = new Selection( shape, NUM_BUTTONS );

		updateSelection();
		moveButton.setVisible( true );
		resizeButton.setVisible( true );
		rotateButton.setVisible( true );
		layerUpButton.setVisible( true );
		layerDownButton.setVisible( true );
		deleteButton.setVisible( true );

		backupLineColor = new Color( lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha() );
		backupFillColor = new Color( fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getAlpha() );
		backupStroke = new BasicStroke( stroke.getLineWidth() );

		lineColor = shape.getLineColor();
		colorPanel.setLineColor( colorToString( lineColor ) );
		fillColor = shape.getFillColor();
		colorPanel.setFillColor( colorToString( fillColor ) );
		stroke = shape.getStroke();
		toolPanel.setStrokeWidthSlider( (int)stroke.getLineWidth() );

		repaint();
	}

	private void updateSelection() {
		Point2D buttonCoords = selection.getButtonSpace( this.getSize(), BUTTON_SIZE, BUTTON_GAP_SIZE );

		if ( buttonCoords != null ) {
			moveButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY(), BUTTON_SIZE, BUTTON_SIZE );
			resizeButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE + BUTTON_GAP_SIZE, BUTTON_SIZE, BUTTON_SIZE );
			rotateButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 2 + BUTTON_GAP_SIZE * 2, BUTTON_SIZE, BUTTON_SIZE );
			layerUpButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 3 + BUTTON_GAP_SIZE * 3, BUTTON_SIZE, BUTTON_SIZE );
			layerDownButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 4 + BUTTON_GAP_SIZE * 4, BUTTON_SIZE, BUTTON_SIZE );
			deleteButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 5 + BUTTON_GAP_SIZE * 5, BUTTON_SIZE, BUTTON_SIZE );
		}
	}

	private void removeSelection() {
		selection = null;

		moveButton.setVisible( false );
		resizeButton.setVisible( false );
		rotateButton.setVisible( false );
		layerUpButton.setVisible( false );
		layerDownButton.setVisible( false );
		deleteButton.setVisible( false );

		lineColor = backupLineColor;
		colorPanel.setLineColor( colorToString( lineColor ) );
		fillColor = backupFillColor;
		colorPanel.setFillColor( colorToString( fillColor ) );
		stroke = backupStroke;
		toolPanel.setStrokeWidthSlider( (int)stroke.getLineWidth() );

		repaint();
	}

	public void setButtonSelection( Tool_t tool ) {
		if ( tool == null ) {
			currentButton.setBackground( BUTTONCOLOR_DEFAULT );
		} else {
			JButton button = null;

			switch ( tool ) {
			case MOVESELECTED:
				button = moveButton;
				break;
			case RESIZESELECTED:
				button = resizeButton;
				break;
			case ROTATESELECTED:
				button = rotateButton;
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

	public void setStrokeSize( float width ) {
		stroke = new BasicStroke( width );

		if ( selection != null ) {
			Drawable shape = selection.getShape();
			shape.setStroke( stroke );

			repaint();
		}
	}

	private String colorToString( Color color ) {
		if ( color.equals( Color.BLACK ) ) {
			return "Black";
		} else if ( color.equals( Color.DARK_GRAY ) ) {
			return "DarkGray";
		} else if ( color.equals( Color.GRAY ) ) {
			return "Gray";
		} else if ( color.equals( Color.LIGHT_GRAY ) ) {
			return "LightGray";
		} else if ( color.equals( Color.WHITE ) ) {
			return "White";
		} else if ( color.equals( Color.PINK ) ) {
			return "Pink";
		} else if ( color.equals( Color.RED ) ) {
			return "Red";
		} else if ( color.equals( Color.ORANGE ) ) {
			return "Orange";
		} else if ( color.equals( Color.YELLOW ) ) {
			return "Yellow";
		} else if ( color.equals( Color.GREEN ) ) {
			return "Green";
		} else if ( color.equals( Color.BLUE ) ) {
			return "Blue";
		} else if ( color.equals( Color.MAGENTA ) ) {
			return "Magenta";
		} else if ( color.equals( Color.CYAN ) ) {
			return "Cyan";
		} else if ( color.equals( new Color( 0x00FFFFFF, true ) ) ) {
			return "NoColor";
		} else {
			return "";
		}
	}

	public void setLineColor( Color lineColor ) {
		this.lineColor = lineColor;

		if ( selection != null ) {
			Drawable shape = selection.getShape();
			shape.setLineColor( lineColor );

			repaint();
		}
	}

	public void setFillColor( Color fillColor ) {
		this.fillColor = fillColor;

		if ( selection != null ) {
			Drawable shape = selection.getShape();
			shape.setFillColor( fillColor );

			repaint();
		}
	}

	public void clearShapes() {
		if ( selection != null ) {
			removeSelection();
		}

		shapes.clear();
		repaint();
	}

	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent( g );

		Graphics2D g2d = (Graphics2D)g;

		for ( Drawable s : shapes ) {
			s.draw( g2d );
		}

		recognizeShape.draw( g2d );

		if ( selection != null ) {
			updateSelection();
			selection.draw( g2d );
		}
	}
}
