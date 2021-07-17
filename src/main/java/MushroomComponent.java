
import com.almasb.fxgl.entity.component.Component;


public class MushroomComponent extends Component{
    private int phase=0;//0 is full, 1 is a little broken, 2 is half broken, 3 is almost broken, 4 is fully broken

    public int getHit() {
        phase++;
        return phase;
    }
}
