
import java.io.IOException;
import java.util.Random;

public class SynthFrame extends Frame {

    private final double[] surface_;
    private final double[] drift_;
    private final double[] noise_;
    private final double[] samples_;

    SynthFrame( String name, Grid grid, Random random ) {
        super( name, grid );
        
        int ns = grid.sampleCount();
        double[] features = createFeatures( grid, random );
        double[] slope = createSlope( grid );
        drift_ = createDrift( grid, random );
        noise_ = createNoise( ns, random );
        surface_ = new double[ ns ];
        samples_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = grid.samplePos( is );
            PixelPos ppos = new PixelPos( spos.ix, spos.iy );
            int ip = grid.pixelIndex( ppos );
            surface_[ is ] = features[ ip ] + slope[ ip ];
            samples_[ is ] = surface_[ is ] + drift_[ is ] + noise_[ is ];
        }
    }

    public double getSample( int is ) {
        return samples_[ is ];
    }

    public Frame getSurface() {
        return new Frame( "surface", getGrid() ) {
            public double getSample( int is ) {
                return surface_[ is ];
            }
        };
    }

    public Frame getDrift() {
        return new Frame( "drift", getGrid() ) {
            public double getSample( int is ) {
                return drift_[ is ];
            }
        };
    }

    public Frame getNoise() {
        return new Frame( "noise", getGrid() ) {
            public double getSample( int is ) {
                return noise_[ is ];
            }
        };
    }

    private static double[] createFeatures( Grid grid, Random random ) {
        int n = grid.pixelCount();
        int nx = grid.nx();
        int ny = grid.ny();
        double[] surf = new double[ n ];
        for ( int i = 0; i < n / 100; i++ ) {
            int cx = random.nextInt( nx );
            int cy = random.nextInt( ny );
            double z = random.nextDouble() * 10;
            surf[ grid.pixelIndex( new PixelPos( cx, cy ) ) ] += z;
            for ( int lx = -1; lx < 2; lx++ ) {
                for ( int ly = -1; ly < 2; ly++ ) {
                    PixelPos ppos = new PixelPos( cx + lx, cy + ly );
                    int index = grid.pixelIndex( ppos );
                    if ( index >= 0 ) {
                        surf[ index ] += z;
                    }
                }
            }
        }
        return surf;
    }

    private static double[] createSlope( Grid grid ) {
        double slopeAmp = 2;
        int ns = grid.sampleCount();
        int ny = grid.ny();
        double[] slope = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = grid.samplePos( is );
            int ix = spos.ix;
            int iy = spos.iy;
            slope[ is ] = slopeAmp * iy / ny;
        }
        return slope;
    }

    private static double[] createDrift( Grid grid, Random random ) {
        int n = grid.sampleCount();
        int nx = grid.nx();
        int nsamp = grid.sampleCount();
        double[] drift = new double[ n ];
        addWave( drift, nx * 12, 2, random );
        addWave( drift, nx * 20, 3, random );
        addWave( drift, nsamp * 6, 4, random );
        addWave( drift, nsamp * 1.5, 0.8, random );
        return drift;
    }

    private static double[] createNoise( int ns, Random random ) {
       double[] noise = new double[ ns ];
       for ( int is = 0; is < ns; is++ ) {
           noise[ is ] = random.nextGaussian() * 0.2;
       }
       return noise;
    }

    private static void addWave( double data[], double lambda,
                                 double amplitude, Random random ) {
        double phase = random.nextDouble() * lambda;
        double l1 = 1.0 / lambda;
        for ( int i = 0; i < data.length; i++ ) {
            data[ i ] += amplitude * Math.sin( phase + i * l1 );
        }
    }

    public static void main( String[] args ) throws IOException {
        SynthFrame frm = new SynthFrame( "z", new Grid( 100, 100 ),
                                         new Random( 234555L ) );
        Util.writeFrames( new Frame[] { frm } );
    }
}
