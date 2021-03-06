package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import nl.ru.ai.selforganisingmap.DataVector;
import nl.ru.ai.selforganisingmap.Polygon_t;
import nl.ru.ai.selforganisingmap.SelfOrganisingMap;

public class DrawPanel extends JPanel {
	private static final Color BACKGROUNDCOLOR = Color.WHITE;

	private static final int NUM_BUTTONS = 6;
	private static final int BUTTON_SIZE = 60;
	private static final int BUTTON_GAP_SIZE_SMALL = 10;
	private static final int BUTTON_GAP_SIZE_BIG = 20;
	private static final Color BUTTONCOLOR_DEFAULT = new Color( 0xFFE0E0E0, true );
	private static final Color BUTTONCOLOR_SELECTED = new Color( 0xFFE0FFFF, true );

	private static final Color LINECOLOR_INIT = Color.BLACK;
	private static final Color FILLCOLOR_INIT = new Color( 0x00FFFFFF, true );
	private static final float STROKEWIDTH_INIT = 5.0f;
	private static final float RECOGNIZE_STROKEWIDTH = 5.0f;

	private static final int HISTORY_SIZE = 50;

	private static final String[] TOOLTIPTEXT = { "Move", "Resize", "Rotate", "Move to foreground", "Move to background", "Delete" };

	private ToolPanel toolPanel;
	private ColorPanel colorPanel;

	private SelfOrganisingMap map;

	private ArrayList<Drawable> shapes;
	private FreeShape recognizeShape;

	private Point2D lastCoords;
	private Point2D startingCoords;
	private Point2D startingScale;
	private double startingAngle;
	private boolean dragging;
	private Tool_t tool;
	private Selection selection;
	private BasicStroke stroke;
	private BasicStroke backupStroke;
	private Color lineColor;
	private Color backupLineColor;
	private Color fillColor;
	private Color backupFillColor;

	private Stack<ActionState> history;

	private JButton moveButton;
	private JButton resizeButton;
	private JButton rotateButton;
	private JButton layerUpButton;
	private JButton layerDownButton;
	private JButton deleteButton;
	private JButton currentButton;

	private Action undoAction = new AbstractAction() {
		public void actionPerformed( ActionEvent e ) {
			undoLastAction();
		}
	};

	private InputHandler inputHandler = new InputHandler() {
		@Override
		public void actionPerformed( ActionEvent e ) {
			Drawable shape = null;
			ActionState state = null;
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

				state = new ActionState( ActionType_t.MOVE_LAYER_UP );
				state.setShape( shape );
				addToHistory( state );

				moveShapeUp( shape );

				break;
			case "LayerDown":
				currentButton.setBackground( BUTTONCOLOR_DEFAULT );

				shape = selection.getShape();

				state = new ActionState( ActionType_t.MOVE_LAYER_DOWN );
				state.setShape( shape );
				addToHistory( state );

				moveShapeDown( shape );

				break;
			case "Delete":
				tool = Tool_t.SELECTION;
				shape = selection.getShape();

				state = new ActionState( ActionType_t.DELETE_DRAWABLE );
				state.setShapes( new ArrayList<Drawable>( shapes ) );
				state.setShape( shape );
				addToHistory( state );

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
			Point2D mouseCoords = new Point2D.Double( e.getX(), e.getY() );

			if ( tool != Tool_t.SELECTION && tool != Tool_t.MOVESELECTED && tool != Tool_t.RESIZESELECTED && tool != Tool_t.ROTATESELECTED && selection != null ) {
				ActionState state = new ActionState( ActionType_t.DESELECT_DRAWABLE );
				state.setShape( selection.getShape() );
				addToHistory( state );

				removeSelection();
			}

			ActionState state = new ActionState( null );
			switch ( tool ) {
			case SELECTION:
				int contained = -1;

				for ( int i = shapes.size() - 1; i >= 0; --i ) {
					if ( shapes.get( i ).contains( (int)mouseCoords.getX(), (int)mouseCoords.getY() ) ) {
						contained = i;
						break;
					}
				}

				if ( contained != -1 ) {
					addToHistory( new ActionState( ActionType_t.SELECT_DRAWABLE ) );

					createSelection( shapes.get( contained ) );
				} else {
					if ( selection != null ) {
						state = new ActionState( ActionType_t.DESELECT_DRAWABLE );
						state.setShape( selection.getShape() );
						addToHistory( state );

						removeSelection();
					}
				}

				break;
			case MOVESELECTED:
				if ( !dragging ) {
					startingCoords = (Point2D)mouseCoords.clone();
				}

				break;
			case RESIZESELECTED:
				if ( !dragging && selection != null ) {
					startingScale = selection.getShape().getScale();
				}

				break;
			case ROTATESELECTED:
				if ( !dragging && selection != null ) {
					Drawable shape = selection.getShape();
					startingAngle = shape.getAngle();
				}

				break;
			case IMAGE:
				state = new ActionState( ActionType_t.ADD_DRAWABLE );
				state.setShapes( new ArrayList<Drawable>( shapes ) );
				addToHistory( state );

				BufferedImage img = null;
				try {
					img = ImageIO.read( getClass().getResource( "/resources/eiffel.png" ) );
				} catch ( IOException exception ) {
					System.err.println( "Could not open file\n " + exception.getMessage() );
					return;
				}

				Image image = new Image( mouseCoords.getX(), mouseCoords.getY(), mouseCoords.getX() + img.getWidth(), mouseCoords.getY() + img.getHeight(), img );

				shapes.add( image );

				break;
			case TEXT:
				int changeTextIndex = -1;
				for ( int i = 0; i < shapes.size(); ++i ) {
					String className = shapes.get( i ).getClass().getName();
					boolean contains = shapes.get( i ).contains( (int)mouseCoords.getX(), (int)mouseCoords.getY() );

					if ( className.equals( "nl.ru.ai.draw_interface.Text" ) && contains ) {
						changeTextIndex = i;
						break;
					}
				}

				if ( changeTextIndex == -1 ) {
					String text = (String)JOptionPane.showInputDialog( (DrawPanel)e.getSource(), "Enter text:", "Text", JOptionPane.PLAIN_MESSAGE, null, null, "Text" );
					if ( text != null ) {
						state = new ActionState( ActionType_t.ADD_DRAWABLE );
						state.setShapes( new ArrayList<Drawable>( shapes ) );
						addToHistory( state );

						Text textObject = new Text( mouseCoords.getX(), mouseCoords.getY(), text, lineColor );
						shapes.add( textObject );
					}
				} else {
					String newText = (String)JOptionPane.showInputDialog( (DrawPanel)e.getSource(), "Change text:", "Text", JOptionPane.PLAIN_MESSAGE, null, null, "Text" );
					if ( newText != null ) {
						Text textObject = (Text)shapes.get( changeTextIndex );

						state = new ActionState( ActionType_t.CHANGE_TEXT );
						state.setShape( textObject );
						state.setText( textObject.getText() );
						addToHistory( state );

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
			Point2D mouseCoords = new Point2D.Double( e.getX(), e.getY() );
			ActionState state = null;

			switch ( tool ) {
			case MOVESELECTED:
				if ( selection != null ) {
					state = new ActionState( ActionType_t.MOVE_DRAWABLE );
					Drawable shape = selection.getShape();
					Point2D oldTranslation = shape.getTranslation();
					Point2D translation = new Point2D.Double( startingCoords.getX() - mouseCoords.getX() + oldTranslation.getX(), startingCoords.getY() - mouseCoords.getY() + oldTranslation.getY() );
					state.setTranslation( translation );

					addToHistory( state );
				}

				break;
			case RESIZESELECTED:
				state = new ActionState( ActionType_t.RESIZE_DRAWABLE );
				state.setScale( startingScale );
				addToHistory( state );

				break;
			case ROTATESELECTED:
				state = new ActionState( ActionType_t.ROTATE_DRAWABLE );
				state.setAngle( startingAngle );
				addToHistory( state );

				break;
			case LINE:
			case TRIANGLE:
			case RECTANGLE:
			case ELLIPSE:
				if ( dragging ) {
					state = new ActionState( ActionType_t.ADD_DRAWABLE );
					if ( shapes.size() <= 1 ) {
						state.setShapes( new ArrayList<Drawable>() );
					} else {
						ArrayList<Drawable> shapesCopy = new ArrayList<Drawable>();
						shapesCopy.addAll( shapes.subList( 0, shapes.size() - 1 ) );
						state.setShapes( shapesCopy );
					}
					addToHistory( state );
				}

				break;
			case RECOGNIZE:
				if ( dragging && recognizeShape.size() >= 1 ) {
					if ( recognizeShape.isStraightLine() ) {
						state = new ActionState( ActionType_t.ADD_DRAWABLE );
						state.setShapes( new ArrayList<Drawable>( shapes ) );
						addToHistory( state );

						Point2D[] coords = recognizeShape.getEndPoints();
						Line line = new Line( coords[0].getX(), coords[0].getY(), coords[1].getX(), coords[1].getY(), lineColor, fillColor, stroke );
						shapes.add( line );
					} else {
						DataVector vector = recognizeShape.getDataVector();
						Rectangle2D bounds = recognizeShape.getBounds();
						Polygon_t type = map.getClassification( vector );
						state = new ActionState( ActionType_t.ADD_DRAWABLE );

						switch ( type ) {
						case TRIANGLE:
							state.setShapes( new ArrayList<Drawable>( shapes ) );
							addToHistory( state );

							Triangle triangle = new Triangle( bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), lineColor, fillColor, stroke );
							shapes.add( triangle );

							break;
						case RECTANGLE:
							state.setShapes( new ArrayList<Drawable>( shapes ) );
							addToHistory( state );

							Rectangle rectangle = new Rectangle( bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), lineColor, fillColor, stroke );
							shapes.add( rectangle );

							break;
						case ELLIPSE:
							state.setShapes( new ArrayList<Drawable>( shapes ) );
							addToHistory( state );

							Ellipse ellipse = new Ellipse( bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY(), lineColor, fillColor, stroke );
							shapes.add( ellipse );
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
				}

				lastCoords = mouseCoords;
				dragging = true;

				break;
			case FREEDRAW:
				if ( dragging ) {
					FreeShape freeShape = (FreeShape)shapes.get( shapes.size() - 1 );
					freeShape.add( lastCoords.getX(), lastCoords.getY(), mouseCoords.getX(), mouseCoords.getY() );
				} else {
					FreeShape shape = new FreeShape( lineColor, fillColor, stroke );

					ActionState state = new ActionState( ActionType_t.ADD_DRAWABLE );
					state.setShapes( new ArrayList<Drawable>( shapes ) );
					addToHistory( state );

					shapes.add( shape );
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
						Point2D center = shape.getCenter();
						Point2D dCoords = new Point2D.Double( mouseCoords.getX() - lastCoords.getX(), mouseCoords.getY() - lastCoords.getY() );
						if ( mouseCoords.getX() < center.getX() ) {
							dCoords = new Point2D.Double( -dCoords.getX(), dCoords.getY() );
						}
						if ( mouseCoords.getY() < center.getY() ) {
							dCoords = new Point2D.Double( dCoords.getX(), -dCoords.getY() );
						}
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

		@Override
		public void keyPressed( KeyEvent e ) {
			if ( e.isControlDown() && e.getKeyCode() == KeyEvent.VK_Z ) {
				undoLastAction();
			}
		}
	};

	public DrawPanel() {
		super();

		setLayout( null );

		shapes = new ArrayList<Drawable>();
		recognizeShape = new FreeShape( Color.BLACK, new Color( 0x00FFFFFF, true ), new BasicStroke( RECOGNIZE_STROKEWIDTH ) );
		tool = Tool_t.RECOGNIZE;
		map = new SelfOrganisingMap( getClass().getResource( "/resources/map/vectors.txt" ) );
		lastCoords = new Point( 0, 0 );

		dragging = false;

		lineColor = LINECOLOR_INIT;
		backupLineColor = lineColor;
		fillColor = FILLCOLOR_INIT;
		backupFillColor = fillColor;
		stroke = new BasicStroke( STROKEWIDTH_INIT );
		backupStroke = stroke;

		history = new Stack<ActionState>();

		addMouseListener( inputHandler );
		addMouseMotionListener( inputHandler );
		selection = null;

		this.setBackground( BACKGROUNDCOLOR );

		moveButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/moveIcon.png" ) ) );
		moveButton.setBackground( BUTTONCOLOR_DEFAULT );
		moveButton.setToolTipText( TOOLTIPTEXT[0] );
		moveButton.setActionCommand( "Move" );
		moveButton.addActionListener( inputHandler );
		add( moveButton );
		moveButton.setVisible( false );

		resizeButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/resizeIcon.png" ) ) );
		resizeButton.setBackground( BUTTONCOLOR_DEFAULT );
		resizeButton.setToolTipText( TOOLTIPTEXT[1] );
		resizeButton.setActionCommand( "Resize" );
		resizeButton.addActionListener( inputHandler );
		add( resizeButton );
		resizeButton.setVisible( false );

		rotateButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/rotateIcon.png" ) ) );
		rotateButton.setBackground( BUTTONCOLOR_DEFAULT );
		rotateButton.setToolTipText( TOOLTIPTEXT[2] );
		rotateButton.setActionCommand( "Rotate" );
		rotateButton.addActionListener( inputHandler );
		add( rotateButton );
		rotateButton.setVisible( false );

		layerUpButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/layerUpIcon.png" ) ) );
		layerUpButton.setBackground( BUTTONCOLOR_DEFAULT );
		layerUpButton.setToolTipText( TOOLTIPTEXT[3] );
		layerUpButton.setActionCommand( "LayerUp" );
		layerUpButton.addActionListener( inputHandler );
		add( layerUpButton );
		layerUpButton.setVisible( false );

		layerDownButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/layerDownIcon.png" ) ) );
		layerDownButton.setBackground( BUTTONCOLOR_DEFAULT );
		layerDownButton.setToolTipText( TOOLTIPTEXT[4] );
		layerDownButton.setActionCommand( "LayerDown" );
		layerDownButton.addActionListener( inputHandler );
		add( layerDownButton );
		layerDownButton.setVisible( false );

		deleteButton = new JButton( new ImageIcon( getClass().getResource( "/resources/icons/deleteIcon.png" ) ) );
		deleteButton.setBackground( BUTTONCOLOR_DEFAULT );
		deleteButton.setToolTipText( TOOLTIPTEXT[5] );
		deleteButton.setActionCommand( "Delete" );
		deleteButton.addActionListener( inputHandler );
		add( deleteButton );
		deleteButton.setVisible( false );

		currentButton = moveButton;

		this.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW ).put( KeyStroke.getKeyStroke( "control Z" ), "undo" );
		this.getActionMap().put( "undo", undoAction );
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
		Point2D buttonCoords = selection.getButtonSpace( this.getSize(), BUTTON_SIZE, BUTTON_GAP_SIZE_SMALL, BUTTON_GAP_SIZE_BIG );

		if ( buttonCoords != null ) {
			moveButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY(), BUTTON_SIZE, BUTTON_SIZE );
			resizeButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE + BUTTON_GAP_SIZE_SMALL, BUTTON_SIZE, BUTTON_SIZE );
			rotateButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 2 + BUTTON_GAP_SIZE_SMALL * 2, BUTTON_SIZE, BUTTON_SIZE );
			layerUpButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 3 + BUTTON_GAP_SIZE_BIG + BUTTON_GAP_SIZE_SMALL * 3, BUTTON_SIZE, BUTTON_SIZE );
			layerDownButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 4 + BUTTON_GAP_SIZE_BIG + BUTTON_GAP_SIZE_SMALL * 4, BUTTON_SIZE, BUTTON_SIZE );
			deleteButton.setBounds( (int)buttonCoords.getX(), (int)buttonCoords.getY() + BUTTON_SIZE * 5 + BUTTON_GAP_SIZE_BIG + BUTTON_GAP_SIZE_SMALL * 5, BUTTON_SIZE, BUTTON_SIZE );
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

			if ( !history.isEmpty() ) {
				ActionState lastState = history.peek();
				if ( lastState.getActionType() != ActionType_t.CHANGE_LINEWIDTH ) {
					ActionState state = new ActionState( ActionType_t.CHANGE_LINEWIDTH );
					state.setStroke( shape.getStroke() );
					addToHistory( state );
				}
			}

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

			if ( !history.isEmpty() ) {
				ActionState lastState = history.peek();
				if ( lastState.getActionType() != ActionType_t.CHANGE_LINECOLOR ) {
					ActionState state = new ActionState( ActionType_t.CHANGE_LINECOLOR );
					state.setLineColor( shape.getLineColor() );
					addToHistory( state );
				}
			}

			shape.setLineColor( lineColor );

			repaint();
		}
	}

	public void setFillColor( Color fillColor ) {
		this.fillColor = fillColor;

		if ( selection != null ) {
			Drawable shape = selection.getShape();

			if ( !history.isEmpty() ) {
				ActionState lastState = history.peek();
				if ( lastState.getActionType() != ActionType_t.CHANGE_FILLCOLOR ) {
					ActionState state = new ActionState( ActionType_t.CHANGE_FILLCOLOR );
					state.setFillColor( shape.getFillColor() );
					addToHistory( state );
				}
			}

			shape.setFillColor( fillColor );

			repaint();
		}
	}

	private void moveShapeUp( Drawable shape ) {
		Rectangle2D bounds = shape.getBounds();

		int shapeIndex = -1;
		for ( int i = 0; i < shapes.size(); ++i ) {
			if ( shape == shapes.get( i ) ) {
				shapeIndex = i;
				break;
			}
		}

		if ( shapeIndex >= 0 ) {
			int intersectIndex = -1;
			for ( int i = shapeIndex + 1; i < shapes.size(); ++i ) {
				if ( shapes.get( i ).intersects( bounds ) ) {
					intersectIndex = i;
					break;
				}
			}

			if ( intersectIndex >= 0 ) {
				shapes.add( intersectIndex, shapes.remove( shapeIndex ) );
			}
		}
	}

	private void moveShapeDown( Drawable shape ) {
		Rectangle2D bounds = shape.getBounds();

		int shapeIndex = -1;
		for ( int i = shapes.size() - 1; i >= 0; --i ) {
			if ( shape == shapes.get( i ) ) {
				shapeIndex = i;
				break;
			}
		}

		if ( shapeIndex >= -1 ) {
			int intersectIndex = -1;
			for ( int i = shapeIndex - 1; i >= 0; --i ) {
				if ( shapes.get( i ).intersects( bounds ) ) {
					intersectIndex = i;
					break;
				}
			}

			if ( intersectIndex >= 0 ) {
				shapes.add( intersectIndex, shapes.remove( shapeIndex ) );
			}
		}
	}

	public void clearShapes() {
		if ( selection != null ) {
			ActionState state = new ActionState( ActionType_t.DESELECT_DRAWABLE );
			state.setShape( selection.getShape() );
			addToHistory( state );

			removeSelection();
		}

		ActionState state = new ActionState( ActionType_t.CLEAR_ALL );
		state.setShapes( new ArrayList<Drawable>( shapes ) );
		addToHistory( state );

		shapes.clear();
		repaint();
	}

	private void addToHistory( ActionState state ) {
		history.push( state );

		while ( history.size() > HISTORY_SIZE ) {
			history.remove( 0 );
		}
	}

	public void undoLastAction() {
		if ( !history.empty() ) {
			ActionState dState = history.pop();

			switch ( dState.getActionType() ) {
			case SELECT_DRAWABLE:
				removeSelection();

				break;
			case DESELECT_DRAWABLE:
				createSelection( dState.getShape() );

				break;
			case MOVE_DRAWABLE:
				if ( selection != null ) {
					selection.getShape().setTranslation( dState.getTranslation() );
				}

				break;
			case RESIZE_DRAWABLE:
				if ( selection != null ) {
					selection.getShape().setScale( dState.getScale() );
				}

				break;
			case ROTATE_DRAWABLE:
				if ( selection != null ) {
					selection.getShape().setAngle( dState.getAngle() );
				}

				break;
			case ADD_DRAWABLE:
				shapes = dState.getShapes();

				break;
			case DELETE_DRAWABLE:
				shapes = dState.getShapes();
				createSelection( dState.getShape() );

				break;
			case CLEAR_ALL:
				shapes = dState.getShapes();

				break;
			case CHANGE_LINEWIDTH:
				if ( selection != null ) {
					selection.getShape().setStroke( dState.getStroke() );
				}

				break;
			case CHANGE_LINECOLOR:
				selection.getShape().setLineColor( dState.getLineColor() );

				break;
			case CHANGE_FILLCOLOR:
				selection.getShape().setFillColor( dState.getFillColor() );

				break;
			case CHANGE_TEXT:
				Text textObject = (Text)dState.getShape();
				textObject.setText( dState.getText() );

				break;
			case MOVE_LAYER_UP:
				moveShapeDown( dState.getShape() );

				break;
			case MOVE_LAYER_DOWN:
				moveShapeUp( dState.getShape() );

				break;
			}

			repaint();
		}
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
			selection.draw( g2d );
			updateSelection();
		}
	}
}
