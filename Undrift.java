
public class Undrift {

    private final Grid grid_;
    private final double[] samples_;
    private final Fit[] yfits_;

    public Undrift( Grid grid, double[] samples ) {
        grid_ = grid;
        samples_ = samples;

        int ny2 = grid_.ny() * 2;
        int nx2 = grid_.nx() * 2;
        yfits_ = new Fit[ ny2 ];
        double[] tracePair = new double[ nx2 ];
        for ( int jy = 0; jy < grid_.ny() * 2; jy++ ) {
            System.arraycopy( samples, jy * nx2, tracePair, 0, nx2 );
            yfits_[ jy ] = createFit( tracePair );
        }
    }

    private Fit createFit( double[] tracePair ) {
 return null;
    }
}
