
import java.io.PrintStream;
import java.util.Random;

public class Drifter {

    private final Grid grid_;
    private final Fit[] yfits_;
    private final double[] drift_;

    public Drifter( Frame frm ) {
        grid_ = frm.getGrid();
        double[] samples = frm.getSamples();

        int nx = grid_.nx();
        int ny = grid_.ny();
        int ny2 = grid_.ny() * 2;
        int nx2 = grid_.nx() * 2;
        yfits_ = new Fit[ ny2 ];
        double[] tracePair = new double[ nx2 ];
        for ( int jy = 0; jy < ny2; jy++ ) {
            double[] deltas = new double[ nx ];
            for ( int ix = 0; ix < nx; ix++ ) {
                int kx0 = jy * nx2 + nx + ix;
                int kx1 = jy * nx2 + nx - 1 - ix;
                deltas[ ix ] = 0.5 * ( samples[ kx0 ] - samples[ kx1 ] );
            }
            yfits_[ jy ] = createLinearFit( deltas );
        }

        int ns = grid_.sampleCount();
        drift_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            int jy = is / ny2;
            int jx = is % ny2;
            drift_[ is ] = yfits_[ jy ].f( jx - nx );
        }
    }

    public Frame getDrift() {
        return Util.createFrame( grid_, drift_ );
    }

    private Fit createLinearFit( double[] data ) {
        int n = data.length;
        double sxy = 0;
        double sxx = 0;
        for ( int i = 0; i < n; i++ ) {
            sxy += data[ i ] * i;
            sxx += i * i;
        }
        final double m = sxy / sxx;
        return new Fit() {
            public double f( double x ) {
                return m * x;
            }
        };
    }

    public static void main( String[] args ) {
        Grid grid = new Grid( 100, 100 );
        Frame in = new SynthFrame( grid, new Random( 234555L ) );
        Frame drift = new Drifter( in ).getDrift();
        PrintStream out = System.out;
        out.println( "t,ix,iy,phase,z,drift" );
        int ns = grid.sampleCount();
        for ( int is = 0; is < ns; is++ ) {
            SamplePos spos = grid.samplePos( is );
            out.println( is + ","
                       + spos.ix + ","
                       + spos.iy + ","
                       + spos.phase + ","
                       + in.getSamples()[ is ] + ","
                       + drift.getSamples()[ is ] );
        }
    }
}
