package nl.ru.ai.draw_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import nl.ru.ai.selforganisingmap.DataVector;

public class FreeShape implements Drawable {
	private static final int MAPVECTOR_SIZE = 30;

	private Shape rect;
	private ArrayList<Line> shape;
	private double angle;
	private Point2D translation;
	private Point2D scale;
	private Color lineColor;
	private Color fillColor;
	private BasicStroke stroke;

	public FreeShape( Color lineColor, Color fillColor, BasicStroke stroke ) {
		this.shape = new ArrayList<Line>();
		this.angle = 0.0d;
		this.translation = new Point2D.Double();
		this.scale = new Point2D.Double( 1.0d, 1.0d );
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.stroke = new BasicStroke( stroke.getLineWidth(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
		this.rect = null;
	}

	@Override
	public void draw( Graphics2D g ) {
		AffineTransform t = AffineTransform.getTranslateInstance( getCenterX(), getCenterY() );
		t.rotate( angle );
		t.scale( scale.getX(), scale.getY() );
		t.translate( -getCenterX(), -getCenterY() );

		rect = t.createTransformedShape( new Rectangle2D.Double( getMinX(), getMinY(), getWidth(), getHeight() ) );

		g.transform( t );

		for ( Line l : shape ) {
			l.draw( g );
		}

		AffineTransform inverseT;
		try {
			inverseT = t.createInverse();
		} catch ( NoninvertibleTransformException exception ) {
			inverseT = new AffineTransform();
			inverseT.setToIdentity();

			System.err.println( exception.getMessage() );
		}
		g.transform( inverseT );
	}

	@Override
	public boolean contains( int x, int y ) {
		boolean contains = false;

		for ( Line l : shape ) {
			contains |= l.contains( x, y );
		}

		return contains;
	}

	@Override
	public float getStrokeWidth() {
		return stroke.getLineWidth();
	}

	@Override
	public void setStrokeWidth( float strokeWidth ) {
		stroke = new BasicStroke( strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

		for ( Line l : shape ) {
			l.setStrokeWidth( strokeWidth );
		}
	}

	@Override
	public Color getLineColor() {
		return lineColor;
	}

	@Override
	public void setLineColor( Color lineColor ) {
		this.lineColor = lineColor;

		for ( Line l : shape ) {
			l.setLineColor( lineColor );
		}
	}

	@Override
	public Color getFillColor() {
		return fillColor;
	}

	@Override
	public void setFillColor( Color fillColor ) {
		this.fillColor = fillColor;

		for ( Line l : shape ) {
			l.setFillColor( fillColor );
		}
	}

	@Override
	public double getAngle() {
		return angle;
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
		this.translation = translation;
		
		for ( Line l : shape ) {
			l.setTranslation( translation );
		}
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
		double minX = Double.MAX_VALUE;

		for ( Line l : shape ) {
			double minLineX = l.getMinX();

			if ( minLineX < minX ) {
				minX = minLineX;
			}
		}

		return minX;
	}

	@Override
	public double getMinY() {
		double minY = Double.MAX_VALUE;

		for ( Line l : shape ) {
			double minLineY = l.getMinY();

			if ( minLineY < minY ) {
				minY = minLineY;
			}
		}

		return minY;
	}

	@Override
	public double getMaxX() {
		double maxX = 0.0d;

		for ( Line l : shape ) {
			double maxLineX = l.getMaxX();

			if ( maxLineX > maxX ) {
				maxX = maxLineX;
			}
		}

		return maxX;
	}

	@Override
	public double getMaxY() {
		double maxY = 0.0d;

		for ( Line l : shape ) {
			double maxLineY = l.getMaxY();

			if ( maxLineY > maxY ) {
				maxY = maxLineY;
			}
		}

		return maxY;
	}

	private double getWidth() {
		return getMaxX() - getMinX();
	}

	private double getHeight() {
		return getMaxY() - getMinY();
	}

	private double getCenterX() {
		return getMinX() + getWidth() / 2.0d;
	}

	private double getCenterY() {
		return getMinY() + getHeight() / 2.0d;
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

	public void add( double x1, double y1, double x2, double y2 ) {
		shape.add( new Line( x1, y1, x2, y2, lineColor, fillColor, stroke ) );
	}

	public void clear() {
		shape.clear();
	}

	public ArrayList<Line> getShape() {
		return shape;
	}

	public int size() {
		return shape.size();
	}

	public double getAngle( int firstIndex, int lastIndex ) {
		double firstX = shape.get( firstIndex ).getX1();
		double firstY = shape.get( firstIndex ).getY1();
		double lastX = shape.get( lastIndex ).getX2();
		double lastY = shape.get( lastIndex ).getY2();

		return Math.atan2( lastY - firstY, lastX - firstX );
	}

	public boolean isStraightLine() {
		double fullAngle = getAngle( 0, shape.size() - 1 );
		double maxDeltaAngle = ( Math.PI / 9.0d );

		if ( shape.size() <= 1 ) {
			return true;
		} else {
			for ( int i = 1; i < shape.size(); ++i ) {
				double angle = getAngle( 0, i );
				double dAngle = fullAngle - angle;
				if ( dAngle > Math.PI ) {
					dAngle -= Math.PI * 2.0d;
				} else if ( dAngle < -Math.PI ) {
					dAngle += Math.PI;
				}

				if ( Math.abs( dAngle ) > maxDeltaAngle ) {
					return false;
				}
			}

			return true;
		}
	}

	public Point2D[] getEndPoints() {
		Point2D[] points = new Point2D[2];
		points[0] = new Point2D.Double( shape.get( 0 ).getX1(), shape.get( 0 ).getY1() );
		points[1] = new Point2D.Double( shape.get( shape.size() - 1 ).getX2(), shape.get( shape.size() - 1 ).getY2() );

		return points;
	}

	private ArrayList<Point> bresenhamLine( Line l ) {
		ArrayList<Point> points = new ArrayList<Point>();

		int x1 = (int)l.getMinX();
		int x2 = (int)l.getMaxX();
		int y1 = (int)l.getMinY();
		int y2 = (int)l.getMaxY();

		if ( ( x2 - x1 ) == 0 || ( y2 - y1 ) / ( x2 - x1 ) > 1.0d ) {
			double inverseDeltaErr = ( x2 - x1 ) / ( y2 - y1 );
			double error = inverseDeltaErr - 0.5d;

			for ( int y = y1; y <= y2; ++y ) {
				points.add( new Point( x1, y ) );
				error += inverseDeltaErr;
				if ( error >= 0.5d ) {
					x1 += 1;
					error -= 1.0d;
				}
			}
		} else {
			double deltaErr = ( y2 - y1 ) / ( x2 - x1 );
			double error = deltaErr - 0.5d;

			for ( int x = x1; x <= x2; ++x ) {
				points.add( new Point( x, y1 ) );
				error += deltaErr;
				if ( error >= 0.5d ) {
					y1 += 1;
					error -= 1.0d;
				}
			}
		}

		return points;
	}

	private void scalePoints( ArrayList<Point> points, int endSize ) {
		Point2D size = getSize();
		double scale = Math.max( size.getX(), size.getY() ) / ( (double)endSize - 1 );
		Rectangle2D bounds = getBounds();
		int minX = (int)bounds.getMinX();
		int minY = (int)bounds.getMinY();

		for ( int i = 0; i < points.size(); ++i ) {
			Point p = points.get( i );

			p.x -= minX;
			p.y -= minY;
			p.x /= scale;
			p.y /= scale;
		}
	}

	public DataVector getDataVector() {
		ArrayList<Point> points = new ArrayList<Point>();

		for ( int i = 0; i < shape.size(); ++i ) {
			List<Point> linePoints = bresenhamLine( shape.get( i ) );
			linePoints = linePoints.subList( 0, linePoints.size() - 1 );
			points.addAll( linePoints );
		}

		scalePoints( points, MAPVECTOR_SIZE );
		DataVector vector = new DataVector();
		int numOfMapPixels = MAPVECTOR_SIZE * MAPVECTOR_SIZE;

		for ( int i = 0; i < numOfMapPixels; ++i ) {
			vector.vector.add( 0.0d );
		}

		for ( Point p : points ) {
			vector.vector.set( p.y * MAPVECTOR_SIZE + p.x, 1.0d );
		}

		vector.center();

		return vector;
	}
}
