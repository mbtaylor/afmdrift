
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public class Drifter {

    private final Grid grid_;
    private final double[] drift_;

    public Drifter( Frame frm ) {
        grid_ = frm.getGrid();
        double[] samples = frm.getSamples();

        int nx = grid_.nx();
        int ny = grid_.ny();
        int ny2 = grid_.ny() * 2;
        int nx2 = grid_.nx() * 2;
        Fit[] xfits = new Fit[ ny2 ];
        for ( int jy = 0; jy < ny2; jy++ ) {
            double[] xdeltas = new double[ nx ];
            for ( int ix = 0; ix < nx; ix++ ) {
                int kx0 = jy * nx2 + nx + ix;
                int kx1 = jy * nx2 + nx - 1 - ix;
                xdeltas[ ix ] = 0.5 * ( samples[ kx0 ] - samples[ kx1 ] );
            }
            xfits[ jy ] = createLinearFit( xdeltas );
        }

        int ns = grid_.sampleCount();
        double[] adrift = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            int jy = is / nx2;
            int jx = is % nx2;
            adrift[ is ] = xfits[ jy ].f( jx - nx );
        }

        double[] yoffs = new double[ ny2 ];
        for ( int jy = 1; jy < ny2; jy++ ) {
            Fit fit = xfits[ jy ];
            yoffs[ jy ] = yoffs[ jy - 1 ] + fit.f( nx ) - fit.f( -nx );
        }

        drift_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            int jy = is / nx2;
            drift_[ is ] = - adrift[ is ] - yoffs[ jy ];
        }
    }

    //  double[] ydeltas = new double[ ny2 ];
    //  for ( int iy = 0; iy < ny; iy++ ) {
    //      int ky0 = iy;
    //      int ky1 = ny2 - 1 - iy;
    //      double s = 0;
    //      for ( int jx = 0; jx < nx2; jx++ ) {
    //          int is0 = ky0 * nx2 + jx;
    //          int is1 = ky1 * nx2 + jx;
    //          SamplePos sp0 = grid_.samplePos( is0 );
    //          SamplePos sp1 = grid_.samplePos( is1 );
    //          assert sp0.ix == sp1.ix;
    //          assert sp0.iy == sp1.iy;
    //          assert sp1.phase - sp0.phase == 2;
    //          double delta =
    //               ( samples[ is1 ] - adrift[ is1 ] - yoffs[ ky1 ] )
    //             - ( samples[ is0 ] - adrift[ is0 ] - yoffs[ ky0 ] );
    //          s += delta;
    //      }
    //      double meanDelta = s / nx2;
    //      ydeltas[ iy ] = 0.5 * meanDelta;
    //  }
    //  Fit deltaFit = createLinearFit( ydeltas );

    public Frame getDrift() {
        return Util.createFrame( "drift", grid_, drift_ );
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

    public static void main( String[] args ) throws IOException {
        Grid grid = new Grid( 100, 100 );
        Frame in = new SynthFrame( "z", grid, new Random( 234555L ) );
        Drifter drifter = new Drifter( in );
        Frame drift = drifter.getDrift();
        Frame sum = new SumFrame( "out", new Frame[] { in, drift } );
        Util.writeFrames( new Frame[] { in, drift, sum } );
    }
}
