
import java.io.PrintStream;
import java.util.Random;

public class SynthFrame {

    private final Gridder gridder_;
    private final Random random_;
    private final double[] surface_;
    private final double[] drift_;
    private final double[] samples_;
    private final double[] noise_;

    SynthFrame( Gridder gridder, Random random ) {
        gridder_ = gridder;
        random_ = random;
        int ns = gridder_.sampleCount();
        surface_ = createSurface( gridder, random );
        drift_ = createDrift( gridder, random );
        noise_ = createNoise( ns, random );
        samples_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = gridder_.samplePos( is );
            PixelPos ppos = new PixelPos( spos.ix_, spos.iy_ );
            int ip = gridder_.pixelIndex( ppos );
            samples_[ is ] = drift_[ is ] + surface_[ ip ] + noise_[ ip ];
        }
    }

    public double[] getSamples() {
        return samples_;
    }

    private static double[] createSurface( Gridder gridder, Random random ) {
        int n = gridder.pixelCount();
        int nx = gridder.nx();
        int ny = gridder.ny();
        double[] surf = new double[ n ];
        for ( int i = 0; i < n / 100; i++ ) {
            int cx = random.nextInt( nx );
            int cy = random.nextInt( ny );
            double z = random.nextDouble() * 10;
            surf[ gridder.pixelIndex( new PixelPos( cx, cy ) ) ] += z;
            for ( int lx = -1; lx < 2; lx++ ) {
                for ( int ly = -1; ly < 2; ly++ ) {
                    PixelPos ppos = new PixelPos( cx + lx, cy + ly );
                    int index = gridder.pixelIndex( ppos );
                    if ( index >= 0 ) {
                        surf[ index ] += z;
                    }
                }
            }
        }
        return surf;
    }

    private static double[] createDrift( Gridder gridder, Random random ) {
        int n = gridder.sampleCount();
        int nx = gridder.nx();
        int nsamp = gridder.sampleCount();
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

    public static void main( String[] args ) {
        Gridder gridder = new Gridder( 100, 100 );
        SynthFrame frm = new SynthFrame( gridder, new Random( 234555L ) );
        double[] samples = frm.getSamples();
        PrintStream out = System.out;
        out.println( "ix,iy,phase,z" );
        for ( int is = 0; is < samples.length; is++ ) {
            SamplePos spos = gridder.samplePos( is );
            out.println( spos.ix_ + ","
                       + spos.iy_ + ","
                       + spos.phase_ + "," 
                       + samples[ is ] );
        }
    }
}
