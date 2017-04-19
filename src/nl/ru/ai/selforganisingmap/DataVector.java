package nl.ru.ai.selforganisingmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class DataVector {
	public ArrayList<Double> vector;
	public Polygon_t classification;

	public DataVector() {
		vector = new ArrayList<Double>();
		classification = null;
	}

	public DataVector( ArrayList<Double> vector, Polygon_t classification ) {
		this.vector = vector;
		this.classification = classification;
	}

	public DataVector( DataVector otherVector ) {
		this.vector = new ArrayList<Double>( otherVector.vector );
		this.classification = otherVector.classification;
	}

	public double getLength() {
		double sumSq = 0.0d;

		for ( Double v : vector ) {
			sumSq += v * v;
		}

		return Math.sqrt( sumSq );
	}

	public double getDistance( DataVector otherVector ) {
		double sum = 0.0d;

		for ( int i = 0; i < otherVector.vector.size(); ++i ) {
			sum += ( vector.get( i ) - otherVector.vector.get( i ) ) * ( vector.get( i ) - otherVector.vector.get( i ) );
		}

		return sum;
	}

	public void normalizeVector() {
		double length = this.getLength();

		for ( int i = 0; i < vector.size(); ++i ) {
			vector.set( i, this.vector.get( i ) / length );
		}
	}

	private void shiftUp() {
		int width = (int)Math.sqrt( (double)vector.size() );
		for ( int j = 0; j < width; ++j ) {
			vector.remove( 0 );
			vector.add( 0.0d );
		}
	}

	private void shiftDown() {
		int width = (int)Math.sqrt( (double)vector.size() );
		int vectorSize = vector.size();
		for ( int j = 0; j < width; ++j ) {
			vector.remove( vectorSize - 1 );
			vector.add( 0, 0.0d );
		}
	}

	private void shiftLeft() {
		int width = (int)Math.sqrt( (double)vector.size() );
		int vectorSize = vector.size();
		int j = 0;
		while ( j < vectorSize ) {
			vector.remove( j );
			j += width;
			vector.add( j - 1, 0.0d );
		}
	}

	private void shiftRight() {
		int width = (int)Math.sqrt( (double)vector.size() );
		int vectorSize = vector.size();
		int j = 0;
		while ( j < vectorSize ) {
			vector.add( j, 0.0d );
			j += width;
			vector.remove( j );
		}
	}

	private Point[] getBounds() {
		int size = (int)Math.sqrt( (double)vector.size() );
		Point[] bounds = { new Point( size - 1, size - 1 ), new Point( 0, 0 ) };

		for ( int y = 0; y < size; ++y ) {
			for ( int x = 0; x < size; ++x ) {
				int i = y * size + x;
				if ( vector.get( i ) != 0.0d ) {
					if ( x < bounds[0].x ) {
						bounds[0].x = x;
					}
					if ( y < bounds[0].y ) {
						bounds[0].y = y;
					}
					if ( x > bounds[1].x ) {
						bounds[1].x = x;
					}
					if ( y > bounds[1].y ) {
						bounds[1].y = y;
					}
				}
			}
		}

		return bounds;
	}

	public void center() {
		int size = (int)Math.sqrt( (double)vector.size() );
		Point[] bounds = getBounds();
		Dimension space = new Dimension( bounds[0].x + size - bounds[1].x - 1, bounds[0].y + size - bounds[1].y - 1 );

		while ( bounds[0].x > space.width / 2 ) {
			shiftLeft();
			bounds[0].x--;
		}

		while ( bounds[0].x < space.width / 2 ) {
			shiftRight();
			bounds[0].x++;
		}

		while ( bounds[0].y > space.height / 2 ) {
			shiftUp();
			bounds[0].y--;
		}

		while ( bounds[0].y < space.height / 2 ) {
			shiftDown();
			bounds[0].y++;
		}
	}

	public void saveImage( String fileName ) {
		Dimension size = new Dimension( (int)Math.sqrt( vector.size() ), (int)Math.sqrt( vector.size() ) );
		BufferedImage img = new BufferedImage( size.width, size.height, BufferedImage.TYPE_INT_RGB );
		Graphics2D g = img.createGraphics();
		g.setPaint( new Color( 255, 255, 255 ) );
		g.fillRect( 0, 0, size.width, size.height );

		for ( int y = 0; y < size.height; ++y ) {
			for ( int x = 0; x < size.width; ++x ) {
				if ( vector.get( y * size.width + x ) != 0.0d ) {
					img.setRGB( x, y, 0x00000000 );
				}
			}
		}

		try {
			ImageIO.write( img, "png", new File( fileName ) );
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}
