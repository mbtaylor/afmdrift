
import java.io.IOException;
import java.util.Random;

public class SynthFrame extends Frame {

    private final double[] surface_;
    private final double[] drift_;
    private final double[] samples_;
    private final double[] noise_;

    SynthFrame( String name, Grid grid, Random random ) {
        super( name, grid );
        int ns = grid.sampleCount();
        surface_ = createSurface( grid, random );
        drift_ = createDrift( grid, random );
        noise_ = createNoise( ns, random );
        samples_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = grid.samplePos( is );
            PixelPos ppos = new PixelPos( spos.ix, spos.iy );
            int ip = grid.pixelIndex( ppos );
            samples_[ is ] = surface_[ ip ] + drift_[ is ] + noise_[ is ];
        }
    }

    public double getSample( int is ) {
        return samples_[ is ];
    }

    private static double[] createSurface( Grid grid, Random random ) {
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
