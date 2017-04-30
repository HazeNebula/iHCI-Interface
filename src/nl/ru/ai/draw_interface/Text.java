package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Text implements Drawable {
	//	public static final double HITBOX_SIZE = 20.0d;

	private Shape rect;
	private String text;
	private double x1, y1, x2, y2, angle;
	private Point2D translation;
	private Point2D scale;
	private Color color;

	public Text( double x1, double y1, String text, Color color ) {
		this.x1 = x1;
		this.y1 = y1;
		this.text = text;
		this.angle = 0.0d;
		this.translation = new Point2D.Double();
		this.scale = new Point2D.Double( 1.0d, 1.0d );
		this.color = color;
	}

	@Override
	public void draw( Graphics2D g ) {
		Font f = new Font( "Arial", Font.PLAIN, 20 );
		g.setFont( f );

		FontMetrics fm = g.getFontMetrics();
		Rectangle2D bounds = fm.getStringBounds( text, g );
		x2 = x1 - bounds.getMinX() + bounds.getWidth();
		y2 = y1 + bounds.getMinY();

		AffineTransform t = AffineTransform.getTranslateInstance( getCenterX(), getCenterY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -getCenterX(), -getCenterY() );
		rect = t.createTransformedShape( new Rectangle2D.Double( getMinX(), getMinY(), getWidth(), getHeight() ) );

		Color oldColor = g.getColor();
		
		g.setColor( color );
		g.transform( t );
		g.drawString( text, (int)x1, (int)y1 );
		AffineTransform inverseT;
		try {
			inverseT = t.createInverse();
		} catch ( NoninvertibleTransformException exception ) {
			inverseT = new AffineTransform();
			inverseT.setToIdentity();

			System.err.println( exception.getMessage() );
		}
		g.transform( inverseT );

		g.setColor( oldColor );
	}

	@Override
	public boolean contains( int x, int y ) {
		return rect.contains( x, y );
	}

	@Override
	public BasicStroke getStroke() {
		return new BasicStroke( 1.0f );
	}
	
	@Override
	public void setStroke( BasicStroke stroke ) {
		return;
	}

	@Override
	public Color getLineColor() {
		return color;
	}

	@Override
	public void setLineColor( Color lineColor ) {
		color = lineColor;
	}

	@Override
	public Color getFillColor() {
		return new Color( 0x00FFFFFF, true );
	}

	@Override
	public void setFillColor( Color fillColor ) {
		return;
	}

	@Override
	public double getAngle() {
		return this.angle;
	}

	@Override
	public void setAngle( double angle ) {
		this.angle = angle;
	}

	@Override
	public Point2D getTranslation() {
		return translation;
	}

	@Override
	public void setTranslation( Point2D translation ) {
		x1 -= this.translation.getX();
		y1 -= this.translation.getY();
		x2 -= this.translation.getX();
		y2 -= this.translation.getY();
		
		this.translation = translation;
		
		x1 += this.translation.getX();
		y1 += this.translation.getY();
		x2 += this.translation.getX();
		y2 += this.translation.getY();
	}

	@Override
	public Point2D getScale() {
		return scale;
	}

	@Override
	public void setScale( Point2D scale ) {
		this.scale = scale;
	}

	@Override
	public double getMinX() {
		return Math.min( x1, x2 );
	}

	@Override
	public double getMinY() {
		return Math.min( y1, y2 );
	}

	@Override
	public double getMaxX() {
		return Math.max( x1, x2 );
	}

	@Override
	public double getMaxY() {
		return Math.max( y1, y2 );
	}

	private double getWidth() {
		return Math.abs( x1 - x2 );
	}

	private double getHeight() {
		return Math.abs( y1 - y2 );
	}

	private double getCenterX() {
		return getMinX() + getWidth() / 2;
	}

	private double getCenterY() {
		return getMinY() + getHeight() / 2;
	}

	@Override
	public Point2D getCenter() {
		return new Point2D.Double( getCenterX(), getCenterY() );
	}

	@Override
	public Point2D getSize() {
		return new Point2D.Double( getWidth(), getHeight() );
	}

	@Override
	public Rectangle2D getBounds() {
		return rect.getBounds2D();
	}
}
