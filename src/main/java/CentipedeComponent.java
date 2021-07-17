
import com.almasb.fxgl.entity.component.Component;

public class CentipedeComponent extends Component {
    private Direction currentDirection=Direction.DOWN;
    private boolean ifAlone=false;
    private int x=4;
    //todo changes movement direction
    public void move(Direction direction,int distance) {
        entity.translate(direction.vector.multiply(distance));
        x=distance;
        currentDirection=direction;
    }
    //moves in the last given direction
    public void stayMove(){
        entity.translate(currentDirection.vector.multiply(x));
    }

    //todo returns the direction
    public String getDirection(){
        if(currentDirection==Direction.DOWN){
            return "DOWN";

        }else if(currentDirection==Direction.UP){
            return "UP";

        }else if(currentDirection==Direction.RIGHT){
            return "RIGHT";

        }else if(currentDirection==Direction.LEFT){
            return "LEFT";
        }
        return" ";
    }
}