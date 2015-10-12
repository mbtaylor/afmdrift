
public abstract class Frame {

    private final String name_;
    private final Grid grid_;

    protected Frame( String name, Grid grid ) {
        grid_ = grid;
        name_ = name;
    }

    public String getName() {
        return name_;
    }

    public Grid getGrid() {
        return grid_;
    }

    public abstract double getSample( int is );
}
