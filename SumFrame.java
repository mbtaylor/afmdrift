
public class SumFrame implements Frame {

    private final String name_;
    private final Grid grid_;
    private final double[] samples_;

    public SumFrame( String name, Frame[] frames ) {
        name_ = name;
        grid_ = frames[ 0 ].getGrid();
        int ns = grid_.sampleCount();
        samples_ = new double[ ns ];
        for ( int is = 0; is < ns; is++ ) {
            double s = 0;
            for ( Frame frm : frames ) {
                s += frm.getSamples()[ is ];
            }
            samples_[ is ] = s;
        }
    }

    public String getName() {
        return name_;
    }

    public Grid getGrid() {
        return grid_;
    }

    public double[] getSamples() {
        return samples_;
    }
}
