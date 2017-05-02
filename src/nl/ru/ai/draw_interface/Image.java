package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Image implements Drawable {
	private Shape rectangle;
	private BufferedImage image;
	private double x1, y1, x2, y2, angle;
	private Point2D translation;
	private Point2D scale;

	public Image( double x1, double y1, double x2, double y2, BufferedImage image ) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.angle = 0.0d;
		this.translation = new Point2D.Double();
		this.scale = new Point2D.Double( 1.0d, 1.0d );
		this.image = image;
	}

	@Override
	public void draw( Graphics2D g ) {		
		AffineTransform t = AffineTransform.getTranslateInstance( getCenterX(), getCenterY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -getCenterX(), -getCenterY() );
		rectangle = t.createTransformedShape( new Rectangle2D.Double( getMinX(), getMinY(), getWidth(), getHeight() ) );
		t.translate( x1, y1 );

		g.drawRenderedImage( image, t );
	}

	@Override
	public boolean contains( int x, int y ) {
		return rectangle.contains( x, y );
	}
	
	@Override
	public boolean intersects( Rectangle2D rect ) {
		return rectangle.intersects( rect );
	}
	
	@Override
	public BasicStroke getStroke() {
		return new BasicStroke( 0.0f );
	}

	@Override
	public void setStroke( BasicStroke stroke ) {
		return;
	}

	@Override
	public Color getLineColor() {
		return Color.BLACK;
	}

	@Override
	public void setLineColor( Color lineColor ) {
		return;
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
		return rectangle.getBounds2D();
	}
}
