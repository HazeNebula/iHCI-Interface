package nl.ru.ai.selforganisingmap;

import java.util.ArrayList;
import java.util.Random;

public class Node {
	public int x;
	public int y;
	public DataVector weights;

	public Node() {
		this.x = 0;
		this.y = 0;
		this.weights = null;
	}

	public Node( int vectorSize, int x, int y, Polygon_t classification ) {
		this.x = x;
		this.y = y;

		weights = new DataVector( new ArrayList<Double>(), classification );
		Random rand = new Random( System.nanoTime() );

		for ( int i = 0; i < vectorSize; ++i ) {
			weights.vector.add( rand.nextDouble() );
		}
	}

	public Node( Node otherNode ) {
		this.x = otherNode.x;
		this.y = otherNode.y;
		this.weights = new DataVector( otherNode.weights );
	}

	public void adjustWeights( DataVector vector, double learningRate, double influence ) {
		for ( int i = 0; i < weights.vector.size(); ++i ) {
			weights.vector.set( i, weights.vector.get( i ) + influence * learningRate * ( vector.vector.get( i ) - weights.vector.get( i ) ) );
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append( Integer.toString( x ) + "," + Integer.toString( y ) + "," + weights.classification.name() );

		for ( int i = 0; i < weights.vector.size(); ++i ) {
			builder.append( "," + Double.toString( weights.vector.get( i ) ) );
		}

		return builder.toString();
	}
}
