
import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
//import static com.almasb.fxglgames.geowars.GeoWarsType.*;

import java.util.Map;
import java.util.function.Supplier;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.getAppHeight;


public class CentipedeFactory  implements EntityFactory {
    //spawns player
    @Spawns("player")
    public Entity newPlayer(SpawnData data) {

        var body = new Rectangle(25, 30, Color.BLUE);
        body.setStroke(Color.GRAY);

        return entityBuilder()
                .type(CentipedeType.PLAYER)
                .from(data)
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(0, 0), 300))
                .viewWithBBox("playerLvl1.png")

                .build();
    }
    //spawns background
    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder()
                .type(CentipedeType.BACKGROUND)
                .from(data)
                .view(new Rectangle(getAppWidth(), getAppHeight(), Color.BLACK))
                .build();
    }
    //spawns mushroom
    @Spawns("mushroom")
    public Entity newMushroom(SpawnData data) {
        return FXGL.entityBuilder()
                .from(data)
                .type(CentipedeType.MUSHROOM)
                .viewWithBBox("mushroomLv1.png")
                .with(new CollidableComponent(true))
                .with(new MushroomComponent())
                .with(new ProjectileComponent(new Point2D(0, 0), 300))
                .build();
    }
    //spawns bullet
    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        play("laser.mp3");
        return FXGL.entityBuilder()
                .from(data)
                .type(CentipedeType.BULLET)
                .viewWithBBox(new Rectangle(20, 4, Color.RED))
                .with(new CollidableComponent(true))
                .with(new ProjectileComponent(new Point2D(0, -1), 300))
                .build();
    }
    //spawns  centipede
    @Spawns("centipede")
    public Entity newCentipede(SpawnData data) {

        return FXGL.entityBuilder()
                .from(data)
                .type(CentipedeType.CENTIPEDE)
                // .viewWithBBox(new Circle(16, 16, 16, Color.GREEN))
                .with(new CollidableComponent(true))
                .with(new CentipedeComponent())
                .viewWithBBox(texture("centipedeBodyLvl1.png").toAnimatedTexture(2,Duration.seconds(1)).play().loop())
                .build();
    }
    //spawns the centipede head
    @Spawns("headCentipede")
    public Entity newHeadCentipede(SpawnData data) {

        return FXGL.entityBuilder()
                .from(data)
                .type(CentipedeType.CENTIPEDE)
                // .viewWithBBox(new Circle(16, 16, 16, Color.GREEN))
                //.view("CentipedeBody.png")
                .with(new CollidableComponent(true))
                .with(new CentipedeComponent())
                .viewWithBBox(texture("centipedeHeadLvl1.png").toAnimatedTexture(2,Duration.seconds(1)).play().loop())
                .build();
    }

    //spawns the spider
    @Spawns("spider")
    public Entity newSpider(SpawnData data) {
        //todo
        return FXGL.entityBuilder()
                .from(data)
                .type(CentipedeType.SPIDER)
                .with(new SpiderComponent())
                .with(new CollidableComponent(true))
                .viewWithBBox(texture("spiderLvl1.png").toAnimatedTexture(3,Duration.seconds(.5)).play().loop())

                .build();
    }
    //spawns the rock
    @Spawns("rock")
    public Entity newRock(SpawnData data) {
        return FXGL.entityBuilder()
                .from(data)
                .type(CentipedeType.ROCK)
                .with(new CollidableComponent(true))
                .viewWithBBox(texture("rockLvl1.png").toAnimatedTexture(2,Duration.seconds(.2)).play().loop())
                .with(new ProjectileComponent(new Point2D(0, 1), 180))
                .build();
    }
    //spawns the score
    @Spawns("scoreText")
    public Entity newScoreText(SpawnData data) {
        String text = data.get("text");

        var e = entityBuilder()//creates the text
                .from(data)
                .view(getUIFactory().newText(text, 24))
                .with(new ExpireCleanComponent(Duration.seconds(0.66)).animateOpacity())
                .build();

        animationBuilder()// creates an animation for the text
                .duration(Duration.seconds(0.66))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(data.getX(), data.getY() - 30))
                .buildAndPlay();

        return e;
    }

}
