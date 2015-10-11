
public class Undrift {

    private final Gridder gridder_;
    private final double[] samples_;
    private final Fit[] yfits_;

    public Undrift( Gridder gridder, double[] samples ) {
        gridder_ = gridder;
        samples_ = samples;

        int ny2 = gridder_.ny() * 2;
        int nx2 = gridder_.nx() * 2;
        yfits_ = new Fit[ ny2 ];
        double[] tracePair = new double[ nx2 ];
        for ( int jy = 0; jy < gridder_.ny() * 2; jy++ ) {
            System.arraycopy( samples, jy * nx2, tracePair, 0, nx2 );
            yfits_[ jy ] = createFit( tracePair );
        }
    }

    private Fit createFit( double[] tracePair ) {
 return null;
    }
}
